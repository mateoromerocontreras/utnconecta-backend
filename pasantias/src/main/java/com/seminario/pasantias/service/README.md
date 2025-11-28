# 📋 Services - Capa de Lógica de Negocio

## 📁 Estructura

```
service/
├── PasantiaService.java       ← Lógica de negocio para Pasantías
├── PostulacionService.java    ← Lógica de negocio para Postulaciones
├── EstudianteService.java     ← Gestión de Estudiantes
├── EmpresaService.java        ← Gestión de Empresas
├── CarreraService.java        ← Gestión de Carreras
└── UsuarioService.java        ← Autenticación y Usuarios
```

---

## 🎯 PasantiaService

Gestiona el ciclo de vida completo de las pasantías.

### Métodos Principales

#### 1. **Crear Pasantía**
```java
PasantiaResponseDTO crearPasantia(PasantiaRequestDTO request)
```

**Validaciones:**
- ✅ Empresa existe
- ✅ Carreras existen
- ✅ Fechas válidas (caducidad > hoy)
- ✅ Estado inicial: `PENDIENTE_DE_APROBACION`

**Ejemplo:**
```java
PasantiaRequestDTO request = new PasantiaRequestDTO();
request.setTitulo("Desarrollador Backend Java");
request.setPuestoACubrir("Desarrollador Junior");
request.setCiudad("Santa Fe");
request.setModalidad("Híbrida");
request.setAsignacionEstimulo(50000F);
request.setCantidadDePasantes(2);
request.setFechaPublicacion(LocalDate.now());
request.setFechaCaducidad(LocalDate.now().plusMonths(3));
request.setIdEmpresa(1);
request.setIdsCarreras(List.of(1, 2));
request.setEmailContacto("rrhh@empresa.com");

PasantiaResponseDTO pasantia = pasantiaService.crearPasantia(request);
```

---

#### 2. **Actualizar Pasantía**
```java
PasantiaResponseDTO actualizarPasantia(Integer id, PasantiaRequestDTO request)
```

**Validaciones:**
- ✅ Pasantía existe
- ✅ No está FINALIZADA ni DADA_DE_BAJA
- ✅ Empresa válida
- ✅ Carreras válidas

**Restricciones:**
- ❌ No se puede modificar si está `FINALIZADA`
- ❌ No se puede modificar si está `DADA_DE_BAJA`

---

#### 3. **Cambiar Estado**
```java
PasantiaResponseDTO actualizarEstado(Integer id, ActualizarEstadoPasantiaDTO request)
```

**Transiciones Permitidas:**

```
PENDIENTE_DE_APROBACION → PUBLICADA
PENDIENTE_DE_APROBACION → DADA_DE_BAJA

PUBLICADA → FINALIZADA
PUBLICADA → DADA_DE_BAJA
PUBLICADA → EXPIRADA

❌ FINALIZADA → (ninguno)
❌ DADA_DE_BAJA → (ninguno)
❌ EXPIRADA → (ninguno)
```

**Ejemplo:**
```java
ActualizarEstadoPasantiaDTO request = new ActualizarEstadoPasantiaDTO();
request.setEstado(EstadoPasantia.PUBLICADA);

pasantiaService.actualizarEstado(1, request);
```

---

#### 4. **Buscar con Filtros** 🔍
```java
PaginaDTO<PasantiaResponseDTO> buscarPasantias(PasantiaFiltroDTO filtro)
```

**Filtros Disponibles:**
- `titulo` - Búsqueda parcial
- `ciudad` - Búsqueda exacta
- `modalidad` - Presencial, Híbrida, Remoto
- `estado` - PUBLICADA, FINALIZADA, etc.
- `empresaId` - Por empresa específica
- `carreraIds` - Por carreras (múltiples)
- `asignacionMinima` / `asignacionMaxima`
- `fechaPublicacionDesde` / `fechaPublicacionHasta`
- `fechaCaducidadDesde` / `fechaCaducidadHasta`
- `pagina` - Número de página (default: 0)
- `tamanio` - Tamaño de página (default: 20, max: 100)
- `ordenarPor` - Campo de ordenamiento
- `ordenDireccion` - ASC o DESC

