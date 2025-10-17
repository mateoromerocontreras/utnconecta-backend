import { useState, useEffect } from "react";
import SearchPanel from "./SearchPanel.jsx";
import "../styles/hero.css";

export default function HomeHero(){
  const [open, setOpen] = useState(false);

  // cerrar bottom sheet con ESC
  useEffect(()=>{
    const onKey = (e)=>{ if(e.key === "Escape") setOpen(false); };
    window.addEventListener("keydown", onKey);
    return ()=> window.removeEventListener("keydown", onKey);
  },[]);

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
        />
      </div>
    </section>
  );
}