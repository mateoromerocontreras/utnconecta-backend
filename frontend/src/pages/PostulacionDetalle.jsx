import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "../styles/pasantias.css";

const API = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/+$/, "");

export default function PostulacionDetalle() {
  const { pasantiaId } = useParams();
  const navigate = useNavigate();
  const [postulaciones, setPostulaciones] = useState([]);
  const [pasantia, setPasantia] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function load() {
      try {
        setLoading(true);
        setError("");
        const token = localStorage.getItem("authToken") || sessionStorage.getItem("authToken");
        
        const headers = {
          Accept: "application/json;charset=UTF-8",
          "Content-Type": "application/json;charset=UTF-8"
        };
        
        if (token) {
          headers.Authorization = `Bearer ${token}`;
        }

        // Fetch all postulaciones for the pasantia
        const resPostulaciones = await fetch(`${API}/postulaciones/pasantia/${pasantiaId}`, {
          headers
        });

        if (!resPostulaciones.ok) {
          throw new Error(`HTTP ${resPostulaciones.status}`);
        }

        const textPostulaciones = await resPostulaciones.text();
        const dataPostulaciones = JSON.parse(textPostulaciones);
        
        if (dataPostulaciones.codigo === 0 && dataPostulaciones.data && Array.isArray(dataPostulaciones.data)) {
          setPostulaciones(dataPostulaciones.data);
          
          // Fetch pasantia details if we have postulaciones
          if (dataPostulaciones.data.length > 0) {
            const pasantiaIdFromPost = dataPostulaciones.data[0].idPasantia;
            const resPasantia = await fetch(`${API}/pasantias/${pasantiaIdFromPost}`, {
              headers: {
                Accept: "application/json;charset=UTF-8",
                "Content-Type": "application/json;charset=UTF-8"
              }
            });
            
            if (resPasantia.ok) {
              const textPasantia = await resPasantia.text();
              const dataPasantia = JSON.parse(textPasantia);
              setPasantia(dataPasantia);
            }
          }
        } else {
          setPostulaciones([]);
        }
      } catch (err) {
        setError(err.message || "No se pudieron cargar las postulaciones.");
      } finally {
        setLoading(false);
      }
    }

    if (pasantiaId) {
      load();
    }
  }, [pasantiaId]);

  if (loading) {
    return (
      <section className="pasantias-page">
        <div className="container">
          <p>Cargando postulaciones...</p>
        </div>
      </section>
    );
  }

  if (error) {
    return (
      <section className="pasantias-page">
        <div className="container">
          <div className="emp-alert error">
            {error}
            <button className="link" onClick={() => navigate("/pasantias")}>
              Volver a pasantías
            </button>
          </div>
        </div>
      </section>
    );
  }

  const getEstadoBadgeClass = (estado) => {
    const estadoLower = estado?.toLowerCase() || "";
    if (estadoLower.includes("pendiente")) return "badge warning";
    if (estadoLower.includes("publicada") || estadoLower.includes("cubierta")) return "badge success";
    if (estadoLower.includes("finalizada")) return "badge";
    if (estadoLower.includes("borrador")) return "badge";
    return "badge";
  };

  return (
    <section className="pasantias-page">
      <div className="container">
        <header style={{ margin: "28px 0 2rem" }}>
          <button 
            className="btn btn-ghost" 
            onClick={() => navigate(`/pasantias/${pasantiaId}`)}
            style={{ marginBottom: "1rem" }}
          >
            ← Volver a Pasantía
          </button>
          <h1>Postulaciones</h1>
          {pasantia && (
            <p className="muted">
              {pasantia.titulo} · {postulaciones.length} {postulaciones.length === 1 ? "postulación" : "postulaciones"}
            </p>
          )}
        </header>

        {postulaciones.length === 0 ? (
          <div className="job-card" style={{ padding: "2rem", textAlign: "center" }}>
            <p>No hay postulaciones para esta pasantía.</p>
          </div>
        ) : (
          <>
            {pasantia && (
              <div className="job-card" style={{ padding: "1.5rem", marginBottom: "2rem" }}>
                <h2 style={{ fontSize: "1.2em", marginBottom: "1rem" }}>Información de la Pasantía</h2>
                <dl style={{ display: "grid", gap: "0.75rem", gridTemplateColumns: "auto 1fr" }}>
                  <dt style={{ fontWeight: "bold" }}>Título:</dt>
                  <dd>{pasantia.titulo}</dd>
                  {pasantia.empresa?.nombre && (
                    <>
                      <dt style={{ fontWeight: "bold" }}>Empresa:</dt>
                      <dd>{pasantia.empresa.nombre}</dd>
                    </>
                  )}
                  {pasantia.ciudad && (
                    <>
                      <dt style={{ fontWeight: "bold" }}>Ciudad:</dt>
                      <dd>{pasantia.ciudad}</dd>
                    </>
                  )}
                  {pasantia.modalidad && (
                    <>
                      <dt style={{ fontWeight: "bold" }}>Modalidad:</dt>
                      <dd>{pasantia.modalidad}</dd>
                    </>
                  )}
                </dl>
              </div>
            )}

            <div style={{ display: "grid", gap: "1.5rem" }}>
              {postulaciones.map((postulacion) => (
                <div key={postulacion.idPostulacion} className="job-card" style={{ padding: "2rem" }}>
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "start", marginBottom: "1.5rem" }}>
                    <div>
                      <h3 style={{ fontSize: "1.3em", marginBottom: "0.5rem" }}>
                        {postulacion.nombreEstudiante} {postulacion.apellidoEstudiante}
                      </h3>
                      <p className="muted" style={{ marginBottom: "0.5rem" }}>
                        Postulación #{postulacion.idPostulacion}
                      </p>
                      <span className={getEstadoBadgeClass(postulacion.estado)}>
                        {postulacion.estado}
                      </span>
                    </div>
                  </div>

                  <div style={{ display: "grid", gap: "1.5rem", gridTemplateColumns: "1fr 1fr" }}>
                    <div>
                      <h4 style={{ fontSize: "1em", marginBottom: "0.75rem", fontWeight: "bold" }}>Información de la Postulación</h4>
                      <dl style={{ display: "grid", gap: "0.5rem" }}>
                        {postulacion.fechaPostulacion && (
                          <>
                            <dt style={{ fontWeight: "600" }}>Fecha de Postulación:</dt>
                            <dd>{new Date(postulacion.fechaPostulacion).toLocaleDateString()}</dd>
                          </>
                        )}
                        {postulacion.fechaInicioContrato && (
                          <>
                            <dt style={{ fontWeight: "600" }}>Fecha de Inicio:</dt>
                            <dd>{new Date(postulacion.fechaInicioContrato).toLocaleDateString()}</dd>
                          </>
                        )}
                        {postulacion.duracionMeses && (
                          <>
                            <dt style={{ fontWeight: "600" }}>Duración:</dt>
                            <dd>{postulacion.duracionMeses} meses</dd>
                          </>
                        )}
                        {postulacion.observaciones && (
                          <>
                            <dt style={{ fontWeight: "600" }}>Observaciones:</dt>
                            <dd style={{ whiteSpace: "pre-wrap" }}>{postulacion.observaciones}</dd>
                          </>
                        )}
                      </dl>
                    </div>

                    <div>
                      <h4 style={{ fontSize: "1em", marginBottom: "0.75rem", fontWeight: "bold" }}>Información del Estudiante</h4>
                      <dl style={{ display: "grid", gap: "0.5rem" }}>
                        {postulacion.emailEstudiante && (
                          <>
                            <dt style={{ fontWeight: "600" }}>Email:</dt>
                            <dd>
                              <a href={`mailto:${postulacion.emailEstudiante}`}>
                                {postulacion.emailEstudiante}
                              </a>
                            </dd>
                          </>
                        )}
                        {postulacion.dniEstudiante && (
                          <>
                            <dt style={{ fontWeight: "600" }}>DNI:</dt>
                            <dd>{postulacion.dniEstudiante}</dd>
                          </>
                        )}
                        {postulacion.legajoEstudiante && (
                          <>
                            <dt style={{ fontWeight: "600" }}>Legajo:</dt>
                            <dd>{postulacion.legajoEstudiante}</dd>
                          </>
                        )}
                        {postulacion.telefonoEstudiante && (
                          <>
                            <dt style={{ fontWeight: "600" }}>Teléfono Celular:</dt>
                            <dd>{postulacion.telefonoEstudiante}</dd>
                          </>
                        )}
                        {postulacion.telefonoFijoEstudiante && (
                          <>
                            <dt style={{ fontWeight: "600" }}>Teléfono Fijo:</dt>
                            <dd>{postulacion.telefonoFijoEstudiante}</dd>
                          </>
                        )}
                        {postulacion.especialidadEstudiante && (
                          <>
                            <dt style={{ fontWeight: "600" }}>Especialidad:</dt>
                            <dd>{postulacion.especialidadEstudiante}</dd>
                          </>
                        )}
                        {(postulacion.calleEstudiante || postulacion.nroCalleEstudiante) && (
                          <>
                            <dt style={{ fontWeight: "600" }}>Dirección:</dt>
                            <dd>
                              {postulacion.calleEstudiante} {postulacion.nroCalleEstudiante}
                              {postulacion.barrioEstudiante && `, ${postulacion.barrioEstudiante}`}
                              {postulacion.localidadEstudiante && `, ${postulacion.localidadEstudiante}`}
                              {postulacion.provinciaEstudiante && `, ${postulacion.provinciaEstudiante}`}
                            </dd>
                          </>
                        )}
                      </dl>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </>
        )}
      </div>
    </section>
  );
}