**Ejemplo:**
```java
PasantiaFiltroDTO filtro = new PasantiaFiltroDTO();
filtro.setEstado(EstadoPasantia.PUBLICADA);
filtro.setCiudad("Santa Fe");
filtro.setModalidad("Remoto");
filtro.setCarreraIds(List.of(1, 2));
filtro.setPagina(0);
filtro.setTamanio(10);
filtro.setOrdenarPor("fechaPublicacion");
filtro.setOrdenDireccion("DESC");

PaginaDTO<PasantiaResponseDTO> resultados = pasantiaService.buscarPasantias(filtro);

System.out.println("Total: " + resultados.getTotalElementos());
System.out.println("Página: " + resultados.getPaginaActual() + "/" + resultados.getTotalPaginas());
resultados.getContenido().forEach(p -> System.out.println(p.getTitulo()));
```

---

#### 5. **Obtener Detalle Completo**
```java
PasantiaDetalleDTO obtenerPasantiaPorId(Integer id)
```

**Incluye:**
- ✅ Información completa de pasantía
- ✅ Datos de empresa
- ✅ Lista de carreras elegibles
- ✅ Todas las postulaciones
- ✅ Estadísticas (borradores, pendientes, publicadas, cubiertas, finalizadas)
- ✅ Días restantes hasta caducidad
- ✅ Indicador si acepta postulaciones

---

#### 6. **Otros Métodos**

```java
// Por empresa
List<PasantiaResponseDTO> obtenerPasantiasPorEmpresa(Integer empresaId)

// Por carrera
List<PasantiaResponseDTO> obtenerPasantiasPorCarrera(Integer carreraId)

// Solo publicadas
List<PasantiaResponseDTO> obtenerPasantiasPublicadas()

// Eliminar (soft delete → DADA_DE_BAJA)
void eliminarPasantia(Integer id)
```

**Restricción de Eliminación:**
- ❌ No se puede eliminar si tiene postulaciones activas (no FINALIZADAS)

---

## 📝 PostulacionService

Gestiona las postulaciones de estudiantes a pasantías.

### Métodos Principales

#### 1. **Crear Postulación**
```java
PostulacionResponseDTO crearPostulacion(PostulacionRequestDTO request)
```

**Validaciones:**
- ✅ Estudiante existe
- ✅ Pasantía existe
- ✅ Pasantía está PUBLICADA
- ✅ Pasantía no ha caducado
- ✅ Estudiante no ha postulado previamente
- ✅ Estado inicial: `BORRADOR`

**Ejemplo:**
```java
PostulacionRequestDTO request = new PostulacionRequestDTO();
request.setIdEstudiante(1);
request.setIdPasantia(1);

PostulacionResponseDTO postulacion = postulacionService.crearPostulacion(request);
```

---

#### 2. **Actualizar Postulación**
```java
PostulacionResponseDTO actualizarPostulacion(Integer id, PostulacionRequestDTO request)
```

**Restricciones:**
- ✅ Solo si está en `BORRADOR` o `PENDIENTE_APROBACION`
- ❌ No se puede modificar si está PUBLICADA, CUBIERTA o FINALIZADA

---

#### 3. **Cambiar Estado**
```java
PostulacionResponseDTO actualizarEstado(Integer id, ActualizarEstadoPostulacionDTO request)
```

**Transiciones Permitidas:**

```
BORRADOR → PENDIENTE_APROBACION

PENDIENTE_APROBACION → PUBLICADA

PUBLICADA → CUBIERTA (requiere fechaInicioContrato + duracionMeses)
PUBLICADA → FINALIZADA

CUBIERTA → FINALIZADA

❌ FINALIZADA → (ninguno)
```

**Ejemplo - Aceptar Postulación:**
```java
ActualizarEstadoPostulacionDTO request = new ActualizarEstadoPostulacionDTO();
request.setEstado(EstadoPostulacion.CUBIERTA);
request.setFechaInicioContrato(LocalDate.now().plusMonths(1));
request.setDuracionMeses(6);

postulacionService.actualizarEstado(1, request);
```

---

#### 4. **Buscar con Filtros** 🔍
```java
PaginaDTO<PostulacionResponseDTO> buscarPostulaciones(PostulacionFiltroDTO filtro)
```

