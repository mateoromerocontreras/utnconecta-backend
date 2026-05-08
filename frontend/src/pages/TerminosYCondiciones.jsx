import "../styles/legal.css";

const sections = [
  {
    title: "1. Aceptación de los términos",
    body: [
      "El acceso y uso de UTN Conecta implica la aceptación de estos Términos y Condiciones. Si no estás de acuerdo con alguna parte, debés abstenerte de utilizar la plataforma.",
      "La plataforma tiene como finalidad facilitar la publicación, búsqueda y gestión de oportunidades académicas y laborales vinculadas a la comunidad universitaria."
    ]
  },
  {
    title: "2. Uso permitido",
    body: [
      "Te comprometés a proporcionar información veraz, actualizada y completa cuando completes formularios, crees tu perfil o participes de procesos de postulación.",
      "No está permitido usar la plataforma para fines ilícitos, fraudulentos, abusivos, discriminatorios o que puedan afectar la seguridad, disponibilidad o integridad del servicio."
    ]
  },
  {
    title: "3. Cuentas y seguridad",
    body: [
      "Cada usuario es responsable de mantener la confidencialidad de sus credenciales y de toda actividad realizada desde su cuenta.",
      "Si detectás un acceso no autorizado o cualquier incidente de seguridad, debés notificarlo a la administración del sistema a la mayor brevedad posible."
    ]
  },
  {
    title: "4. Contenidos y responsabilidades",
    body: [
      "Las publicaciones de empresas, estudiantes o administradores son responsabilidad de quien las emite. UTN Conecta puede moderar, suspender o retirar contenido que infrinja estos términos o la normativa aplicable.",
      "La plataforma no garantiza la disponibilidad ininterrumpida del servicio ni la exactitud absoluta de toda información publicada por terceros."
    ]
  },
  {
    title: "5. Propiedad intelectual",
    body: [
      "Los elementos propios de la plataforma, su diseño, marcas, textos y desarrollos técnicos están protegidos por la normativa de propiedad intelectual aplicable.",
      "No podés copiar, modificar, distribuir o reutilizar esos elementos sin autorización expresa, salvo en los casos permitidos por la ley."
    ]
  },
  {
    title: "6. Modificaciones",
    body: [
      "UTN Conecta puede actualizar estos Términos y Condiciones para reflejar cambios operativos, legales o técnicos. La versión vigente será siempre la publicada en esta sección.",
      "El uso continuado de la plataforma después de una modificación implica la aceptación de los nuevos términos."
    ]
  }
];

export default function TerminosYCondiciones() {
  return (
    <section className="legal-page">
      <div className="container">
        <article className="legal-card">
          <p className="legal-eyebrow">Documentación legal</p>
          <h1 className="legal-title">Términos y condiciones</h1>
          <p className="legal-intro">
            Estos términos establecen las reglas generales de uso de UTN Conecta.
            Funcionan como un marco estándar de acceso, participación y responsabilidad para estudiantes, empresas y administradores.
          </p>

          {sections.map((section) => (
            <div className="legal-section" key={section.title}>
              <h2>{section.title}</h2>
              {section.body.map((paragraph) => (
                <p key={paragraph}>{paragraph}</p>
              ))}
            </div>
          ))}

          <div className="legal-note">
            Este contenido es informativo y debe complementarse con la redacción legal y administrativa que corresponda a la institución.
          </div>
        </article>
      </div>
    </section>
  );
}
