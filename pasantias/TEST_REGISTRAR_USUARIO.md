# API de Autenticación

Esta es una API simple para autenticación de usuarios.

## Endpoints

### `POST /usuarios/registrarUsuario`

Registra un nuevo usuario en el sistema.

#### Estructura del Request:
```json
{
  "username": "string",
  "email": "string", 
  "password": "string",
  "rol": "ESTUDIANTE|EMPRESA|ADMINISTRADOR|SUPERVISOR"
}
```

#### Validaciones:
- **Username**: Obligatorio y único en el sistema
- **Email**: Obligatorio
- **Password**: Obligatorio, debe cumplir requisitos de seguridad:
  - Mínimo 8 caracteres
  - Al menos 1 letra minúscula
  - Al menos 1 número
- **Rol**: Obligatorio, debe existir en la base de datos

#### Respuestas:
- **Éxito**: `{"code": 0, "message": null}` con HTTP 200
- **Error**: `{"code": -1, "message": "descripción del error"}` con HTTP 200

#### Ejemplos de uso:

**Registro exitoso:**
```bash
curl -X POST http://localhost:8080/usuarios/registrarUsuario \
  -H "Content-Type: application/json" \
  -d '{"username": "nuevo_usuario", "email": "usuario@email.com", "password": "mipassword123", "rol": "ESTUDIANTE"}'
```

**Respuesta:**
```json
{"code": 0, "message": null}
```

#### Tests de validación realizados:

**1. Username duplicado:**
```bash
curl -X POST http://localhost:8080/usuarios/registrarUsuario \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "email": "test2@example.com", "password": "password123", "rol": "ESTUDIANTE"}'
```
**Respuesta:**
```json
{"code": -1, "message": "El username ya existe"}
```

**2. Contraseña débil:**
```bash
curl -X POST http://localhost:8080/usuarios/registrarUsuario \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser2", "email": "test2@example.com", "password": "123", "rol": "ESTUDIANTE"}'
```
**Respuesta:**
```json
{"code": -1, "message": "La contraseña debe tener al menos 8 caracteres, una letra minúscula y un número"}
```

**3. Rol inexistente:**
```bash
curl -X POST http://localhost:8080/usuarios/registrarUsuario \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser2", "email": "test2@example.com", "password": "password123", "rol": "INEXISTENTE"}'
```
**Respuesta:**
```json
{"code": -1, "message": "El rol especificado no existe: INEXISTENTE"}
```

**4. Registro con rol EMPRESA:**
```bash
curl -X POST http://localhost:8080/usuarios/registrarUsuario \
  -H "Content-Type: application/json" \
  -d '{"username": "empresa_test", "email": "empresa@example.com", "password": "password123", "rol": "EMPRESA"}'
```
**Respuesta:**
```json
{"code": 0, "message": null}
```

#### Roles disponibles:
- `ESTUDIANTE`: Estudiante que busca pasantías
- `EMPRESA`: Empresa que ofrece pasantías  
- `ADMINISTRADOR`: Administrador del sistema con acceso completo
- `SUPERVISOR`: Supervisor de pasantías

#### Configuración de Seguridad:
- Endpoint público, no requiere autenticación
- Las contraseñas se encriptan automáticamente con BCrypt
- Los usuarios se crean en estado activo por defecto