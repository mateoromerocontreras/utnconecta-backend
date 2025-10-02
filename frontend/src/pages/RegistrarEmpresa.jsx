import React, { useState } from "react";
import "../styles/empresa.css";

export default function RegistrarEmpresa() {
  const [form, setForm] = useState({
    nombre: "",
    razonSocial: "",
    cuit: "",
    ciudad: "",
    direccion: "",
    email: ""
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

    // Validadito rápido en front
    if (!form.nombre || !form.ciudad || !form.direccion || !form.email) {
      setMessage("⚠️ Completá los campos obligatorios.");
      setMessageType("error");
      return;
    }

    const payload = {
      nombre: form.nombre,
      ciudad: form.ciudad,
      direccion: form.direccion,
      emailContacto: form.email,
      cuit: form.cuit || null,
      razonSocial: form.razonSocial || null
    };

    try {
      setSubmitting(true);
      setMessage("Enviando…"); setMessageType("info");

      const res = await fetch("http://localhost:8080/empresas", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      if (res.ok) {
  setMessage("✅ ¡Empresa registrada con éxito!");
  setMessageType("success");
  setForm({ nombre:"", razonSocial:"", cuit:"", ciudad:"", direccion:"", email:"" });
  setTimeout(() => {
    setMessage("");
    setMessageType("");
  }, 2000);
        setForm({ nombre:"", razonSocial:"", cuit:"", ciudad:"", direccion:"", email:"" });
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
            placeholder="Nombre de la empresa"
            value={form.nombre}
            onChange={onChange}
            required
          />
          <input
            type="text"
            name="ciudad"
            placeholder="Ciudad"
            value={form.ciudad}
            onChange={onChange}
            required
          />
          <input
            type="text"
            name="razonSocial"
            placeholder="Razón social (opcional)"
            value={form.razonSocial}
            onChange={onChange}
          />
          <input
            type="text"
            name="direccion"
            placeholder="Dirección"
            value={form.direccion}
            onChange={onChange}
            required
          />
          <input
            type="text"
            name="cuit"
            placeholder="CUIT"
            value={form.cuit}
            onChange={onChange}
          />
          <input
            type="email"
            name="email"
            placeholder="Email de contacto"
            value={form.email}
            onChange={onChange}
            required
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

      <p className="empresa-cta">
        ¡Unite y publicá pasantías gratis!<br />
        Encontrá los mejores estudiantes para tu empresa.
      </p>
    </section>
  );
}
