import { useState, useEffect } from "react";
import "../styles/registrar-pasantia.css";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/, "");

function getStoredItem(key) {
  const persisted = localStorage.getItem(key);
  if (persisted !== null) return persisted;
  return sessionStorage.getItem(key);
}

export default function RegistrarPasantia() {
  const [form, setForm] = useState({
    titulo: "",
    puestoACubrir: "",
    ciudad: "",
    modalidad: "Presencial",
    asignacionEstimulo: "",
    cantidadDePasantes: "1",
    fechaPublicacion: "",
    fechaCaducidad: "",
    emailContacto: "",
    conocimientos: "",
    otrosRequisitos: "",
    beneficios: "",
    idsCarreras: []
  });

  const [empresaId, setEmpresaId] = useState(null);
  const [carreras, setCarreras] = useState([]);
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState(""); // "success" | "error" | "info"
  const [submitting, setSubmitting] = useState(false);
  const [loading, setLoading] = useState(true);

  // Obtener empresa del usuario autenticado
  useEffect(() => {
    const fetchEmpresaDelUsuario = async () => {
      try {
        const token = getStoredItem("authToken");
        const userInfoStr = getStoredItem("userInfo");
        
        if (!token || !userInfoStr) {
          setMessage("⚠️ Debes iniciar sesión como empresa para acceder a esta página.");
          setMessageType("error");
          setLoading(false);
          return;
        }

        // Parsear información del usuario
        let userData;
        try {
          userData = JSON.parse(userInfoStr);
        } catch (e) {
          console.error("Error parsing userInfo:", e);
          setMessage("⚠️ Error al leer datos de sesión. Por favor, vuelve a iniciar sesión.");
          setMessageType("error");
          setLoading(false);
          return;
        }
        
        // Verificar que sea rol EMPRESA (nombre del rol = "EMPRESA")
        if (userData.rol !== "EMPRESA") {
          setMessage(`❌ Solo usuarios con rol EMPRESA pueden crear pasantías. Tu rol actual: ${userData.rol || "desconocido"}`);
          setMessageType("error");
          setLoading(false);
          return;
        }

        // Obtener empresa asociada al usuario
        const empresasRes = await fetch(`${API}/empresas/consultarEmpresas`, {
          headers: { "Authorization": `Bearer ${token}` }
        });

        if (!empresasRes.ok) {
          throw new Error("No se pudo obtener la empresa");
        }

        const empresas = await empresasRes.json();
        
        // Buscar empresa del usuario por email
        const miEmpresa = empresas.find(e => 
          e.email && userData.email && 
          e.email.toLowerCase() === userData.email.toLowerCase()
        );

        if (!miEmpresa) {
          setMessage("⚠️ No se encontró una empresa asociada a tu usuario. Contacta al administrador.");
          setMessageType("error");
          setLoading(false);
          return;
        }

        setEmpresaId(miEmpresa.idEmpresa);
        setForm(f => ({ ...f, emailContacto: miEmpresa.email || "" }));
        
      } catch (err) {
        console.error(err);
        setMessage("🚨 Error al cargar información de la empresa.");
        setMessageType("error");
      } finally {
        setLoading(false);
      }
    };

    fetchEmpresaDelUsuario();
  }, []);

  // Obtener lista de carreras
  useEffect(() => {
    const fetchCarreras = async () => {
      try {
        const res = await fetch(`${API}/carreras/listarCarreras`);
        if (res.ok) {
          const data = await res.json();
          console.log("Carreras cargadas:", data);
          setCarreras(data || []);
        } else {
          console.error("Error al cargar carreras, status:", res.status);
        }
      } catch (err) {
        console.error("Error al cargar carreras:", err);
      }
    };

    fetchCarreras();
  }, []);

  const handleCarreraChange = (carreraId, checked) => {
    setForm((prevForm) => {
      const currentIds = Array.isArray(prevForm.idsCarreras) ? prevForm.idsCarreras : [];
      let newIds;
      
      if (checked) {
        // Agregar carrera si no está ya en la lista
        newIds = currentIds.includes(carreraId) 
          ? currentIds 
          : [...currentIds, carreraId];
      } else {
        // Remover carrera de la lista
        newIds = currentIds.filter(id => id !== carreraId);
      }
      
      return { ...prevForm, idsCarreras: newIds };
    });
  };

  const onChange = (e) => {
    const { name, value, type, checked } = e.target;
    
    if (type === "checkbox") {
      const carreraId = Number(value);
      if (isNaN(carreraId)) {
        console.error("Invalid carrera ID in onChange:", value);
        return;
      }
      handleCarreraChange(carreraId, checked);
    } else {
      setForm((f) => ({ ...f, [name]: value }));
    }
  };

  const onSubmit = async (e) => {
    e.preventDefault();

    // Validación de campos obligatorios
    if (!form.titulo) {
      setMessage("⚠️ El título es obligatorio.");
      setMessageType("error");
      return;
    }
    if (!form.puestoACubrir) {
      setMessage("⚠️ El puesto a cubrir es obligatorio.");
      setMessageType("error");
      return;
    }
    if (!form.ciudad) {
      setMessage("⚠️ La ciudad es obligatoria.");
      setMessageType("error");
      return;
    }
    if (!form.fechaPublicacion) {
      setMessage("⚠️ La fecha de publicación es obligatoria.");
      setMessageType("error");
      return;
    }
    if (!form.fechaCaducidad) {
      setMessage("⚠️ La fecha de caducidad es obligatoria.");
      setMessageType("error");
      return;
    }
    if (form.idsCarreras.length === 0) {
      setMessage("⚠️ Debes seleccionar al menos una carrera.");
      setMessageType("error");
      return;
    }
    if (!empresaId) {
      setMessage("⚠️ No se pudo identificar tu empresa. Por favor, recarga la página.");
      setMessageType("error");
      return;
    }

    const payload = {
      titulo: form.titulo,
      puestoACubrir: form.puestoACubrir,
      ciudad: form.ciudad,
      modalidad: form.modalidad,
      asignacionEstimulo: form.asignacionEstimulo ? parseFloat(form.asignacionEstimulo) : null,
      cantidadDePasantes: parseInt(form.cantidadDePasantes),
      fechaPublicacion: form.fechaPublicacion,
      fechaCaducidad: form.fechaCaducidad,
      idEmpresa: empresaId,
      idsCarreras: form.idsCarreras,
      emailContacto: form.emailContacto || null,
      conocimientos: form.conocimientos || null,
      otrosRequisitos: form.otrosRequisitos || null,
      beneficios: form.beneficios || null
    };

    try {
      setSubmitting(true);
      setMessage("Enviando…");
      setMessageType("info");

      const token = getStoredItem("authToken");
      if (!token) {
        throw new Error("No hay token de autenticación. Por favor, inicia sesión.");
      }

      const res = await fetch(`${API}/pasantias/registrar`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });

      const data = await res.json();

      if (res.ok && res.status === 201) {
        setMessage("✅ ¡Pasantía publicada con éxito! Ya es visible para los estudiantes.");
        setMessageType("success");
        
        // Limpiar formulario
        setForm({
          titulo: "",
          puestoACubrir: "",
          ciudad: "",
          modalidad: "Presencial",
          asignacionEstimulo: "",
          cantidadDePasantes: "1",
          fechaPublicacion: "",
          fechaCaducidad: "",
          emailContacto: form.emailContacto, // Mantener email
          conocimientos: "",
          otrosRequisitos: "",
          beneficios: "",
          idsCarreras: []
        });

        setTimeout(() => {
          setMessage("");
          setMessageType("");
        }, 3000);
        
      } else if (res.status === 403) {
        setMessage(`❌ ${data.mensaje || "No tienes permiso para crear esta pasantía."}`);
        setMessageType("error");
      } else if (res.status === 400) {
        setMessage(`⚠️ ${data.mensaje || "Datos inválidos. Revisa el formulario."}`);
        setMessageType("error");
      } else if (res.status === 401) {
        setMessage("❌ Sesión expirada. Vuelve a iniciar sesión.");
        setMessageType("error");
      } else {
        setMessage(`⚠️ ${data.mensaje || "Error inesperado. Intenta más tarde."}`);
        setMessageType("error");
      }
    } catch (err) {
      console.error(err);
      setMessage("🚨 No se pudo conectar con el servidor.");
      setMessageType("error");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <section className="pasantia-hero" style={{ backgroundImage: "url('/i.jpg')" }}>
        <div className="pasantia-card">
          <p className="loading-message">Cargando información...</p>
        </div>
      </section>
    );
  }

  if (!empresaId) {
    return (
      <section className="pasantia-hero" style={{ backgroundImage: "url('/i.jpg')" }}>
        <div className="pasantia-card">
          {message && (
            <div className={`alert alert-${messageType}`} role="alert">
              {message}
            </div>
          )}
          <div className="pasantia-actions">
            <button
              type="button"
              className="btn cancelar"
              onClick={() => (window.location.href = "/")}
            >
              Volver al inicio
            </button>
          </div>
        </div>
      </section>
    );
  }

  return (
    <section
      className="pasantia-hero"
      style={{ backgroundImage: "url('/i.jpg')" }}
      aria-label="Fondo oficinas"
    >
      <div className="pasantia-card">
        <h2 className="pasantia-title">
          Publica una nueva pasantía para estudiantes de ingeniería
        </h2>

        <form className="pasantia-grid" onSubmit={onSubmit} noValidate>
          {/* Información básica */}
          <h3 className="section-title">Información de la pasantía</h3>
          
          <input
            type="text"
            name="titulo"
            placeholder="Título de la pasantía *"
            value={form.titulo}
            onChange={onChange}
            required
            className="full-width"
          />
          
          <input
            type="text"
            name="puestoACubrir"
            placeholder="Puesto a cubrir *"
            value={form.puestoACubrir}
            onChange={onChange}
            required
            className="full-width"
          />

          <input
            type="text"
            name="ciudad"
            placeholder="Ciudad *"
            value={form.ciudad}
            onChange={onChange}
            required
          />

          <select
            name="modalidad"
            value={form.modalidad}
            onChange={onChange}
            required
          >
            <option value="Presencial">Presencial</option>
            <option value="Remoto">Remoto</option>
            <option value="Híbrida">Híbrida</option>
          </select>

          <input
            type="number"
            name="asignacionEstimulo"
            placeholder="Asignación estímulo (opcional)"
            value={form.asignacionEstimulo}
            onChange={onChange}
            step="0.01"
            min="0"
          />

          <input
            type="number"
            name="cantidadDePasantes"
            placeholder="Cantidad de pasantes *"
            value={form.cantidadDePasantes}
            onChange={onChange}
            required
            min="1"
          />

          {/* Fechas */}
          <h3 className="section-title">Fechas</h3>

          <div className="date-field">
            <label htmlFor="fechaPublicacion">Fecha de publicación *</label>
            <input
              type="date"
              id="fechaPublicacion"
              name="fechaPublicacion"
              value={form.fechaPublicacion}
              onChange={onChange}
              required
            />
          </div>

          <div className="date-field">
            <label htmlFor="fechaCaducidad">Fecha de caducidad *</label>
            <input
              type="date"
              id="fechaCaducidad"
              name="fechaCaducidad"
              value={form.fechaCaducidad}
              onChange={onChange}
              required
            />
          </div>

          {/* Contacto */}
          <h3 className="section-title">Información de contacto</h3>
          
          <input
            type="email"
            name="emailContacto"
            placeholder="Email de contacto (opcional)"
            value={form.emailContacto}
            onChange={onChange}
            className="full-width"
          />

          {/* Requisitos y beneficios */}
          <h3 className="section-title">Requisitos y beneficios</h3>
          
          <textarea
            name="conocimientos"
            placeholder="Conocimientos requeridos (opcional)"
            value={form.conocimientos}
            onChange={onChange}
            rows="4"
            className="full-width"
          />
          
          <textarea
            name="otrosRequisitos"
            placeholder="Otros requisitos (opcional)"
            value={form.otrosRequisitos}
            onChange={onChange}
            rows="4"
            className="full-width"
          />
          
          <textarea
            name="beneficios"
            placeholder="Beneficios ofrecidos (opcional)"
            value={form.beneficios}
            onChange={onChange}
            rows="4"
            className="full-width"
          />

          {/* Carreras */}
          <h3 className="section-title">Carreras relacionadas *</h3>
          
          <div className="carreras-grid">
            {carreras.length > 0 ? (
              carreras.map((carrera) => {
                const carreraId = Number(carrera.id);
                if (isNaN(carreraId)) {
                  console.error("Invalid carrera ID:", carrera.id, carrera);
                  return null;
                }
                const isChecked = Array.isArray(form.idsCarreras) && form.idsCarreras.includes(carreraId);
                return (
                  <label key={carrera.id || carreraId} className="checkbox-label">
                    <input
                      type="checkbox"
                      name={`carrera-${carreraId}`}
                      value={String(carreraId)}
                      checked={isChecked}
                      onChange={onChange}
                    />
                    <span>{carrera.nombre || 'Sin nombre'}</span>
                  </label>
                );
              })
            ) : (
              <p className="no-carreras">
                {carreras.length === 0 ? "No hay carreras disponibles" : "Cargando carreras..."}
              </p>
            )}
          </div>

          <div className="pasantia-actions">
            <button type="submit" className="btn registrar" disabled={submitting}>
              {submitting ? "Registrando…" : "Publicar pasantía"}
            </button>
            <button
              type="button"
              className="btn cancelar"
              onClick={() => (window.location.href = "/")}
              disabled={submitting}
            >
              Cancelar
            </button>
          </div>
        </form>

        {message && (
          <div
            className={`alert ${
              messageType === "success"
                ? "alert-success"
                : messageType === "error"
                ? "alert-error"
                : "alert-info"
            }`}
            role={messageType === "error" ? "alert" : "status"}
          >
            {message}
          </div>
        )}

        <p className="pasantia-info">
          <strong>Nota:</strong> La pasantía será publicada inmediatamente
          y será visible para todos los estudiantes.
        </p>
      </div>
    </section>
  );
}
