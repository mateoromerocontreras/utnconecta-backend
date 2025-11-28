import { useEffect, useRef, useState, useMemo } from "react";
import "../styles/search.css";

export default function SearchPanel({ id, open, onClose, onSearch, variant = "inline", careers = [] }) {
  const sheetRef = useRef(null);
  const [formValues, setFormValues] = useState({
    texto: "",
    carrera: "",
    modalidad: ""
  });

  const [careerOptions, setCareerOptions] = useState([]);
  const hasInjectedCareers = Array.isArray(careers) && careers.length > 0;
  const API = useMemo(() => (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/, ""), []);

  // Foco inicial en el primer input cuando se abre el panel
  useEffect(() => {
    if (!open) return;
    const firstInput = sheetRef.current?.querySelector("input, select, button");
    if (firstInput) {
      setTimeout(() => firstInput.focus(), 120);
    }
  }, [open]);

  // Cargar carreras si no se proporcionan desde arriba
  useEffect(() => {
    if (hasInjectedCareers) {
      setCareerOptions(careers);
      return;
    }
    let cancelled = false;
    async function loadCareers() {
      try {
        const res = await fetch(`${API}/carreras/listarCarreras`, {
          headers: { Accept: "application/json" }
        });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();
        if (!cancelled && Array.isArray(data)) {
          setCareerOptions(data.map((c) => c.nombre).filter(Boolean));
        }
      } catch (err) {
        console.error("No se pudieron cargar las carreras para el buscador:", err);
      }
    }
    loadCareers();
    return () => { cancelled = true; };
  }, [careers, hasInjectedCareers, API]);

  function updateField(field, value) {
    setFormValues((prev) => ({
      ...prev,
      [field]: value
    }));
  }

  function handleSubmit(event) {
    event.preventDefault();

    if (typeof onSearch === "function") {
      onSearch({
        texto: formValues.texto.trim(),
        carrera: formValues.carrera,
        modalidad: formValues.modalidad
      });
    }

    if (variant === "bottom" && typeof onClose === "function") {
      onClose();
    }
  }

  const renderedCareers = careerOptions && careerOptions.length > 0 ? careerOptions : careers;

  return (
    <div
      id={id}
      className={`search-panel ${variant} ${open ? "open" : ""}`}
      aria-hidden={!open}
      role="region"
      aria-label="Busqueda de pasantias"
      ref={sheetRef}
    >
      <div className="search-inner container">
        <form className="search-grid" onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Cargo o puesto"
            aria-label="Cargo o puesto"
            value={formValues.texto}
            onChange={(event) => updateField("texto", event.target.value)}
          />
          <div className="select">
            <select
              aria-label="Carrera"
              value={formValues.carrera}
              onChange={(event) => updateField("carrera", event.target.value)}
            >
              <option value="">Carrera</option>
              {renderedCareers.map((carrera) => (
                <option key={carrera} value={carrera}>
                  {carrera}
                </option>
              ))}
            </select>
            <span className="caret">v</span>
          </div>
          <div className="select">
            <select
              aria-label="Modalidad"
              value={formValues.modalidad}
              onChange={(event) => updateField("modalidad", event.target.value)}
            >
              <option value="">Modalidad</option>
              <option>Presencial</option>
              <option>Hibrida</option>
              <option>Remota</option>
            </select>
            <span className="caret">v</span>
          </div>
          <button className="btn big" type="submit" aria-label="Buscar">
            <img
              src="/icons/search.svg"
              alt=""
              aria-hidden="true"
              className="icon"
            />
          </button>
        </form>
      </div>
    </div>
  );
}
