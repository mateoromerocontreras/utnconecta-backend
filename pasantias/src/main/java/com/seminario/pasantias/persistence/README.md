# MyBatis Mappers - Documentación

Documentación completa de los Mappers de MyBatis para el sistema de gestión de pasantías.

## 📁 Estructura

```
persistence/                           # Interfaces Mapper
├── PasantiaMapper.java               # Mapper de Pasantia
├── PostulacionMapper.java            # Mapper de Postulacion (actualizado)
├── EstudianteMapper.java             # Ya existía
├── EmpresaMapper.java                # Ya existía
├── CarreraMapper.java                # Ya existía
├── UsuarioMapper.java                # Ya existía
└── [otros mappers]

resources/mapper/                      # Archivos XML MyBatis
├── PasantiaMapper.xml                # Consultas dinámicas de Pasantia
└── PostulacionMapper.xml             # Consultas dinámicas de Postulacion
```

---

## 🗺️ PasantiaMapper

### Métodos Disponibles

#### Consultas Básicas

| Método | Tipo | Descripción |
|--------|------|-------------|
| `findById(Integer id)` | SELECT | Buscar pasantía por ID |
| `findByIdWithRelations(Integer id)` | SELECT | Buscar con empresa, carreras y postulaciones |
| `findAll()` | SELECT | Listar todas las pasantías |
| `findAllActive()` | SELECT | Solo pasantías PUBLICADA |

#### Consultas por Criterio

| Método | Parámetros | Descripción |
|--------|------------|-------------|
| `findByEmpresa` | `Integer idEmpresa` | Pasantías de una empresa |
| `findByCiudad` | `String ciudad` | Pasantías en una ciudad |
| `findByModalidad` | `String modalidad` | Por modalidad (Presencial/Híbrida/Remoto) |

#### Consultas Dinámicas (XML)

| Método | Parámetros | Descripción |
|--------|------------|-------------|
| `findWithFilters` | `PasantiaFiltroDTO` | Búsqueda con múltiples filtros |
| `countWithFilters` | `PasantiaFiltroDTO` | Contar resultados con filtros |

#### Operaciones CRUD

| Método | Tipo | Descripción |
|--------|------|-------------|
| `insert(Pasantia)` | INSERT | Crear nueva pasantía |
| `update(Pasantia)` | UPDATE | Actualizar pasantía completa |
| `updateEstado` | UPDATE | Solo cambiar estado |
| `delete(Integer id)` | DELETE | Eliminar pasantía |
| `existsById(Integer id)` | SELECT | Verificar existencia |

#### Relación con Carreras

| Método | Descripción |
|--------|-------------|
| `findCarrerasByPasantiaId` | Obtener carreras de una pasantía |
| `insertPasantiaCarrera` | Asociar carrera a pasantía |
| `deleteAllCarrerasByPasantiaId` | Eliminar todas las carreras de una pasantía |

#### Estadísticas

| Método | Descripción |
|--------|-------------|
| `countPostulaciones` | Total de postulaciones |
| `countPostulacionesByEstado` | Postulaciones por estado |

---

### Ejemplos de Uso

#### 1. Buscar pasantía básica
```java
Optional<Pasantia> pasantia = pasantiaMapper.findById(1);
```

#### 2. Buscar con todas las relaciones
```java
Optional<Pasantia> pasantia = pasantiaMapper.findByIdWithRelations(1);
// Incluye: empresa, carreras, postulaciones
```

#### 3. Buscar con filtros
```java
PasantiaFiltroDTO filtro = new PasantiaFiltroDTO();
filtro.setCiudad("Córdoba");
filtro.setModalidad("Híbrida");
filtro.setSoloActivas(true);
filtro.setPagina(0);
filtro.setTamanio(10);

List<Pasantia> pasantias = pasantiaMapper.findWithFilters(filtro);
Long total = pasantiaMapper.countWithFilters(filtro);
```

#### 4. Crear pasantía
```java
Pasantia pasantia = new Pasantia();
pasantia.setTitulo("Pasantía Backend");
pasantia.setPuestoACubrir("Desarrollador Java");
// ... setear otros campos
pasantiaMapper.insert(pasantia);
// El ID se genera automáticamente
Integer idGenerado = pasantia.getIdPasantia();
```

