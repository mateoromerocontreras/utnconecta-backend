# 🧪 Test: Crear Pasantía

Este test verifica que el endpoint `POST /api/pasantias/crear` funciona correctamente y que la pasantía se guarda en la base de datos con el estado inicial correcto.

## 📋 Prerequisitos

1. **Docker y MySQL corriendo:**
   ```bash
   cd script_bd
   docker-compose up -d
   ```

2. **Aplicación Spring Boot corriendo:**
   ```bash
   cd pasantias
   ./mvnw spring-boot:run
   ```

3. **Base de datos actualizada:**
   - La base de datos debe tener el nuevo schema con las tablas `Pasantia`, `Pasantia_Carrera` y `Postulacion` actualizadas

## 🚀 Ejecutar el Test

### Opción 1: Script automatizado

```bash
./test_crear_pasantia.sh
```

Este script:
- ✅ Envía un POST al endpoint `/api/pasantias/crear`
- ✅ Muestra el request body formateado
- ✅ Muestra el HTTP status code
- ✅ Muestra la respuesta formateada
- ✅ Verifica que el estado inicial sea `PENDIENTE_DE_APROBACION`

### Opción 2: cURL manual

```bash
curl -X POST http://localhost:8080/api/pasantias/crear \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Desarrollador Backend Spring Boot",
    "puestoACubrir": "Desarrollador Junior Backend",
    "ciudad": "Santa Fe",
    "modalidad": "Híbrida",
    "asignacionEstimulo": 55000.0,
    "cantidadDePasantes": 2,
    "fechaPublicacion": "2025-11-01",
    "fechaCaducidad": "2026-02-01",
    "idEmpresa": 1,
    "idsCarreras": [6],
    "emailContacto": "rrhh@biofarmaweb.com.ar"
  }' | jq '.'
```

## ✅ Respuesta Esperada

**HTTP Status:** `201 Created`

**Response Body:**
```json
{
  "codigo": 0,
  "mensaje": "Pasantía creada exitosamente. Estado inicial: PENDIENTE_DE_APROBACION",
  "data": {
    "idPasantia": 4,
    "titulo": "Desarrollador Backend Spring Boot",
    "puestoACubrir": "Desarrollador Junior Backend",
    "ciudad": "Santa Fe",
    "modalidad": "Híbrida",
    "asignacionEstimulo": 55000.0,
    "cantidadDePasantes": 2,
    "fechaPublicacion": "2025-11-01",
    "fechaCaducidad": "2026-02-01",
    "estado": "PENDIENTE_DE_APROBACION",
    "emailContacto": "rrhh@biofarmaweb.com.ar",
    "idEmpresa": 1,
    "nombreEmpresa": "BIOFARMA S.A",
    "cantidadPostulaciones": 0,
    "aceptaPostulaciones": false,
    "diasRestantes": 93
  }
}
```

### 🔍 Puntos Clave a Verificar:

1. ✅ **Estado inicial:** `"estado": "PENDIENTE_DE_APROBACION"`
2. ✅ **HTTP 201 Created:** Recurso creado correctamente
3. ✅ **idPasantia generado:** ID autoincremental asignado
4. ✅ **Campos calculados:**
   - `cantidadPostulaciones: 0`
   - `aceptaPostulaciones: false` (porque está en PENDIENTE_DE_APROBACION)
   - `diasRestantes: XX` (días hasta fecha_caducidad)
5. ✅ **Empresa asociada:** `nombreEmpresa` se incluye (desnormalizado)

## 🔍 Verificar en la Base de Datos

### Opción 1: Script SQL automatizado

```bash
cd script_bd
docker exec -i script_bd-db-1 mysql -uroot -prootpassword db_pasantias < sql/verificar_pasantias.sql
```

### Opción 2: MySQL CLI

```bash
docker exec -it script_bd-db-1 mysql -uroot -prootpassword db_pasantias
```

Luego ejecutar:

```sql
-- Ver la última pasantía creada
SELECT 
    p.id_pasantia,
    p.titulo,
    p.estado,
    p.ciudad,
    p.modalidad,
    e.nombre AS empresa,
    p.fecha_creacion
FROM Pasantia p
INNER JOIN Empresa e ON p.id_empresa = e.id_empresa
ORDER BY p.fecha_creacion DESC
LIMIT 1;

-- Ver carreras asociadas
SELECT 
    p.titulo,
    c.nombre AS carrera
FROM Pasantia p
INNER JOIN Pasantia_Carrera pc ON p.id_pasantia = pc.id_pasantia
INNER JOIN Carrera c ON pc.id_carrera = c.id_carrera
WHERE p.id_pasantia = (SELECT MAX(id_pasantia) FROM Pasantia);
```