**Filtros Disponibles:**
- `estudianteId` - Por estudiante específico
- `pasantiaId` - Por pasantía específica
- `empresaId` - Por empresa
- `estado` - BORRADOR, PUBLICADA, CUBIERTA, etc.
- `fechaPostulacionDesde` / `fechaPostulacionHasta`
- `fechaInicioContratoDesde` / `fechaInicioContratoHasta`
- `pagina`, `tamanio`, `ordenarPor`, `ordenDireccion`

**Ejemplo:**
```java
PostulacionFiltroDTO filtro = new PostulacionFiltroDTO();
filtro.setEstado(EstadoPostulacion.CUBIERTA);
filtro.setEmpresaId(1);

PaginaDTO<PostulacionResponseDTO> resultados = postulacionService.buscarPostulaciones(filtro);
```

---

#### 5. **Otros Métodos**

```java
// Detalle completo
PostulacionDetalleDTO obtenerPostulacionPorId(Integer id)

// Por estudiante
List<PostulacionResponseDTO> obtenerPostulacionesPorEstudiante(Integer estudianteId)

// Por pasantía
List<PostulacionResponseDTO> obtenerPostulacionesPorPasantia(Integer pasantiaId)

// Todas
List<PostulacionResponseDTO> consultarPostulaciones()

// Eliminar (solo BORRADOR)
void eliminarPostulacion(Integer id)
```

---

## 🔄 Flujos de Negocio Completos

### Flujo 1: Publicar una Pasantía

```java
// 1. Empresa crea pasantía
PasantiaRequestDTO request = new PasantiaRequestDTO();
request.setTitulo("Backend Developer");
request.setIdEmpresa(1);
request.setIdsCarreras(List.of(1, 2));
// ... otros campos

PasantiaResponseDTO pasantia = pasantiaService.crearPasantia(request);
// Estado: PENDIENTE_DE_APROBACION

// 2. Admin aprueba
ActualizarEstadoPasantiaDTO aprobar = new ActualizarEstadoPasantiaDTO();
aprobar.setEstado(EstadoPasantia.PUBLICADA);

pasantiaService.actualizarEstado(pasantia.getIdPasantia(), aprobar);
// Estado: PUBLICADA → Estudiantes pueden postularse
```

---

### Flujo 2: Estudiante Postula y es Aceptado

```java
// 1. Estudiante crea postulación
PostulacionRequestDTO request = new PostulacionRequestDTO();
request.setIdEstudiante(1);
request.setIdPasantia(1);

PostulacionResponseDTO postulacion = postulacionService.crearPostulacion(request);
// Estado: BORRADOR

// 2. Estudiante envía a revisión
ActualizarEstadoPostulacionDTO enviar = new ActualizarEstadoPostulacionDTO();
enviar.setEstado(EstadoPostulacion.PENDIENTE_APROBACION);

postulacionService.actualizarEstado(postulacion.getIdPostulacion(), enviar);
// Estado: PENDIENTE_APROBACION

// 3. Admin aprueba
ActualizarEstadoPostulacionDTO publicar = new ActualizarEstadoPostulacionDTO();
publicar.setEstado(EstadoPostulacion.PUBLICADA);

postulacionService.actualizarEstado(postulacion.getIdPostulacion(), publicar);
// Estado: PUBLICADA

// 4. Empresa acepta y define contrato
ActualizarEstadoPostulacionDTO aceptar = new ActualizarEstadoPostulacionDTO();
aceptar.setEstado(EstadoPostulacion.CUBIERTA);
aceptar.setFechaInicioContrato(LocalDate.now().plusMonths(1));
aceptar.setDuracionMeses(6);

postulacionService.actualizarEstado(postulacion.getIdPostulacion(), aceptar);
// Estado: CUBIERTA → Contrato iniciado

// 5. Finalizar pasantía
ActualizarEstadoPostulacionDTO finalizar = new ActualizarEstadoPostulacionDTO();
finalizar.setEstado(EstadoPostulacion.FINALIZADA);

postulacionService.actualizarEstado(postulacion.getIdPostulacion(), finalizar);
// Estado: FINALIZADA
```

---

### Flujo 3: Buscar Pasantías para Estudiante

