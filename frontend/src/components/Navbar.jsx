import { Link, NavLink } from "react-router-dom";
import { useState, useEffect, useCallback } from "react";
import "../styles/navbar.css";

function getStoredItem(key) {
  const persisted = localStorage.getItem(key);
  if (persisted !== null) return persisted;
  return sessionStorage.getItem(key);
}

export default function Navbar(){
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
    } catch (e) {
      console.error("Error parsing user info:", e);
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

  return (
    <header className="nav">
      <div className="container nav-row">
        <Link to="/" className="brand">
          <img src="/logo-utn.png" alt="UTN Conecta" />
          <span>UTN CONECTA</span>
        </Link>
        <nav className="links">
          <NavLink to="/" end>Inicio</NavLink>
          <NavLink to="/pasantias">Pasantias</NavLink>
          <NavLink to={user?.rol === "ADMINISTRADOR" ? "/empresa-list" : "/empresas"}>
            Empresas
          </NavLink>
          {user?.rol === "ADMINISTRADOR" && (
            <NavLink to="/carreras">Carreras</NavLink>
          )}
        </nav>
        <div className="actions">
          {user ? (
            <div className="user-menu">
              <span className="user-greeting">
                Hola, <strong>{user.username}</strong> ({user.rol})
              </span>
              <Link
                to="/perfil"
                className="profile-link"
                aria-label="Ver perfil"
              >
                <img src="/icons/profile.svg" alt="" aria-hidden="true" />
              </Link>
            </div>
          ) : (
            <>
              <Link to="/registrarse" className="btn-register">Registrarse</Link>
              <Link to="/login" className="btn login">Iniciar Sesion</Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
