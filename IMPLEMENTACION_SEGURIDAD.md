# 🔐 Implementación de Seguridad - Endpoint /api/pasantias/crear

## ✅ Resumen de Cambios

Se implementó un sistema completo de autenticación y autorización para el endpoint `/api/pasantias/crear` según los siguientes requisitos:

### Requisitos Implementados

1. ✅ **Autenticación JWT obligatoria** - El usuario debe estar en una sesión autenticada
2. ✅ **Validación de rol** - Solo usuarios con rol EMPRESA o ADMINISTRADOR pueden acceder
3. ✅ **Validación de propiedad** - Usuario EMPRESA solo puede crear pasantías para su empresa
4. ✅ **Privilegios de administrador** - Usuario ADMINISTRADOR puede crear para cualquier empresa

---

## 📦 Archivos Creados/Modificados

### 1. SecurityService.java (NUEVO)
**Ubicación**: `pasantias/src/main/java/com/seminario/pasantias/security/SecurityService.java`

**Responsabilidades**:
- Obtener usuario autenticado desde SecurityContext
- Verificar roles del usuario
- Obtener empresa asociada al usuario EMPRESA
- Validar permisos para crear/modificar pasantías

**Métodos principales**:
```java
public Usuario getUsuarioAutenticado()
public boolean tieneRol(String nombreRol)
public boolean esAdministrador()
public boolean esEmpresa()
public Empresa getEmpresaDelUsuario()
public boolean puedeCrearPasantiaParaEmpresa(Integer empresaId)
public boolean puedeModificarPasantia(Integer pasantiaId)
public void validarPermisoCrearPasantia(Integer empresaId)
public void validarPermisoModificarPasantia(Integer pasantiaId)
```

---

### 2. EmpresaMapper.java (MODIFICADO)
**Ubicación**: `pasantias/src/main/java/com/seminario/pasantias/persistence/EmpresaMapper.java`

**Cambios**:
```java
// Agregado nuevo método
@Select("SELECT * FROM Empresa WHERE id_usuario = #{idUsuario}")
Empresa findByIdUsuario(@Param("idUsuario") Integer idUsuario);
```

---

### 3. Empresa.java (MODIFICADO)
**Ubicación**: `pasantias/src/main/java/com/seminario/pasantias/entity/Empresa.java`

**Cambios**:
```java
// Agregados nuevos campos para relación con Usuario
private Integer idUsuario;
private Boolean activo;
private LocalDateTime fechaCreacion;
```

---

### 4. PasantiaController.java (MODIFICADO)
**Ubicación**: `pasantias/src/main/java/com/seminario/pasantias/controller/PasantiaController.java`

**Cambios**:

#### Inyección de SecurityService
```java
private final SecurityService securityService;

@Autowired
public PasantiaController(PasantiaService pasantiaService, SecurityService securityService) {
    this.pasantiaService = pasantiaService;
    this.securityService = securityService;
}
```

#### Anotación @PreAuthorize en endpoints
```java
@PostMapping("/crear")
@PreAuthorize("hasAnyRole('EMPRESA', 'ADMINISTRADOR')")
public ResponseEntity<?> crearPasantia(@Valid @RequestBody PasantiaRequestDTO request) {
    // 1. Validar permisos
    securityService.validarPermisoCrearPasantia(request.getIdEmpresa());
    
    // 2. Crear pasantía
    PasantiaResponseDTO pasantia = pasantiaService.crearPasantia(request);
    ...
}
```

#### Manejo de SecurityException
```java
catch (SecurityException e) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("codigo", -3);
    errorResponse.put("mensaje", e.getMessage());
    errorResponse.put("tipo", "PERMISO_DENEGADO");
    
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
}
```

**Endpoints protegidos**:
- ✅ POST `/api/pasantias/crear` - @PreAuthorize + validación empresa
- ✅ PUT `/api/pasantias/{id}/actualizar` - @PreAuthorize + validación empresa
- ✅ PUT `/api/pasantias/{id}/estado` - @PreAuthorize + validación empresa
- ✅ DELETE `/api/pasantias/{id}` - @PreAuthorize + validación empresa

---

### 5. SecurityConfig.java (MODIFICADO)
**Ubicación**: `pasantias/src/main/java/com/seminario/pasantias/security/SecurityConfig.java`

**Cambios**:

#### Habilitación de @PreAuthorize
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // ← NUEVO
public class SecurityConfig {
```

#### Configuración de permisos para pasantías
```java
.authorizeHttpRequests(auth -> auth
    // Pasantías públicas (solo lectura)
    .requestMatchers(HttpMethod.GET, "/api/pasantias/publicadas").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/pasantias/buscar").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/pasantias/{id}").permitAll()
    
