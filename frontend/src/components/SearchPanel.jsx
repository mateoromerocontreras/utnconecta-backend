import { useEffect, useRef } from "react";
import "../styles/search.css";

export default function SearchPanel({ id, open, onClose, variant = "inline" }){
  const sheetRef = useRef(null);

  // Foco inicial en el primer input cuando se abre
  useEffect(()=>{
    if (!open) return;
    const inp = sheetRef.current?.querySelector("input, select, button");
    if (inp) setTimeout(()=>inp.focus(), 120);
  }, [open]);

  return (
    <div
      id={id}
      className={`search-panel ${variant} ${open ? "open" : ""}`}
      aria-hidden={!open}
      role="region"
      aria-label="Búsqueda de pasantías"
      ref={sheetRef}
    >
      <div className="search-inner container">
        <div className="search-grid">
          <input
            type="text"
            placeholder="Cargo o puesto"
            aria-label="Cargo o puesto"
          />
          <div className="select">
            <select aria-label="Carrera" defaultValue="">
              <option value="" disabled> Carrera </option>
              <option>Ingeniería en Sistemas</option>
              <option>Ingeniería Industrial</option>
              <option>Ingeniería Química</option>
              <option>Ingeniería Civil</option>
              <option>Ingeniería en Energía Eléctrica</option>
              <option>Ingeniería Electrónica</option>
              <option>Ingeniería Mecánica</option>
              <option>Ingeniería Metalúrgica</option>
            </select>
            <span className="caret">▼</span>
          </div>
          <div className="select">
            <select aria-label="Modalidad" defaultValue="">
              <option value="" disabled> Modalidad </option>
              <option>Presencial</option>
              <option>Híbrida</option>
              <option>Remota</option>
            </select>
            <span className="caret">▼</span>
          </div>
          <button className="btn big" type="button" aria-label="Buscar">
            <img
                src="/icons/search.svg"
                alt=""
                aria-hidden="true"
                className="icon"
              />
            </button>
        </div>
      </div>
    </div>
  );
}
