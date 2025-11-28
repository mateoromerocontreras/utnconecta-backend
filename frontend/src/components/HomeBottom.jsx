import { useEffect, useState } from "react";
import "../styles/home-bottom.css";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/, "");

function formatRelativeDate(dateString) {
  if (!dateString) return "Fecha no disponible";
  const date = new Date(dateString);
  const now = new Date();
  const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24));
  if (diffDays === 0) return "Hoy";
  if (diffDays === 1) return "Ayer";
  if (diffDays < 7) return `Hace ${diffDays} dias`;
  if (diffDays < 30) return `Hace ${Math.floor(diffDays / 7)} semana${Math.floor(diffDays / 7) !== 1 ? "s" : ""}`;
  if (diffDays < 365) return `Hace ${Math.floor(diffDays / 30)} mes${Math.floor(diffDays / 30) !== 1 ? "es" : ""}`;
  return `Hace ${Math.floor(diffDays / 365)} anio${Math.floor(diffDays / 365) !== 1 ? "s" : ""}`;
}

export default function HomeBottom() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function load() {
      try {
        setLoading(true);
        setError("");
        const res = await fetch(`${API}/pasantias`, {
          headers: { Accept: "application/json;charset=UTF-8" }
        });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const text = await res.text();
        const data = JSON.parse(text);

        const mapped = Array.isArray(data)
          ? data
              .map((p, idx) => ({
                id: p.idPasantia ?? idx,
                titulo: p.titulo || "Pasantia sin titulo",
                empresa: p.nombreEmpresa || "Empresa no especificada",
                ciudad: p.ciudad || "Ciudad no informada",
                modalidad: p.modalidad || "Modalidad no informada",
                publicada: formatRelativeDate(p.fechaPublicacion),
                publicadaSort: p.fechaPublicacion ? new Date(p.fechaPublicacion).getTime() : 0
              }))
              .sort((a, b) => b.publicadaSort - a.publicadaSort)
              .slice(0, 3)
          : [];
        setJobs(mapped);
      } catch (err) {
        console.error("No se pudieron cargar las pasantias:", err);
        setError(err.message || "No se pudieron cargar las pasantias.");
        setJobs([]);
      } finally {
        setLoading(false);
      }
    }

    load();
  }, []);

  return (
    <section className="home-bottom">
      <div className="container">

        <div className="brands">
          <h3>Empresas que confian</h3>
          <div className="brand-grid">
            <img src="/logos/company-1.svg" alt="Empresa 1" />
            <img src="/logos/company-2.svg" alt="Empresa 2" />
            <img src="/logos/company-3.svg" alt="Empresa 3" />
            <img src="/logos/company-4.svg" alt="Empresa 4" />
            <img src="/logos/company-5.svg" alt="Empresa 5" />
          </div>
        </div>

        <div className="latest">
          <div className="latest-head">
            <h3>Ultimas pasantias</h3>
            <a className="link-more" href="/pasantias">Ver todas &rarr;</a>
          </div>

          {error && (
            <div className="emp-alert error">
              No se pudieron cargar las pasantias. {error}
            </div>
          )}

          {!error && (
            <div className="job-grid">
              {loading && Array.from({ length: 3 }).map((_, i) => (
                <article key={`skeleton-${i}`} className="job-card skeleton" aria-hidden="true" />
              ))}

              {!loading && jobs.length === 0 && (
                <div className="emp-alert info" style={{ gridColumn: "1 / -1" }}>
                  No hay pasantias disponibles por ahora.
                </div>
              )}

              {!loading && jobs.map((j) => (
                <article key={j.id} className="job-card">
                  <header>
                    <h4>{j.titulo}</h4>
                    <span className="badge">{j.modalidad}</span>
                  </header>
                  <p className="meta">{j.empresa} - {j.ciudad}</p>
                  <footer>
                    <span className="time">{j.publicada}</span>
                    <a className="btn-ver" href={`/pasantias/${j.id}`}>Ver</a>
                  </footer>
                </article>
              ))}
            </div>
          )}
        </div>

        <div className="faq-cta">
          <div className="faq">
            <h3>Preguntas frecuentes</h3>

            <details>
              <summary>Como me postulo a una pasantia?</summary>
              <p>Ingresa a <a href="/pasantias">Pasantias</a>, filtra por carrera/modo y hace clic en "Postular".</p>
            </details>

            <details>
              <summary>Necesito experiencia previa?</summary>
              <p>No siempre. Muchas busquedas son para primeros pasos y practicas supervisadas.</p>
            </details>

            <details>
              <summary>Puedo cargar mi CV?</summary>
              <p>Si, desde <a href="/registrarse">Registrarse</a> podes crear tu perfil y subir tu CV.</p>
            </details>
          </div>

          <div className="cta-final">
            <h3>Listo para dar el siguiente paso?</h3>
            <p>Explora pasantias acordes a tu carrera y modalidad preferida.</p>
            <a className="btn btn-big" href="/pasantias">Buscar pasantias</a>
          </div>
        </div>

      </div>
    </section>
  );
}
