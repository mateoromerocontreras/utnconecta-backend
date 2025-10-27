import { useEffect, useMemo, useState, useCallback } from "react";
import { Link } from "react-router-dom";
import "../styles/empresas.css";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/,"");

function getStoredItem(key) {
  const persisted = localStorage.getItem(key);
  if (persisted !== null) return persisted;
  return sessionStorage.getItem(key);
}

function Toast({ toasts, onClose }) {
  return (
    <div className="toast-stack" role="status" aria-live="polite">
      {toasts.map(t => (
        <div
          key={t.id}
          className={`toast ${t.type === "error" ? "toast-error" : "toast-success"}`}
          onClick={() => onClose(t.id)}
        >
          {t.message}
          <button className="toast-close" aria-label="Cerrar" onClick={() => onClose(t.id)}>×</button>
        </div>
      ))}
    </div>
  );
}

function ConfirmDialog({ open, title, message, confirmText = "Eliminar", cancelText = "Cancelar", onConfirm, onCancel, loading = false }) {
  if (!open) return null;
  return (
    <div className="modal-overlay" role="dialog" aria-modal="true">
      <div className="modal">
        <h3 className="modal-title">{title}</h3>
        <p className="modal-text">{message}</p>
        <div className="modal-actions">
          <button className="btn btn-outline" onClick={onCancel} disabled={loading}>
            {cancelText}
          </button>
          <button className="btn btn-danger" onClick={onConfirm} disabled={loading}>
            {loading ? "Eliminando…" : confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}

export default function Empresas() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [q, setQ] = useState("");
  const [user, setUser] = useState(null);

  const readUserFromStorage = useCallback(() => {
    const token = getStoredItem("authToken");
    const userInfoRaw = getStoredItem("userInfo");

    if (!token || !userInfoRaw) {
      setUser(null);
      return;
    }

    try {
      setUser(JSON.parse(userInfoRaw));
    } catch (err) {
      console.error("Error parsing user info:", err);
      localStorage.removeItem("authToken");
      localStorage.removeItem("userInfo");
      sessionStorage.removeItem("authToken");
      sessionStorage.removeItem("userInfo");
      setUser(null);
    }
  }, []);

  useEffect(() => {
    readUserFromStorage();
    const handleAuthChange = () => readUserFromStorage();

    window.addEventListener("storage", handleAuthChange);
    window.addEventListener("auth-change", handleAuthChange);

    return () => {
      window.removeEventListener("storage", handleAuthChange);
      window.removeEventListener("auth-change", handleAuthChange);
    };
  }, [readUserFromStorage]);

  const isAdmin = user?.rol === "ADMINISTRADOR";

  // toasts
  const [toasts, setToasts] = useState([]);
  const pushToast = useCallback((message, type = "success") => {
    const id = crypto.randomUUID();
    setToasts(curr => [...curr, { id, message, type }]);
    // autodestruir a los 4.5s
    setTimeout(() => setToasts(curr => curr.filter(t => t.id !== id)), 4500);
  }, []);
  const closeToast = (id) => setToasts(curr => curr.filter(t => t.id !== id));

  // confirmación
  const [confirm, setConfirm] = useState({ open: false, id: null, loading: false, nombre: "" });

  async function load() {
    try {
      setLoading(true);
      setError("");
      const res = await fetch(`${API}/empresas`, { headers: { Accept: "application/json" } });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      // Mapear los campos del backend a lo que espera el frontend
      const mappedData = Array.isArray(data) ? data.map((empresa, index) => ({
        ...empresa,
        id: empresa.idEmpresa || index, // Usar idEmpresa o index como fallback
        emailContacto: empresa.email, // Mapear email a emailContacto
      })) : [];
      setRows(mappedData);
    } catch (err) {
      setError(err.message || "No se pudo obtener el listado.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, []);

  // abrir confirm
  function askDelete(row) {
    if (!isAdmin) return;
    // Usar idEmpresa si está disponible, sino usar el index mapeado
    const empresaId = row.idEmpresa || row.id;
    setConfirm({ open: true, id: empresaId, loading: false, nombre: row.nombre || row.razonSocial || "la empresa" });
  }

  // confirmar delete
  async function doDelete() {
    try {
      if (!isAdmin) {
        setConfirm({ open: false, id: null, loading: false, nombre: "" });
        pushToast("No tienes permisos para eliminar empresas.", "error");
        return;
      }

      setConfirm(c => ({ ...c, loading: true }));
      
      // Obtener token de autenticación
      const token = getStoredItem("authToken");
      if (!token) {
        throw new Error("No hay token de autenticación. Por favor, inicia sesión.");
      }
      
      const res = await fetch(`${API}/empresas/${confirm.id}`, {
        method: "DELETE",
        headers: { 
          "Accept": "application/json",
          "Authorization": `Bearer ${token}`
        }
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      setRows((rows) => rows.filter(r => (r.idEmpresa ?? r.id) !== confirm.id));
      setConfirm({ open: false, id: null, loading: false, nombre: "" });
      pushToast("Empresa eliminada correctamente.", "success");
    } catch (err) {
      setConfirm(c => ({ ...c, loading: false }));
      pushToast("No se pudo eliminar la empresa. " + (err.message || ""), "error");
    }
  }

  const filtered = useMemo(() => {
    const t = q.trim().toLowerCase();
    if (!t) return rows;
    return rows.filter((r) => {
      const nombre = (r.nombre || r.razonSocial || "").toLowerCase();
      const ciudad = (r.ciudad || "").toLowerCase();
      const cuit   = (r.cuit || "").toLowerCase();
      const email  = (r.emailContacto || r.email || "").toLowerCase(); // Buscar en ambos campos
      return (
        nombre.includes(t) ||
        ciudad.includes(t) ||
        cuit.includes(t) ||
        email.includes(t)
      );
    });
  }, [rows, q]);

  return (
    <section className="empresas-page">
      <div className="empresas-bar">
        <div className="container empresas-bar-inner">
          <input
            type="search"
            className="emp-search"
            placeholder="Buscar por nombre, ciudad, CUIT o email…"
            value={q}
            onChange={(e) => setQ(e.target.value)}
            enterKeyHint="search"
          />
          <div className="empresas-actions">
            <button
              className="btn btn-outline sm"
              onClick={() => {
                setQ("");
                load();
              }}
              disabled={loading && q === ""}
            >
              {loading && q === "" ? "Limpiando…" : "Limpiar"}
            </button>
            {isAdmin && (
              <Link to="/registrar-empresa" className="btn btn-primary sm">
                + Registrar empresa
              </Link>
            )}
          </div>
        </div>
      </div>

      <div className="container empresas-wrap">
        <header className="emp-head">
          <h1>Empresas</h1>
          <p className="muted">
            {loading ? "Cargando…" :
              error ? "—" :
              `${filtered.length} resultado${filtered.length !== 1 ? "s" : ""}`}
          </p>
        </header>

        {error && (
          <div className="emp-alert error">
            Ocurrió un error: {error} —{" "}
            <button className="link" onClick={load}>Reintentar</button>
          </div>
        )}

        {loading && !error && (
          <div className="emp-grid">
            {Array.from({ length: 6 }).map((_, i) => (
              <div key={i} className="emp-card skeleton" />
            ))}
          </div>
        )}

        {!loading && !error && (
          <>
            {filtered.length === 0 ? (
              <div className="emp-empty">
                No se encontraron empresas con ese criterio.
              </div>
            ) : (
              <div className="emp-grid">
                {filtered.map((e, idx) => (
                  <article key={e.id ?? idx} className="emp-card">
                    <div className="emp-logo">
                      {e.logoUrl ? (
                        <img src={e.logoUrl} alt={e.nombre || e.razonSocial} />
                      ) : (
                        <div className="emp-logo-fallback" aria-hidden="true">
                          {(e.nombre || e.razonSocial || "?").substring(0,1)}
                        </div>
                      )}
                    </div>

                    <div className="emp-body">
                      <h3 className="emp-name">{e.nombre || e.razonSocial || "Empresa"}</h3>
                      <p className="emp-meta">
                        {(e.ciudad || "—")}
                        {(e.calle || e.nroCalle || e.barrio) && " · "}
                        {e.calle && `${e.calle}`}
                        {e.nroCalle && ` ${e.nroCalle}`}
                        {e.piso && `, Piso ${e.piso}`}
                        {e.departamento && ` Depto ${e.departamento}`}
                        {e.barrio && ` - ${e.barrio}`}
                      </p>
                      <p className="emp-email">
                        {e.emailContacto || e.email ? (
                          <a href={`mailto:${e.emailContacto || e.email}`} className="link">
                            {e.emailContacto || e.email}
                          </a>
                        ) : (
                          <span className="muted">Sin email</span>
                        )}
                      </p>
                    </div>

                    <div className="emp-actions">
                      <button className="btn btn-ghost" onClick={() => alert("Perfil aún no implementado")}>
                        Ver perfil
                      </button>
                      {(e.emailContacto || e.email) && (
                        <a href={`mailto:${e.emailContacto || e.email}`} className="btn btn-primary sm">
                          Contactar
                        </a>
                      )}
                      {isAdmin && (
                        <button
                          className="btn btn-danger sm btn-icon"
                          onClick={() => askDelete(e)}
                          aria-label={`Eliminar ${e.nombre || e.razonSocial || "empresa"}`}
                        >
                          <img src="/icons/trash.svg" alt="" aria-hidden="true" />
                        </button>
                      )}
                    </div>
                  </article>
                ))}
              </div>
            )}
          </>
        )}
      </div>

      {/* Toasts */}
      <Toast toasts={toasts} onClose={closeToast} />

      {/* Confirmación */}
      <ConfirmDialog
        open={isAdmin ? confirm.open : false}
        title="Eliminar empresa"
        message={`Vas a eliminar ${confirm.nombre}. Esta acción no se puede deshacer.`}
        confirmText="Eliminar definitivamente"
        onConfirm={doDelete}
        onCancel={() => setConfirm({ open: false, id: null, loading: false, nombre: "" })}
        loading={confirm.loading}
      />
    </section>
  );
}
