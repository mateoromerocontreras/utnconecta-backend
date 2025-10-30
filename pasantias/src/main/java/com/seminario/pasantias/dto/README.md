# DTOs (Data Transfer Objects)

Documentación completa de los DTOs del sistema de gestión de pasantías.

## 📁 Estructura

```
dto/
├── request/                           # DTOs de entrada (cliente → servidor)
│   ├── PasantiaRequestDTO.java       # Crear/actualizar pasantía
│   ├── PostulacionRequestDTO.java    # Crear/actualizar postulación
│   ├── ActualizarEstadoPasantiaDTO.java
│   ├── ActualizarEstadoPostulacionDTO.java
│   ├── PasantiaFiltroDTO.java        # Filtros de búsqueda
│   └── PostulacionFiltroDTO.java     # Filtros de búsqueda
│
└── response/                          # DTOs de salida (servidor → cliente)
    ├── PasantiaResponseDTO.java      # Respuesta básica
    ├── PasantiaDetalleDTO.java       # Respuesta detallada
    ├── PostulacionResponseDTO.java   # Respuesta básica
    ├── PostulacionDetalleDTO.java    # Respuesta detallada
    └── PaginaDTO.java                # Respuestas paginadas (genérico)
```

---

## 📥 DTOs Request (Entrada)

### 1. PasantiaRequestDTO

**Propósito**: Crear o actualizar una pasantía.

**Validaciones incluidas**:
- ✅ Título: 5-200 caracteres, obligatorio
- ✅ Modalidad: Pattern "Presencial|Híbrida|Remoto"
- ✅ Asignación: Debe ser positiva
- ✅ Cantidad de pasantes: 1-50
- ✅ Fechas: No en el pasado, caducidad > publicación
- ✅ Email: Formato válido
- ✅ Carreras: Al menos 1, máximo 20

**Ejemplo de uso**:
```java
PasantiaRequestDTO dto = new PasantiaRequestDTO();
dto.setTitulo("Pasantía en Desarrollo Backend");
dto.setPuestoACubrir("Desarrollador Java Jr");
dto.setCiudad("Córdoba");
dto.setModalidad("Híbrida");
dto.setAsignacionEstimulo(50000.0f);
dto.setCantidadDePasantes(2);
dto.setFechaPublicacion(LocalDate.now());
dto.setFechaCaducidad(LocalDate.now().plusMonths(2));
dto.setIdEmpresa(1);
dto.setIdsCarreras(List.of(1, 2, 3));
dto.setEmailContacto("rrhh@empresa.com");
```

---

### 2. PostulacionRequestDTO

**Propósito**: Crear o actualizar una postulación.

**Validaciones incluidas**:
- ✅ Fecha postulación: No futura
- ✅ Fecha inicio contrato: Futura
- ✅ Duración: 1-24 meses
- ✅ IDs: Positivos y obligatorios
- ✅ Contrato completo: Si hay fecha, debe haber duración

**Ejemplo de uso**:
```java
PostulacionRequestDTO dto = new PostulacionRequestDTO();
dto.setFechaPostulacion(LocalDate.now());
dto.setIdPasantia(1);
dto.setIdEstudiante(5);
dto.setEstado(EstadoPostulacion.BORRADOR);
// Contrato se completa al aceptar
```

---

### 3. ActualizarEstadoPasantiaDTO

**Propósito**: Cambiar el estado de una pasantía.

**Campos**:
- `estado`: Nuevo estado (obligatorio)
- `motivo`: Razón del cambio (opcional)

---

### 4. ActualizarEstadoPostulacionDTO

**Propósito**: Cambiar el estado de una postulación.

**Campos**:
- `estado`: Nuevo estado (obligatorio)
- `fechaInicioContrato`: Requerido si estado = CUBIERTA
- `duracionMeses`: Requerido si estado = CUBIERTA
- `observaciones`: Comentarios (opcional)

---

### 5. PasantiaFiltroDTO

**Propósito**: Filtrar y paginar búsquedas de pasantías.

