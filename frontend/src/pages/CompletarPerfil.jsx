import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/completar-perfil.css";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/, "");

function getStoredItem(key) {
  const persisted = localStorage.getItem(key);
  if (persisted !== null) return persisted;
  return sessionStorage.getItem(key);
}

export default function CompletarPerfil() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    especialidad: "",
    nroLegajo: "",
    calle: "",
    nroCalle: "",
    barrio: "",
    localidad: "",
    provincia: "",
    telFijo: ""
  });
  
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [submitError, setSubmitError] = useState("");
  const [success, setSuccess] = useState(false);

  // Verificar autenticación
  useEffect(() => {
    const token = getStoredItem("authToken");
    const userInfoStr = getStoredItem("userInfo");
    
    if (!token || !userInfoStr) {
      navigate("/login", { replace: true });
      return;
    }
    
    const userInfo = JSON.parse(userInfoStr);
    if (userInfo.rol !== "ESTUDIANTE") {
      navigate("/", { replace: true });
    }
  }, [navigate]);

  function validate(values) {
    const e = {};
    
    if (!values.especialidad) {
      e.especialidad = "La especialidad/carrera es obligatoria";
    }
    
    if (!values.nroLegajo) {
      e.nroLegajo = "El número de legajo es obligatorio";
    }
    
    if (!values.calle) {
      e.calle = "La calle es obligatoria";
    }
    
    if (!values.nroCalle) {
      e.nroCalle = "El número de calle es obligatorio";
    } else if (!/^\d+$/.test(values.nroCalle)) {
      e.nroCalle = "Debe ser un número";
    }
    
    if (!values.barrio) {
      e.barrio = "El barrio es obligatorio";
    }
    
    if (!values.localidad) {
      e.localidad = "La localidad es obligatoria";
    }
    
    if (!values.provincia) {
      e.provincia = "La provincia es obligatoria";
    }
    
    return e;
  }

  function handleChange(ev) {
    const { name, value } = ev.target;
    setForm((f) => ({ ...f, [name]: value }));
    
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: "" }));
    }
  }

  async function handleSubmit(ev) {
    ev.preventDefault();
    const e = validate(form);
    setErrors(e);
    setSubmitError("");
    
    if (Object.keys(e).length) return;

    try {
      setLoading(true);
      
      const token = getStoredItem("authToken");
      const userInfoStr = getStoredItem("userInfo");
      const userInfo = JSON.parse(userInfoStr);

      const response = await fetch(`${API}/estudiantes/completarPerfil?email=${userInfo.email}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          especialidad: form.especialidad,
          nroLegajo: form.nroLegajo,
          calle: form.calle,
          nroCalle: parseInt(form.nroCalle),
          barrio: form.barrio,
          localidad: form.localidad,
          provincia: form.provincia,
          telFijo: form.telFijo || null
        })
      });

      const data = await response.json();

      if (!response.ok || data.code !== 0) {
        setSubmitError(data.message || "Error al completar el perfil");
        return;
      }

      setSuccess(true);
      
      // Redirigir a la página de perfil después de 2 segundos
      setTimeout(() => {
        navigate("/perfil", { replace: true });
      }, 2000);
      
    } catch (error) {
      console.error("Error al completar perfil:", error);
      setSubmitError("Error de conexión. Intentá nuevamente.");
    } finally {
      setLoading(false);
    }
  }

  function handleSkip() {
    // Permitir omitir por ahora, pero mostrar advertencia
    if (confirm("¿Estás seguro? Necesitarás completar tu perfil más adelante para acceder a todas las funcionalidades.")) {
      navigate("/perfil", { replace: true });
    }
  }

  if (success) {
    return (
      <section className="completar-perfil-hero">
        <div className="container">
          <div className="success-card">
            <div className="success-icon">✓</div>
            <h2>¡Perfil completado exitosamente!</h2>
            <p>Serás redirigido a tu perfil...</p>
          </div>
        </div>
      </section>
    );
  }

  return (
    <section className="completar-perfil-hero">
      <div className="container completar-perfil-grid">
        <div className="completar-perfil-card">
          <div className="perfil-header">
            <h1 className="perfil-title">Completá tu perfil</h1>
            <p className="perfil-subtitle">Necesitamos algunos datos adicionales para continuar</p>
          </div>

          <form onSubmit={handleSubmit} className="perfil-form" noValidate>
            {submitError && <div className="perfil-error">{submitError}</div>}

            <div className="form-section">
              <h3 className="section-title">Información Académica</h3>
              
              <div className="form-row">
                <div className={`field ${errors.especialidad ? "has-error" : ""}`}>
                  <label htmlFor="especialidad">Especialidad/Carrera *</label>
                  <input
                    id="especialidad"
                    type="text"
                    name="especialidad"
                    placeholder="Ingeniería en Sistemas"
                    value={form.especialidad}
                    onChange={handleChange}
                    disabled={loading}
                  />
                  {errors.especialidad && <div className="error">{errors.especialidad}</div>}
                </div>

                <div className={`field ${errors.nroLegajo ? "has-error" : ""}`}>
                  <label htmlFor="nroLegajo">Número de Legajo *</label>
                  <input
                    id="nroLegajo"
                    type="text"
                    name="nroLegajo"
                    placeholder="12345"
                    value={form.nroLegajo}
                    onChange={handleChange}
                    disabled={loading}
                  />
                  {errors.nroLegajo && <div className="error">{errors.nroLegajo}</div>}
                </div>
              </div>
            </div>

            <div className="form-section">
              <h3 className="section-title">Dirección</h3>
              
              <div className="form-row">
                <div className={`field field-large ${errors.calle ? "has-error" : ""}`}>
                  <label htmlFor="calle">Calle *</label>
                  <input
                    id="calle"
                    type="text"
                    name="calle"
                    placeholder="Av. Vélez Sarsfield"
                    value={form.calle}
                    onChange={handleChange}
                    disabled={loading}
                  />
                  {errors.calle && <div className="error">{errors.calle}</div>}
                </div>

                <div className={`field field-small ${errors.nroCalle ? "has-error" : ""}`}>
                  <label htmlFor="nroCalle">Número *</label>
                  <input
                    id="nroCalle"
                    type="text"
                    name="nroCalle"
                    placeholder="1234"
                    value={form.nroCalle}
                    onChange={handleChange}
                    disabled={loading}
                  />
                  {errors.nroCalle && <div className="error">{errors.nroCalle}</div>}
                </div>
              </div>

              <div className={`field ${errors.barrio ? "has-error" : ""}`}>
                <label htmlFor="barrio">Barrio *</label>
                <input
                  id="barrio"
                  type="text"
                  name="barrio"
                  placeholder="Centro"
                  value={form.barrio}
                  onChange={handleChange}
                  disabled={loading}
                />
                {errors.barrio && <div className="error">{errors.barrio}</div>}
              </div>

              <div className="form-row">
                <div className={`field ${errors.localidad ? "has-error" : ""}`}>
                  <label htmlFor="localidad">Localidad *</label>
                  <input
                    id="localidad"
                    type="text"
                    name="localidad"
                    placeholder="Córdoba"
                    value={form.localidad}
                    onChange={handleChange}
                    disabled={loading}
                  />
                  {errors.localidad && <div className="error">{errors.localidad}</div>}
                </div>

                <div className={`field ${errors.provincia ? "has-error" : ""}`}>
                  <label htmlFor="provincia">Provincia *</label>
                  <input
                    id="provincia"
                    type="text"
                    name="provincia"
                    placeholder="Córdoba"
                    value={form.provincia}
                    onChange={handleChange}
                    disabled={loading}
                  />
                  {errors.provincia && <div className="error">{errors.provincia}</div>}
                </div>
              </div>
            </div>

            <div className="form-section">
              <h3 className="section-title">Contacto Adicional (opcional)</h3>
              
              <div className={`field`}>
                <label htmlFor="telFijo">Teléfono Fijo</label>
                <input
                  id="telFijo"
                  type="tel"
                  name="telFijo"
                  placeholder="3514567890"
                  value={form.telFijo}
                  onChange={handleChange}
                  disabled={loading}
                />
              </div>
            </div>

            <div className="form-actions">
              <button 
                type="button" 
                className="btn-secondary" 
                onClick={handleSkip}
                disabled={loading}
              >
                Completar más tarde
              </button>
              <button 
                type="submit" 
                className="btn-primary" 
                disabled={loading}
              >
                {loading ? "Guardando..." : "Completar perfil"}
              </button>
            </div>
          </form>
        </div>

        <div className="perfil-art">
          <div className="info-box">
            <h3>¿Por qué necesitamos estos datos?</h3>
            <ul>
              <li>✓ Para verificar tu identidad como estudiante</li>
              <li>✓ Para que las empresas puedan contactarte</li>
              <li>✓ Para personalizar tu experiencia</li>
              <li>✓ Para enviarte oportunidades relevantes</li>
            </ul>
          </div>
        </div>
      </div>
    </section>
  );
}
