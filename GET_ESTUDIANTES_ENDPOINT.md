# Endpoint getEstudiantes

## Descripción
Nuevo endpoint que reemplaza a `/listarEstudiantes` con funcionalidad mejorada para obtener estudiantes.

## URL
```
GET /estudiantes/getEstudiantes
```

## Parámetros de Request

| Parámetro | Tipo   | Requerido | Descripción                                    |
|-----------|--------|-----------|-----------------------------------------------|
| nombre    | String | No        | Nombre del estudiante a buscar (opcional)     |

## Funcionalidad

### 1. Sin parámetros
**Request:**
```
GET /estudiantes/getEstudiantes
```

**Response:**
- Devuelve una lista de todos los estudiantes activos
- Solo incluye datos básicos (no incluye username ni password del usuario)
- Formato: Array de EstudianteBasicResponse

### 2. Con parámetro nombre
**Request:**
```
GET /estudiantes/getEstudiantes?nombre=Juan
```

**Response:**
- Si encuentra el estudiante: Devuelve el objeto EstudianteBasicResponse
- Si no encuentra: Devuelve `Optional.empty()`

## Estructura de Respuesta (EstudianteBasicResponse)

```json
{
  "idEstudiante": 1,
  "dni": "12345678",
  "apellido": "Pérez",
  "nombre": "Juan",
  "especialidad": "Ingeniería en Sistemas",
  "nroLegajo": "12345",
  "calle": "San Martín",
  "nroCalle": 123,
  "barrio": "Centro",
  "localidad": "Córdoba",
  "provincia": "Córdoba",
  "email": "juan.perez@estudiantes.com",
  "telCelular": "351-123-4567",
  "telFijo": "351-456-7890",
  "activo": true,
  "fechaCreacion": "2023-10-19T10:30:00"
}
```

## Características de Seguridad

✅ **Datos excluidos por seguridad:**
- Username del usuario
- Password del usuario  
- Datos internos del sistema de usuarios

✅ **Solo estudiantes activos:**
- Filtra automáticamente por `activo = TRUE`

## Ejemplos de Uso

### Obtener todos los estudiantes
```bash
curl -X GET "http://localhost:8080/estudiantes/getEstudiantes"
```

### Buscar estudiante por nombre
```bash
curl -X GET "http://localhost:8080/estudiantes/getEstudiantes?nombre=Juan"
```

## Cambios Implementados

1. **Nuevo DTO:** `EstudianteBasicResponse` - Excluye datos sensibles
2. **Nuevo método Mapper:** `findByNombre()` - Busca por nombre exacto
3. **Nuevos métodos Service:** 
   - `getAllEstudiantesBasic()` - Todos los estudiantes como respuesta básica
   - `getEstudianteByNombreBasic()` - Busca por nombre como respuesta básica
4. **Endpoint modificado:** `/listarEstudiantes` → `/getEstudiantes`

## Compatibilidad

⚠️ **Breaking Change:** El endpoint anterior `/listarEstudiantes` fue reemplazado por `/getEstudiantes`

💡 **Migración:** Actualizar todas las llamadas del frontend de `/listarEstudiantes` a `/getEstudiantes`