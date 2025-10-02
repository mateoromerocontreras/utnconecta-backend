import { useState } from "react";
import "../styles/login.css";

export default function Login() {
  const [form, setForm] = useState({ email: "", password: "", remember: true });
  const [showPass, setShowPass] = useState(false);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  function validate(values) {
    const e = {};
    if (!values.email) e.email = "Ingresá tu email";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email)) e.email = "Email inválido";
    if (!values.password) e.password = "Ingresá tu contraseña";
    return e;
  }

  function handleChange(ev) {
    const { name, value, checked, type } = ev.target;
    setForm((f) => ({ ...f, [name]: type === "checkbox" ? checked : value }));
  }

  async function handleSubmit(ev) {
    ev.preventDefault();
    const e = validate(form);
    setErrors(e);
    if (Object.keys(e).length) return;

    try {
      setLoading(true);
      // TODO: llamar a tu API real
      await new Promise((r) => setTimeout(r, 900));
      alert("Login OK (simulado)");
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="login-hero">
      <div className="container login-grid">
        {/* Columna izquierda (form) */}
        <div className="login-card">
          


          

          
          
          <div className="or-divider">
            <span>Iniciar sesión con tu email</span>
          </div>

          <form onSubmit={handleSubmit} className="login-form" noValidate>
            <div className={`field ${errors.email ? "has-error" : ""}`}>
              <input
                type="email"
                name="email"
                placeholder="Email"
                value={form.email}
                onChange={handleChange}
                autoComplete="email"
                disabled={loading}
              />
              {errors.email && <div className="error">{errors.email}</div>}
            </div>

            

            <div className={`field ${errors.password ? "has-error" : ""}`}>
              <div className="pass-wrap">
                <input
                  type={showPass ? "text" : "password"}
                  name="password"
                  placeholder="Contraseña"
                  value={form.password}
                  onChange={handleChange}
                  autoComplete="current-password"
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

            <div className="row between">
              <label className="check">
                <input
                  type="checkbox"
                  name="remember"
                  checked={form.remember}
                  onChange={handleChange}
                />
                <span>Mantén mi cuenta conectada</span>
              </label>
              <a href="#" className="link small">Olvidaste tu contraseña?</a>
            </div>

            <button className="btn-primary btn-block" disabled={loading}>
              {loading ? "Logging in…" : "Iniciar sesion"}
            </button>

            <p className="muted center tiny">
              ¿No tienes una cuenta? <a href="/registrarse" className="link">Registrarse</a>
            </p>
          </form>

          <button className="btn-google" type="button">
              <img src="/icons/google.svg" alt="" aria-hidden="true" />
              <span>Inicia sesión con Google</span>
          </button>
        </div>

          

        {/* Columna derecha (copy + shapes) */}
<div className="login-art">
  <h2>
    Cambiá tu futuro,
    <br />
    comenzá tu carrera
    <br />
    profesional hoy.
  </h2>
</div>

      </div>
    </section>
  );
}
