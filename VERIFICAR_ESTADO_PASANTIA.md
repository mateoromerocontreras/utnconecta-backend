# Cómo Verificar el Cambio de Estado de una Pasantía

Esta guía explica diferentes métodos para verificar si una pasantía cambió exitosamente de `PENDIENTE_DE_APROBACION` a `PUBLICADA`.

## 📋 Métodos de Verificación

### 1. **Vía API REST (Recomendado)**

#### Obtener detalles de una pasantía específica:
```bash
curl -X GET "http://localhost:8080/pasantias/{id}" \
  -H "Accept: application/json;charset=UTF-8" | jq '.estado'
```

**Ejemplo:**
```bash
curl -X GET "http://localhost:8080/pasantias/1" \
  -H "Accept: application/json;charset=UTF-8"
```

**Respuesta esperada:**
```json
{
  "idPasantia": 1,
  "titulo": "Pasantía de Desarrollo",
  "estado": "PUBLICADA",  // ← Verificar este campo
  ...
}
```

#### Verificar en la lista completa:
```bash
curl -X GET "http://localhost:8080/pasantias" \
  -H "Accept: application/json;charset=UTF-8" | jq '.[] | select(.idPasantia == 1) | .estado'
```

---

### 2. **Script Automatizado**

Ejecuta el script de verificación:
```bash
./test_verificar_estado_pasantia.sh
```

El script:
- ✅ Verifica vía API (GET /pasantias/{id})
- ✅ Verifica vía API (GET /pasantias - lista completa)
- ✅ Verifica directamente en la base de datos
- ✅ Muestra consultas SQL útiles

---

### 3. **Directamente en la Base de Datos**

#### Conectar a la base de datos:
```bash
docker exec -it db_pasantias mysql -uroot -proot db_pasantias
```

#### Consulta SQL para verificar estado:
```sql
SELECT 
    id_pasantia,
    titulo,
    estado,
    fecha_publicacion,
    fecha_actualizacion
FROM Pasantia
WHERE id_pasantia = 1;  -- Reemplaza con el ID real
```

#### Ver todas las pasantías con su estado:
```sql
SELECT 
    id_pasantia,
    titulo,
    estado,
    fecha_actualizacion
FROM Pasantia
ORDER BY fecha_actualizacion DESC;
```

#### Contar pasantías por estado:
```sql
SELECT 
    estado,
    COUNT(*) as cantidad
FROM Pasantia
GROUP BY estado;
```

#### Ver pasantías que fueron aprobadas recientemente:
```sql
SELECT 
    id_pasantia,
    titulo,
    estado,
    fecha_actualizacion,
    TIMESTAMPDIFF(MINUTE, fecha_creacion, fecha_actualizacion) as minutos_desde_creacion
FROM Pasantia
WHERE estado = 'PUBLICADA'
ORDER BY fecha_actualizacion DESC;
```

**Archivo SQL completo:** `script_bd/sql/verificar_estado_pasantia.sql`

---

### 4. **Desde el Frontend**

1. **Navegar a la página de pasantías:**
   - URL: `http://localhost:3000/pasantias` o `http://localhost:5173/pasantias`
   - Busca la pasantía en la lista
   - El estado se muestra en cada tarjeta

2. **Ver detalles de la pasantía:**
   - Haz clic en "Ver detalles"
   - El estado se muestra en la sección de información

3. **Para administradores:**
   - Si el estado es `PENDIENTE_DE_APROBACION`, verás el botón "Aprobar"
   - Después de aprobar, el estado cambia a `PUBLICADA` y el botón desaparece

---

### 5. **Verificación Programática (Java/Spring)**

Si estás escribiendo tests o código Java:

```java
// Obtener pasantía por ID
PasantiaDetalleDTO pasantia = pasantiaService.obtenerPasantiaPorId(id);

// Verificar estado
if (pasantia.getEstado() == EstadoPasantia.PUBLICADA) {
    System.out.println("✓ Pasantía está PUBLICADA");
} else {
    System.out.println("⚠ Estado actual: " + pasantia.getEstado());
}
```

---

## 🔍 Verificación Paso a Paso

### Antes de Aprobar:
1. Verifica que la pasantía existe y está en `PENDIENTE_DE_APROBACION`:
   ```bash
   curl "http://localhost:8080/pasantias/1" | jq '.estado'
   # Debe retornar: "PENDIENTE_DE_APROBACION"
   ```

### Aprobar la Pasantía:
```bash
curl -X PUT "http://localhost:8080/pasantias/1/aprobar" \
  -H "Authorization: Bearer <TOKEN_ADMINISTRADOR>" \
  -H "Content-Type: application/json"
```

### Después de Aprobar:
1. Verifica el nuevo estado:
   ```bash
   curl "http://localhost:8080/pasantias/1" | jq '.estado'
   # Debe retornar: "PUBLICADA"
   ```

2. Verifica en la base de datos:
   ```sql
   SELECT estado FROM Pasantia WHERE id_pasantia = 1;
   -- Debe retornar: PUBLICADA
   ```

---

## ✅ Checklist de Verificación

- [ ] Estado en API (`GET /pasantias/{id}`) es `PUBLICADA`
- [ ] Estado en lista (`GET /pasantias`) es `PUBLICADA`
- [ ] Estado en base de datos es `PUBLICADA`
- [ ] `fecha_actualizacion` se actualizó recientemente
- [ ] El botón "Aprobar" desapareció del frontend (si eres admin)
- [ ] La pasantía aparece en búsquedas públicas

---

## 🐛 Troubleshooting

### El estado no cambió después de aprobar:

1. **Verifica que el token de autenticación sea válido:**
   ```bash
   curl -X GET "http://localhost:8080/auth/validar" \
     -H "Authorization: Bearer <TOKEN>"
   ```

2. **Verifica que el usuario tenga rol ADMINISTRADOR:**
   - El endpoint `/pasantias/{id}/aprobar` solo funciona para ADMINISTRADOR

3. **Verifica que la pasantía esté en estado PENDIENTE_DE_APROBACION:**
   ```sql
   SELECT estado FROM Pasantia WHERE id_pasantia = 1;
   ```

4. **Revisa los logs del backend:**
   - Busca errores en la consola donde corre Spring Boot
   - Verifica que no haya excepciones de validación

5. **Verifica la respuesta del endpoint de aprobación:**
   ```bash
   curl -X PUT "http://localhost:8080/pasantias/1/aprobar" \
     -H "Authorization: Bearer <TOKEN>" \
     -v
   ```

---

## 📝 Notas Importantes

- El campo `fecha_actualizacion` se actualiza automáticamente cuando cambia el estado
- Solo usuarios con rol `ADMINISTRADOR` pueden aprobar pasantías
- Una pasantía solo puede pasar de `PENDIENTE_DE_APROBACION` a `PUBLICADA` (no al revés)
- Después de aprobar, la pasantía queda disponible para postulaciones

---

## 🔗 Archivos Relacionados

- `test_verificar_estado_pasantia.sh` - Script de verificación automatizado
- `script_bd/sql/verificar_estado_pasantia.sql` - Consultas SQL útiles
- `pasantias/src/main/java/com/seminario/pasantias/controller/PasantiaController.java` - Endpoint de aprobación
- `pasantias/src/main/java/com/seminario/pasantias/service/PasantiaService.java` - Lógica de cambio de estado

