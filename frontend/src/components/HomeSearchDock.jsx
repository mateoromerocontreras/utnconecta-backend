import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import SearchPanel from "./SearchPanel.jsx";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/, "");

export default function HomeSearchDock() {
  const [open, setOpen] = useState(false);
  const [careers, setCareers] = useState([]);
  const navigate = useNavigate();

  const apiUrl = useMemo(() => API, []);

  useEffect(() => {
    const onKey = (event) => {
      if (event.key === "Escape") setOpen(false);
    };

    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, []);

  useEffect(() => {
    async function loadCareers() {
      try {
        const res = await fetch(`${apiUrl}/carreras/listarCarreras`, {
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
  }, [apiUrl]);

  return (
    <section className="home-search-dock" aria-label="Buscador de pasantías">
      <div className={`bottom-dock ${open ? "open" : ""}`}>
        <button
          className="btn bottom-cta"
          aria-expanded={open}
          aria-controls="bottom-search"
          onClick={() => setOpen((value) => !value)}
        >
          Buscar pasantías
        </button>

        <SearchPanel
          id="bottom-search"
          open={open}
          variant="bottom"
          onClose={() => setOpen(false)}
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