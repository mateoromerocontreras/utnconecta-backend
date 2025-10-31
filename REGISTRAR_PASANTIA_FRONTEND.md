# 📝 Frontend: Registrar Pasantía

## 📋 Descripción

Página frontend para que usuarios con rol **EMPRESA** puedan registrar nuevas pasantías en el sistema. La pasantía se crea en estado `PENDIENTE_DE_APROBACION` y debe ser aprobada por un administrador antes de ser visible para estudiantes.

---

## 🔐 Control de Acceso

### Requisitos
- ✅ Usuario autenticado con JWT
- ✅ Rol: **EMPRESA** (id_rol = 3)
- ✅ Usuario debe tener una empresa asociada en el sistema

### Validaciones de Seguridad
1. **Verificación de sesión**: Se valida token JWT almacenado
2. **Verificación de rol**: Solo usuarios EMPRESA pueden acceder
3. **Empresa asociada**: El usuario debe estar vinculado a una empresa
4. **Permiso de creación**: Backend valida que el usuario pueda crear para su empresa

---

## 🎯 Características Implementadas

### 1. Carga Automática de Datos
- ✅ Obtiene empresa del usuario autenticado
- ✅ Precarga email de contacto de la empresa
- ✅ Carga lista de carreras disponibles desde API
- ✅ Validación de permisos antes de mostrar formulario

### 2. Formulario Completo
**Campos obligatorios:**
- Título de la pasantía
- Puesto a cubrir
- Ciudad
- Fecha de publicación
- Fecha de caducidad
- Al menos una carrera seleccionada

**Campos opcionales:**
- Asignación estímulo (salario)
- Email de contacto
- Cantidad de pasantes (default: 1)
- Modalidad (Presencial/Remoto/Híbrida)

### 3. Validaciones Frontend
- ✅ Campos obligatorios marcados con `*`
- ✅ Validación de fechas (publicación < caducidad)
- ✅ Validación de carreras (mínimo 1 seleccionada)
- ✅ Feedback inmediato de errores
- ✅ Deshabilita botón durante envío

### 4. Integración con Backend
- ✅ Endpoint: `POST /api/pasantias/crear`
- ✅ Autenticación JWT en header
- ✅ Envío de `idEmpresa` automático
- ✅ Manejo de respuestas HTTP (201, 400, 401, 403, 500)
- ✅ Mensajes descriptivos según error

---

## 📂 Archivos Creados

### 1. `RegistrarPasantia.jsx`
**Ubicación**: `frontend/src/pages/RegistrarPasantia.jsx`

**Funcionalidades**:
```javascript
// Estado del formulario
const [form, setForm] = useState({...});
const [empresaId, setEmpresaId] = useState(null);
const [carreras, setCarreras] = useState([]);

// Obtener empresa del usuario
useEffect(() => {
  fetchEmpresaDelUsuario(); // Valida rol y obtiene empresa
}, []);

// Obtener carreras disponibles
useEffect(() => {
  fetchCarreras(); // Carga desde /carreras/listarCarreras
}, []);

// Submit con validaciones
const onSubmit = async (e) => {
  // 1. Validar campos obligatorios
  // 2. Construir payload con idEmpresa
  // 3. Enviar a /api/pasantias/crear con JWT
  // 4. Manejar respuesta y mostrar mensaje
};
```

**Estados de carga**:
- `loading`: Cargando empresa y permisos
- `submitting`: Enviando formulario
- `message` + `messageType`: Feedback al usuario

---

### 2. `registrar-pasantia.css`
**Ubicación**: `frontend/src/styles/registrar-pasantia.css`

**Estilos principales**:
```css
.pasantia-hero        /* Container principal con fondo */
.pasantia-card        /* Tarjeta del formulario */
.pasantia-grid        /* Grid 2 columnas responsive */
.section-title        /* Títulos de sección */
.carreras-grid        /* Grid de checkboxes para carreras */
.checkbox-label       /* Estilo de checkbox + label */
.pasantia-actions     /* Botones de acción */
.alert-success/error  /* Mensajes de feedback */
.pasantia-info        /* Nota informativa sobre aprobación */
```

