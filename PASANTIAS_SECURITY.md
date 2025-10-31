# Seguridad en Endpoint /api/pasantias/crear

## 📋 Resumen

El endpoint `/api/pasantias/crear` está protegido con autenticación JWT y autorización basada en roles.

## 🔐 Requisitos de Seguridad Implementados

### 1. Autenticación Obligatoria
- **Método**: JWT Bearer Token
- **Header**: `Authorization: Bearer <token>`
- **Sin token**: Respuesta `401 Unauthorized`

### 2. Autorización por Roles

#### Roles Permitidos
- ✅ **ADMINISTRADOR** (rol ID 1)
- ✅ **EMPRESA** (rol ID 3)
- ❌ ESTUDIANTE (rol ID 2) - NO permitido
- ❌ SUPERVISOR (rol ID 4) - NO permitido

### 3. Validación de Propiedad

#### Usuario con rol EMPRESA
- ✅ **Puede crear** pasantías para su propia empresa
- ❌ **NO puede crear** pasantías para otras empresas
- **Validación**: `Empresa.id_usuario = Usuario.id_usuario`

#### Usuario con rol ADMINISTRADOR
- ✅ **Puede crear** pasantías para cualquier empresa
- ✅ Sin restricciones de empresa

## 🔄 Flujo de Validación

```
1. Request → JWT Filter
   ↓
2. ¿Token válido?
   → NO → 401 Unauthorized
   → SÍ → Continuar
   ↓
3. @PreAuthorize("hasAnyRole('EMPRESA', 'ADMINISTRADOR')")
   ↓
4. ¿Tiene rol correcto?
   → NO → 403 Forbidden
   → SÍ → Continuar
   ↓
5. SecurityService.validarPermisoCrearPasantia(empresaId)
   ↓
6. ¿Es ADMINISTRADOR?
   → SÍ → Permitir ✅
   ↓
7. ¿Es EMPRESA?
   ↓
8. ¿empresa.id_usuario = usuario.id_usuario?
   → NO → 403 Forbidden + mensaje explicativo
   → SÍ → Permitir ✅
   ↓
9. PasantiaService.crearPasantia()
   ↓
10. Estado forzado: PENDIENTE_DE_APROBACION
```

## 📝 Ejemplos de Uso

### Ejemplo 1: Usuario EMPRESA crea para su empresa ✅

**Datos:**
- Usuario: `biofarma_user` (rol: EMPRESA, id_usuario: 4)
- Empresa: BIOFARMA S.A (id_empresa: 1, id_usuario: 4)
- Match: ✅ id_usuario coincide

**Request:**
```bash
curl -X POST http://localhost:8080/api/pasantias/crear \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR..." \
  -d '{
    "titulo": "Desarrollador Backend Java",
    "idEmpresa": 1,
    ...
  }'
```

**Response: 201 Created**
```json
{
  "codigo": 0,
  "mensaje": "Pasantía creada exitosamente. Estado inicial: PENDIENTE_DE_APROBACION",
  "data": {
    "idPasantia": 123,
    "estado": "PENDIENTE_DE_APROBACION",
    ...
  }
}
```

---

### Ejemplo 2: Usuario EMPRESA intenta crear para otra empresa ❌

**Datos:**
- Usuario: `biofarma_user` (rol: EMPRESA, id_usuario: 4)
- Empresa: Hospital Italiano (id_empresa: 2, id_usuario: 5)
- Match: ❌ id_usuario NO coincide

**Request:**
```bash
curl -X POST http://localhost:8080/api/pasantias/crear \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR..." \
  -d '{
    "titulo": "Desarrollador Frontend",
    "idEmpresa": 2,
    ...
  }'
```

**Response: 403 Forbidden**
```json
{
  "codigo": -3,
  "mensaje": "No tienes permiso para crear pasantías para la empresa 2. Solo puedes crear pasantías para tu empresa (1 - BIOFARMA S.A)",
  "tipo": "PERMISO_DENEGADO"
}
```

