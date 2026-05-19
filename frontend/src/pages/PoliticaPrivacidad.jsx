import "../styles/legal.css";

const sections = [
  {
    title: "1. Información que recopilamos",
    body: [
      "Podemos recopilar datos que brindes directamente al registrarte, completar tu perfil, postularte a una pasantía o contactar con la plataforma.",
      "También podemos procesar datos técnicos básicos, como información del navegador, dirección IP, registros de uso y eventos de sesión, para administrar y proteger el servicio."
    ]
  },
  {
    title: "2. Finalidad del tratamiento",
    body: [
      "Utilizamos la información para autenticar usuarios, mostrar oportunidades, gestionar postulaciones, mejorar funcionalidades y enviar notificaciones vinculadas al servicio.",
      "En caso de comunicaciones administrativas o técnicas, también podremos usar tus datos para responder consultas, prevenir abusos y cumplir obligaciones legales o institucionales."
    ]
  },
  {
    title: "3. Base de legitimación",
    body: [
      "El tratamiento puede fundarse en tu consentimiento, en la ejecución de la relación de uso de la plataforma, en el cumplimiento de obligaciones legales o en intereses legítimos vinculados a la operación del sistema.",
      "Cuando la normativa aplicable requiera un consentimiento específico, lo solicitaremos de manera previa, informada y verificable."
    ]
  },
  {
    title: "4. Compartición de información",
    body: [
      "Podemos compartir información con áreas internas autorizadas, instituciones vinculadas al funcionamiento de la plataforma o proveedores tecnológicos que colaboren con la operación del servicio bajo obligaciones de confidencialidad.",
      "No vendemos datos personales. Solo realizamos transferencias cuando sean necesarias para prestar el servicio, cumplir obligaciones legales o proteger derechos y seguridad de la comunidad."
    ]
  },
  {
    title: "5. Conservación y seguridad",
    body: [
      "Conservamos los datos durante el tiempo necesario para cumplir las finalidades indicadas, las obligaciones institucionales y los plazos legales aplicables.",
      "Adoptamos medidas razonables de seguridad administrativas, técnicas y organizativas para proteger la información frente a accesos no autorizados, pérdida, alteración o divulgación indebida."
    ]
  },
  {
    title: "6. Derechos de las personas usuarias",
    body: [
      "Podés solicitar acceso, rectificación, actualización o eliminación de tus datos, así como ejercer cualquier otro derecho que reconozca la normativa vigente.",
      "Para ejercer esos derechos, debés comunicarte con la administración de la plataforma o con el canal institucional que corresponda, identificándote de forma suficiente."
    ]
  }
];

export default function PoliticaPrivacidad() {
  return (
    <section className="legal-page">
      <div className="container">
        <article className="legal-card">
          <p className="legal-eyebrow">Documentación legal</p>
          <h1 className="legal-title">Política de privacidad</h1>
          <p className="legal-intro">
            Esta política describe, en términos generales, cómo puede recolectarse, utilizarse y resguardarse la información personal dentro de UTN Conecta.
            Su redacción sigue un formato estándar y debe alinearse con la normativa de protección de datos aplicable.
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
            La política puede actualizarse para reflejar cambios normativos, técnicos o operativos. Recomendamos revisarla periódicamente.
          </div>
        </article>
      </div>
    </section>
  );
}
