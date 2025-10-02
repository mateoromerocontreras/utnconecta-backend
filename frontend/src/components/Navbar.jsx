import { Link, NavLink } from "react-router-dom";
import "../styles/navbar.css";

export default function Navbar(){
  return (
    <header className="nav">
      <div className="container nav-row">
        <Link to="/" className="brand">
          <img src="/logo-utn.png" alt="UTN Conecta" />
          <span>UTN CONECTA</span>
        </Link>
        <nav className="links">
          <NavLink to="/" end>Inicio</NavLink>
          <NavLink to="/pasantias">Pasantías</NavLink>
          <NavLink to="/empresas">Empresas</NavLink>
        </nav>
        <div className="actions">
          <Link to="/registrarse" className="btn-register">Registrarse</Link>
          <Link to="/login" className="btn login">Iniciar Sesión</Link>
        </div>
      </div>
    </header>
  );
}