```java
// Estudiante busca pasantías remotas de su carrera
PasantiaFiltroDTO filtro = new PasantiaFiltroDTO();
filtro.setEstado(EstadoPasantia.PUBLICADA);
filtro.setModalidad("Remoto");
filtro.setCarreraIds(List.of(1)); // ID de su carrera
filtro.setOrdenarPor("fechaPublicacion");
filtro.setOrdenDireccion("DESC");

PaginaDTO<PasantiaResponseDTO> pasantias = pasantiaService.buscarPasantias(filtro);

// Ver detalles de una
PasantiaDetalleDTO detalle = pasantiaService.obtenerPasantiaPorId(pasantias.getContenido().get(0).getIdPasantia());

System.out.println("Título: " + detalle.getTitulo());
System.out.println("Empresa: " + detalle.getEmpresa().getNombre());
System.out.println("Asignación: $" + detalle.getAsignacionEstimulo());
System.out.println("Postulaciones: " + detalle.getEstadisticas().getTotal());
System.out.println("Días restantes: " + detalle.getDiasRestantes());
System.out.println("Acepta postulaciones: " + detalle.getAceptaPostulaciones());
```

---

## 🎨 Conversión DTO ↔ Entity

### PasantiaMapperUtil

```java
@Component
public class PasantiaMapperUtil {
    Pasantia requestDtoToEntity(PasantiaRequestDTO dto)
    void updateEntityFromRequestDto(PasantiaRequestDTO dto, Pasantia entity)
    PasantiaResponseDTO entityToResponseDto(Pasantia entity)
    PasantiaDetalleDTO entityToDetalleDto(Pasantia entity)
}
```

### PostulacionMapperUtil

```java
@Component
public class PostulacionMapperUtil {
    Postulacion requestDtoToEntity(PostulacionRequestDTO dto)
    void updateEntityFromRequestDto(PostulacionRequestDTO dto, Postulacion entity)
    PostulacionResponseDTO entityToResponseDto(Postulacion entity)
    PostulacionDetalleDTO entityToDetalleDto(Postulacion entity)
}
```

**Campos Calculados Automáticos:**
- `cantidadPostulaciones` - Cuenta postulaciones de pasantía
- `diasRestantes` - Días hasta caducidad
- `aceptaPostulaciones` - Si está PUBLICADA y no caducó
- `esEditable` - Si está en BORRADOR o PENDIENTE_APROBACION
- Estadísticas por estado

---

## 🔒 Transacciones

Todos los métodos de escritura usan `@Transactional`:
- ✅ Rollback automático en errores
- ✅ Consistencia de datos garantizada
- ✅ Métodos de lectura con `@Transactional(readOnly = true)`

---

## ⚠️ Manejo de Errores

### Excepciones Lanzadas

```java
// Recurso no encontrado
throw new IllegalArgumentException("Pasantía no encontrada con ID: " + id);

// Estado inválido
throw new IllegalStateException("La pasantía no está disponible para postulaciones");

// Validación de negocio
throw new IllegalStateException("El estudiante ya tiene una postulación para esta pasantía");
```

### Recomendación para Controllers

```java
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ErrorDTO> handleNotFound(IllegalArgumentException e) {
    return ResponseEntity.status(404).body(new ErrorDTO(e.getMessage()));
}

@ExceptionHandler(IllegalStateException.class)
public ResponseEntity<ErrorDTO> handleBusinessRule(IllegalStateException e) {
    return ResponseEntity.status(400).body(new ErrorDTO(e.getMessage()));
}
```

---

## 📊 Estadísticas y Campos Calculados

### PasantiaDetalleDTO.EstadisticasPostulacionDTO

```java
{
  "total": 15,
  "borradores": 3,
  "pendientes": 5,
  "publicadas": 4,
  "cubiertas": 2,
  "finalizadas": 1
}
```

### Campos Calculados

```java
PasantiaResponseDTO {
  ...
  "cantidadPostulaciones": 15,
  "diasRestantes": 45,
  "aceptaPostulaciones": true
}

PostulacionResponseDTO {
  ...
  "esEditable": true
}
```

---

## ✅ Próximos Pasos

1. **Controllers** - Exponer endpoints REST
2. **Exception Handlers** - Manejo centralizado de errores
3. **Tests** - Unit tests para lógica de negocio
4. **Seguridad** - JWT, roles, permisos

---

## 📚 Dependencias

- MyBatis (Persistence)
- Spring Transactions
- Jakarta Validation
- Lombok
- Java 21