#### 5. Asociar carreras
```java
// Primero eliminar asociaciones existentes
pasantiaMapper.deleteAllCarrerasByPasantiaId(idPasantia);

// Luego agregar nuevas
for (Integer idCarrera : idsCarreras) {
    pasantiaMapper.insertPasantiaCarrera(idPasantia, idCarrera);
}
```

---

## 🎯 PostulacionMapper

### Métodos Disponibles

#### Consultas Básicas

| Método | Tipo | Descripción |
|--------|------|-------------|
| `findById(Integer id)` | SELECT | Buscar postulación por ID |
| `findByIdWithRelations(Integer id)` | SELECT | Buscar con pasantía y estudiante |
| `findAll()` | SELECT | Listar todas las postulaciones |

#### Consultas por Criterio

| Método | Parámetros | Descripción |
|--------|------------|-------------|
| `findByEstudiante` | `Integer idEstudiante` | Postulaciones de un estudiante |
| `findByPasantiaId` | `Integer idPasantia` | Postulaciones de una pasantía |
| `findByEstado` | `String estado` | Postulaciones por estado |
| `findActiveByEstudiante` | `Integer idEstudiante` | Postulaciones no finalizadas |

#### Consultas Dinámicas (XML)

| Método | Parámetros | Descripción |
|--------|------------|-------------|
| `findWithFilters` | `PostulacionFiltroDTO` | Búsqueda con múltiples filtros |
| `countWithFilters` | `PostulacionFiltroDTO` | Contar resultados con filtros |

#### Operaciones CRUD

| Método | Tipo | Descripción |
|--------|------|-------------|
| `insert(Postulacion)` | INSERT | Crear nueva postulación |
| `update(Postulacion)` | UPDATE | Actualizar postulación completa |
| `updateEstado` | UPDATE | Solo cambiar estado |
| `updateContrato` | UPDATE | Actualizar datos del contrato |
| `delete(Integer id)` | DELETE | Eliminar postulación |

#### Validaciones

| Método | Descripción |
|--------|-------------|
| `existsById` | Verificar si existe postulación |
| `existsByEstudianteAndPasantia` | Verificar postulación duplicada |

#### Estadísticas

| Método | Descripción |
|--------|-------------|
| `countByEstudiante` | Total de postulaciones de un estudiante |
| `countByPasantia` | Total de postulaciones de una pasantía |

---

### Ejemplos de Uso

#### 1. Buscar postulación básica
```java
Optional<Postulacion> postulacion = postulacionMapper.findById(1);
```

#### 2. Buscar con relaciones
```java
Optional<Postulacion> postulacion = postulacionMapper.findByIdWithRelations(1);
// Incluye: pasantia completa, estudiante completo
```

#### 3. Postulaciones de un estudiante
```java
List<Postulacion> postulaciones = postulacionMapper.findByEstudiante(5);
```

#### 4. Verificar postulación duplicada
```java
boolean yaPostulo = postulacionMapper.existsByEstudianteAndPasantia(
    idEstudiante, 
    idPasantia
);
```

#### 5. Crear postulación
```java
Postulacion postulacion = new Postulacion();
postulacion.setFechaPostulacion(LocalDate.now());
postulacion.setEstado(EstadoPostulacion.BORRADOR);

Pasantia pasantia = new Pasantia();
pasantia.setIdPasantia(1);
postulacion.setPasantia(pasantia);

Estudiante estudiante = new Estudiante();
estudiante.setIdEstudiante(5);
postulacion.setEstudiante(estudiante);

postulacionMapper.insert(postulacion);
```

#### 6. Actualizar a CUBIERTA con contrato
```java
postulacionMapper.updateEstado(idPostulacion, "CUBIERTA");
postulacionMapper.updateContrato(
    idPostulacion,
    LocalDate.now().plusDays(30).toString(),
    6 // duracion en meses
);
```

#### 7. Buscar con filtros
```java
PostulacionFiltroDTO filtro = new PostulacionFiltroDTO();
filtro.setIdEstudiante(5);
filtro.setEstado(EstadoPostulacion.PUBLICADA);
filtro.setSoloActivas(true);

List<Postulacion> postulaciones = postulacionMapper.findWithFilters(filtro);
```