**Campos disponibles** (todos opcionales):
```java
- busqueda: Texto en título/puesto
- ciudad: Filtrar por ciudad
- modalidad: Presencial/Híbrida/Remoto
- estado: Estado específico
- idEmpresa: Filtrar por empresa
- idsCarreras: Filtrar por carreras
- asignacionMinima/Maxima: Rango de asignación
- fechaPublicacionDesde/Hasta: Rango de fechas
- soloActivas: Solo PUBLICADA
- ordenarPor: Campo de ordenamiento
- direccion: ASC/DESC
- pagina: Número de página
- tamanio: Elementos por página
```

---

### 6. PostulacionFiltroDTO

**Propósito**: Filtrar y paginar búsquedas de postulaciones.

**Campos disponibles** (todos opcionales):
```java
- idEstudiante: Postulaciones de un estudiante
- idPasantia: Postulaciones a una pasantía
- estado: Estado específico
- fechaDesde/Hasta: Rango de fechas
- idEmpresa: Postulaciones a pasantías de empresa
- soloActivas: Excluir FINALIZADA
- ordenarPor: Campo de ordenamiento
- direccion: ASC/DESC
- pagina: Número de página
- tamanio: Elementos por página
```

---

## 📤 DTOs Response (Salida)

### 1. PasantiaResponseDTO

**Propósito**: Respuesta básica para listados.

**Características**:
- ✅ Información esencial de la pasantía
- ✅ Campos desnormalizados (nombreEmpresa)
- ✅ Campos calculados (diasRestantes, cantidadPostulaciones)
- ✅ Sin relaciones anidadas completas

**Uso ideal**:
- Listados de pasantías
- Búsquedas
- Cards/thumbnails

---

### 2. PasantiaDetalleDTO

**Propósito**: Respuesta completa para vista de detalle.

**Características**:
- ✅ Toda la información de PasantiaResponseDTO
- ✅ Relaciones completas: empresa, carreras, postulaciones
- ✅ Estadísticas de postulaciones
- ✅ DTOs internos anidados (EmpresaSimpleDTO, CarreraSimpleDTO)

**Uso ideal**:
- Vista detallada de una pasantía
- Panel de administración
- Dashboard de empresa

**DTOs internos incluidos**:
```java
- EmpresaSimpleDTO: id, nombre, cuit, email
- CarreraSimpleDTO: id, nombre, codigo
- PostulacionSimpleDTO: id, fecha, estado, estudiante
- EstadisticasPostulacionDTO: contadores por estado
```

---

### 3. PostulacionResponseDTO

**Propósito**: Respuesta básica para listados.

**Características**:
- ✅ Información esencial de la postulación
- ✅ Campos desnormalizados (tituloPasantia, nombreEstudiante, nombreEmpresa)
- ✅ Campo calculado (esEditable)
- ✅ Sin relaciones anidadas completas

**Uso ideal**:
- Listados de postulaciones
- Mis postulaciones (estudiante)
- Postulaciones recibidas (empresa)

---

### 4. PostulacionDetalleDTO

**Propósito**: Respuesta completa para vista de detalle.

**Características**:
- ✅ Toda la información de PostulacionResponseDTO
- ✅ Relaciones completas: pasantia, estudiante
- ✅ Historial y observaciones
- ✅ DTOs internos anidados

**Uso ideal**:
- Vista detallada de una postulación
- Revisión de postulación (empresa)
- Seguimiento (estudiante)

**DTOs internos incluidos**:
```java
- PasantiaSimpleDTO: Datos completos de la pasantía
- EstudianteSimpleDTO: Datos completos del estudiante
```

---

### 5. PaginaDTO<T>

**Propósito**: Wrapper genérico para respuestas paginadas.

**Tipo genérico**: Funciona con cualquier tipo de contenido.

**Metadata incluida**:
```java
- contenido: List<T> de elementos
- paginaActual: Número de página (base 0)
- tamanioPagina: Elementos por página
- totalElementos: Total de registros
- totalPaginas: Páginas disponibles
- esPrimeraPagina: boolean
- esUltimaPagina: boolean
- tienePaginaAnterior: boolean
- tienePaginaSiguiente: boolean
- elementosEnPagina: Elementos en esta página
```

