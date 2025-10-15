import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/login.css";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/, "");

export default function Login() {
  const [form, setForm] = useState({ email: "", password: "", remember: true });
  const [showPass, setShowPass] = useState(false);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [loginError, setLoginError] = useState("");
  const navigate = useNavigate();

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
    setLoginError("");
    if (Object.keys(e).length) return;

    try {
      setLoading(true);

      const response = await fetch(`${API}/auth/iniciarSesion`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json"
        },
        body: JSON.stringify({
          email: form.email,
          password: form.password
        })
      });

      const data = await response.json();

      if (!response.ok || !data.token) {
        setLoginError(data.message || "Credenciales incorrectas");
        return;
      }

      const preferredStorage = form.remember ? localStorage : sessionStorage;
      const secondaryStorage = form.remember ? sessionStorage : localStorage;

      ["authToken", "userInfo"].forEach((key) => secondaryStorage.removeItem(key));
      preferredStorage.setItem("authToken", data.token);
      preferredStorage.setItem(
        "userInfo",
        JSON.stringify({
          username: data.username,
          email: data.email,
          rol: data.rol
        })
      );

      window.dispatchEvent(new Event("auth-change"));
      navigate("/", { replace: true });
    } catch (error) {
      console.error("Error de login:", error);
      setLoginError("Error de conexión. Intentá nuevamente.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="login-hero">
      <div className="container login-grid">
        <div className="login-card">
          <div className="or-divider">
            <span>Iniciar sesión con tu email</span>
          </div>

          <form onSubmit={handleSubmit} className="login-form" noValidate>
            {loginError && <div className="login-error">{loginError}</div>}

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
              <a href="#" className="link small">
                ¿Olvidaste tu contraseña?
              </a>
            </div>

            <button className="btn-primary btn-block" disabled={loading}>
              {loading ? "Logueandose" : "Iniciar sesión"}
            </button>

            <p className="muted center tiny">
              ¿No tienes una cuenta?{" "}
              <a href="/registrarse" className="link">
                Registrarse
              </a>
            </p>
          </form>

          <button className="btn-google" type="button">
            <img src="/icons/google.svg" alt="" aria-hidden="true" />
            <span>Inicia sesión con Google</span>
          </button>
        </div>

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
