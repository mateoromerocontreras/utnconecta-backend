import { useState, useEffect, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import SearchPanel from "../components/SearchPanel.jsx";
import "../styles/pasantias.css";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/, "");

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

export default function PasantiasPublicadas() {
  const navigate = useNavigate();
  const [filters, setFilters] = useState({ texto: "", carrera: "", modalidad: "" });
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function load() {
    try {
      setLoading(true);
      setError("");
      // Usar el endpoint específico para pasantías publicadas
      const res = await fetch(`${API}/pasantias/publicadas`, {
        headers: { 
          Accept: "application/json;charset=UTF-8",
          "Content-Type": "application/json;charset=UTF-8"
        }
      });
      
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      
      // Asegurar que la respuesta se decodifique como UTF-8
      const text = await res.text();
      const data = JSON.parse(text);
      
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
      setError(err.message || "No se pudo obtener el listado de pasantías publicadas.");
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
  }

  function handleVerDetalles(id) {
    navigate(`/pasantias/${id}`);
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
          <h1>Pasantías Publicadas</h1>
          <p className="muted">
            {loading ? "Cargando…" :
              error ? "—" :
              `${filtered.length} pasantía${filtered.length !== 1 ? "s" : ""} disponible${filtered.length !== 1 ? "s" : ""}${(filters.texto || filters.carrera || filters.modalidad) ? " (filtrado)" : ""}`}
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
                  ? "No se encontraron pasantías publicadas con esos filtros."
                  : "No hay pasantías publicadas disponibles en este momento."}
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