**Resultado esperado:**
```
+-------------+------------------------------------+---------------------------+--------+-----------+----------------+---------------------+
| id_pasantia | titulo                             | estado                    | ciudad | modalidad | empresa        | fecha_creacion      |
+-------------+------------------------------------+---------------------------+--------+-----------+----------------+---------------------+
|           4 | Desarrollador Backend Spring Boot  | PENDIENTE_DE_APROBACION   | Santa Fe| Híbrida  | BIOFARMA S.A   | 2025-10-31 15:30:00 |
+-------------+------------------------------------+---------------------------+--------+-----------+----------------+---------------------+
```

## ❌ Casos de Error

### Error 400 - Validación

**Request con datos inválidos:**
```bash
curl -X POST http://localhost:8080/api/pasantias/crear \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Dev",
    "idEmpresa": 999,
    "idsCarreras": []
  }'
```

**Respuesta esperada:**
```json
{
  "codigo": -1,
  "mensaje": "La empresa con ID 999 no existe",
  "tipo": "VALIDACION"
}
```

### Error 409 - Regla de Negocio

**Request con fecha de caducidad anterior a hoy:**
```bash
curl -X POST http://localhost:8080/api/pasantias/crear \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Test",
    "fechaCaducidad": "2020-01-01",
    ...
  }'
```

## 📊 Estructura de la Base de Datos

### Tabla `Pasantia`
```sql
CREATE TABLE Pasantia (
    id_pasantia INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    puesto_a_cubrir VARCHAR(150) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    modalidad VARCHAR(50) NOT NULL,
    asignacion_estimulo FLOAT,
    cantidad_de_pasantes INT NOT NULL DEFAULT 1,
    fecha_publicacion DATE NOT NULL,
    fecha_caducidad DATE NOT NULL,
    estado ENUM('PUBLICADA', 'FINALIZADA', 'DADA_DE_BAJA', 'PENDIENTE_DE_APROBACION', 'EXPIRADA') 
           NOT NULL DEFAULT 'PENDIENTE_DE_APROBACION',
    email_contacto VARCHAR(100) NOT NULL,
    id_empresa INT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_empresa) REFERENCES Empresa(id_empresa) ON DELETE CASCADE
);
```

### Tabla `Pasantia_Carrera` (relación N:N)
```sql
CREATE TABLE Pasantia_Carrera (
    id_pasantia INT NOT NULL,
    id_carrera INT NOT NULL,
    PRIMARY KEY (id_pasantia, id_carrera),
    FOREIGN KEY (id_pasantia) REFERENCES Pasantia(id_pasantia) ON DELETE CASCADE,
    FOREIGN KEY (id_carrera) REFERENCES Carrera(id_carrera) ON DELETE CASCADE
);
```

## 🔄 Flujo Completo de Test

1. **Iniciar servicios:**
   ```bash
   # Terminal 1: MySQL
   cd script_bd && docker-compose up -d
   
   # Terminal 2: Spring Boot
   cd pasantias && ./mvnw spring-boot:run
   ```

2. **Ejecutar test:**
   ```bash
   ./test_crear_pasantia.sh
   ```

3. **Verificar en BD:**
   ```bash
   cd script_bd
   docker exec -i script_bd-db-1 mysql -uroot -prootpassword db_pasantias < sql/verificar_pasantias.sql
   ```

4. **Verificar estado:**
   - ✅ Estado debe ser `PENDIENTE_DE_APROBACION`
   - ✅ Debe existir registro en tabla `Pasantia`
   - ✅ Debe existir relación en tabla `Pasantia_Carrera`

## 📝 Notas Importantes

1. **Estado Inicial Automático:**
   - El usuario NO puede elegir el estado inicial
   - El Service siempre fuerza `PENDIENTE_DE_APROBACION`
   - Esto es una regla de negocio implementada en `PasantiaService.crearPasantia()`

2. **Validaciones Automáticas:**
   - Empresa debe existir
   - Carreras deben existir
   - Fecha de caducidad debe ser futura
   - Todos los campos obligatorios validados por Jakarta Validation

3. **Campos Calculados:**
   - `cantidadPostulaciones` - Se calcula en el mapper
   - `diasRestantes` - Diferencia entre hoy y fecha_caducidad
   - `aceptaPostulaciones` - Solo true si está PUBLICADA y no caducó

## 🎯 Próximos Tests

- [ ] Test de cambio de estado (PENDIENTE_DE_APROBACION → PUBLICADA)
- [ ] Test de actualizar pasantía
- [ ] Test de búsqueda con filtros
- [ ] Test de eliminar pasantía (soft delete)
- [ ] Test de crear postulación a pasantía publicada
