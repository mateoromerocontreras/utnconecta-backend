## Project summary (current state)

### Stack and versions
- **Backend**: Spring Boot `3.5.5` (`pasantias/pom.xml`)
- **Java**: **JDK 25** (`<java.version>25</java.version>` + `maven-compiler-plugin <release>25</release>`)
- **Build/Test**: Maven Wrapper (`pasantias/mvnw`), JUnit 5 (`spring-boot-starter-test`)
- **Security**: Spring Security (`spring-boot-starter-security`) + `spring-security-test` (tests)
- **DB access**: MyBatis `3.0.5` (`mybatis-spring-boot-starter`)
- **DB driver**: MySQL Connector/J (runtime) (`com.mysql:mysql-connector-j`)
- **API docs**: Springdoc OpenAPI `2.5.0` (`springdoc-openapi-starter-webmvc-ui`)
- **JWT**: JJWT `0.11.5` (`jjwt-api/impl/jackson`)
- **Coverage**: JaCoCo `0.8.14`
- **DB runtime**:
  - **Documented**: MySQL `8.0` via Docker Compose (`script_bd/docker-compose.yml`)
  - **Also supported**: MariaDB/MySQL service on host (works with the current test setup)

### What’s proven to work (automated integration tests)
Implemented and passing integration tests (real DB + MockMvc) in:
- `pasantias/src/test/java/com/seminario/pasantias/PasantiasIntegrationTests.java`

Scenarios covered (from `pasantias/backend_tests.md`):
- **TS-01 Register Internship**: `POST /pasantias/registrar` as `EMPRESA` creates pasantía in `PENDIENTE_DE_APROBACION`.
- **TS-02 Submit Application**: `POST /postulaciones/registrarPostulacion` as `ESTUDIANTE` creates postulación for a `PUBLICADA` pasantía (explicit `estado=PENDIENTE_APROBACION`).
- **TS-03 Company Visibility**: `GET /postulaciones/postulacionesMiEmpresa` as `EMPRESA` only returns postulaciones for that company’s pasantías.
- **TS-04 Finalize Cycle (atomic)**: `PUT /postulaciones/{id}/estado` as `EMPRESA` sets postulación to `CUBIERTA` (with contract fields) and sets pasantía to `FINALIZADA` in the same transaction.
- **TS-05 Cross-Career Guard**: mismatch between student `especialidad` and pasantía allowed careers rejects with **400** + `"Carrera no permitida"`.
- **TS-06 Impersonation Guard**: `EMPRESA` cannot create pasantía for a different `idEmpresa` (returns **403**).

Execution summary is recorded in:
- `test_summary_04052026.md`

### Main directories
- **`frontend/`**: React + Vite client
- **`pasantias/`**: Spring Boot backend (controllers/services/MyBatis mappers)
- **`script_bd/`**: DB docker-compose + SQL init (`script_bd/sql/`)
- **`docs/`**: diagrams and generated PlantUML assets

### Important files (practical entry points)
- **Backend**
  - `pasantias/pom.xml`: dependencies + compiler config (requires JDK 25)
  - `pasantias/src/main/resources/application.properties`: local DB config for running the app
  - `pasantias/src/test/resources/application-test.properties`: DB config used by tests
  - `pasantias/src/test/resources/sql/schema.sql`: test seed/reset script
  - `pasantias/src/test/java/com/seminario/pasantias/PasantiasIntegrationTests.java`: TS-01..TS-06 suite
- **DB**
  - `script_bd/docker-compose.yml`: MySQL 8.0 container setup
- **Docs**
  - `README.md`: project usage and links (Swagger, services)
  - `SECURITY.md`, `ENDPOINTS_SECURITY.md`, `SECURITY_ROADMAP.md`: security status + roadmap
  - `pasantias/backend_tests.md`: target test specification (TS-01..TS-06)

### Warnings / gotchas
- **JDK 25 is required for builds/tests**. If you run Maven under Java 21 you’ll get `release version 25 not supported`.
- **MariaDB/MySQL root auth mismatch** is common on Linux (root uses socket auth). Tests are configured to avoid relying on root login.
- **Test DB reset**: `pasantias/src/test/resources/sql/schema.sql` drops/recreates tables inside the configured test schema. Don’t point `application-test.properties` at a production database.
- **Security is marked “in development”** in `README.md` and security docs; hardening/role protection is still a known workstream.

### Next steps (recommended)
- **Security hardening**: align endpoint authorization with `ENDPOINTS_SECURITY.md`, add automated authorization tests.
- **Stabilize “Finalize Cycle” API contract**: the new endpoint `PUT /postulaciones/{id}/estado` should be documented in Swagger and validated against frontend needs.
- **Reduce noisy `System.out.println`** logging in controllers/services (replace with structured logging).
- **Testing ergonomics**:
  - set `JAVA_HOME` permanently in your shell to JDK 25
  - consider isolating tests with a dedicated DB/schema/container to avoid sharing a developer DB