**Responsive**:
- Desktop: Grid 2 columnas
- Tablet (≤768px): Grid 1 columna
- Mobile (≤480px): Optimizado para pantallas pequeñas

---

### 3. `App.jsx` (MODIFICADO)
**Ruta agregada**:
```jsx
import RegistrarPasantia from "./pages/RegistrarPasantia.jsx";

<Route path="/registrar-pasantia" element={<RegistrarPasantia />} />
```

---

## 🚀 Uso

### 1. Acceso a la Página
**URL**: `http://localhost:5173/registrar-pasantia`

**Flujo**:
1. Usuario EMPRESA inicia sesión
2. Navega a `/registrar-pasantia`
3. Sistema valida automáticamente:
   - ✅ Token JWT válido
   - ✅ Rol EMPRESA
   - ✅ Empresa asociada existe
4. Si OK: Muestra formulario
5. Si NO: Muestra mensaje de error

---

### 2. Ejemplo de Navegación
Agregar botón en navbar o página de perfil:

```jsx
{/* En Navbar.jsx o componente de perfil */}
{rol === "EMPRESA" && (
  <a href="/registrar-pasantia" className="nav-link">
    Publicar Pasantía
  </a>
)}
```

---

### 3. Flujo Completo

#### Paso 1: Llenar Formulario
```
Título: Desarrollador Backend Java
Puesto: Desarrollador Junior Backend
Ciudad: Santa Fe
Modalidad: Híbrida
Asignación: 65000
Cantidad: 2
Fecha Publicación: 2025-02-01
Fecha Caducidad: 2025-05-01
Email: rrhh@biofarma.com.ar
Carreras: [✓] Ingeniería en Sistemas
         [✓] Ingeniería Informática
```

#### Paso 2: Submit
```javascript
// Frontend envía:
POST /api/pasantias/crear
Headers: {
  "Authorization": "Bearer eyJhbGc...",
  "Content-Type": "application/json"
}
Body: {
  "titulo": "Desarrollador Backend Java",
  "puestoACubrir": "Desarrollador Junior Backend",
  "ciudad": "Santa Fe",
  "modalidad": "Híbrida",
  "asignacionEstimulo": 65000.0,
  "cantidadDePasantes": 2,
  "fechaPublicacion": "2025-02-01",
  "fechaCaducidad": "2025-05-01",
  "idEmpresa": 1,  // ← Autodetectado
  "idsCarreras": [1, 2],
  "emailContacto": "rrhh@biofarma.com.ar"
}
```

#### Paso 3: Backend Valida
```
1. JWT válido ✅
2. Rol EMPRESA ✅
3. Usuario pertenece a empresa 1 ✅
4. Empresa 1 existe ✅
5. Carreras 1 y 2 existen ✅
6. Fechas válidas ✅
```

#### Paso 4: Respuesta
```json
{
  "codigo": 0,
  "mensaje": "Pasantía creada exitosamente. Estado inicial: PENDIENTE_DE_APROBACION",
  "data": {
    "idPasantia": 123,
    "titulo": "Desarrollador Backend Java",
    "estado": "PENDIENTE_DE_APROBACION",
    "empresa": {
      "idEmpresa": 1,
      "nombre": "BIOFARMA S.A"
    },
    ...
  }
}
```

#### Paso 5: Frontend Muestra Éxito
```
✅ ¡Pasantía registrada con éxito! 
   Estado: PENDIENTE_DE_APROBACION
```

---

## 🛡️ Manejo de Errores

### Error 401 - No Autenticado
```
Usuario no tiene token JWT válido
→ Mensaje: "❌ Sesión expirada. Vuelve a iniciar sesión."
```

### Error 403 - Sin Permisos
```
Usuario no es EMPRESA o no es dueño de la empresa
→ Mensaje: "❌ No tienes permiso para crear esta pasantía."
```

### Error 400 - Datos Inválidos
```
Campos requeridos faltantes o formato incorrecto
→ Mensaje: "⚠️ Datos inválidos. Revisa el formulario."
```

### Usuario Sin Empresa Asociada
```
Usuario EMPRESA pero sin empresa en base de datos
→ Mensaje: "⚠️ No se encontró una empresa asociada a tu usuario. 
           Contacta al administrador."
```

