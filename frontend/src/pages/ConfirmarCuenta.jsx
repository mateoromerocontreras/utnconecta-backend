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
  const token = searchParams.get("token");

  const verificarCuenta = useCallback(async () => {
    if (!token) {
      setError("Token no proporcionado");
      return;
    }

    try {
      setLoading(true);
      setError("");

      const response = await fetch(`${API}/auth/confirmar-cuenta?token=${encodeURIComponent(token)}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json"
        }
      });

      const data = await response.json();

      if (response.ok && data.codigo === 0) {
        setSuccess(true);
        // Redirigir al login después de 5 segundos
        setTimeout(() => {
          navigate("/login", { replace: true });
        }, 5000);
      } else {
        setError(data.mensaje || "Error al verificar la cuenta");
      }
    } catch (err) {
      console.error("Error de verificación:", err);
      setError("Error de conexión. Intentá nuevamente.");
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
        <div className="login-card">
          <div className="or-divider">
            <span>Confirmar cuenta</span>
          </div>

          {loading && (
            <div className="center" style={{ padding: "2rem" }}>
              <div style={{ fontSize: "1.5rem", marginBottom: "1rem" }}>⏳</div>
              <p>Verificando tu cuenta...</p>
            </div>
          )}

          {success && !loading && (
            <div className="center" style={{ padding: "2rem" }}>
              <div style={{ fontSize: "3rem", marginBottom: "1rem" }}>✓</div>
              <h2 style={{ color: "#4caf50", marginBottom: "1rem" }}>¡Cuenta verificada exitosamente!</h2>
              <p style={{ marginBottom: "2rem", color: "#666" }}>
                Tu email ha sido verificado. Serás redirigido al inicio de sesión en unos segundos.
              </p>
              <Link to="/login" className="btn-primary btn-block">
                Ir al inicio de sesión
              </Link>
            </div>
          )}

          {error && !loading && !success && (
            <div className="center" style={{ padding: "2rem" }}>
              <div style={{ fontSize: "3rem", marginBottom: "1rem" }}>✗</div>
              <h2 style={{ color: "#f44336", marginBottom: "1rem" }}>Error al verificar</h2>
              <div className="login-error" style={{ marginBottom: "2rem" }}>
                {error}
              </div>
              <div style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
                <Link to="/login" className="btn-primary btn-block">
                  Ir al inicio de sesión
                </Link>
                <Link to="/registrarse" className="btn-outline btn-block">
                  Registrarse nuevamente
                </Link>
              </div>
            </div>
          )}

          {!token && !loading && (
            <div className="center" style={{ padding: "2rem" }}>
              <div style={{ fontSize: "3rem", marginBottom: "1rem" }}>⚠️</div>
              <h2 style={{ marginBottom: "1rem" }}>Token no encontrado</h2>
              <p style={{ marginBottom: "2rem", color: "#666" }}>
                No se proporcionó un token de verificación. Por favor, usa el enlace que recibiste por email.
              </p>
              <Link to="/login" className="btn-primary btn-block">
                Ir al inicio de sesión
              </Link>
            </div>
          )}
        </div>

        <div className="login-art">
          <h2>
            Verificá tu cuenta,
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