    // Pasantías protegidas (crear, modificar, eliminar)
    .requestMatchers("/api/pasantias/**").hasAnyRole("ADMINISTRADOR", "EMPRESA")
    ...
)
```

---

### 6. schema.sql (MODIFICADO)
**Ubicación**: `script_bd/sql/schema.sql`

**Cambios en tabla Empresa**:
```sql
CREATE TABLE Empresa (
    id_empresa INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    ...
    id_usuario INT,  -- ← NUEVO: FK a Usuario
    activo BOOLEAN DEFAULT TRUE,  -- ← NUEVO
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- ← NUEVO
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE SET NULL
);
```

**Usuarios empresa creados**:
```sql
-- Usuario para BIOFARMA S.A
INSERT INTO Usuario (username, email, password, id_rol, activo)
VALUES ('biofarma_user', 'rrhh@biofarmaweb.com.ar', '<bcrypt_hash>', 3, TRUE);

-- Usuario para Hospital Italiano
INSERT INTO Usuario (username, email, password, id_rol, activo)
VALUES ('hospital_user', 'seleccion@hospital-italiano.org.ar', '<bcrypt_hash>', 3, TRUE);
```

**Relación Empresa-Usuario**:
```sql
-- BIOFARMA S.A vinculada a usuario 4
INSERT INTO Empresa (..., id_usuario, activo, fecha_creacion)
VALUES (..., 4, TRUE, CURRENT_TIMESTAMP);

-- Hospital Italiano vinculado a usuario 5
INSERT INTO Empresa (..., id_usuario, activo, fecha_creacion)
VALUES (..., 5, TRUE, CURRENT_TIMESTAMP);
```

---

### 7. test_crear_pasantia_con_seguridad.sh (NUEVO)
**Ubicación**: Raíz del proyecto

**Descripción**: Script bash completo que prueba todos los escenarios de seguridad

**Tests incluidos**:
1. ✅ Login como usuario EMPRESA (biofarma_user)
2. ✅ Crear pasantía para propia empresa → 201 Created
3. ✅ Intentar crear para otra empresa → 403 Forbidden
4. ✅ Login como usuario ADMINISTRADOR
5. ✅ Crear pasantía para cualquier empresa → 201 Created
6. ✅ Intentar crear sin token JWT → 401 Unauthorized

**Uso**:
```bash
chmod +x test_crear_pasantia_con_seguridad.sh
./test_crear_pasantia_con_seguridad.sh
```

---

### 8. PASANTIAS_SECURITY.md (NUEVO)
**Ubicación**: Raíz del proyecto

**Contenido**:
- 📋 Resumen de seguridad
- 🔐 Requisitos implementados
- 🔄 Flujo de validación completo
- 📝 Ejemplos de uso con curl
- 🗄️ Estructura de base de datos
- 🧪 Instrucciones de testing
- 🛠️ Componentes de seguridad
- 🔑 Códigos de respuesta HTTP

---

## 🔄 Flujo de Validación Completo

```
Cliente → Request con JWT
    ↓
JwtAuthenticationFilter
    ↓ (valida token, carga usuario)
SecurityContext (usuario autenticado)
    ↓
PasantiaController
    ↓
@PreAuthorize("hasAnyRole('EMPRESA', 'ADMINISTRADOR')")
    ↓ (verifica rol)
SecurityService.validarPermisoCrearPasantia(empresaId)
    ↓
    ├─ ¿Es ADMINISTRADOR? → ✅ Permitir
    │
    └─ ¿Es EMPRESA?
           ↓
       ¿empresa.id_usuario == usuario.id_usuario?
           ├─ SÍ → ✅ Permitir
           └─ NO → ❌ 403 Forbidden
    ↓
PasantiaService.crearPasantia()
    ↓ (forzar estado PENDIENTE_DE_APROBACION)
Base de Datos
```

---

## 🎯 Casos de Uso

### Caso 1: Usuario EMPRESA crea para su empresa ✅
```
Usuario: biofarma_user (id_usuario=4, rol=EMPRESA)
Empresa: BIOFARMA S.A (id_empresa=1, id_usuario=4)
Request: { idEmpresa: 1, ... }
Resultado: ✅ 201 Created - Estado: PENDIENTE_DE_APROBACION
```

### Caso 2: Usuario EMPRESA intenta crear para otra empresa ❌
```
Usuario: biofarma_user (id_usuario=4, rol=EMPRESA)
Empresa: Hospital Italiano (id_empresa=2, id_usuario=5)
Request: { idEmpresa: 2, ... }
Resultado: ❌ 403 Forbidden
Mensaje: "No tienes permiso para crear pasantías para la empresa 2. 
         Solo puedes crear pasantías para tu empresa (1 - BIOFARMA S.A)"
