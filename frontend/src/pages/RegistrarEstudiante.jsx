import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/registrar-estudiante.css";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/, "");

export default function RegistrarEstudiante() {
  const [form, setForm] = useState({
    nombre: "",
    apellido: "",
    dni: "",
    telCelular: "",
    email: "",
    password: "",
    confirmPassword: "",
    acceptTerms: false
  });
  
  const [showPass, setShowPass] = useState(false);
  const [showConfirmPass, setShowConfirmPass] = useState(false);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [registerError, setRegisterError] = useState("");
  const navigate = useNavigate();

  function validate(values) {
    const e = {};
    
    if (!values.nombre) {
      e.nombre = "El nombre es obligatorio";
    }
    
    if (!values.apellido) {
      e.apellido = "El apellido es obligatorio";
    }
    
    if (!values.dni) {
      e.dni = "El DNI es obligatorio";
    } else if (!/^\d{7,8}$/.test(values.dni)) {
      e.dni = "DNI inválido (7-8 dígitos)";
    }
    
    if (!values.telCelular) {
      e.telCelular = "El teléfono celular es obligatorio";
    } else if (!/^\d{10}$/.test(values.telCelular.replace(/[\s-]/g, ""))) {
      e.telCelular = "Teléfono inválido (10 dígitos)";
    }
    
    if (!values.email) {
      e.email = "El email es obligatorio";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email)) {
      e.email = "Email inválido";
    }
    
    if (!values.password) {
      e.password = "La contraseña es obligatoria";
    } else if (!/^(?=.*[a-z])(?=.*\d).{8,}$/.test(values.password)) {
      e.password = "Mínimo 8 caracteres, una letra minúscula y un número";
    }
    
    if (!values.confirmPassword) {
      e.confirmPassword = "Confirma tu contraseña";
    } else if (values.password !== values.confirmPassword) {
      e.confirmPassword = "Las contraseñas no coinciden";
    }
    
    if (!values.acceptTerms) {
      e.acceptTerms = "Debes aceptar los términos y condiciones";
    }
    
    return e;
  }

  function handleChange(ev) {
    const { name, value, checked, type } = ev.target;
    setForm((f) => ({ ...f, [name]: type === "checkbox" ? checked : value }));
    // Limpiar error del campo cuando el usuario empieza a escribir
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: "" }));
    }
  }

  async function handleSubmit(ev) {
    ev.preventDefault();
    const e = validate(form);
    setErrors(e);
    setRegisterError("");
    
    if (Object.keys(e).length) return;

    try {
      setLoading(true);

      const response = await fetch(`${API}/auth/registrarEstudiante`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json"
        },
        body: JSON.stringify({
          nombre: form.nombre,
          apellido: form.apellido,
          dni: form.dni,
          telCelular: form.telCelular,
          email: form.email,
          password: form.password
        })
      });

      const data = await response.json();

      if (!response.ok || !data.token) {
        setRegisterError(data.message || "Error al crear la cuenta");
        return;
      }

      // Guardar token y datos del usuario en localStorage
      localStorage.setItem("authToken", data.token);
      localStorage.setItem(
        "userInfo",
        JSON.stringify({
          username: data.username,
          email: data.email,
          rol: data.rol
        })
      );

      // Disparar evento de cambio de autenticación
      window.dispatchEvent(new Event("auth-change"));
      
      // Redirigir a completar perfil
      navigate("/perfil/completar", { replace: true });
      
    } catch (error) {
      console.error("Error de registro:", error);
      setRegisterError("Error de conexión. Intentá nuevamente.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="register-hero">
      <div className="container register-grid">
        <div className="register-card">
          <div className="register-header">
            <h1 className="register-title">Crear cuenta de estudiante</h1>
            <p className="register-subtitle">Completá tus datos para comenzar</p>
          </div>

          <form onSubmit={handleSubmit} className="register-form" noValidate>
            {registerError && <div className="register-error">{registerError}</div>}

            <div className="form-row">
              <div className={`field ${errors.nombre ? "has-error" : ""}`}>
                <label htmlFor="nombre">Nombre *</label>
                <input
                  id="nombre"
                  type="text"
                  name="nombre"
                  placeholder="Juan"
                  value={form.nombre}
                  onChange={handleChange}
                  disabled={loading}
                />
                {errors.nombre && <div className="error">{errors.nombre}</div>}
              </div>

              <div className={`field ${errors.apellido ? "has-error" : ""}`}>
                <label htmlFor="apellido">Apellido *</label>
                <input
                  id="apellido"
                  type="text"
                  name="apellido"
                  placeholder="Pérez"
                  value={form.apellido}
                  onChange={handleChange}
                  disabled={loading}
                />
                {errors.apellido && <div className="error">{errors.apellido}</div>}
              </div>
            </div>

            <div className="form-row">
              <div className={`field ${errors.dni ? "has-error" : ""}`}>
                <label htmlFor="dni">DNI *</label>
                <input
                  id="dni"
                  type="text"
                  name="dni"
                  placeholder="12345678"
                  value={form.dni}
                  onChange={handleChange}
                  disabled={loading}
                  maxLength="8"
                />
                {errors.dni && <div className="error">{errors.dni}</div>}
              </div>

              <div className={`field ${errors.telCelular ? "has-error" : ""}`}>
                <label htmlFor="telCelular">Teléfono celular *</label>
                <input
                  id="telCelular"
                  type="tel"
                  name="telCelular"
                  placeholder="3512345678"
                  value={form.telCelular}
                  onChange={handleChange}
                  disabled={loading}
                />
                {errors.telCelular && <div className="error">{errors.telCelular}</div>}
              </div>
            </div>

            <div className={`field ${errors.email ? "has-error" : ""}`}>
              <label htmlFor="email">Email *</label>
              <input
                id="email"
                type="email"
                name="email"
                placeholder="ejemplo@email.com"
                value={form.email}
                onChange={handleChange}
                autoComplete="email"
                disabled={loading}
              />
              {errors.email && <div className="error">{errors.email}</div>}
            </div>

            <div className={`field ${errors.password ? "has-error" : ""}`}>
              <label htmlFor="password">Contraseña *</label>
              <div className="pass-wrap">
                <input
                  id="password"
                  type={showPass ? "text" : "password"}
                  name="password"
                  placeholder="Mínimo 8 caracteres"
                  value={form.password}
                  onChange={handleChange}
                  autoComplete="new-password"
                  disabled={loading}
                />
                <button
                  type="button"
                  className="btn-eye"
                  aria-label={showPass ? "Ocultar contraseña" : "Mostrar contraseña"}
                  onClick={() => setShowPass((v) => !v)}
                >
                  {showPass ? "🙈" : "👁️"}
                </button>
              </div>
              {errors.password && <div className="error">{errors.password}</div>}
            </div>

            <div className={`field ${errors.confirmPassword ? "has-error" : ""}`}>
              <label htmlFor="confirmPassword">Confirmar contraseña *</label>
              <div className="pass-wrap">
                <input
                  id="confirmPassword"
                  type={showConfirmPass ? "text" : "password"}
                  name="confirmPassword"
                  placeholder="Repetí tu contraseña"
                  value={form.confirmPassword}
                  onChange={handleChange}
                  autoComplete="new-password"
                  disabled={loading}
                />
                <button
                  type="button"
                  className="btn-eye"
                  aria-label={showConfirmPass ? "Ocultar contraseña" : "Mostrar contraseña"}
                  onClick={() => setShowConfirmPass((v) => !v)}
                >
                  {showConfirmPass ? "🙈" : "👁️"}
                </button>
              </div>
              {errors.confirmPassword && <div className="error">{errors.confirmPassword}</div>}
            </div>

            <div className={`field-checkbox ${errors.acceptTerms ? "has-error" : ""}`}>
              <label className="check">
                <input
                  type="checkbox"
                  name="acceptTerms"
                  checked={form.acceptTerms}
                  onChange={handleChange}
                />
                <span>
                  Acepto los{" "}
                  <a href="#" className="link" onClick={(e) => e.preventDefault()}>
                    términos y condiciones
                  </a>{" "}
                  y las{" "}
                  <a href="#" className="link" onClick={(e) => e.preventDefault()}>
                    políticas de privacidad
                  </a>
                </span>
              </label>
              {errors.acceptTerms && <div className="error">{errors.acceptTerms}</div>}
            </div>

            <button className="btn-primary btn-block" disabled={loading}>
              {loading ? "Creando cuenta..." : "Crear cuenta"}
            </button>

            <p className="muted center">
              ¿Ya tienes una cuenta?{" "}
              <a href="/login" className="link">
                Ingresa como estudiante
              </a>
            </p>
          </form>
        </div>

        <div className="register-art">
          <h2>
            Tu futuro
            <br />
            profesional
            <br />
            comienza aquí
          </h2>
        </div>
      </div>
    </section>
  );
}
