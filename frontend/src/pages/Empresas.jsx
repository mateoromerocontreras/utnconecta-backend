import { useEffect, useMemo, useState, useCallback } from "react";
import "../styles/empresas.css";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/,"");

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
      setRows(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message || "No se pudo obtener el listado.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, []);

  // abrir confirm
  function askDelete(row) {
    setConfirm({ open: true, id: row.id, loading: false, nombre: row.nombre || row.razonSocial || "la empresa" });
  }

  // confirmar delete
  async function doDelete() {
    try {
      setConfirm(c => ({ ...c, loading: true }));
      const res = await fetch(`${API}/empresas/${confirm.id}`, {
        method: "DELETE",
        headers: { Accept: "application/json" }
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      setRows((rows) => rows.filter(r => r.id !== confirm.id));
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
      const email  = (r.emailContacto || "").toLowerCase();
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
          <button className="btn btn-outline sm" onClick={load} disabled={loading}>
            {loading ? "Actualizando…" : "Recargar"}
          </button>
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
                        {e.ciudad && e.direccion ? " · " : ""}
                        {e.direccion || ""}
                      </p>
                      <p className="emp-email">
                        {e.emailContacto ? (
                          <a href={`mailto:${e.emailContacto}`} className="link">
                            {e.emailContacto}
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
                      {e.emailContacto && (
                        <a href={`mailto:${e.emailContacto}`} className="btn btn-primary sm">
                          Contactar
                        </a>
                      )}
                      <button className="btn btn-danger sm" onClick={() => askDelete(e)}>
                        Eliminar
                      </button>
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
        open={confirm.open}
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
