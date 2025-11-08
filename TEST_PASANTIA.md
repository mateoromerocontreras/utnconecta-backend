# Test de Endpoint POST /pasantias/registrar

## Prerequisitos
1. Base de datos MySQL corriendo (puerto 3306)
2. Aplicación Spring Boot corriendo (puerto 8080)

## Iniciar servicios

### Base de datos:
```bash
cd script_bd
docker-compose up -d
```

### Aplicación Spring Boot:
```bash
cd pasantias
mvn spring-boot:run
# O si ya está compilado:
java -jar target/pasantias-0.0.1-SNAPSHOT.jar
```

## Test con PowerShell

```powershell
.\test_pasantia_endpoint.ps1
```

## Test con cURL (Windows)

```bash
curl -X POST http://localhost:8080/pasantias/registrar ^
  -H "Content-Type: application/json" ^
  -d @test_pasantia.json
```

## Test con cURL (Linux/Mac)

```bash
curl -X POST http://localhost:8080/pasantias/registrar \
  -H "Content-Type: application/json" \
  -d @test_pasantia.json
```

## Verificar en Base de Datos

### Con Docker:
```bash
docker exec -it db_pasantias mysql -uroot -pmy-secret-pw db_pasantias -e "SELECT * FROM Pasantia ORDER BY id_pasantia DESC LIMIT 1;"
```

### Con MySQL Client:
```sql
USE db_pasantias;
SELECT 
    id_pasantia,
    titulo,
    puesto_a_cubrir,
    ciudad,
    modalidad,
    conocimientos,
    otros_requisitos,
    beneficios,
    estado,
    fecha_publicacion,
    fecha_caducidad
FROM Pasantia 
ORDER BY id_pasantia DESC 
LIMIT 1;
```

## Payload de Ejemplo

El archivo `test_pasantia.json` contiene un ejemplo válido con:
- Empresa ID: 1 (BIOFARMA S.A - existe en BD)
- Carrera ID: 6 (Ingeniería en Sistemas - existe en BD)
- Todos los campos requeridos
- Los nuevos campos: conocimientos, otrosRequisitos, beneficios

## Respuesta Esperada

### Éxito (201 Created):
```json
{
  "codigo": 0,
  "mensaje": "Pasantía registrada exitosamente",
  "data": {
    "idPasantia": 4,
    "titulo": "Desarrollador Full Stack",
    "puestoACubrir": "Desarrollador Full Stack Junior",
    "ciudad": "Córdoba",
    "modalidad": "Híbrida",
    "asignacionEstimulo": 55000.0,
    "cantidadDePasantes": 2,
    "fechaPublicacion": "2025-01-15",
    "fechaCaducidad": "2025-04-15",
    "estado": "PENDIENTE_DE_APROBACION",
    "emailContacto": "rrhh@test.com",
    "conocimientos": "Java, Spring Boot, React, MySQL, Git",
    "otrosRequisitos": "Estudiante avanzado, disponibilidad part-time",
    "beneficios": "Capacitación, ambiente de trabajo dinámico, posibilidad de continuidad",
    "idEmpresa": 1,
    "nombreEmpresa": "BIOFARMA S.A",
    "cantidadPostulaciones": 0,
    "aceptaPostulaciones": false,
    "diasRestantes": 90
  }
}
```

### Error (400 Bad Request):
```json
{
  "codigo": -1,
  "mensaje": "Error de validación o negocio"
}
```