**Ejemplo de uso**:
```java
PaginaDTO<PasantiaResponseDTO> pagina = PaginaDTO.<PasantiaResponseDTO>builder()
    .contenido(listaPasantias)
    .paginaActual(0)
    .tamanioPagina(10)
    .totalElementos(50L)
    .totalPaginas(5)
    .esPrimeraPagina(true)
    .esUltimaPagina(false)
    .tienePaginaAnterior(false)
    .tienePaginaSiguiente(true)
    .elementosEnPagina(10)
    .build();
```

---

## 🎯 Patrones de Uso

### Patrón Request-Response

```
Cliente                   Servidor
   │                         │
   │  PasantiaRequestDTO     │
   ├────────────────────────>│
   │                         │ Validación
   │                         │ Lógica de negocio
   │                         │ Persistencia
   │                         │
   │  PasantiaResponseDTO    │
   │<────────────────────────┤
   │                         │
```

### Patrón de Listado con Filtros

```
Cliente                        Servidor
   │                              │
   │  PasantiaFiltroDTO          │
   │  (ciudad, modalidad, etc.)  │
   ├─────────────────────────────>│
   │                              │ Construir query
   │                              │ Aplicar filtros
   │                              │ Paginar
   │                              │
   │  PaginaDTO<PasantiaResponseDTO> │
   │<─────────────────────────────┤
   │                              │
```

---

## ✨ Ventajas del Diseño

### 1. Separación Request/Response
✅ **Claridad**: Distinto propósito, distinta estructura  
✅ **Flexibilidad**: Evolucionar entrada y salida independientemente  
✅ **Seguridad**: No exponer campos internos

### 2. DTOs Básicos vs Detallados
✅ **Performance**: Listados ligeros, detalles completos  
✅ **Bandwidth**: Menos datos en listados  
✅ **UX**: Datos apropiados para cada vista

### 3. Validaciones en Request
✅ **Fail Fast**: Errores antes de llegar a lógica  
✅ **Documentación**: Las validaciones documentan reglas  
✅ **Consistencia**: Mismas reglas en toda la app

### 4. Desnormalización en Response
✅ **Performance**: Menos joins en cliente  
✅ **Simplicidad**: Cliente no hace agregaciones  
✅ **UX**: Datos listos para mostrar

### 5. Genéricos (PaginaDTO<T>)
✅ **Reutilización**: Una clase para todo  
✅ **Consistencia**: Misma estructura de paginación  
✅ **Type Safety**: Compilador valida tipos

---

## 🔧 Validaciones Disponibles

### Anotaciones Jakarta Validation

```java
@NotNull          // No puede ser null
@NotBlank         // String no vacío/blank
@NotEmpty         // Colección no vacía
@Size(min, max)   // Tamaño de String/Colección
@Min(value)       // Valor mínimo numérico
@Max(value)       // Valor máximo numérico
@Positive         // Debe ser > 0
@Email            // Formato de email válido
@Pattern(regexp)  // Regex personalizado
@Future           // Fecha futura
@Past             // Fecha pasada
@FutureOrPresent  // Fecha futura o actual
@PastOrPresent    // Fecha pasada o actual
@DecimalMin       // Mínimo para decimales
@AssertTrue       // Método debe retornar true
```

### Validaciones Personalizadas

```java
// En PasantiaRequestDTO
@AssertTrue(message = "...")
public boolean isFechaCaducidadValida() {
    return fechaCaducidad.isAfter(fechaPublicacion);
}
```

---

## 📊 Diagrama UML

Ver `docs/diagrams/dtos-structure.puml` para diagrama completo de la estructura.

Generar con:
```bash
./generate-diagrams.sh
```

---

## 🚀 Próximos Pasos

1. ✅ **DTOs creados**
2. ⏭️ **Mappers MyBatis** - Acceso a datos
3. ⏭️ **Mapper Utils** - Conversión Entity ↔ DTO
4. ⏭️ **Services** - Lógica de negocio
5. ⏭️ **Controllers** - Endpoints REST

---

**Última actualización**: 30 de octubre de 2025
