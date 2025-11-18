import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import SearchPanel from "./SearchPanel.jsx";
import "../styles/hero.css";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/, "");

export default function HomeHero(){
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  const [careers, setCareers] = useState([]);

  // cerrar bottom sheet con ESC
  useEffect(()=>{
    const onKey = (e)=>{ if(e.key === "Escape") setOpen(false); };
    window.addEventListener("keydown", onKey);
    return ()=> window.removeEventListener("keydown", onKey);
  },[]);

  useEffect(() => {
    async function loadCareers() {
      try {
        const res = await fetch(`${API}/carreras/listarCarreras`, {
          headers: { Accept: "application/json" }
        });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();
        if (Array.isArray(data)) {
          setCareers(data.map((c) => c.nombre).filter(Boolean));
        }
      } catch (err) {
        console.error("No se pudo cargar carreras para la búsqueda:", err);
      }
    }
    loadCareers();
  }, []);

  return (
    <section className="hero">
      <div className="hero-grid">
        {/* Columna izquierda */}
        <div className="hero-left">
          <div className="container">
            <h1 className="hero-title">Encuentra tu <br/> pasantía</h1>
            <p className="hero-sub">Comienza tu trayecto profesional</p>

            <div className="hero-promo">
               <img
                  src="/icons/hat.svg"
                  className="promo-icon"
                  alt=""
                  aria-hidden="true"
                />
              <span>Impulsá tu carrera profesional</span>
            </div>
          </div>
        </div>

        {/* Columna derecha: imagen */}
        <div className="hero-right" role="img" aria-label="Personas trabajando en una mesa" />
        
      </div>

      {/* CTA y Bottom Sheet pegados al borde inferior */}
      <div className={`bottom-dock ${open ? "open" : ""}`}>
        {/* BOTÓN GRANDE ABAJO */}
        <button
          className="btn bottom-cta"
          aria-expanded={open}
          aria-controls="bottom-search"
          onClick={()=>setOpen(!open)}
        >
          Buscar pasantías
        </button>

        {/* PANEL DE BÚSQUEDA (BOTTOM SHEET) */}
        <SearchPanel
          id="bottom-search"
          open={open}
          variant="bottom"
          onClose={()=>setOpen(false)}
          careers={careers}
          onSearch={(values) => {
            const params = new URLSearchParams();
            if (values.texto) params.set("texto", values.texto);
            if (values.carrera) params.set("carrera", values.carrera);
            if (values.modalidad) params.set("modalidad", values.modalidad);
            const query = params.toString();
            navigate(query ? `/pasantias?${query}` : "/pasantias");
          }}
        />
      </div>
    </section>
  );
}
