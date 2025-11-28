import { useState, useEffect, useCallback } from "react";
import { useSearchParams, useNavigate, Link } from "react-router-dom";
import "../styles/login.css";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/, "");

export default function ConfirmarCuenta() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const token = searchParams.get("token");

  const verificarCuenta = useCallback(async () => {
    if (!token) {
      setError("Token no proporcionado");
      return;
    }

    try {
      setLoading(true);
      setError("");
      setMessage("");

      const response = await fetch(`${API}/auth/confirmar?token=${encodeURIComponent(token)}`, {
        method: "GET",
        headers: {
          Accept: "application/json"
        }
      });

      const data = await response.json();
      const ok = response.ok && (data.code === 0 || data.codigo === 0);

      if (ok) {
        setSuccess(true);
        setMessage(data.message || data.mensaje || "Cuenta verificada correctamente. Ya podes iniciar sesion.");
        setTimeout(() => {
          navigate("/login", { replace: true });
        }, 4000);
      } else {
        setError(data.message || data.mensaje || "Error al verificar la cuenta");
      }
    } catch (err) {
      console.error("Error de verificacion:", err);
      setError("Error de conexion. Intenta nuevamente.");
    } finally {
      setLoading(false);
    }
  }, [token, navigate]);

  useEffect(() => {
    if (token) {
      verificarCuenta();
    } else {
      setError("Token no proporcionado");
    }
  }, [token, verificarCuenta]);

  return (
    <section className="login-hero">
      <div className="container login-grid">
        <div className="login-card" style={{ padding: "2.5rem" }}>
          <div className="or-divider">
            <span>Confirmar cuenta</span>
          </div>

          {loading && (
            <div className="center" style={{ padding: "2rem" }}>
              <div style={{ fontSize: "2rem", marginBottom: "1rem", color: "#1976d2" }}>...</div>
              <p style={{ margin: 0, color: "#555" }}>Verificando tu cuenta...</p>
            </div>
          )}

          {success && !loading && (
            <div className="center" style={{ padding: "2rem" }}>
              <div style={{ fontSize: "2rem", marginBottom: "1rem", color: "#4caf50" }}>✔</div>
              <h2 style={{ color: "#4caf50", marginBottom: "0.75rem" }}>Cuenta verificada</h2>
              <p style={{ marginBottom: "2rem", color: "#666" }}>
                {message || "Tu email fue confirmado. Te redirigimos al inicio de sesion."}
              </p>
              <Link to="/login" className="btn-primary btn-block">
                Ir al inicio de sesion
              </Link>
            </div>
          )}

          {error && !loading && !success && (
            <div className="center" style={{ padding: "2rem" }}>
              <div style={{ fontSize: "2rem", marginBottom: "1rem", color: "#f44336" }}>!</div>
              <h2 style={{ color: "#f44336", marginBottom: "0.75rem" }}>No pudimos verificar</h2>
              <div className="login-error" style={{ marginBottom: "1.5rem" }}>
                {error}
              </div>
              <div style={{ display: "flex", flexDirection: "column", gap: "0.75rem" }}>
                <Link to="/login" className="btn-primary btn-block">
                  Ir al inicio de sesion
                </Link>
                <Link to="/registrarse" className="btn-outline btn-block">
                  Registrarse nuevamente
                </Link>
              </div>
            </div>
          )}

          {!token && !loading && !success && (
            <div className="center" style={{ padding: "2rem" }}>
              <div style={{ fontSize: "2rem", marginBottom: "1rem" }}>?</div>
              <h2 style={{ marginBottom: "1rem" }}>Token no encontrado</h2>
              <p style={{ marginBottom: "2rem", color: "#666" }}>
                Usa el enlace que recibiste por email para confirmar tu cuenta.
              </p>
              <Link to="/login" className="btn-primary btn-block">
                Ir al inicio de sesion
              </Link>
            </div>
          )}
        </div>

        <div className="login-art">
          <h2>
            Verifica tu cuenta,
            <br />
            comienza tu carrera
            <br />
            profesional hoy.
          </h2>
        </div>
      </div>
    </section>
  );
}