---

### Ejemplo 3: Usuario ADMINISTRADOR crea para cualquier empresa ✅

**Datos:**
- Usuario: `admin` (rol: ADMINISTRADOR)
- Empresa: Hospital Italiano (id_empresa: 2)
- Validación: ✅ Administrador puede crear para cualquier empresa

**Request:**
```bash
curl -X POST http://localhost:8080/api/pasantias/crear \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR..." \
  -d '{
    "titulo": "Analista de Datos",
    "idEmpresa": 2,
    ...
  }'
```

**Response: 201 Created**
```json
{
  "codigo": 0,
  "mensaje": "Pasantía creada exitosamente. Estado inicial: PENDIENTE_DE_APROBACION",
  "data": {
    "idPasantia": 124,
    "estado": "PENDIENTE_DE_APROBACION",
    ...
  }
}
```

---

### Ejemplo 4: Request sin token JWT ❌

**Request:**
```bash
curl -X POST http://localhost:8080/api/pasantias/crear \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Pasantía sin autenticación",
    "idEmpresa": 1,
    ...
  }'
```

**Response: 401 Unauthorized**
```json
{
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

---

### Ejemplo 5: Token JWT de usuario ESTUDIANTE ❌

**Datos:**
- Usuario: `juan_perez` (rol: ESTUDIANTE)

**Request:**
```bash
curl -X POST http://localhost:8080/api/pasantias/crear \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token_estudiante>" \
  -d '{
    "titulo": "Intentando crear pasantía",
    "idEmpresa": 1,
    ...
  }'
```

**Response: 403 Forbidden**
```json
{
  "error": "Forbidden",
  "message": "Access Denied"
}
```

## 🗄️ Estructura de Base de Datos

### Tabla Usuario
```sql
CREATE TABLE Usuario (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    FOREIGN KEY (id_rol) REFERENCES Rol(id_rol)
);
```

### Tabla Empresa
```sql
CREATE TABLE Empresa (
    id_empresa INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    id_usuario INT,  -- ← Nuevo campo
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE SET NULL
);
```

### Usuarios de Ejemplo
```sql
-- Usuario ADMINISTRADOR
INSERT INTO Usuario (username, email, password, id_rol, activo)
VALUES ('admin', 'admin@sistema.com', '<bcrypt_hash>', 1, TRUE);

-- Usuario EMPRESA - BIOFARMA
INSERT INTO Usuario (username, email, password, id_rol, activo)
VALUES ('biofarma_user', 'rrhh@biofarmaweb.com.ar', '<bcrypt_hash>', 3, TRUE);

-- Usuario EMPRESA - Hospital Italiano
INSERT INTO Usuario (username, email, password, id_rol, activo)
VALUES ('hospital_user', 'seleccion@hospital-italiano.org.ar', '<bcrypt_hash>', 3, TRUE);
```

### Relación Empresa-Usuario
```sql
-- BIOFARMA S.A vinculada a biofarma_user
INSERT INTO Empresa (nombre, cuit, id_usuario, ...) 
VALUES ('BIOFARMA S.A', '30-12345678-9', 4, ...);

-- Hospital Italiano vinculado a hospital_user
INSERT INTO Empresa (nombre, cuit, id_usuario, ...) 
VALUES ('Hospital Italiano de Buenos Aires', '30-87654321-0', 5, ...);
```

## 🧪 Testing

### Script de Pruebas
Ejecutar: `./test_crear_pasantia_con_seguridad.sh`

**Tests incluidos:**
1. ✅ Login de usuario EMPRESA
2. ✅ Crear pasantía para propia empresa (éxito)
3. ✅ Intentar crear para otra empresa (fallo 403)
4. ✅ Login de usuario ADMINISTRADOR
5. ✅ Crear pasantía para cualquier empresa (éxito)
6. ✅ Intentar crear sin token (fallo 401)

### Verificación en Base de Datos
```sql
-- Ver pasantías creadas y su estado
SELECT 
    p.id_pasantia,
    p.titulo,
    p.estado,
    e.nombre AS empresa,
    e.id_usuario AS empresa_usuario_id
