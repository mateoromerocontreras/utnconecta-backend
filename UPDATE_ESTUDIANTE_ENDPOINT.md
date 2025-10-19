# Endpoint updateEstudiante

## Descripción
Endpoint que permite a un estudiante actualizar su email y/o contraseña.

## URL
```
PUT /estudiantes/updateEstudiante
```

## Parámetros de Request

### Query Parameter
| Parámetro    | Tipo   | Requerido | Descripción                                    |
|--------------|--------|-----------|-----------------------------------------------|
| currentEmail | String | Sí        | Email actual del estudiante para identificarlo |

### Request Body (EstudianteUpdateProfileRequest)
| Campo    | Tipo   | Requerido | Descripción                                    |
|----------|--------|-----------|-----------------------------------------------|
| email    | String | No        | Nuevo email (se verifica que no exista)       |
| password | String | No        | Nueva contraseña (mín. 8 chars, 1 letra, 1 número) |

**Nota:** Al menos uno de los campos (email o password) debe ser proporcionado.

## Validaciones

### Email
- ✅ Formato válido de email
- ✅ No puede existir otro usuario con el mismo email
- ✅ Si es igual al actual, no se actualiza

### Contraseña
- ✅ Mínimo 8 caracteres
- ✅ Al menos 1 letra minúscula
- ✅ Al menos 1 número
- ✅ Se encripta automáticamente con BCrypt

### Autorización
- ✅ El usuario debe ser de rol ESTUDIANTE
- ✅ El email actual debe existir en el sistema

## Ejemplos de Request

### 1. Cambiar solo email
```json
PUT /estudiantes/updateEstudiante?currentEmail=juan.viejo@estudiantes.com

{
  "email": "juan.nuevo@estudiantes.com"
}
```

### 2. Cambiar solo contraseña
```json
PUT /estudiantes/updateEstudiante?currentEmail=juan@estudiantes.com

{
  "password": "nuevaPassword123"
}
```

### 3. Cambiar email y contraseña
```json
PUT /estudiantes/updateEstudiante?currentEmail=juan.viejo@estudiantes.com

{
  "email": "juan.nuevo@estudiantes.com",
  "password": "nuevaPassword123"
}
```

## Response

### Éxito
```json
{
  "code": 0,
  "message": "Perfil actualizado exitosamente"
}
```

### Errores Comunes

#### Email ya existe
```json
{
  "code": -1,
  "message": "Ya existe un usuario con ese email"
}
```

#### Contraseña inválida
```json
{
  "code": -1,
  "message": "La contraseña debe tener al menos 8 caracteres, una letra minúscula y un número"
}
```

#### Usuario no encontrado
```json
{
  "code": -1,
  "message": "Usuario no encontrado"
}
```

#### Sin campos para actualizar
```json
{
  "code": -1,
  "message": "Debe proporcionar al menos un campo para actualizar (email o contraseña)"
}
```

## Comportamiento

### Actualización de Email
1. Verifica que el nuevo email no exista
2. Actualiza el email en la tabla `Usuario`
3. Actualiza el username en la tabla `Usuario` (mantiene consistencia)
4. Actualiza el email en la tabla `Estudiante`

### Actualización de Contraseña
1. Valida el formato de la contraseña
2. Encripta la nueva contraseña con BCrypt
3. Actualiza en la tabla `Usuario`

## Seguridad

- 🔒 **Encriptación**: Las contraseñas se encriptan con BCrypt
- 🔒 **Validación de rol**: Solo estudiantes pueden usar este endpoint
- 🔒 **Verificación de existencia**: Previene duplicados de email
- 🔒 **Validación de entrada**: Formato de email y fortaleza de contraseña

## Archivos Creados/Modificados

1. **Nuevo DTO:** `EstudianteUpdateProfileRequest.java`
2. **Service modificado:** `EstudianteService.java` - Agregado método `updateEstudianteProfile()`
3. **Controller modificado:** `EstudianteController.java` - Agregado endpoint `PUT /updateEstudiante`

## Casos de Uso

- ✅ Estudiante quiere cambiar su email por uno más actual
- ✅ Estudiante quiere cambiar su contraseña por seguridad
- ✅ Estudiante necesita actualizar ambos datos a la vez
- ✅ Prevenir que dos estudiantes tengan el mismo email