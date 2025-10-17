# Tests del Endpoint `/auth/iniciarSesion`

Este documento contiene las instrucciones para probar completamente el endpoint de inicio de sesión con autenticación JWT.

## 📋 **Información del Endpoint**

- **URL**: `POST /auth/iniciarSesion`
- **Tipo**: Público (no requiere autenticación previa)
- **Propósito**: Autenticar usuario y generar token JWT con datos del usuario
- **Respuesta**: AuthResponse con token JWT, username, email y rol

## 🔧 **Preparación**

### Usuarios de Prueba Disponibles:

| Username | Password | Rol | Estado |
|----------|----------|-----|--------|
| `admin` | `admin123` | ADMINISTRADOR | Activo |
| `estudiante1` | `estudiante123` | ESTUDIANTE | Activo |
| `empresa1` | `empresa123` | EMPRESA | Activo |
| `testuser` | `password123` | ESTUDIANTE | Activo |
| `empresa_test` | `password123` | EMPRESA | Activo |

### Verificar que la aplicación esté corriendo:

```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health
# Debe retornar: 403 (aplicación corriendo)
```

## 🧪 **Casos de Prueba**

### ✅ **Test 1: Login exitoso con usuario admin**

```bash
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}' \
  -w "\n%{http_code}\n"
```

**Resultado esperado:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "admin",
  "email": "admin@example.com",
  "rol": "ADMINISTRADOR"
}
200
```

---

### ✅ **Test 2: Login exitoso con usuario estudiante**

```bash
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "estudiante1", "password": "estudiante123"}' \
  -w "\n%{http_code}\n"
```

**Resultado esperado:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "estudiante1",
  "email": "estudiante1@example.com",
  "rol": "ESTUDIANTE"
}
200
```

---

### ✅ **Test 3: Login exitoso con usuario empresa**

```bash
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "empresa1", "password": "empresa123"}' \
  -w "\n%{http_code}\n"
```

**Resultado esperado:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "empresa1",
  "email": "empresa1@example.com",
  "rol": "EMPRESA"
}
200
```

---

### ✅ **Test 4: Login exitoso con usuario creado dinámicamente**

```bash
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser123", "password": "password123"}' \
  -w "\n%{http_code}\n"
```

**Resultado esperado:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "testuser123",
  "email": "testuser123@example.com",
  "rol": "ESTUDIANTE"
}
200
```

---

### ❌ **Test 5: Error - Usuario inexistente**

```bash
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "usuarioInexistente", "password": "cualquierpass"}' \
  -w "\n%{http_code}\n"
```

**Resultado esperado:**
```json
{
  "token": null,
  "username": null,
  "email": null,
  "rol": null,
  "message": "Credenciales inválidas"
}
400
```

---

### ❌ **Test 6: Error - Contraseña incorrecta**

```bash
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "contraseñaIncorrecta"}' \
  -w "\n%{http_code}\n"
```

**Resultado esperado:**
```json
{
  "token": null,
  "username": null,
  "email": null,
  "rol": null,
  "message": "Credenciales inválidas"
}
400
```

---

### ❌ **Test 7: Error - Username vacío**

```bash
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "", "password": "admin123"}' \
  -w "\n%{http_code}\n"
```

**Resultado esperado:**
```json
{
  "token": null,
  "username": null,
  "email": null,
  "rol": null,
  "message": "El username es obligatorio"
}
400
```

---

### ❌ **Test 8: Error - Password vacío**

```bash
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": ""}' \
  -w "\n%{http_code}\n"
```

**Resultado esperado:**
```json
{
  "token": null,
  "username": null,
  "email": null,
  "rol": null,
  "message": "La contraseña es obligatoria"
}
400
```

---

### ❌ **Test 9: Error - JSON malformado**

```bash
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password":}' \
  -w "\n%{http_code}\n"
```

**Resultado esperado:**
```json
Error de parsing JSON
400
```

---

### ❌ **Test 10: Error - Campos faltantes**

```bash
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "admin"}' \
  -w "\n%{http_code}\n"
```

**Resultado esperado:**
```json
{
  "token": null,
  "username": null,
  "email": null,
  "rol": null,
  "message": "La contraseña es obligatoria"
}
400
```

## 🎯 **Script de Prueba Automatizado**

Puedes ejecutar todos los tests de una vez con este script:

```bash
#!/bin/bash

echo "=== Tests del Endpoint /auth/iniciarSesion ==="
echo

# Test 1: Login exitoso admin
echo "Test 1: Login exitoso admin"
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}' \
  -w "\n%{http_code}\n"
echo

# Test 2: Login exitoso estudiante
echo "Test 2: Login exitoso estudiante"
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "estudiante1", "password": "estudiante123"}' \
  -w "\n%{http_code}\n"
echo

# Test 3: Login exitoso empresa
echo "Test 3: Login exitoso empresa"
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "empresa1", "password": "empresa123"}' \
  -w "\n%{http_code}\n"
echo

# Test 4: Usuario inexistente
echo "Test 4: Usuario inexistente"
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "usuarioInexistente", "password": "cualquierpass"}' \
  -w "\n%{http_code}\n"
echo

# Test 5: Contraseña incorrecta
echo "Test 5: Contraseña incorrecta"
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "contraseñaIncorrecta"}' \
  -w "\n%{http_code}\n"
echo

# Test 6: Username vacío
echo "Test 6: Username vacío"
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "", "password": "admin123"}' \
  -w "\n%{http_code}\n"
echo

# Test 7: Password vacío
echo "Test 7: Password vacío"
curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": ""}' \
  -w "\n%{http_code}\n"
echo

echo "=== Fin de Tests ==="
```

## 📊 **Interpretación de Resultados**

### Respuestas Exitosas:
- **Token JWT**: String válido con formato `eyJhbGciOiJIUzUxMiJ9...`
- **Username**: Nombre del usuario autenticado
- **Email**: Email del usuario autenticado  
- **Rol**: Rol asignado al usuario
- **HTTP Status**: `200`

### Respuestas de Error:
- **Token**: `null`
- **Username**: `null`
- **Email**: `null`
- **Rol**: `null`
- **Message**: Descripción del error específico
- **HTTP Status**: `400`

### Mensajes de Error Posibles:
- `"El username es obligatorio"`
- `"La contraseña es obligatoria"`
- `"Credenciales inválidas"`
- `"Usuario inactivo"`
- `"Error en la autenticación: [detalle]"`

## 🔍 **Validaciones Implementadas**

1. ✅ **Validación de campos obligatorios**
2. ✅ **Verificación de existencia del usuario**
3. ✅ **Validación de contraseña con BCrypt**
4. ✅ **Verificación de estado activo del usuario**
5. ✅ **Autenticación completa con AuthenticationManager**
6. ✅ **Generación de token JWT con claims**
7. ✅ **Respuestas en formato AuthResponse**

## 📝 **Notas**

- Este endpoint **GENERA tokens JWT** para autenticación posterior
- El token JWT contiene claims: username, rol, fechas de emisión y expiración
- Todas las contraseñas están encriptadas con BCrypt
- El endpoint es público pero requiere credenciales válidas
- El token generado es válido por 1 hora (3600 segundos)
- Usar el token en header `Authorization: Bearer [token]` para endpoints protegidos