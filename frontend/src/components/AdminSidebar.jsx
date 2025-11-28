import { NavLink } from "react-router-dom";
import "../styles/admin-layout.css";

const navItems = [
  { to: "/pasantias", label: "Pasantias" },
  { to: "/empresas", label: "Empresas" },
  { to: "/carreras", label: "Carreras" },
  { to: "/administrar-usuarios", label: "Gestion de usuarios" },
];

export default function AdminSidebar({ user }) {
  const renderLinkClass = ({ isActive }) =>
    `admin-sidebar__link${isActive ? " is-active" : ""}`;
  const displayName =
    user?.rol === "ADMINISTRADOR"
      ? "Administrador"
      : user?.username ?? "Usuario";

  return (
    <aside className="admin-sidebar" aria-label="Navegacion principal de administrador">
      <div className="admin-sidebar__brand">
        <img src="/logo-utn.png" alt="" aria-hidden="true" />
        <div>
          <p className="admin-sidebar__brand-name">UTN Conecta</p>
          <span className="admin-sidebar__brand-role">{displayName}</span>
        </div>
      </div>

      <div className="admin-sidebar__home">
        <NavLink to="/" className={renderLinkClass} end>
          Inicio
        </NavLink>
      </div>

      <nav className="admin-sidebar__nav">
        <p className="admin-sidebar__nav-label">Secciones</p>
        {navItems.map((item) => (
          <NavLink key={item.to} to={item.to} className={renderLinkClass} end>
            {item.label}
          </NavLink>
        ))}
      </nav>

      <div className="admin-sidebar__account">
        <p className="admin-sidebar__nav-label">Cuenta</p>
        <NavLink to="/perfil" className={renderLinkClass} end>
          Perfil
        </NavLink>
      </div>
    </aside>
  );
}
