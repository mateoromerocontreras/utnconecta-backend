# Backend integration tests summary (04/05/2026)

## Scope
This summary covers the **TS-01..TS-06** integration tests implemented in:

- `pasantias/src/test/java/com/seminario/pasantias/PasantiasIntegrationTests.java`

## How they were executed

```bash
export JAVA_HOME=/usr/lib/jvm/jdk-25.0.2-oracle-x64
export PATH="$JAVA_HOME/bin:$PATH"
./pasantias/mvnw -f pasantias test -Dspring.profiles.active=test -Dtest=PasantiasIntegrationTests
```

## Overall result
- **Build**: SUCCESS
- **Test suite**: `com.seminario.pasantias.PasantiasIntegrationTests`
- **Tests run**: 6
- **Failures**: 0
- **Errors**: 0
- **Skipped**: 0

## Test-by-test results
- **TS-01 Register Internship**: PASS  
  Creates internship via `POST /pasantias/registrar` as `EMPRESA`; asserts created + `PENDIENTE_DE_APROBACION`.

- **TS-02 Submit Application**: PASS  
  Submits application via `POST /postulaciones/registrarPostulacion` for a `PUBLICADA` internship; asserts created + DB row exists.

- **TS-03 Company Visibility**: PASS  
  Fetches `GET /postulaciones/postulacionesMiEmpresa` as `EMPRESA`; asserts response does not contain applications for other companies’ internships.

- **TS-04 Finalize Cycle**: PASS  
  Executes `PUT /postulaciones/{id}/estado` as `EMPRESA`; asserts postulación becomes `CUBIERTA` and pasantía becomes `FINALIZADA`.

- **TS-05 Cross-Career Guard**: PASS  
  Submits application with career mismatch; asserts **400** with `codigo=-1` and message contains `"Carrera no permitida"`.

- **TS-06 Impersonation Guard**: PASS  
  Attempts to create internship for another company as `EMPRESA`; asserts **403** with `codigo=-1` and message contains `"No tienes permiso"`.

