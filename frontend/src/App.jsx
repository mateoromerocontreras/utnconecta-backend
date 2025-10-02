import { Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar.jsx";
import Home from "./pages/Home.jsx";
import Internships from "./pages/Internships.jsx";
import Companies from "./pages/Companies.jsx";
import Register from "./pages/Register.jsx";
import Login from "./pages/Login.jsx";
import Empresas from "./pages/Empresas.jsx";

// NUEVO:
import RegistrarEmpresa from "./pages/RegistrarEmpresa.jsx";

export default function App() {
  return (
    <div className="app">
      <Navbar />
      <main className="main">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/pasantias" element={<Internships />} />
          <Route path="/empresas" element={<Companies />} />
          <Route path="/registrarse" element={<Register />} />
          <Route path="/login" element={<Login />} />
          <Route path="/empresa-list" element={<Empresas />} />
          <Route path="/empresa" element={<RegistrarEmpresa />} />
        </Routes>
      </main>
    </div>
  );
}
