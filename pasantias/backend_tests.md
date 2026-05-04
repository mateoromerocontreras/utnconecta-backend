## Technical Specification: Backend Integration Test Suite

### 1. Test Architecture & Environment
- **Strategy:** Full integration tests with real MyBatis mappers.
- **Stack:** JUnit 5, MockMvc, AssertJ, MyBatis mappers (use `PasantiaMapper` / `PostulacionMapper`) or `JdbcTemplate` for DB assertions.
- **Database Isolation:**
    - Use `@SpringBootTest` with `application-test.properties` (see `src/test/resources/application-test.properties`).
    - Annotate tests with `@Transactional` (and `@Rollback`) where appropriate; seed DB with `@Sql` before test methods to guarantee deterministic state.
- **Data Seeding:**
    - Use the repository-provided schema seed: `@Sql(scripts = "file:script_bd/sql/schema.sql", executionPhase = BEFORE_TEST_METHOD)`.
    - This file contains schema + targeted seed rows used by the tests.

---

### 2. High-Level Test Scenarios (The Suite)

#### Phase A: The Happy Path (Lifecycle Flow)
| ID | Scenario | Actor | Key Constraint |
| :--- | :--- | :--- | :--- |
| **TS-01** | **Register Internship** | `EMPRESA` | Creates pasantía with `EstadoPasantia.PENDIENTE_DE_APROBACION`.
| **TS-02** | **Submit Application** | `ESTUDIANTE` | Internship must be `PUBLICADA`. Test must explicitly send `estado = PENDIENTE_APROBACION` in the request DTO.
| **TS-03** | **Company Visibility** | `EMPRESA` | Only see applications for their pasantias.
| **TS-04** | **Finalize Cycle** | `EMPRESA` | Postulación `CUBIERTA` + Pasantía `FINALIZADA` updated atomically.

#### Phase B: Security & Business Rules (Negative Paths)
| ID | Scenario | Actor | Expected Failure |
| :--- | :--- | :--- | :--- |
| **TS-05** | **Cross-Career Guard** | `ESTUDIANTE` | **403/400** when career mismatch.
| **TS-06** | **Impersonation Guard**| `EMPRESA_A` | **403 Forbidden** when creating for another company.

---

### 3. Professional Implementation Details

#### Seed Logic (`script_bd/sql/schema.sql`)
Use the provided `script_bd/sql/schema.sql` (it creates schema and inserts targeted seed rows). Reference it in tests with:

```java
@Sql(scripts = "file:script_bd/sql/schema.sql", executionPhase = BEFORE_TEST_METHOD)
```

#### Test Logic Snippets (Conceptual Java)

**TEST 5: Cross-Career Restriction**
```java
@Test
@WithMockUser(username = "ana@derecho.com", roles = "ESTUDIANTE")
void student_ShouldNotApplyTo_InternshipOutsideHisCareer() throws Exception {
    // GIVEN: A 'Sistemas' internship (id: 50)
    // WHEN: Ana (from 'Derecho') tries to apply
        mockMvc.perform(post("/postulaciones/registrarPostulacion")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PostulacionRequestDTO(...))))
        // THEN: System rejects with Business Logic Error
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.mensaje").value(containsString("Carrera no permitida")));
}
```

**TEST 6: Ownership Enforcement**
```java
@Test
@WithMockUser(username = "techcorp_user", roles = "EMPRESA")
void company_ShouldNotCreateInternship_ForOtherCompany() throws Exception {
    // GIVEN: Request where idEmpresa is 20 (LegalMind), but auth is TechCorp
    PasantiaRequestDTO maliciousRequest = PasantiaRequestDTO.builder()
            .idEmpresa(20L) 
            .titulo("Hacked Post")
            .build();

    // WHEN/THEN: Expecting Forbidden or Conflict
        mockMvc.perform(post("/pasantias/registrar")
            .content(objectMapper.writeValueAsString(maliciousRequest)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.mensaje").exists())
        .andExpect(jsonPath("$.codigo").value(-1));
}
```

---

### 4. Senior QA Execution Checklist
1.  **MyBatis assertions:** Use `PasantiaMapper` and `PostulacionMapper` (or `JdbcTemplate`) to assert DB rows and enum values after MockMvc calls.
2.  **DTO / DB state:** Tests must explicitly set `estado = PENDIENTE_APROBACION` in `PostulacionRequestDTO` when creating an application. The DB schema no longer contains `BORRADOR` — schema default is `PENDIENTE_APROBACION`.
3.  **Response keys:** Controllers return `{codigo, mensaje, data}` — update JSON assertions to use `$.codigo`, `$.mensaje`, and `$.data`.
4.  **Logging:** Run Maven with `-Dorg.slf4j.simpleLogger.defaultLogLevel=info` to view SQL and verify affected rows.

### Run Command (Strict Context)
```bash
# Run with the 'test' profile (ensures `application-test.properties` is used)
./mvnw -f pasantias test -Dspring.profiles.active=test -Dtest=PasantiasIntegrationTests
```