import { useState } from "react";
import SearchPanel from "../components/SearchPanel.jsx";
import "../styles/pasantias.css";

export default function Internships() {
  const [filters, setFilters] = useState({ texto: "", carrera: "", modalidad: "" });

  // Mock de resultados (reemplazá por datos reales cuando tengas API)
  const jobs = [
    { id: 1, titulo: "Desarrollador Frontend Jr", empresa: "TechNova", ciudad: "Córdoba", modalidad: "Híbrida", carrera: "Sistemas", publicada: "Hoy" },
    { id: 2, titulo: "Data Analyst Trainee", empresa: "DataFlow", ciudad: "Rosario", modalidad: "Remota", carrera: "Industrial", publicada: "Ayer" },
    { id: 3, titulo: "QA Tester", empresa: "SoftLabs", ciudad: "Mendoza", modalidad: "Presencial", carrera: "Sistemas", publicada: "Hace 2 días" },
    { id: 4, titulo: "Backend Intern", empresa: "Cloudify", ciudad: "CABA", modalidad: "Remota", carrera: "Sistemas", publicada: "Hoy" },
  ];

  const filtered = jobs.filter(j => {
    const t = filters.texto.toLowerCase();
    const byTexto = !t || j.titulo.toLowerCase().includes(t) || j.empresa.toLowerCase().includes(t) || j.ciudad.toLowerCase().includes(t);
    const byCarrera = !filters.carrera || j.carrera === filters.carrera;
    const byModalidad = !filters.modalidad || j.modalidad === filters.modalidad;
    return byTexto && byCarrera && byModalidad;
  });

  function handleSearch(payload) {
    setFilters(payload);
    // Si querés usar querystring:
    // const qs = new URLSearchParams(payload).toString();
    // navigate(`/pasantias?${qs}`);
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
            {filtered.length} resultado{filtered.length !== 1 ? "s" : ""}{(filters.texto || filters.carrera || filters.modalidad) ? " (filtrado)" : ""}
          </p>
        </header>

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
              <footer>
                <span className="time">{j.publicada}</span>
                <a className="btn btn-ghost" href="#">Postular</a>
              </footer>
            </article>
          ))}
          {filtered.length === 0 && (
            <div className="no-results">
              No se encontraron pasantías con esos filtros.
            </div>
          )}
        </div>
      </div>
    </section>
  );
}
