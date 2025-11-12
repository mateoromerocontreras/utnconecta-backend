import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "../styles/pasantias.css";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/, "");

export default function PasantiaDetalle() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [pasantia, setPasantia] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function load() {
      try {
        setLoading(true);
        setError("");
        const res = await fetch(`${API}/pasantias/${id}`, {
          headers: { 
            Accept: "application/json;charset=UTF-8",
            "Content-Type": "application/json;charset=UTF-8"
          }
        });

        if (!res.ok) {
          const text = await res.text();
          const errorData = text ? JSON.parse(text) : {};
          throw new Error(errorData.mensaje || `HTTP ${res.status}`);
        }

        // Asegurar que la respuesta se decodifique como UTF-8
        const text = await res.text();
        const data = JSON.parse(text);
        setPasantia(data);
      } catch (err) {
        setError(err.message || "No se pudo cargar la pasantía.");
      } finally {
        setLoading(false);
      }
    }

    if (id) {
      load();
    }
  }, [id]);

  if (loading) {
    return (
      <section className="pasantias-page">
        <div className="container">
          <p>Cargando detalles...</p>
        </div>
      </section>
    );
  }

  if (error || !pasantia) {
    return (
      <section className="pasantias-page">
        <div className="container">
          <div className="emp-alert error">
            {error || "Pasantía no encontrada"}
            <button className="link" onClick={() => navigate("/pasantias")}>
              Volver a pasantías
            </button>
          </div>
        </div>
      </section>
    );
  }

  return (
    <section className="pasantias-page">
      <div className="container">
        <header style={{ marginBottom: "2rem" }}>
          <button 
            className="btn btn-ghost" 
            onClick={() => navigate("/pasantias")}
            style={{ marginBottom: "1rem" }}
          >
            ← Volver
          </button>
          <h1>{pasantia.titulo}</h1>
          <p className="muted">
            {pasantia.empresa?.nombre || "Empresa no especificada"} · {pasantia.ciudad} · {pasantia.modalidad}
          </p>
        </header>

        <div style={{ display: "grid", gap: "2rem", gridTemplateColumns: "2fr 1fr" }}>
          <article className="job-card" style={{ padding: "2rem" }}>
            <h2>Descripción</h2>
            {pasantia.puestoACubrir && (
              <div style={{ marginBottom: "1.5rem" }}>
                <h3 style={{ fontSize: "1.1em", marginBottom: "0.5rem" }}>Puesto a cubrir</h3>
                <p>{pasantia.puestoACubrir}</p>
              </div>
            )}

            {pasantia.conocimientos && (
              <div style={{ marginBottom: "1.5rem" }}>
                <h3 style={{ fontSize: "1.1em", marginBottom: "0.5rem" }}>Conocimientos requeridos</h3>
                <p style={{ whiteSpace: "pre-wrap" }}>{pasantia.conocimientos}</p>
              </div>
            )}

            {pasantia.otrosRequisitos && (
              <div style={{ marginBottom: "1.5rem" }}>
                <h3 style={{ fontSize: "1.1em", marginBottom: "0.5rem" }}>Otros requisitos</h3>
                <p style={{ whiteSpace: "pre-wrap" }}>{pasantia.otrosRequisitos}</p>
              </div>
            )}

            {pasantia.beneficios && (
              <div style={{ marginBottom: "1.5rem" }}>
                <h3 style={{ fontSize: "1.1em", marginBottom: "0.5rem" }}>Beneficios</h3>
                <p style={{ whiteSpace: "pre-wrap" }}>{pasantia.beneficios}</p>
              </div>
            )}
          </article>

          <aside>
            <div className="job-card" style={{ padding: "1.5rem", marginBottom: "1rem" }}>
              <h3 style={{ fontSize: "1.1em", marginBottom: "1rem" }}>Información</h3>
              <dl style={{ display: "grid", gap: "0.75rem" }}>
                <div>
                  <dt style={{ fontWeight: "bold", marginBottom: "0.25rem" }}>Estado</dt>
                  <dd>
                    <span className="badge">{pasantia.estado}</span>
                  </dd>
                </div>
                {pasantia.cantidadDePasantes && (
                  <div>
                    <dt style={{ fontWeight: "bold", marginBottom: "0.25rem" }}>Cantidad de pasantes</dt>
                    <dd>{pasantia.cantidadDePasantes}</dd>
                  </div>
                )}
                {pasantia.asignacionEstimulo && (
                  <div>
                    <dt style={{ fontWeight: "bold", marginBottom: "0.25rem" }}>Asignación estimulo</dt>
                    <dd>${pasantia.asignacionEstimulo.toLocaleString()}</dd>
                  </div>
                )}
                {pasantia.fechaPublicacion && (
                  <div>
                    <dt style={{ fontWeight: "bold", marginBottom: "0.25rem" }}>Fecha de publicación</dt>
                    <dd>{new Date(pasantia.fechaPublicacion).toLocaleDateString()}</dd>
                  </div>
                )}
                {pasantia.fechaCaducidad && (
                  <div>
                    <dt style={{ fontWeight: "bold", marginBottom: "0.25rem" }}>Fecha de caducidad</dt>
                    <dd>{new Date(pasantia.fechaCaducidad).toLocaleDateString()}</dd>
                  </div>
                )}
                {pasantia.diasRestantes !== null && pasantia.diasRestantes !== undefined && (
                  <div>
                    <dt style={{ fontWeight: "bold", marginBottom: "0.25rem" }}>Días restantes</dt>
                    <dd>{pasantia.diasRestantes} días</dd>
                  </div>
                )}
                {pasantia.emailContacto && (
                  <div>
                    <dt style={{ fontWeight: "bold", marginBottom: "0.25rem" }}>Email de contacto</dt>
                    <dd>
                      <a href={`mailto:${pasantia.emailContacto}`}>{pasantia.emailContacto}</a>
                    </dd>
                  </div>
                )}
              </dl>
            </div>

            {pasantia.carreras && pasantia.carreras.length > 0 && (
              <div className="job-card" style={{ padding: "1.5rem" }}>
                <h3 style={{ fontSize: "1.1em", marginBottom: "1rem" }}>Carreras</h3>
                <ul style={{ listStyle: "none", padding: 0, margin: 0 }}>
                  {pasantia.carreras.map((carrera) => (
                    <li key={carrera.idCarrera} style={{ marginBottom: "0.5rem" }}>
                      {carrera.nombre}
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </aside>
        </div>
      </div>
    </section>
  );
}

