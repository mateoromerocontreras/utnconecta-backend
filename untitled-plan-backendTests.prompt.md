## Plan: Backend Tests — Pasantías Module

TL;DR - Add 4 focused integration tests (create internship, student apply, company retrieve applications, finalize flow) using real DTOs/endpoints discovered in the codebase; seed DB with provided SQL dump or @Sql scripts; use MockMvc + JUnit5 + transactional rollbacks.

**Steps**
1. Create integration test class `PasantiasIntegrationTests` under `src/test/java`.
   - *depends on step 2.*
2. Add DB seeding for tests:
   - Option A (preferred): Use `script_bd/sql/schema.sql` via `@Sql(scripts = "/script_bd/sql/schema.sql")` on test class.
   - Option B: programmatic seeding in `@BeforeEach` using mappers/services to insert 2 empresas and 3 estudiantes.
3. Implement 4 tests (JUnit5 + MockMvc):
   - TEST 1 — Internship Creation (empresa)
     - Endpoint: `POST /pasantias/registrar` with `PasantiaRequestDTO` (fields: `titulo`, `puestoACubrir`, `ciudad`, `modalidad`, `asignacionEstimulo`, `cantidadDePasantes`, `fechaCaducidad`, `emailContacto`, `idsCarreras`, `idEmpresa`)
     - Assert HTTP 201 and DB `estado = PENDIENTE_DE_APROBACION` (note: controller may require ADMIN/EMPRESA; use mock auth).
   - TEST 2 — Student Application
     - Endpoint: `POST /postulaciones/registrarPostulacion` with `PostulacionRequestDTO` (`idPasantia`, `idEstudiante`, `fechaPostulacion`, `fechaInicioContrato`, `duracionMeses`, `estado`)
     - Preconditions: target pasantia in state `PUBLICADA`.
     - Assert HTTP 201 and `postulacion.estado = BORRADOR` or `PENDIENTE_APROBACION` as per DTO default (code shows BORRADOR default); assert FK mapping to `id_pasantia` and `estudiante_id`.
   - TEST 3 — Retrieve Applications (empresa)
     - Endpoint: `GET /postulaciones/postulacionesMiEmpresa` (or `GET /postulaciones/pasantia/{pasantiaId}` to scope)
     - Auth: `EMPRESA` user owning the pasantia.
     - Assert HTTP 200 and response contains only applications for that empresa's pasantias.
   - TEST 4 — Closing the Loop (finalization)
     - Update specific postulacion state via `PUT` endpoint (controller exposes `actualizarEstado` via DTO) — use `ActualizarEstadoPostulacionDTO` with `estado=CUBIERTA`, `fechaInicioContrato`, `duracionMeses`.
     - Then call `PUT /pasantias/{id}/finalizar` or service method to set pasantia `estado = FINALIZADA`.
     - Assert DB reflects `postulacion.estado = CUBIERTA` and `pasantia.estado = FINALIZADA`. Assert no new applications accepted (attempt to POST -> 4xx).
4. Test setup & auth
   - Use `@WithMockUser(roles = "EMPRESA")` or mock `SecurityContextHolder` if controllers rely on `SecurityService`.
   - Alternatively mock JWT via test config if real token parsing is enforced.
5. Transactional isolation & cleanup
   - Annotate tests with `@Transactional` and `@Rollback` or use `@TestExecutionListeners` with `@Sql` and `@DirtiesContext` to ensure DB hygiene.
6. Assertions & helpers
   - Use repositories/mappers (e.g., `PasantiaMapper`, `PostulacionMapper`) to assert DB state after requests.
   - Use DTO-to-entity mappers (`PasantiaMapperUtil`, `PostulacionMapperUtil`) for constructing request bodies.

**Relevant files**
- [pasantias/src/main/java/com/seminario/pasantias/entity/Pasantia.java](pasantias/src/main/java/com/seminario/pasantias/entity/Pasantia.java) — entity fields and `estado` enum reference
- [pasantias/src/main/java/com/seminario/pasantias/entity/Postulacion.java](pasantias/src/main/java/com/seminario/pasantias/entity/Postulacion.java)
- [pasantias/src/main/java/com/seminario/pasantias/entity/EstadoPasantia.java](pasantias/src/main/java/com/seminario/pasantias/entity/EstadoPasantia.java)
- [pasantias/src/main/java/com/seminario/pasantias/dto/request/PasantiaRequestDTO.java](pasantias/src/main/java/com/seminario/pasantias/dto/request/PasantiaRequestDTO.java)
- [pasantias/src/main/java/com/seminario/pasantias/dto/request/PostulacionRequestDTO.java](pasantias/src/main/java/com/seminario/pasantias/dto/request/PostulacionRequestDTO.java)
- [pasantias/src/main/java/com/seminario/pasantias/controller/PasantiaController.java](pasantias/src/main/java/com/seminario/pasantias/controller/PasantiaController.java)
- [pasantias/src/main/java/com/seminario/pasantias/controller/PostulacionController.java](pasantias/src/main/java/com/seminario/pasantias/controller/PostulacionController.java)
- [script_bd/pasantias_dump.sql](script_bd/pasantias_dump.sql) — SQL seed option

**Verification**
1. Run the tests:

```bash
./mvnw -f pasantias test -DskipITs=false
```

2. Run a single test class:

```bash
./mvnw -f pasantias -Dtest=com.seminario.pasantias.PasantiasIntegrationTests test
```

3. Confirm DB state using mapper assertions in tests or by querying MySQL (localhost:3306, user=root, pass=root) after disabling rollback for debugging.

**Decisions / Assumptions**
- Security during tests: prefer `@WithMockUser` for role simulation unless controllers require full JWT verification.
- Use `script_bd/pasantias_dump.sql` for deterministic seeding; fallback to programmatic seeding if SQL causes environment-specific failures.
- Initial pasantia creation controller sets `PENDIENTE_DE_APROBACION` by default; an admin approve step may be needed to make it `PUBLICADA` before student applications.

**Further Considerations**
1. Do you prefer SQL-based seeding (`script_bd/pasantias_dump.sql`) or Java-based programmatic seeding in `@BeforeEach`? Recommendation: SQL for speed and determinism.
2. If you want, I can now generate the test class `PasantiasIntegrationTests` with the 4 tests and seed scripts—approve to proceed.
