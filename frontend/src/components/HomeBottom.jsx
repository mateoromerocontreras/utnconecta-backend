import "../styles/home-bottom.css";

export default function HomeBottom() {
  // Mock de últimas pasantías (cambialo por datos reales cuando tengas API)
  const jobs = [
    { titulo: "Desarrollador Frontend Jr", empresa: "TechNova", ciudad: "Córdoba", modalidad: "Híbrida", publicada: "Hoy" },
    { titulo: "Data Analyst Trainee", empresa: "DataFlow", ciudad: "Rosario", modalidad: "Remota", publicada: "Ayer" },
    { titulo: "QA Tester", empresa: "SoftLabs", ciudad: "Mendoza", modalidad: "Presencial", publicada: "Hace 2 días" },
  ];

  return (
    <section className="home-bottom">
      <div className="container">

        {/* 1) Logos de empresas */}
        <div className="brands">
          <h3>Empresas que confían</h3>
          <div className="brand-grid">
            <img src="/logos/company-1.svg" alt="Empresa 1" />
            <img src="/logos/company-2.svg" alt="Empresa 2" />
            <img src="/logos/company-3.svg" alt="Empresa 3" />
            <img src="/logos/company-4.svg" alt="Empresa 4" />
            <img src="/logos/company-5.svg" alt="Empresa 5" />
          </div>
        </div>

        {/* 2) Últimas pasantías */}
        <div className="latest">
          <div className="latest-head">
            <h3>Últimas pasantías</h3>
            <a className="link-more" href="/pasantias">Ver todas →</a>
          </div>

          <div className="job-grid">
            {jobs.map((j, i) => (
              <article key={i} className="job-card">
                <header>
                  <h4>{j.titulo}</h4>
                  <span className="badge">{j.modalidad}</span>
                </header>
                <p className="meta">{j.empresa} · {j.ciudad}</p>
                <footer>
                  <span className="time">{j.publicada}</span>
                  <a className="btn btn-ghost" href="/pasantias">Postular</a>
                </footer>
              </article>
            ))}
          </div>
        </div>

        {/* 3) FAQ + CTA final */}
        <div className="faq-cta">
          <div className="faq">
            <h3>Preguntas frecuentes</h3>

            <details>
              <summary>¿Cómo me postulo a una pasantía?</summary>
              <p>Ingresá a <a href="/pasantias">Pasantías</a>, filtrá por carrera/modo y hacé clic en “Postular”.</p>
            </details>

            <details>
              <summary>¿Necesito experiencia previa?</summary>
              <p>No siempre. Muchas búsquedas son para primeros pasos y prácticas supervisadas.</p>
            </details>

            <details>
              <summary>¿Puedo cargar mi CV?</summary>
              <p>Sí, desde <a href="/registrarse">Registrarse</a> podés crear tu perfil y subir tu CV.</p>
            </details>
          </div>

          <div className="cta-final">
            <h3>¿Listo para dar el siguiente paso?</h3>
            <p>Explorá pasantías acordes a tu carrera y modalidad preferida.</p>
            <a className="btn btn-big" href="/pasantias">Buscar pasantías</a>
          </div>
        </div>

      </div>
    </section>
  );
}