FROM Pasantia p
JOIN Empresa e ON p.id_empresa = e.id_empresa
ORDER BY p.fecha_creacion DESC;
```

## 🛠️ Componentes de Seguridad

### 1. SecurityService.java
```java
@Service
public class SecurityService {
    // Obtiene usuario autenticado desde SecurityContext
    public Usuario getUsuarioAutenticado();
    
    // Verifica si tiene rol específico
    public boolean tieneRol(String nombreRol);
    
    // Obtiene empresa del usuario EMPRESA
    public Empresa getEmpresaDelUsuario();
    
    // Valida permiso para crear pasantía
    public void validarPermisoCrearPasantia(Integer empresaId);
    
    // Valida permiso para modificar pasantía
    public void validarPermisoModificarPasantia(Integer pasantiaId);
}
```

### 2. PasantiaController.java
```java
@PostMapping("/crear")
@PreAuthorize("hasAnyRole('EMPRESA', 'ADMINISTRADOR')")
public ResponseEntity<?> crearPasantia(@Valid @RequestBody PasantiaRequestDTO request) {
    // 1. Validar permisos
    securityService.validarPermisoCrearPasantia(request.getIdEmpresa());
    
    // 2. Crear pasantía
    PasantiaResponseDTO pasantia = pasantiaService.crearPasantia(request);
    
    // 3. Retornar respuesta
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

### 3. SecurityConfig.java
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // ← Habilita @PreAuthorize
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http.authorizeHttpRequests(auth -> auth
            // Pasantías públicas (buscar, ver)
            .requestMatchers(HttpMethod.GET, "/api/pasantias/publicadas").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/pasantias/buscar").permitAll()
            
            // Pasantías protegidas (crear, modificar, eliminar)
            .requestMatchers("/api/pasantias/**").hasAnyRole("ADMINISTRADOR", "EMPRESA")
            ...
        );
    }
}
```

## 🔑 Códigos de Respuesta

| Código | Nombre | Descripción |
|--------|--------|-------------|
| 201 | Created | Pasantía creada exitosamente |
| 400 | Bad Request | Datos de entrada inválidos |
| 401 | Unauthorized | Token JWT faltante o inválido |
| 403 | Forbidden | Sin permisos para esta acción |
| 409 | Conflict | Error de regla de negocio |
| 500 | Internal Server Error | Error inesperado del servidor |

## 📊 Códigos de Error Personalizados

```json
{
  "codigo": -3,  // Error de permisos
  "codigo": -1,  // Error de validación
  "codigo": -2,  // Error de regla de negocio
  "codigo": -99  // Error interno
}
```

## 🔒 Mejores Prácticas Implementadas

1. ✅ **Autenticación JWT**: Tokens seguros y stateless
2. ✅ **Autorización por Rol**: @PreAuthorize con roles
3. ✅ **Validación de Propiedad**: Usuario-Empresa ownership
4. ✅ **Principio de Least Privilege**: Mínimos permisos necesarios
5. ✅ **Mensajes de Error Específicos**: Feedback claro al usuario
6. ✅ **Separación de Responsabilidades**: SecurityService dedicado
7. ✅ **Estado Forzado**: PENDIENTE_DE_APROBACION siempre
8. ✅ **Trazabilidad**: id_usuario en tabla Empresa

## 📚 Documentación Relacionada

- [ENDPOINTS_SECURITY.md](ENDPOINTS_SECURITY.md) - Lista completa de endpoints
- [TEST_CREAR_PASANTIA.md](TEST_CREAR_PASANTIA.md) - Tests funcionales
- [SECURITY.md](SECURITY.md) - Arquitectura de seguridad general
- [README.md](README.md) - Documentación principal del proyecto

---

**Última actualización**: 2025-02-01  
**Versión**: 1.0.0  
**Estado**: ✅ Implementado y probado
