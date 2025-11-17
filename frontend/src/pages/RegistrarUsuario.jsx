import { useState, useCallback, useEffect } from "react";
import "../styles/registrar-usuario.css";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/, "");

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

// Obtiene un valor persistido en localStorage o sessionStorage (según dónde exista)
function getStoredItem(key) {
  const persisted = localStorage.getItem(key);
  if (persisted !== null) return persisted;
  return sessionStorage.getItem(key);
}

export default function RegistrarUsuario() {
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
          No tienes permisos para acceder a esta sección. Solo los administradores pueden gestionar usuarios.
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

  // Estados para las diferentes vistas
  const [activeTab, setActiveTab] = useState("consultar"); // consultar, registrar, modificar
  
  // Estados para listado de usuarios
  const [usuarios, setUsuarios] = useState([]);
  const [loadingUsuarios, setLoadingUsuarios] = useState(false);
  const [filtroNombre, setFiltroNombre] = useState("");
  
  // Estados para registro de usuario
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    rol: ""
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  // Estados para modificación de usuario
  const [editingUser, setEditingUser] = useState(null);
  const [editFormData, setEditFormData] = useState({
    idUsuario: "",
    nombre: "",
    email: "",
    password: "",
    rol: "",
    activo: true
  });

  // Estados para confirmación de eliminación
  const [confirmDelete, setConfirmDelete] = useState({
    open: false,
    user: null,
    loading: false
  });

  // toasts
  const [toasts, setToasts] = useState([]);
  const pushToast = useCallback((message, type = "success") => {
    const id = crypto.randomUUID();
    setToasts(curr => [...curr, { id, message, type }]);
    // autodestruir a los 4.5s
    setTimeout(() => setToasts(curr => curr.filter(t => t.id !== id)), 4500);
  }, []);
  const closeToast = (id) => setToasts(curr => curr.filter(t => t.id !== id));

  // Cargar usuarios al montar el componente
  useEffect(() => {
    if (activeTab === "consultar") {
      loadUsuarios();
    }
  }, [activeTab]);

  // Función para cargar todos los usuarios
  const loadUsuarios = async (nombre = "") => {
    try {
      setLoadingUsuarios(true);
      const token = getStoredItem("authToken");
      if (!token) {
        pushToast("No hay token de autenticación. Por favor, inicia sesión.", "error");
        return;
      }

      const url = nombre ? `${API}/usuarios/consultarUsuario?nombre=${encodeURIComponent(nombre)}` : `${API}/usuarios/consultarUsuario`;
      const response = await fetch(url, {
        headers: {
          "Authorization": `Bearer ${token}`,
          "Accept": "application/json"
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }

      const data = await response.json();
      
      if (Array.isArray(data)) {
        setUsuarios(data);
      } else if (data && typeof data === 'object') {
        setUsuarios([data]);
      } else {
        setUsuarios([]);
      }
    } catch (err) {
      pushToast("Error al cargar usuarios: " + (err.message || ""), "error");
      setUsuarios([]);
    } finally {
      setLoadingUsuarios(false);
    }
  };

  // Función para buscar usuario por nombre
  const handleSearch = (e) => {
    e.preventDefault();
    loadUsuarios(filtroNombre);
  };

  // Validaciones del lado cliente
  const validateForm = () => {
    const newErrors = {};

    if (!formData.username.trim()) {
      newErrors.username = "El username es obligatorio";
    }

    if (!formData.email.trim()) {
      newErrors.email = "El email es obligatorio";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = "Formato de email inválido";
    }

    if (!formData.password) {
      newErrors.password = "La contraseña es obligatoria";
    } else if (!/^(?=.*[a-z])(?=.*\d).{8,}$/.test(formData.password)) {
      newErrors.password = "La contraseña debe tener al menos 8 caracteres, una letra minúscula y un número";
    }

    if (!formData.rol) {
      newErrors.rol = "El rol es obligatorio";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Limpiar error del campo cuando el usuario empiece a escribir
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ""
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    try {
      setLoading(true);
      const token = getStoredItem("authToken");
      if (!token) {
        pushToast("No hay token de autenticación. Por favor, inicia sesión.", "error");
        return;
      }

      const response = await fetch(`${API}/usuarios/registrarUsuario`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(formData)
      });

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }

      const data = await response.json();
      
      if (data.code === 0) {
        pushToast("Usuario registrado exitosamente", "success");
        // Limpiar formulario
        setFormData({
          username: "",
          email: "",
          password: "",
          rol: ""
        });
        // Recargar usuarios si estamos en la pestaña de consultar
        if (activeTab === "consultar") {
          loadUsuarios();
        }
      } else {
        pushToast(data.message || "Error al Registrar usuario", "error");
      }
    } catch (err) {
      pushToast("Error de conexión: " + (err.message || ""), "error");
    } finally {
      setLoading(false);
    }
  };

  // Funciones para Modificar usuario
  const openEditUser = (usuario) => {
    setEditingUser(usuario);
    setEditFormData({
      idUsuario: usuario.username,
      nombre: usuario.username,
      email: usuario.email,
      password: "",
      rol: usuario.rol?.nombre || "",
      activo: usuario.activo
    });
    setActiveTab("modificar");
  };

  const handleEditInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setEditFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    
    try {
      setLoading(true);
      const token = getStoredItem("authToken");
      if (!token) {
        pushToast("No hay token de autenticación. Por favor, inicia sesión.", "error");
        return;
      }

      const response = await fetch(`${API}/usuarios/actualizarUsuario`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(editFormData)
      });

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }

      const data = await response.json();
      
      if (data.code === 0) {
        pushToast("Usuario actualizado exitosamente", "success");
        setEditingUser(null);
        setActiveTab("consultar");
        loadUsuarios();
      } else {
        pushToast(data.message || "Error al actualizar usuario", "error");
      }
    } catch (err) {
      pushToast("Error de conexión: " + (err.message || ""), "error");
    } finally {
      setLoading(false);
    }
  };

  const cancelEdit = () => {
    setEditingUser(null);
    setActiveTab("consultar");
  };

  // Funciones para eliminar usuario
  const openDeleteConfirm = (usuario) => {
    setConfirmDelete({
      open: true,
      user: usuario,
      loading: false
    });
  };

  const handleDeleteUser = async () => {
    try {
      setConfirmDelete(prev => ({ ...prev, loading: true }));
      const token = getStoredItem("authToken");
      if (!token) {
        pushToast("No hay token de autenticación. Por favor, inicia sesión.", "error");
        return;
      }

      const response = await fetch(`${API}/usuarios/eliminarUsuario`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ nombre: confirmDelete.user.username })
      });

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }

      const data = await response.json();
      
      if (data.code === 0) {
        pushToast("Usuario eliminado exitosamente", "success");
        setConfirmDelete({ open: false, user: null, loading: false });
        loadUsuarios(); // Recargar la lista
      } else {
        pushToast(data.message || "Error al eliminar usuario", "error");
      }
    } catch (err) {
      pushToast("Error de conexión: " + (err.message || ""), "error");
    } finally {
      setConfirmDelete(prev => ({ ...prev, loading: false }));
    }
  };

  const cancelDelete = () => {
    setConfirmDelete({ open: false, user: null, loading: false });
  };

  return (
    <section className="registrar-usuario-page">
      <div className="container">
        <header className="section-head">
          <div>
            <p className="section-head__eyebrow">Panel administrativo</p>
            <h1>Gestion de usuarios</h1>
            <p className="muted">
              Controla altas, bajas y permisos de todo el equipo desde un unico lugar.
            </p>
          </div>
          <div className="section-head__meta">
            <span className="section-head__badge">
              {loadingUsuarios ? "Cargando..." : `${usuarios.length} usuario${usuarios.length === 1 ? "" : "s"}`}
            </span>
          </div>
        </header>
        <div className="form-wrapper">

          {/* Tabs */}
          <div className="tabs">
            <button 
              className={`tab ${activeTab === "consultar" ? "active" : ""}`}
              onClick={() => setActiveTab("consultar")}
            >
              Consultar usuarios
            </button>
            <button 
              className={`tab ${activeTab === "registrar" ? "active" : ""}`}
              onClick={() => setActiveTab("registrar")}
            >
              Registrar usuario
            </button>
            {editingUser && (
              <button 
                className={`tab ${activeTab === "modificar" ? "active" : ""}`}
                onClick={() => setActiveTab("modificar")}
              >
                Modificar usuario
              </button>
            )}
          </div>

          {/* Contenido de Consultar usuarios */}
          {activeTab === "consultar" && (
            <div className="tab-content">
              <div className="search-section">
                <form onSubmit={handleSearch} className="search-form">
                  <input
                    type="text"
                    placeholder="Buscar por nombre de usuario..."
                    value={filtroNombre}
                    onChange={(e) => setFiltroNombre(e.target.value)}
                    className="form-input"
                  />
                  <button type="submit" className="btn btn-secondary">Buscar</button>
                  <button 
                    type="button" 
                    onClick={() => {setFiltroNombre(""); loadUsuarios();}} 
                    className="btn btn-outline"
                  >
                    Mostrar Todos
                  </button>
                </form>
              </div>

              {loadingUsuarios ? (
                <div className="loading">Cargando usuarios...</div>
              ) : (
                <div className="usuarios-table">
                  {usuarios.length === 0 ? (
                    <p className="no-data">No se encontraron usuarios.</p>
                  ) : (
                    <table className="table">
                      <thead>
                        <tr>
                          <th>Username</th>
                          <th>Email</th>
                          <th>Rol</th>
                          <th>Estado</th>
                          <th>Fecha Creación</th>
                          <th>Acciones</th>
                        </tr>
                      </thead>
                      <tbody>
                        {usuarios.map((usuario) => (
                          <tr key={usuario.idUsuario}>
                            <td>{usuario.username}</td>
                            <td>{usuario.email}</td>
                            <td>{usuario.rol?.nombre || "N/A"}</td>
                            <td>
                              <span className={`status ${usuario.activo ? "active" : "inactive"}`}>
                                {usuario.activo ? "Activo" : "Inactivo"}
                              </span>
                            </td>
                            <td>{new Date(usuario.fechaCreacion).toLocaleDateString()}</td>
                            <td>
                              <div className="action-buttons">
                                <button 
                                  className="btn-icon-only"
                                  onClick={() => openEditUser(usuario)}
                                  aria-label={`Editar ${usuario.username}`}
                                >
                                  <img src="/icons/edit.svg" alt="" aria-hidden="true" />
                                </button>
                                <button 
                                  className="btn btn-small btn-danger"
                                  onClick={() => openDeleteConfirm(usuario)}
                                  aria-label={`Eliminar ${usuario.username}`}
                                >
                                  <img src="/icons/trash.svg" alt="" aria-hidden="true" />
                                </button>
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  )}
                </div>
              )}
            </div>
          )}

          {/* Contenido de Registrar usuario */}
          {activeTab === "registrar" && (
            <div className="tab-content">
              <form className="user-form" onSubmit={handleSubmit}>
                <div className="form-group">
                  <label htmlFor="username" className="form-label">
                    Username *
                  </label>
                  <input
                    type="text"
                    id="username"
                    name="username"
                    className={`form-input ${errors.username ? 'error' : ''}`}
                    value={formData.username}
                    onChange={handleInputChange}
                    placeholder="Ingrese el username"
                    disabled={loading}
                  />
                  {errors.username && <span className="error-text">{errors.username}</span>}
                </div>

                <div className="form-group">
                  <label htmlFor="email" className="form-label">
                    Email *
                  </label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    className={`form-input ${errors.email ? 'error' : ''}`}
                    value={formData.email}
                    onChange={handleInputChange}
                    placeholder="usuario@ejemplo.com"
                    disabled={loading}
                  />
                  {errors.email && <span className="error-text">{errors.email}</span>}
                </div>

                <div className="form-group">
                  <label htmlFor="password" className="form-label">
                    Contraseña *
                  </label>
                  <input
                    type="password"
                    id="password"
                    name="password"
                    className={`form-input ${errors.password ? 'error' : ''}`}
                    value={formData.password}
                    onChange={handleInputChange}
                    placeholder="Mínimo 8 caracteres, 1 minúscula, 1 número"
                    disabled={loading}
                  />
                  {errors.password && <span className="error-text">{errors.password}</span>}
                </div>

                <div className="form-group">
                  <label htmlFor="rol" className="form-label">
                    Rol *
                  </label>
                  <select
                    id="rol"
                    name="rol"
                    className={`form-input ${errors.rol ? 'error' : ''}`}
                    value={formData.rol}
                    onChange={handleInputChange}
                    disabled={loading}
                  >
                    <option value="">Seleccione un rol</option>
                    <option value="ESTUDIANTE">Estudiante</option>
                    <option value="EMPRESA">Empresa</option>
                    <option value="ADMINISTRADOR">Administrador</option>
                  </select>
                  {errors.rol && <span className="error-text">{errors.rol}</span>}
                </div>

                <div className="form-actions">
                  <button 
                    type="submit" 
                    className="btn btn-primary"
                    disabled={loading}
                  >
                    {loading ? "Registrando..." : "Registrar usuario"}
                  </button>
                </div>
              </form>
            </div>
          )}

          {/* Contenido de Modificar usuario */}
          {activeTab === "modificar" && editingUser && (
            <div className="tab-content">
              <h3>Modificar usuario: {editingUser.username}</h3>
              <form className="user-form" onSubmit={handleEditSubmit}>
                <div className="form-group">
                  <label htmlFor="edit-nombre" className="form-label">
                    Username
                  </label>
                  <input
                    type="text"
                    id="edit-nombre"
                    name="nombre"
                    className="form-input"
                    value={editFormData.nombre}
                    onChange={handleEditInputChange}
                    placeholder="Nuevo username"
                    disabled={loading}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="edit-email" className="form-label">
                    Email
                  </label>
                  <input
                    type="email"
                    id="edit-email"
                    name="email"
                    className="form-input"
                    value={editFormData.email}
                    onChange={handleEditInputChange}
                    placeholder="Nuevo email"
                    disabled={loading}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="edit-password" className="form-label">
                    Nueva Contraseña (opcional)
                  </label>
                  <input
                    type="password"
                    id="edit-password"
                    name="password"
                    className="form-input"
                    value={editFormData.password}
                    onChange={handleEditInputChange}
                    placeholder="Dejar vacío para mantener la actual"
                    disabled={loading}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="edit-rol" className="form-label">
                    Rol
                  </label>
                  <select
                    id="edit-rol"
                    name="rol"
                    className="form-input"
                    value={editFormData.rol}
                    onChange={handleEditInputChange}
                    disabled={loading}
                  >
                    <option value="">Seleccione un rol</option>
                    <option value="ESTUDIANTE">Estudiante</option>
                    <option value="EMPRESA">Empresa</option>
                    <option value="ADMINISTRADOR">Administrador</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="checkbox-label">
                    <span>Usuario activo</span>
                    <input
                      type="checkbox"
                      name="activo"
                      checked={editFormData.activo}
                      onChange={handleEditInputChange}
                      disabled={loading}
                    />
                  </label>
                </div>

                <div className="form-actions">
                  <button 
                    type="submit" 
                    className="btn btn-primary"
                    disabled={loading}
                  >
                    {loading ? "Actualizando..." : "Actualizar Usuario"}
                  </button>
                  <button 
                    type="button" 
                    className="btn btn-secondary"
                    onClick={cancelEdit}
                    disabled={loading}
                  >
                    Cancelar
                  </button>
                </div>
              </form>
          </div>
        )}
      </div>

      {/* Modal de confirmación de eliminación */}
      {confirmDelete.open && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Confirmar Eliminación</h3>
            <p>
              ¿Está seguro que desea eliminar al usuario{' '}
              <strong>{confirmDelete.user?.nombre} {confirmDelete.user?.apellido}</strong>?
            </p>
            <p className="warning-text">Esta acción no se puede deshacer.</p>
            <div className="modal-actions">
              <button 
                className="btn btn-secondary"
                onClick={cancelDelete}
                disabled={confirmDelete.loading}
              >
                Cancelar
              </button>
              <button 
                className="btn btn-danger"
                onClick={handleDeleteUser}
                disabled={confirmDelete.loading}
              >
                {confirmDelete.loading ? 'Eliminando...' : 'Eliminar'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
      <Toast toasts={toasts} onClose={closeToast} />
    </section>
  );
}






