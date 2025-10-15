import { useEffect, useMemo, useState, useCallback } from "react";
import "../styles/carreras.css";

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

function EditDialog({ open, carrera, onSave, onCancel, loading = false }) {
  const [nombre, setNombre] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (open && carrera) {
      setNombre(carrera.nombre || "");
      setError("");
    }
  }, [open, carrera]);

  const handleSave = () => {
    if (!nombre.trim()) {
      setError("El nombre es obligatorio");
      return;
    }
    onSave(nombre.trim());
  };

  if (!open) return null;

  return (
    <div className="modal-overlay" role="dialog" aria-modal="true">
      <div className="modal">
        <h3 className="modal-title">Editar Carrera</h3>
        <div className="modal-form">
          <label htmlFor="edit-nombre" className="form-label">Nombre de la carrera</label>
          <input
            type="text"
            id="edit-nombre"
            className={`form-input ${error ? 'error' : ''}`}
            value={nombre}
            onChange={(e) => {
              setNombre(e.target.value);
              if (error) setError("");
            }}
            placeholder="Ingrese el nombre de la carrera"
            disabled={loading}
          />
          {error && <span className="error-text">{error}</span>}
        </div>
        <div className="modal-actions">
          <button className="btn btn-outline" onClick={onCancel} disabled={loading}>
            Cancelar
          </button>
          <button className="btn btn-primary" onClick={handleSave} disabled={loading}>
            {loading ? "Guardando…" : "Guardar"}
          </button>
        </div>
      </div>
    </div>
  );
}

