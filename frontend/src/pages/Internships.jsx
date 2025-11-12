import { useState, useEffect, useMemo, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import SearchPanel from "../components/SearchPanel.jsx";
import "../styles/pasantias.css";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/, "");

function getStoredItem(key) {
  const persisted = localStorage.getItem(key);
  if (persisted !== null) return persisted;
  return sessionStorage.getItem(key);
}

// Helper para formatear fecha relativa
function formatRelativeDate(dateString) {
  if (!dateString) return "Fecha no disponible";
  
  const date = new Date(dateString);
  const now = new Date();
  const diffTime = now - date;
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
  
  if (diffDays === 0) return "Hoy";
  if (diffDays === 1) return "Ayer";
  if (diffDays < 7) return `Hace ${diffDays} días`;
  if (diffDays < 30) return `Hace ${Math.floor(diffDays / 7)} semana${Math.floor(diffDays / 7) !== 1 ? "s" : ""}`;
  if (diffDays < 365) return `Hace ${Math.floor(diffDays / 30)} mes${Math.floor(diffDays / 30) !== 1 ? "es" : ""}`;
  return `Hace ${Math.floor(diffDays / 365)} año${Math.floor(diffDays / 365) !== 1 ? "s" : ""}`;
}

export default function Internships() {
  const navigate = useNavigate();
  const [filters, setFilters] = useState({ texto: "", carrera: "", modalidad: "" });
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [user, setUser] = useState(null);
  const [approving, setApproving] = useState(new Set());

  const readUserFromStorage = useCallback(() => {
    const token = getStoredItem("authToken");
    const userInfoRaw = getStoredItem("userInfo");

    if (!token || !userInfoRaw) {
      setUser(null);
      return;
    }

    try {
      setUser(JSON.parse(userInfoRaw));
    } catch (e) {
      console.error("Error parsing user info:", e);
      setUser(null);
    }
  }, []);

  useEffect(() => {
    readUserFromStorage();
    const handleAuthChange = () => readUserFromStorage();
    window.addEventListener("storage", handleAuthChange);
    window.addEventListener("auth-change", handleAuthChange);
    return () => {
      window.removeEventListener("storage", handleAuthChange);
      window.removeEventListener("auth-change", handleAuthChange);
    };
  }, [readUserFromStorage]);

  const isAdmin = user?.rol === "ADMINISTRADOR";

  async function load() {
    try {
      setLoading(true);
      setError("");
      const res = await fetch(`${API}/pasantias`, {
        headers: { Accept: "application/json;charset=UTF-8" }
      });
      
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      
      const data = await res.json();
      
      // Mapear los datos del backend al formato esperado por el frontend
      const mappedData = Array.isArray(data) ? data.map((p) => ({
        id: p.idPasantia,
        titulo: p.titulo || "",
        empresa: p.nombreEmpresa || "Empresa no especificada",
        ciudad: p.ciudad || "",
        modalidad: p.modalidad || "",
        carrera: "Varias", // PasantiaResponseDTO no incluye carreras, usar placeholder
        publicada: formatRelativeDate(p.fechaPublicacion),
        estado: p.estado,
        puestoACubrir: p.puestoACubrir,
        asignacionEstimulo: p.asignacionEstimulo,
        emailContacto: p.emailContacto,
        aceptaPostulaciones: p.aceptaPostulaciones,
        diasRestantes: p.diasRestantes
      })) : [];
      
      setJobs(mappedData);
    } catch (err) {
      setError(err.message || "No se pudo obtener el listado de pasantías.");
      setJobs([]);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  const filtered = useMemo(() => {
    return jobs.filter(j => {
      const t = filters.texto.toLowerCase();
      const byTexto = !t || 
        j.titulo.toLowerCase().includes(t) || 
        j.empresa.toLowerCase().includes(t) || 
        j.ciudad.toLowerCase().includes(t) ||
        (j.puestoACubrir && j.puestoACubrir.toLowerCase().includes(t));
      const byCarrera = !filters.carrera || j.carrera === filters.carrera;
      const byModalidad = !filters.modalidad || j.modalidad === filters.modalidad;
      return byTexto && byCarrera && byModalidad;
    });
  }, [jobs, filters]);

  function handleSearch(payload) {
    setFilters(payload);
    // Si querés usar querystring:
    // const qs = new URLSearchParams(payload).toString();
    // navigate(`/pasantias?${qs}`);
  }

  async function handleVerDetalles(id) {
    navigate(`/pasantias/${id}`);
  }

  async function handleAprobar(id, e) {
    e.preventDefault();
    e.stopPropagation();
    
    if (!isAdmin) {
      alert("Solo los administradores pueden aprobar pasantías");
      return;
    }

    const token = getStoredItem("authToken");
    if (!token) {
      alert("Debes iniciar sesión para realizar esta acción");
      return;
    }

    if (!confirm("¿Estás seguro de que deseas aprobar esta pasantía?")) {
      return;
    }

    setApproving(prev => new Set(prev).add(id));

    try {
      const res = await fetch(`${API}/pasantias/${id}/aprobar`, {
        method: "PUT",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });

      const data = await res.json();

      if (!res.ok) {
        throw new Error(data.mensaje || `Error ${res.status}`);
      }

      // Recargar la lista de pasantías
      await load();
      alert("Pasantía aprobada exitosamente");
    } catch (err) {
      console.error("Error al aprobar pasantía:", err);
      alert(err.message || "Error al aprobar la pasantía");
    } finally {
      setApproving(prev => {
        const next = new Set(prev);
        next.delete(id);
        return next;
      });
    }
  }

  return (
    <section className="pasantias-page">
      {/* BARRA STICKY con el mismo SearchPanel */}
      <div className="filters-bar">
        <div className="container">
          {/* Usamos el SearchPanel ya abierto y en modo inline. La barra lo deja fijo. */}
          <SearchPanel open={true} variant="inline" onSearch={handleSearch} />
        </div>
      </div>

      {/* Contenido / resultados */}
      <div className="container results-wrap">
        <header className="results-head">
          <h1>Pasantías</h1>
          <p className="muted">
            {loading ? "Cargando…" :
              error ? "—" :
              `${filtered.length} resultado${filtered.length !== 1 ? "s" : ""}${(filters.texto || filters.carrera || filters.modalidad) ? " (filtrado)" : ""}`}
          </p>
        </header>

        {error && (
          <div className="emp-alert error" style={{ marginBottom: "1rem" }}>
            Ocurrió un error: {error} —{" "}
            <button className="link" onClick={load}>Reintentar</button>
          </div>
        )}

        {loading && !error && (
          <div className="results-grid">
            {Array.from({ length: 6 }).map((_, i) => (
              <div key={i} className="job-card skeleton" style={{ minHeight: "150px" }} />
            ))}
          </div>
        )}

        {!loading && !error && (
          <>
            {filtered.length === 0 ? (
              <div className="no-results">
                {filters.texto || filters.carrera || filters.modalidad
                  ? "No se encontraron pasantías con esos filtros."
                  : "No hay pasantías disponibles en este momento."}
              </div>
            ) : (
              <div className="results-grid">
                {filtered.map(j => (
                  <article key={j.id} className="job-card">
                    <header>
                      <h3>{j.titulo}</h3>
                      <span className="badge">{j.modalidad}</span>
                    </header>
                    <p className="meta">
                      {j.empresa} · {j.ciudad} · {j.carrera}
                    </p>
                    {j.puestoACubrir && (
                      <p className="meta" style={{ fontSize: "0.9em", color: "#666", marginTop: "0.25rem" }}>
                        {j.puestoACubrir}
                      </p>
                    )}
                    <footer>
                      <span className="time">{j.publicada}</span>
                      <div style={{ display: "flex", gap: "0.5rem", flexWrap: "wrap" }}>
                        <button 
                          className="btn btn-ghost" 
                          onClick={() => handleVerDetalles(j.id)}
                          style={{ fontSize: "0.9em" }}
                        >
                          Ver detalles
                        </button>
                        {j.aceptaPostulaciones && (
                          <a className="btn btn-ghost" href={`/pasantias/${j.id}`}>
                            Postular
                          </a>
                        )}
                        {isAdmin && j.estado === "PENDIENTE_DE_APROBACION" && (
                          <button
                            className="btn btn-ghost"
                            onClick={(e) => handleAprobar(j.id, e)}
                            disabled={approving.has(j.id)}
                            style={{ 
                              fontSize: "0.9em",
                              color: "#28a745",
                              borderColor: "#28a745"
                            }}
                          >
                            {approving.has(j.id) ? "Aprobando..." : "Aprobar"}
                          </button>
                        )}
                      </div>
                    </footer>
                  </article>
                ))}
              </div>
            )}
          </>
        )}
      </div>
    </section>
  );
}