---

## 🔧 Configuración MyBatis

### application.properties

```properties
# MyBatis Configuration
mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.type-aliases-package=com.seminario.pasantias.entity
mybatis.configuration.map-underscore-to-camel-case=true
mybatis.configuration.default-fetch-size=100
mybatis.configuration.default-statement-timeout=30
```

### Explicación

- **mapper-locations**: Ubicación de los archivos XML
- **type-aliases-package**: Paquete de entidades (no necesitas FQN)
- **map-underscore-to-camel-case**: `fecha_publicacion` → `fechaPublicacion`
- **default-fetch-size**: Cantidad de filas por fetch
- **default-statement-timeout**: Timeout en segundos

---

## 📝 Archivos XML

### PasantiaMapper.xml

**Características**:
- ✅ Filtros dinámicos con `<if>`
- ✅ Búsqueda por texto con LIKE
- ✅ Filtro por múltiples carreras con `<foreach>`
- ✅ Ordenamiento dinámico con `<choose>`
- ✅ Paginación con LIMIT/OFFSET
- ✅ ResultMap con asociación a Empresa

**Filtros disponibles**:
```xml
- busqueda (título o puesto)
- ciudad
- modalidad
- estado
- idEmpresa
- idsCarreras (múltiples)
- asignacionMinima/Maxima
- fechaPublicacionDesde/Hasta
- soloActivas
- ordenarPor (titulo, fechaPublicacion, asignacionEstimulo, ciudad)
- direccion (ASC/DESC)
- pagina/tamanio (paginación)
```

### PostulacionMapper.xml

**Características**:
- ✅ Filtros dinámicos con `<if>`
- ✅ Joins con Pasantia y Estudiante
- ✅ Filtro por empresa (a través de pasantía)
- ✅ Ordenamiento dinámico
- ✅ Paginación
- ✅ ResultMap con asociaciones

**Filtros disponibles**:
```xml
- idEstudiante
- idPasantia
- estado
- fechaDesde/Hasta
- idEmpresa
- soloActivas
- ordenarPor (fechaPostulacion, estado)
- direccion (ASC/DESC)
- pagina/tamanio (paginación)
```

---

## 🎯 Patrones de Mapeo

### 1. Anotaciones (@Select, @Insert, etc.)

**Ventajas**: Rápido, simple, en el mismo archivo  
**Uso**: Consultas simples y estáticas

```java
@Select("SELECT * FROM pasantia WHERE id_pasantia = #{id}")
Optional<Pasantia> findById(@Param("id") Integer id);
```

### 2. XML (mapper/*.xml)

**Ventajas**: Consultas complejas, dinámicas, reutilizables  
**Uso**: Filtros dinámicos, múltiples condiciones

```xml
<select id="findWithFilters" ...>
    SELECT * FROM pasantia
    <where>
        <if test="ciudad != null">
            AND ciudad = #{ciudad}
        </if>
    </where>
</select>
```

### 3. Asociaciones (@One, @Many)

**Uso**: Cargar relaciones automáticamente

```java
@Result(property = "empresa", column = "idEmpresa", 
        one = @One(select = "EmpresaMapper.findById"))
```

---

## ✨ Ventajas de MyBatis

1. **SQL Explícito**: Control total sobre queries
2. **Performance**: Optimización manual de consultas
3. **Flexibilidad**: Queries dinámicas fáciles
4. **Mapeo Automático**: ResultMap simplifica el código
5. **Reutilización**: Statements compartidos
6. **Type Safety**: Interfaces tipadas
7. **Paginación**: Fácil con LIMIT/OFFSET

---

## 🚀 Próximos Pasos

1. ✅ **Mappers creados**
2. ✅ **XML con filtros dinámicos**
3. ⏭️ **Mapper Utils** - Conversión Entity ↔ DTO
4. ⏭️ **Services** - Lógica de negocio
5. ⏭️ **Controllers** - Endpoints REST

---

## 📚 Referencias

- [MyBatis Documentation](https://mybatis.org/mybatis-3/)
- [Dynamic SQL](https://mybatis.org/mybatis-3/dynamic-sql.html)
- [Spring Boot with MyBatis](https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)

---

**Última actualización**: 30 de octubre de 2025
