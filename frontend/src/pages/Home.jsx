import { useCallback, useEffect, useState } from "react";
import HomeHero from "../components/HomeHero.jsx";
import HomeBottom from "../components/HomeBottom.jsx";
import HomeSearchDock from "../components/HomeSearchDock.jsx";
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
      <section className="admin-home admin-home--landing">
        <div className="container">
          <div className="admin-home-logo" aria-hidden="true">
            <img src="/logo-utn.png" alt="" />
          </div>
          <h1>Bienvenido a la consola UTN Conecta</h1>
          <p>Selecciona una seccion desde el panel lateral para comenzar.</p>
        </div>
      </section>
    );
  }

  return (
    <>
      <HomeHero />

      <HomeBottom />

      <HomeSearchDock />
    </>
  );
}
