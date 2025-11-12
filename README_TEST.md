# Testing Pasantía with New Fields

This guide explains how to test the new fields (`conocimientos`, `otrosRequisitos`, `beneficios`) added to the Pasantía entity.

## Quick Start

### Option 1: Manual Testing (Recommended for Development)

1. **Start the database** (if not already running):
   ```bash
   cd script_bd
   docker-compose up -d db
   cd ..
   ```

2. **Start the backend** in one terminal:
   ```bash
   cd pasantias
   ./mvnw spring-boot:run -DskipTests
   ```

3. **Run the test** in another terminal:
   ```bash
   ./test_pasantia_quick.sh
   ```

### Option 2: Using the Dev Script

```bash
./start-backend-dev.sh
```

Then in another terminal:
```bash
./test_pasantia_quick.sh
```

## What the Test Does

The test script (`test_pasantia_quick.sh`) performs the following checks:

1. ✅ **Security Test**: Attempts to create a pasantía WITHOUT authentication (should fail with 401/403)
2. ✅ **Authentication**: Logs in to get a JWT token
3. ✅ **Create Pasantía**: Creates a new pasantía WITH the three new fields populated
4. ✅ **Database Verification**: Queries the database directly to confirm the fields are saved
5. ✅ **API Verification**: Retrieves the pasantía through the GET endpoint to confirm fields are returned

## New Fields

- **conocimientos** (TEXT): Required knowledge and skills
- **otrosRequisitos** (TEXT): Other requirements
- **beneficios** (TEXT): Benefits offered

## Expected Results

✅ **Security**: Endpoint requires authentication (401/403 without token)  
✅ **Creation**: Pasantía created successfully (201 status)  
✅ **Database**: All three new fields saved correctly  
✅ **API Response**: Fields returned in GET requests  

## Database Schema Update

The schema has been updated in `script_bd/sql/schema.sql`:

```sql
CREATE TABLE Pasantia (
    ...
    conocimientos TEXT,
    otros_requisitos TEXT,
    beneficios TEXT,
    ...
);
```

If your database doesn't have these columns, recreate it:

```bash
cd script_bd
docker-compose down
docker-compose up -d db
```

## Manual API Testing

### Create Pasantía (with authentication):

```bash
# 1. Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/iniciar-sesion \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}' \
  | jq -r '.data.token')

# 2. Create Pasantía
curl -X POST http://localhost:8080/api/pasantias/crear \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "titulo": "Desarrollador Backend",
    "puestoACubrir": "Junior Developer",
    "ciudad": "Santa Fe",
    "modalidad": "Híbrida",
    "asignacionEstimulo": 60000.0,
    "cantidadDePasantes": 2,
    "fechaPublicacion": "2025-11-01",
    "fechaCaducidad": "2026-02-01",
    "idEmpresa": 1,
    "idsCarreras": [1],
    "emailContacto": "rrhh@company.com",
    "conocimientos": "Java, Spring Boot, MySQL",
    "otrosRequisitos": "Teamwork, Docker basics",
    "beneficios": "Competitive salary, training, flexible hours"
  }' | jq '.'
```

### Get Pasantía:

```bash
curl -s http://localhost:8080/api/pasantias/1 | jq '.data | {
  id, titulo, conocimientos, otrosRequisitos, beneficios
}'
```

## Troubleshooting

### Backend won't start:
- Check if port 8080 is in use: `lsof -i :8080`
- Kill existing process: `kill -9 <PID>`

### Database connection error:
- Verify database is running: `docker ps | grep db_pasantias`
- Check credentials in `application.properties`

### Fields not saving:
- Verify database schema has the new columns
- Check backend logs for SQL errors

## Files Updated

- ✅ `entity/Pasantia.java` - Added 3 new fields
- ✅ `dto/request/PasantiaRequestDTO.java` - Added with validation
- ✅ `dto/response/PasantiaResponseDTO.java` - Added fields
- ✅ `dto/response/PasantiaDetalleDTO.java` - Added fields
- ✅ `persistence/PasantiaMapper.java` - Updated all SQL queries
- ✅ `resources/mapper/PasantiaMapper.xml` - Updated ResultMap and queries
- ✅ `util/PasantiaMapperUtil.java` - Updated all mapping methods
- ✅ `script_bd/sql/schema.sql` - Added columns to Pasantia table
- ✅ `controller/PasantiaController.java` - No changes needed (works through DTOs)
