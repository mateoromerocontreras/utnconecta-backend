import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import HomeHero from "../components/HomeHero.jsx";
import HomeBottom from "../components/HomeBottom.jsx";
import "../styles/home-admin.css";

function getStoredItem(key) {
  const persisted = localStorage.getItem(key);
  if (persisted !== null) return persisted;
  return sessionStorage.getItem(key);
}

export default function Home() {
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

  if (isAdmin) {
    return (
      <section className="admin-home">
        <div className="container">
          <div className="admin-home-head">
            <h2>Gestión del sitio</h2>
            <p>Accedé rápidamente a las herramientas de administración disponibles para vos.</p>
          </div>
          <div className="admin-home-grid">
            <Link to="/registrarse" className="admin-home-card">
              <span className="admin-home-icon" aria-hidden="true">
                <img src="/icons/profile.svg" alt="" />
              </span>
              <div className="admin-home-body">
                <h3>Gestión de usuarios</h3>
                <p>Registrá nuevas cuentas y administrá los roles del equipo.</p>
              </div>
              <span className="admin-home-arrow" aria-hidden="true">→</span>
            </Link>
          </div>
        </div>
      </section>
    );
  }

  return (
    <>
      <HomeHero />

      <HomeBottom />
    </>
  );
}
