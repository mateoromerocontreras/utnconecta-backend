import { useState } from "react";
import "../styles/registrar-empresa.css";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/, "");

function getStoredItem(key) {
  const persisted = localStorage.getItem(key);
  if (persisted !== null) return persisted;
  return sessionStorage.getItem(key);
}

export default function RegistrarEmpresa() {
  const [form, setForm] = useState({
    nombre: "",
    razonSocial: "",
    cuit: "",
    ciudad: "",
    calle: "",
    nroCalle: "",
    piso: "",
    departamento: "",
    barrio: "",
    email: "",
    contactoNombre: "",
    contactoApellido: "",
    contactoEmail: "",
    contactoTelefono: ""
  });
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState(""); // "success" | "error" | "info"
  const [submitting, setSubmitting] = useState(false);

  const onChange = (e) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
  };

  const onSubmit = async (e) => {
    e.preventDefault();

    // Validación de campos obligatorios según el backend
    if (!form.nombre) {
      setMessage("⚠️ El nombre es obligatorio.");
      setMessageType("error");
      return;
    }
    if (!form.razonSocial) {
      setMessage("⚠️ La razón social es obligatoria.");
      setMessageType("error");
      return;
    }
    if (!form.cuit) {
      setMessage("⚠️ El CUIT es obligatorio.");
      setMessageType("error");
      return;
    }
    if (!form.ciudad) {
      setMessage("⚠️ La ciudad es obligatoria.");
      setMessageType("error");
      return;
    }
    if (!form.calle) {
      setMessage("⚠️ La calle es obligatoria.");
      setMessageType("error");
      return;
    }
    if (!form.contactoNombre || !form.contactoApellido || !form.contactoEmail) {
      setMessage("⚠️ Los datos del contacto (nombre, apellido y email) son obligatorios.");
      setMessageType("error");
      return;
    }

    const payload = {
      nombre: form.nombre,
      razonSocial: form.razonSocial,
      cuit: form.cuit,
      ciudad: form.ciudad,
      calle: form.calle,
      nroCalle: form.nroCalle ? parseInt(form.nroCalle) : null,
      piso: form.piso || null,
      departamento: form.departamento || null,
      barrio: form.barrio || null,
      email: form.email || null,
      contacto: [
        {
          nombre: form.contactoNombre,
          apellido: form.contactoApellido,
          emailResponsable: form.contactoEmail,
          telefonoResponsable: form.contactoTelefono || null
        }
      ]
    };

    try {
      setSubmitting(true);
      setMessage("Enviando…"); setMessageType("info");

      // Obtener token de autenticación
      const token = getStoredItem("authToken");
      if (!token) {
        throw new Error("No hay token de autenticación. Por favor, inicia sesión.");
      }

      const res = await fetch(`${API}/empresas/crearEmpresa`, {
        method: "POST",
        headers: { 
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      });

      if (res.ok) {
        setMessage("✅ ¡Empresa registrada con éxito!");
        setMessageType("success");
        setForm({ 
          nombre: "", 
          razonSocial: "", 
          cuit: "", 
          ciudad: "", 
          calle: "", 
          nroCalle: "", 
          piso: "", 
          departamento: "", 
          barrio: "", 
          email: "",
          contactoNombre: "",
          contactoApellido: "",
          contactoEmail: "",
          contactoTelefono: ""
        });
        setTimeout(() => {
          setMessage("");
          setMessageType("");
        }, 2000);
      } else if (res.status === 400) {
        const err = await res.text();
        setMessage(`⚠️ Datos inválidos. ${err || "Revisá el formulario."}`);
        setMessageType("error");
      } else if (res.status === 401 || res.status === 403) {
        setMessage("❌ Credenciales inválidas. Vuelve a intentarlo.");
        setMessageType("error");
      } else {
        const err = await res.text();
        setMessage(`⚠️ Error inesperado. ${err || "Intentá más tarde."}`);
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

  return (
    <section
      className="empresa-hero"
      style={{ backgroundImage: "url('/i.jpg')" }}
      aria-label="Fondo oficinas"
    >
      <div className="empresa-card">
        <h2 className="empresa-title">
          Conecta con los futuros ingenieros que transformarán tu empresa
        </h2>

        <form className="empresa-grid" onSubmit={onSubmit} noValidate>
          <input
            type="text"
            name="nombre"
            placeholder="Nombre de la empresa *"
            value={form.nombre}
            onChange={onChange}
            required
          />
          <input
            type="text"
            name="razonSocial"
            placeholder="Razón social *"
            value={form.razonSocial}
            onChange={onChange}
            required
          />
          <input
            type="text"
            name="cuit"
            placeholder="CUIT *"
            value={form.cuit}
            onChange={onChange}
            required
          />
          <input
            type="text"
            name="ciudad"
            placeholder="Ciudad *"
            value={form.ciudad}
            onChange={onChange}
            required
          />
          <input
            type="text"
            name="calle"
            placeholder="Calle *"
            value={form.calle}
            onChange={onChange}
            required
          />
          <input
            type="text"
            name="nroCalle"
            placeholder="Número de calle"
            value={form.nroCalle}
            onChange={onChange}
          />
          <input
            type="text"
            name="piso"
            placeholder="Piso"
            value={form.piso}
            onChange={onChange}
          />
          <input
            type="text"
            name="departamento"
            placeholder="Departamento"
            value={form.departamento}
            onChange={onChange}
          />
          <input
            type="text"
            name="barrio"
            placeholder="Barrio"
            value={form.barrio}
            onChange={onChange}
          />
          <input
            type="email"
            name="email"
            placeholder="Email de la empresa"
            value={form.email}
            onChange={onChange}
          />

          <h3 className="contacto-section-title">Datos del contacto</h3>

          <input
            type="text"
            name="contactoNombre"
            placeholder="Nombre del contacto *"
            value={form.contactoNombre}
            onChange={onChange}
            required
          />
          <input
            type="text"
            name="contactoApellido"
            placeholder="Apellido del contacto *"
            value={form.contactoApellido}
            onChange={onChange}
            required
          />
          <input
            type="email"
            name="contactoEmail"
            placeholder="Email del contacto *"
            value={form.contactoEmail}
            onChange={onChange}
            required
          />
          <input
            type="tel"
            name="contactoTelefono"
            placeholder="Teléfono del contacto"
            value={form.contactoTelefono}
            onChange={onChange}
          />

          <div className="empresa-actions">
            <button type="submit" className="btn registrar" disabled={submitting}>
              {submitting ? "Registrando…" : "Registrar empresa"}
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

        <p className="empresa-legal">
          Al hacer clic en Registrar empresa, aceptás las condiciones legales y
          políticas de privacidad de UTN CONECTA para crear una cuenta, publicar
          vacantes y contactar candidatos.
        </p>
      </div>
    </section>
  );
}
