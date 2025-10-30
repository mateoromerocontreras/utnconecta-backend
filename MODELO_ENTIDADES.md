# Modelo de Entidades - Sistema de Pasantías

Este documento describe el modelo de datos actualizado del sistema de gestión de pasantías.

## 📋 Entidades Principales

### 1. Pasantia

Representa una oferta de pasantía publicada por una empresa.

#### Atributos

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `idPasantia` | Integer | Identificador único |
| `titulo` | String | Título de la oferta |
| `puestoACubrir` | String | Nombre del puesto a cubrir |
| `ciudad` | String | Ciudad donde se realiza |
| `modalidad` | String | Presencial, Híbrida, Remoto |
| `asignacionEstimulo` | Float | Monto de la asignación |
| `cantidadDePasantes` | Integer | Número de plazas disponibles |
| `fechaPublicacion` | LocalDate | Fecha de publicación |
| `fechaCaducidad` | LocalDate | Fecha límite |
| `estado` | EstadoPasantia | Estado actual (enum) |
| `emailContacto` | String | Email de contacto |

#### Relaciones

- `empresa` (Empresa): Empresa que publica la pasantía
- `carreras` (List<Carrera>): Carreras elegibles
- `postulaciones` (List<Postulacion>): Postulaciones recibidas

---

### 2. Postulacion

Representa la postulación de un estudiante a una pasantía.

#### Atributos

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `idPostulacion` | Integer | Identificador único |
| `fechaPostulacion` | LocalDate | Fecha de postulación |
| `fechaInicioContrato` | LocalDate | Fecha de inicio del contrato |
| `duracionMeses` | Integer | Duración en meses |
| `estado` | EstadoPostulacion | Estado actual (enum) |

#### Relaciones

- `pasantia` (Pasantia): Pasantía a la que se postula
- `estudiante` (Estudiante): Estudiante que postula

---

## 🏷️ Enumeraciones

### EstadoPasantia

Define los estados del ciclo de vida de una pasantía.

```java
public enum EstadoPasantia {
    PUBLICADA,                  // Visible y aceptando postulaciones
    FINALIZADA,                 // Todas las plazas cubiertas
    DADA_DE_BAJA,              // Cancelada por empresa o rechazada
    PENDIENTE_DE_APROBACION,   // Esperando aprobación de facultad
    EXPIRADA                    // Fecha de caducidad alcanzada
}
```

#### Transiciones de Estados

```
PENDIENTE_DE_APROBACION → PUBLICADA (aprobación)
PENDIENTE_DE_APROBACION → DADA_DE_BAJA (rechazo/cancelación)
PUBLICADA → FINALIZADA (plazas cubiertas)
PUBLICADA → EXPIRADA (fecha límite)
PUBLICADA → DADA_DE_BAJA (cancelación)
EXPIRADA → PUBLICADA (extensión de fecha)
```

---

### EstadoPostulacion

Define los estados del ciclo de vida de una postulación.

```java
public enum EstadoPostulacion {
    BORRADOR,                   // Guardada pero no enviada
    PENDIENTE_APROBACION,       // Enviada, esperando revisión
    PUBLICADA,                  // Aprobada y visible
    CUBIERTA,                   // Estudiante aceptado
    FINALIZADA                  // Proceso completado o rechazado
}
```

#### Transiciones de Estados

```
BORRADOR → PENDIENTE_APROBACION (envío)
PENDIENTE_APROBACION → PUBLICADA (aprobación)
PENDIENTE_APROBACION → BORRADOR (requiere cambios)
PUBLICADA → CUBIERTA (aceptación)
PUBLICADA → FINALIZADA (rechazo)
CUBIERTA → FINALIZADA (completado)
```

---

## 🔄 Relaciones entre Entidades

```
Empresa 1 ──── n Pasantia
               │
               │ 1
               │
               n
           Postulacion
               │
               │ n
               │
               1
           Estudiante

Pasantia n ──── n Carrera
```

### Descripción de Relaciones

1. **Empresa - Pasantia**: Una empresa puede publicar múltiples pasantías
2. **Pasantia - Postulacion**: Una pasantía puede tener múltiples postulaciones (composición)
3. **Estudiante - Postulacion**: Un estudiante puede realizar múltiples postulaciones
4. **Pasantia - Carrera**: Una pasantía puede estar dirigida a múltiples carreras (muchos a muchos)

---

## 📊 Diagramas UML

Los diagramas visuales del modelo se encuentran en `docs/diagrams/generated/`:

### Diagramas Disponibles

1. **Diagrama de Clases Principal** (`class-diagram.puml`)
   - Vista general de todas las entidades y sus relaciones

2. **Modelo de Pasantia Detallado** (`pasantia-detail.puml`)
   - Vista detallada de la entidad Pasantia

3. **Modelo de Postulacion Detallado** (`postulacion-detail.puml`)
   - Vista detallada de la entidad Postulacion

4. **Diagrama de Estados - Pasantía** (`state-pasantia.puml`)
   - Ciclo de vida de una pasantía

5. **Diagrama de Estados - Postulación** (`state-postulacion.puml`)
   - Ciclo de vida de una postulación

---

## 🎯 Reglas de Negocio

### Pasantía

1. Una pasantía solo acepta postulaciones en estado `PUBLICADA`
2. Una pasantía pasa a `FINALIZADA` cuando se cubren todas las plazas
3. Una pasantía pasa a `EXPIRADA` cuando se alcanza `fechaCaducidad`
4. Una pasantía puede ser `DADA_DE_BAJA` en cualquier momento antes de `FINALIZADA`

### Postulación

1. Un estudiante solo puede postular a pasantías en estado `PUBLICADA`
2. Una postulación debe pasar por `PENDIENTE_APROBACION` antes de ser `PUBLICADA`
3. Solo postulaciones `PUBLICADAS` pueden pasar a `CUBIERTA`
4. Una postulación `CUBIERTA` representa un contrato activo

---

## 🔧 Archivos Fuente

```
pasantias/src/main/java/com/seminario/pasantias/entity/
├── Pasantia.java
├── EstadoPasantia.java
├── Postulacion.java
├── EstadoPostulacion.java
├── Estudiante.java
├── Empresa.java
└── Carrera.java
```

---

## 📝 Cambios Recientes

### Pasantia
- ✅ Simplificada a atributos esenciales
- ✅ Cambiado `BigDecimal` a `Float` para `asignacionEstimulo`
- ✅ Cambiado `LocalDateTime` a `LocalDate` para fechas
- ✅ Agregada relación directa a `Empresa`
- ✅ Agregada lista de `Postulacion`

### Postulacion
- ✅ Agregados atributos de contrato (`fechaInicioContrato`, `duracionMeses`)
- ✅ Cambiado `String` a `EstadoPostulacion` (type-safe)
- ✅ Agregadas relaciones directas a `Pasantia` y `Estudiante`

### EstadoPasantia
- ✅ Simplificado a enum básico
- ✅ Estados actualizados según requerimientos
- ✅ Eliminados métodos helper (se implementarán en servicios)

### EstadoPostulacion
- ✅ Creado como nuevo enum
- ✅ Estados definidos según flujo de negocio

---

## 📚 Documentación Adicional

- Ver `PLANTUML_INTEGRATION.md` para uso de PlantUML
- Ver `docs/diagrams/README.md` para documentación de diagramas
- Ver diagramas generados en `docs/diagrams/generated/`

---

**Última actualización**: 30 de octubre de 2025
