package com.seminario.pasantias;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seminario.pasantias.dto.request.PasantiaRequestDTO;
import com.seminario.pasantias.dto.request.ActualizarEstadoPostulacionDTO;
import com.seminario.pasantias.dto.request.PostulacionRequestDTO;
import com.seminario.pasantias.entity.Empresa;
import com.seminario.pasantias.entity.EstadoPasantia;
import com.seminario.pasantias.entity.EstadoPostulacion;
import com.seminario.pasantias.entity.Pasantia;
import com.seminario.pasantias.entity.Postulacion;
import com.seminario.pasantias.persistence.PasantiaMapper;
import com.seminario.pasantias.persistence.PostulacionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(
        scripts = "classpath:sql/schema.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
)
class PasantiasIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasantiaMapper pasantiaMapper;

    @Autowired
    private PostulacionMapper postulacionMapper;

    @Test
    @WithMockUser(username = "biofarma_user", roles = "EMPRESA")
    void TS_01_registerInternship_shouldCreatePublishedInternship() throws Exception {
        PasantiaRequestDTO request = new PasantiaRequestDTO();
        request.setTitulo("Pasantía QA TS-01");
        request.setPuestoACubrir("Backend Junior");
        request.setCiudad("Córdoba");
        request.setModalidad("Híbrida");
        request.setAsignacionEstimulo(75000f);
        request.setCantidadDePasantes(1);
        request.setFechaPublicacion(LocalDate.now());
        request.setFechaCaducidad(LocalDate.now().plusDays(30));
        request.setIdEmpresa(1);
        request.setIdsCarreras(List.of(6));
        request.setEmailContacto("rrhh@biofarmaweb.com.ar");

        MvcResult result = mockMvc.perform(
                        post("/pasantias/registrar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(0))
                .andExpect(jsonPath("$.data.estado").value("PUBLICADA"))
                .andReturn();

        Integer createdId = extractDataIdAsInt(result);
        assertThat(createdId).isNotNull();

        Pasantia created = pasantiaMapper.findById(createdId).orElseThrow();
        assertThat(created.getEstado()).isEqualTo(EstadoPasantia.PUBLICADA);
        assertThat(created.getEmpresa()).isNotNull();
        assertThat(created.getEmpresa().getIdEmpresa()).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = "estudiante1", roles = "ESTUDIANTE")
    void TS_02_submitApplication_shouldCreatePostulacionWhenInternshipIsPublished() throws Exception {
        Integer pasantiaId = createPublishedInternshipForTest();

        PostulacionRequestDTO request = new PostulacionRequestDTO();
        request.setFechaPostulacion(LocalDate.now());
        request.setEstado(EstadoPostulacion.PENDIENTE_APROBACION);
        request.setIdPasantia(pasantiaId);
        // estudianteId=1 tiene especialidad "Ingeniería en Sistemas" (seed) y coincide con carreraId=6
        request.setIdEstudiante(1);

        mockMvc.perform(
                        post("/postulaciones/registrarPostulacion")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(0))
                .andExpect(jsonPath("$.data.estado").value("PENDIENTE_APROBACION"));

        assertThat(postulacionMapper.existsByEstudianteAndPasantia(1, pasantiaId)).isTrue();
        assertThat(pasantiaMapper.findById(pasantiaId).orElseThrow().getEstado()).isEqualTo(EstadoPasantia.PUBLICADA);
    }

    @Test
    @WithMockUser(username = "biofarma_user", roles = "EMPRESA")
    void TS_03_companyVisibility_shouldOnlySeeApplicationsForOwnInternships() throws Exception {
        Integer pasantiaBiofarmaId = createPublishedInternship(1, "Pasantía TS-03 BIOFARMA");
        Integer pasantiaIndacorId = createPublishedInternship(2, "Pasantía TS-03 INDACOR");

        createPostulacion(pasantiaBiofarmaId, 2, EstadoPostulacion.PENDIENTE_APROBACION);
        createPostulacion(pasantiaIndacorId, 2, EstadoPostulacion.PENDIENTE_APROBACION);

        MvcResult result = mockMvc.perform(get("/postulaciones/postulacionesMiEmpresa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode data = root.path("data");
        assertThat(data.isArray()).isTrue();
        assertThat(data.size()).isGreaterThanOrEqualTo(1);

        boolean containsOurPasantia = false;
        for (JsonNode item : data) {
            int idPasantia = item.path("idPasantia").asInt();
            String nombreEmpresa = item.path("nombreEmpresa").asText();
            assertThat(idPasantia).isNotEqualTo(pasantiaIndacorId);
            assertThat(nombreEmpresa).isEqualTo("BIOFARMA S.A");
            if (idPasantia == pasantiaBiofarmaId) {
                containsOurPasantia = true;
            }
        }
        assertThat(containsOurPasantia).isTrue();
    }

    @Test
    @WithMockUser(username = "biofarma_user", roles = "EMPRESA")
    void TS_04_finalizeCycle_shouldCoverPostulacionAndFinalizePasantiaAtomically() throws Exception {
        Integer pasantiaId = createPublishedInternship(1, "Pasantía TS-04");
        Integer postulacionId = createPublishedPostulacion(pasantiaId, 2);

        ActualizarEstadoPostulacionDTO request = new ActualizarEstadoPostulacionDTO();
        request.setEstado(EstadoPostulacion.CUBIERTA);
        request.setFechaInicioContrato(LocalDate.now().plusDays(7));
        request.setDuracionMeses(6);

        mockMvc.perform(
                        put("/postulaciones/{id}/estado", postulacionId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(0))
                .andExpect(jsonPath("$.data.estado").value("CUBIERTA"));

        assertThat(pasantiaMapper.findById(pasantiaId).orElseThrow().getEstado()).isEqualTo(EstadoPasantia.FINALIZADA);
        assertThat(postulacionMapper.findById(postulacionId).orElseThrow().getEstado()).isEqualTo(EstadoPostulacion.CUBIERTA);
    }

    @Test
    @WithMockUser(username = "estudiante1", roles = "ESTUDIANTE")
    void TS_05_crossCareerGuard_shouldRejectWhenCareerMismatch() throws Exception {
        // estudianteId=2 (María López) tiene especialidad "Ingeniería Industrial" (seed)
        // esta pasantía solo permite carreraId=6 ("Ingeniería en Sistemas")
        Integer pasantiaId = createPublishedInternship(1, "Pasantía TS-05 Sistemas only");

        PostulacionRequestDTO request = new PostulacionRequestDTO();
        request.setFechaPostulacion(LocalDate.now());
        request.setEstado(EstadoPostulacion.PENDIENTE_APROBACION);
        request.setIdPasantia(pasantiaId);
        request.setIdEstudiante(2);

        mockMvc.perform(
                        post("/postulaciones/registrarPostulacion")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value(-1))
                .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("Carrera no permitida")));
    }

    @Test
    @WithMockUser(username = "biofarma_user", roles = "EMPRESA")
    void TS_06_impersonationGuard_companyShouldNotCreateInternshipForOtherCompany() throws Exception {
        PasantiaRequestDTO request = new PasantiaRequestDTO();
        request.setTitulo("Pasantía TS-06 Impersonation");
        request.setPuestoACubrir("Backend Junior");
        request.setCiudad("Córdoba");
        request.setModalidad("Híbrida");
        request.setAsignacionEstimulo(75000f);
        request.setCantidadDePasantes(1);
        request.setFechaPublicacion(LocalDate.now());
        request.setFechaCaducidad(LocalDate.now().plusDays(30));

        // biofarma_user pertenece a empresaId=1 en el seed; intentamos crear para empresaId=2 (INDACOR)
        request.setIdEmpresa(2);
        request.setIdsCarreras(List.of(6));
        request.setEmailContacto("rrhh@biofarmaweb.com.ar");

        mockMvc.perform(
                        post("/pasantias/registrar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value(-1))
                .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("No tienes permiso")));
    }

    private Integer extractDataIdAsInt(MvcResult result) throws Exception {
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode idNode = root.path("data").path("idPasantia");
        if (idNode.isInt()) {
            return idNode.asInt();
        }
        if (idNode.isTextual()) {
            return Integer.valueOf(idNode.asText());
        }
        return null;
    }

    private Integer createPublishedInternshipForTest() {
        Pasantia pasantia = new Pasantia();
        pasantia.setTitulo("Pasantía QA TS-02");
        pasantia.setPuestoACubrir("Backend Junior");
        pasantia.setCiudad("Córdoba");
        pasantia.setModalidad("Híbrida");
        pasantia.setAsignacionEstimulo(75000f);
        pasantia.setCantidadDePasantes(1);
        pasantia.setFechaPublicacion(LocalDate.now());
        pasantia.setFechaCaducidad(LocalDate.now().plusDays(30));
        pasantia.setEstado(EstadoPasantia.PUBLICADA);
        pasantia.setEmailContacto("rrhh@biofarmaweb.com.ar");

        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(1);
        pasantia.setEmpresa(empresa);

        pasantiaMapper.insert(pasantia);
        pasantiaMapper.insertPasantiaCarrera(pasantia.getIdPasantia(), 6);
        return pasantia.getIdPasantia();
    }

    private Integer createPublishedInternship(Integer empresaId, String titulo) {
        Pasantia pasantia = new Pasantia();
        pasantia.setTitulo(titulo);
        pasantia.setPuestoACubrir("Backend Junior");
        pasantia.setCiudad("Córdoba");
        pasantia.setModalidad("Híbrida");
        pasantia.setAsignacionEstimulo(75000f);
        pasantia.setCantidadDePasantes(1);
        pasantia.setFechaPublicacion(LocalDate.now());
        pasantia.setFechaCaducidad(LocalDate.now().plusDays(30));
        pasantia.setEstado(EstadoPasantia.PUBLICADA);
        pasantia.setEmailContacto("rrhh@biofarmaweb.com.ar");

        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(empresaId);
        pasantia.setEmpresa(empresa);

        pasantiaMapper.insert(pasantia);
        pasantiaMapper.insertPasantiaCarrera(pasantia.getIdPasantia(), 6);
        return pasantia.getIdPasantia();
    }

    private Integer createPublishedPostulacion(Integer pasantiaId, Integer estudianteId) {
        return createPostulacion(pasantiaId, estudianteId, EstadoPostulacion.PUBLICADA);
    }

    private Integer createPostulacion(Integer pasantiaId, Integer estudianteId, EstadoPostulacion estado) {
        Postulacion postulacion = new Postulacion();
        postulacion.setFechaPostulacion(LocalDate.now());
        postulacion.setEstado(estado);

        Pasantia pasantia = new Pasantia();
        pasantia.setIdPasantia(pasantiaId);
        postulacion.setPasantia(pasantia);

        com.seminario.pasantias.entity.Estudiante estudiante = new com.seminario.pasantias.entity.Estudiante();
        estudiante.setIdEstudiante(estudianteId);
        postulacion.setEstudiante(estudiante);

        postulacionMapper.insert(postulacion);
        return postulacion.getIdPostulacion();
    }
}
