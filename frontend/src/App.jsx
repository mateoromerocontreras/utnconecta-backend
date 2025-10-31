import { Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar.jsx";
import Home from "./pages/Home.jsx";
import Internships from "./pages/Internships.jsx";
import Login from "./pages/Login.jsx";
import Empresas from "./pages/Empresas.jsx";

// NUEVO:
import RegistrarEmpresa from "./pages/RegistrarEmpresa.jsx";
import RegistrarUsuario from "./pages/RegistrarUsuario.jsx";
import RegistrarEstudiante from "./pages/RegistrarEstudiante.jsx";
import CompletarPerfil from "./pages/CompletarPerfil.jsx";
import Carreras from "./pages/Carreras.jsx";
import RegistrarCarrera from "./pages/RegistrarCarrera.jsx";
import Perfil from "./pages/Perfil.jsx";
import ModificarPerfil from "./pages/ModificarPerfil.jsx";
import RegistrarPasantia from "./pages/RegistrarPasantia.jsx";

export default function App() {
  return (
    <div className="app">
      <Navbar />
      <main className="main">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/pasantias" element={<Internships />} />
          <Route path="/empresas" element={<Empresas />} />
          <Route path="/administrar-usuarios" element={<RegistrarUsuario />} />
          <Route path="/registrarse" element={<RegistrarEstudiante />} />
          <Route path="/login" element={<Login />} />
          <Route path="/empresa-list" element={<Empresas />} />
          <Route path="/registrar-empresa" element={<RegistrarEmpresa />} />
          <Route path="/carreras" element={<Carreras />} />
          <Route path="/registrar-carrera" element={<RegistrarCarrera />} />
          <Route path="/registrar-pasantia" element={<RegistrarPasantia />} />
          <Route path="/perfil" element={<Perfil />} />
          <Route path="/perfil/completar" element={<CompletarPerfil />} />
          <Route path="/perfil/modificar" element={<ModificarPerfil />} />
        </Routes>
      </main>
    </div>
  );
}
