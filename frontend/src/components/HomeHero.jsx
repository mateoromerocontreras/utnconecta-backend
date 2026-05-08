import "../styles/hero.css";

export default function HomeHero(){
  return (
    <section className="hero">
      <div className="hero-grid">
        {/* Columna izquierda */}
        <div className="hero-left">
          <div className="container">
            <h1 className="hero-title">Encuentra tu <br/> pasantía</h1>
            <p className="hero-sub">Comienza tu trayecto profesional</p>

            <div className="hero-promo">
               <img
                  src="/icons/hat.svg"
                  className="promo-icon"
                  alt=""
                  aria-hidden="true"
                />
              <span>Impulsá tu carrera profesional</span>
            </div>
          </div>
        </div>

        {/* Columna derecha: imagen */}
        <div className="hero-right" role="img" aria-label="Personas trabajando en una mesa" />
        
      </div>

    </section>
  );
}