---

## 📊 Estados de la Pasantía

### Estado Inicial (Automático)
```
PENDIENTE_DE_APROBACION
```

### Flujo de Estados
```
PENDIENTE_DE_APROBACION
    ↓ (Administrador aprueba)
PUBLICADA
    ↓ (Estudiantes postulan)
CUBIERTA / EXPIRADA / FINALIZADA
```

### Nota Importante
El estado `PENDIENTE_DE_APROBACION` se fuerza en el backend:
- ✅ Usuario NO puede seleccionar estado
- ✅ Siempre inicia en PENDIENTE
- ✅ Solo ADMINISTRADOR puede aprobar → PUBLICADA

---

## 🧪 Testing Manual

### Test 1: Acceso con Usuario EMPRESA ✅
```bash
1. Login como biofarma_user (rol EMPRESA)
2. Ir a /registrar-pasantia
3. Verificar: Formulario se muestra
4. Verificar: Email precargado
5. Verificar: Lista de carreras visible
```

### Test 2: Crear Pasantía Exitosa ✅
```bash
1. Llenar todos los campos obligatorios
2. Seleccionar 2 carreras
3. Click "Publicar pasantía"
4. Verificar: Mensaje de éxito
5. Verificar: Formulario se limpia
6. Verificar en BD: Pasantía creada con estado PENDIENTE_DE_APROBACION
```

### Test 3: Validación de Campos ✅
```bash
1. Dejar título vacío
2. Click submit
3. Verificar: Mensaje "⚠️ El título es obligatorio."
4. Llenar título
5. No seleccionar carreras
6. Click submit
7. Verificar: Mensaje "⚠️ Debes seleccionar al menos una carrera."
```

### Test 4: Acceso con Rol Incorrecto ❌
```bash
1. Login como estudiante (rol ESTUDIANTE)
2. Ir a /registrar-pasantia
3. Verificar: Mensaje "❌ Solo usuarios con rol EMPRESA pueden crear pasantías."
4. Verificar: Formulario NO se muestra
```

### Test 5: Sin Autenticación ❌
```bash
1. Logout
2. Ir a /registrar-pasantia
3. Verificar: Mensaje "⚠️ Debes iniciar sesión como empresa..."
```

---

## 🔗 Integración con Navbar

Para agregar enlace en Navbar (solo visible para EMPRESA):

```jsx
// En Navbar.jsx
const rol = getStoredItem("rol"); // Obtener desde localStorage/sessionStorage

{rol === "EMPRESA" && (
  <a href="/registrar-pasantia" className="nav-link">
    <span className="icon">📝</span> Publicar Pasantía
  </a>
)}
```

---

## 📝 Próximas Mejoras

### Funcionalidades Sugeridas
- [ ] Previsualización de pasantía antes de publicar
- [ ] Guardar como borrador
- [ ] Editar pasantías existentes (lista de mis pasantías)
- [ ] Ver estadísticas de postulaciones
- [ ] Filtro de carreras por facultad
- [ ] Validación avanzada de fechas (no permitir fechas pasadas)
- [ ] Autocompletado de ciudad
- [ ] Editor rich text para descripción ampliada

### Optimizaciones
- [ ] Cache de lista de carreras
- [ ] Lazy loading de datos
- [ ] Debounce en validaciones
- [ ] Progressive enhancement

---

## 📚 Documentación Relacionada

- **Backend**: [PasantiaController.java](../pasantias/src/main/java/com/seminario/pasantias/controller/PasantiaController.java)
- **Seguridad**: [PASANTIAS_SECURITY.md](../PASANTIAS_SECURITY.md)
- **Testing**: [test_crear_pasantia_con_seguridad.sh](../test_crear_pasantia_con_seguridad.sh)
- **API Docs**: [TEST_CREAR_PASANTIA.md](../TEST_CREAR_PASANTIA.md)

---

**Autor**: GitHub Copilot  
**Fecha**: 2025-01-31  
**Versión**: 1.0.0  
**Estado**: ✅ IMPLEMENTADO