```

### Caso 3: Usuario ADMINISTRADOR crea para cualquier empresa ✅
```
Usuario: admin (rol=ADMINISTRADOR)
Empresa: Hospital Italiano (id_empresa=2)
Request: { idEmpresa: 2, ... }
Resultado: ✅ 201 Created - Estado: PENDIENTE_DE_APROBACION
```

### Caso 4: Request sin autenticación ❌
```
Usuario: (ninguno)
Request: { idEmpresa: 1, ... } (sin token JWT)
Resultado: ❌ 401 Unauthorized
```

### Caso 5: Usuario ESTUDIANTE intenta crear ❌
```
Usuario: juan_perez (rol=ESTUDIANTE)
Request: { idEmpresa: 1, ... }
Resultado: ❌ 403 Forbidden (bloqueado por @PreAuthorize)
```

---

## 🧪 Verificación

### 1. Compilación
```bash
cd pasantias
mvn clean compile
```
**Estado**: ✅ Sin errores de compilación

### 2. Tests Automáticos
```bash
./test_crear_pasantia_con_seguridad.sh
```
**Resultado esperado**: 
- ✅ Todos los tests pasan
- ✅ 6 escenarios verificados

### 3. Verificación en Base de Datos
```sql
-- Ver pasantías creadas con estado correcto
SELECT 
    p.id_pasantia,
    p.titulo,
    p.estado,
    e.nombre AS empresa,
    e.id_usuario AS empresa_usuario_id,
    p.fecha_creacion
FROM Pasantia p
JOIN Empresa e ON p.id_empresa = e.id_empresa
WHERE p.estado = 'PENDIENTE_DE_APROBACION'
ORDER BY p.fecha_creacion DESC;
```

---

## 📊 Códigos de Respuesta

| HTTP Status | Código | Descripción | Escenario |
|-------------|--------|-------------|-----------|
| 201 Created | 0 | Pasantía creada | Usuario tiene permisos |
| 400 Bad Request | -1 | Datos inválidos | Validación DTO falló |
| 401 Unauthorized | - | Sin autenticación | Token JWT faltante/inválido |
| 403 Forbidden | -3 | Sin permisos | Usuario no puede crear para esa empresa |
| 409 Conflict | -2 | Regla negocio | Estado inválido, empresa no existe, etc. |
| 500 Internal Server Error | -99 | Error servidor | Excepción no manejada |

---

## 🔒 Principios de Seguridad Aplicados

1. ✅ **Authentication**: JWT obligatorio en todos los endpoints protegidos
2. ✅ **Authorization**: Verificación de rol con @PreAuthorize
3. ✅ **Ownership Validation**: Usuario EMPRESA solo accede a sus recursos
4. ✅ **Least Privilege**: Mínimos permisos necesarios por rol
5. ✅ **Defense in Depth**: Múltiples capas de validación
6. ✅ **Audit Trail**: id_usuario registrado en Empresa para trazabilidad
7. ✅ **Fail Secure**: Bloqueo por defecto, permiso explícito requerido
8. ✅ **Error Messages**: Mensajes específicos sin exponer detalles internos

---

## 📚 Documentación Adicional

- **[PASANTIAS_SECURITY.md](PASANTIAS_SECURITY.md)** - Documentación detallada de seguridad
- **[TEST_CREAR_PASANTIA.md](TEST_CREAR_PASANTIA.md)** - Tests funcionales básicos
- **[ENDPOINTS_SECURITY.md](ENDPOINTS_SECURITY.md)** - Todos los endpoints y su seguridad
- **[SECURITY.md](SECURITY.md)** - Arquitectura general de seguridad

---

## 🎉 Estado Final

### ✅ Implementación Completa
- [x] SecurityService creado con lógica de validación
- [x] EmpresaMapper con método findByIdUsuario
- [x] Entidad Empresa actualizada con id_usuario
- [x] PasantiaController protegido con @PreAuthorize
- [x] SecurityConfig habilitado para method security
- [x] Schema SQL con relación Usuario-Empresa
- [x] Usuarios empresa de prueba creados
- [x] Script de tests con autenticación JWT
- [x] Documentación completa

### ✅ Compilación Sin Errores
- SecurityService.java: ✅ No errors
- PasantiaController.java: ✅ No errors
- EmpresaMapper.java: ✅ No errors
- Empresa.java: ✅ No errors
- SecurityConfig.java: ✅ No errors

### ✅ Ready for Testing
El sistema está listo para:
1. Iniciar la aplicación
2. Ejecutar script de tests
3. Verificar resultados en base de datos

---

**Autor**: GitHub Copilot  
**Fecha**: 2025-02-01  
**Versión**: 1.0.0  
**Estado**: ✅ COMPLETADO