export default function Carreras() {
  // Verificar si el usuario es administrador
  const storedUserInfo = getStoredItem("userInfo");
  const userInfo = storedUserInfo ? JSON.parse(storedUserInfo) : {};
  const isAdmin = userInfo.rol === "ADMINISTRADOR";

  // Si no es administrador, mostrar mensaje de acceso denegado
  if (!isAdmin) {
    return (
      <div className="container" style={{ padding: "50px 20px", textAlign: "center" }}>
        <h2 style={{ color: "#dc3545", marginBottom: "20px" }}>Acceso Denegado</h2>
        <p style={{ fontSize: "18px", color: "#666", marginBottom: "30px" }}>
          No tienes permisos para gestionar carreras. Solo los administradores pueden acceder a esta funcionalidad.
        </p>
        <button 
          className="btn btn-primary"
          onClick={() => window.location.href = "/"}
        >
          Volver al Inicio
        </button>
      </div>
    );
  }

  const [carreras, setCarreras] = useState([]);
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

  // confirmación de eliminación
  const [confirm, setConfirm] = useState({ open: false, carrera: null, loading: false });

  // edición
  const [edit, setEdit] = useState({ open: false, carrera: null, loading: false });

  async function loadCarreras() {
    try {
      setLoading(true);
      setError("");
      const res = await fetch(`${API}/carreras/listarCarreras`, { 
        headers: { Accept: "application/json" } 
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      // El endpoint ahora devuelve List<Carrera> con id y nombre reales
      setCarreras(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message || "No se pudo obtener el listado de carreras.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { loadCarreras(); }, []);

  // abrir confirm para eliminar
  function askDelete(carrera) {
    setConfirm({ open: true, carrera, loading: false });
  }

  // confirmar eliminación
  async function doDelete() {
    try {
      setConfirm(c => ({ ...c, loading: true }));
      
      // Obtener token de autenticación
      const token = getStoredItem("authToken");
      if (!token) {
        throw new Error("No hay token de autenticación. Por favor, inicia sesión.");
      }
      
      const res = await fetch(`${API}/carreras/deleteCarrera`, {
        method: "POST",
        headers: { 
          "Content-Type": "application/json",
          "Accept": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ nombre: confirm.carrera.nombre })
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      
      const data = await res.json();
      if (data.code === 0) {
        setCarreras(prev => prev.filter(c => c.nombre !== confirm.carrera.nombre));
        setConfirm({ open: false, carrera: null, loading: false });
        pushToast("Carrera eliminada correctamente.", "success");
      } else {
        throw new Error(data.message || "Error al eliminar carrera");
      }
    } catch (err) {
      setConfirm(c => ({ ...c, loading: false }));
      pushToast("No se pudo eliminar la carrera. " + (err.message || ""), "error");
    }
  }

  // abrir modal de edición
  function openEdit(carrera) {
    setEdit({ open: true, carrera, loading: false });
  }

  // guardar edición
  async function saveEdit(nuevoNombre) {
    try {
      setEdit(e => ({ ...e, loading: true }));
      
      // Obtener token de autenticación
      const token = getStoredItem("authToken");
      if (!token) {
        throw new Error("No hay token de autenticación. Por favor, inicia sesión.");
      }
      
      const res = await fetch(`${API}/carreras/updateCarrera`, {
        method: "POST",
        headers: { 
          "Content-Type": "application/json",
          "Accept": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ 
          id: edit.carrera.id, 
          nombre: nuevoNombre 
        })
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      
      const data = await res.json();
      if (data.code === 0) {
        setCarreras(prev => prev.map(c => 
          c.nombre === edit.carrera.nombre 
            ? { ...c, nombre: nuevoNombre }
            : c
        ));
        setEdit({ open: false, carrera: null, loading: false });
        pushToast("Carrera actualizada correctamente.", "success");
      } else {
        throw new Error(data.message || "Error al actualizar carrera");
      }
    } catch (err) {
      setEdit(e => ({ ...e, loading: false }));
      pushToast("No se pudo actualizar la carrera. " + (err.message || ""), "error");
    }
  }

  const filtered = useMemo(() => {
    const t = q.trim().toLowerCase();
    if (!t) return carreras;
    return carreras.filter(c => c.nombre.toLowerCase().includes(t));
  }, [carreras, q]);

  return (
    <section className="carreras-page">
      <div className="carreras-bar">
        <div className="container carreras-bar-inner">
          <input
            type="search"
            className="carreras-search"
            placeholder="Buscar carrera por nombre…"
            value={q}
            onChange={(e) => setQ(e.target.value)}
            enterKeyHint="search"
          />
          <div className="carreras-actions">
            <button
              className="btn btn-outline sm"
              onClick={() => {
                setQ("");
                loadCarreras();
              }}
              disabled={loading && q === ""}
            >
              {loading && q === "" ? "Limpiando…" : "Limpiar"}
            </button>
            <a href="/registrar-carrera" className="btn btn-primary sm">
              + Nueva Carrera
            </a>
          </div>
        </div>
      </div>

      <div className="container carreras-wrap">
        <header className="carreras-head">
          <h1>Carreras</h1>
          <p className="muted">
            {loading ? "Cargando…" :
              error ? "—" :
              `${filtered.length} carrera${filtered.length !== 1 ? "s" : ""}`}
          </p>
        </header>

        {error && (
          <div className="carreras-alert error">
            Ocurrió un error: {error} —{" "}
            <button className="link" onClick={loadCarreras}>Reintentar</button>
          </div>
        )}

        {loading && !error && (
          <div className="carreras-grid">
            {Array.from({ length: 6 }).map((_, i) => (
              <div key={i} className="carrera-card skeleton" />
            ))}
          </div>
        )}

        {!loading && !error && (
          <>
            {filtered.length === 0 ? (
              <div className="carreras-empty">
                {q ? "No se encontraron carreras con ese criterio." : "No hay carreras registradas."}
                {!q && (
                  <a href="/registrar-carrera" className="btn btn-primary" style={{marginTop: '16px'}}>
                    Registrar primera carrera
                  </a>
                )}
              </div>
            ) : (
              <div className="carreras-grid">
                {filtered.map((carrera, idx) => (
                  <article key={carrera.nombre + idx} className="carrera-card">
                    <div className="carrera-logo" aria-hidden="true">
                      {(carrera.nombre || "?").substring(0, 1).toUpperCase()}
                    </div>
                    <div className="carrera-body">
                      <h3 className="carrera-name">{carrera.nombre}</h3>
                      <p className="carrera-meta">Programa académico registrado</p>
                    </div>
                    <div className="carrera-actions">
                      <button 
                        className="btn-icon-only" 
                        onClick={() => openEdit(carrera)}
                        aria-label={`Editar ${carrera.nombre}`}
                      >
                        <img src="/icons/edit.svg" alt="" aria-hidden="true" />
                      </button>
                      <button
                        className="btn btn-danger sm btn-icon"
                        onClick={() => askDelete(carrera)}
                        aria-label={`Eliminar ${carrera.nombre}`}
                      >
                        <img src="/icons/trash.svg" alt="" aria-hidden="true" />
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

      {/* Confirmación de eliminación */}
      <ConfirmDialog
        open={confirm.open}
        title="Eliminar carrera"
        message={`¿Estás seguro de que querés eliminar la carrera "${confirm.carrera?.nombre}"? Esta acción no se puede deshacer.`}
        confirmText="Eliminar definitivamente"
        onConfirm={doDelete}
        onCancel={() => setConfirm({ open: false, carrera: null, loading: false })}
        loading={confirm.loading}
      />

      {/* Modal de edición */}
      <EditDialog
        open={edit.open}
        carrera={edit.carrera}
        onSave={saveEdit}
        onCancel={() => setEdit({ open: false, carrera: null, loading: false })}
        loading={edit.loading}
      />
    </section>
  );
}
