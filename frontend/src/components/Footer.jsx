import { Link } from "react-router-dom";
import "../styles/footer.css";

export default function Footer() {
  return (
    <footer className="site-footer">
      <div className="container footer-grid">
        <div className="footer-brand">
          {/* Contact block requested for the public site footer. */}
          <p className="footer-title">UTN Conecta</p>
          <p className="footer-address">
            Maestro M. Lopez esq. Cruz Roja Argentina - Ciudad Universitaria<br />
            C.P. (X5016ZAA). Tel:+54-0351-598-6016 / Conmutador 598-6000 o 598-6001<br />
            Facultad Regional Córdoba
          </p>
        </div>

        <div className="footer-links">
          <p className="footer-heading">Legal</p>
          <Link to="/terminos-y-condiciones">Términos y condiciones</Link>
          <Link to="/politica-de-privacidad">Política de privacidad</Link>
        </div>
      </div>

      <div className="container footer-bottom">
        <span>Plataforma de vinculación académica y laboral.</span>
        <span>© {new Date().getFullYear()} UTN Conecta</span>
      </div>
    </footer>
  );
}
