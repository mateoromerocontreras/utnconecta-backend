# Quick Test Guide - POST /pasantias/registrar

## ⚡ Pasos Rápidos para Probar

### 1. Iniciar Base de Datos
```powershell
cd script_bd
docker-compose up -d
```

Esperar ~30 segundos para que MySQL esté listo.

### 2. Iniciar Aplicación Spring Boot
```powershell
cd ..\pasantias
mvn spring-boot:run
```

O si ya está compilado:
```powershell
java -jar target\pasantias-0.0.1-SNAPSHOT.jar
```

Esperar hasta ver: `Tomcat started on port 8080`

### 3. Ejecutar Test
```powershell
cd ..
.\test_pasantia_endpoint.ps1
```

O manualmente:
```powershell
$json = Get-Content test_pasantia.json -Raw
Invoke-RestMethod -Uri "http://localhost:8080/pasantias/registrar" -Method Post -ContentType "application/json" -Body $json
```

### 4. Verificar en Base de Datos
```powershell
docker exec -it db_pasantias mysql -uroot -pmy-secret-pw db_pasantias -e "SELECT id_pasantia, titulo, conocimientos, otros_requisitos, beneficios FROM Pasantia ORDER BY id_pasantia DESC LIMIT 1;"
```

O usar el script SQL:
```powershell
docker exec -i db_pasantias mysql -uroot -pmy-secret-pw db_pasantias < verify_database.sql
```

## ✅ Checklist de Verificación

- [ ] Base de datos corriendo (puerto 3306)
- [ ] Aplicación Spring Boot corriendo (puerto 8080)
- [ ] Request exitoso (código 0 en respuesta)
- [ ] Nuevo registro en tabla `Pasantia`
- [ ] Campos `conocimientos`, `otros_requisitos`, `beneficios` guardados correctamente
- [ ] Relación con carrera creada en `Pasantia_Carrera`

## 🔍 Verificar Errores Comunes

### Error: "La empresa con ID X no existe"
- Verificar que existe empresa con ID 1: `SELECT * FROM Empresa WHERE id_empresa = 1;`

### Error: "La carrera con ID X no existe"  
- Verificar que existe carrera con ID 6: `SELECT * FROM Carrera WHERE id_carrera = 6;`

### Error: "Unable to connect"
- Verificar que la aplicación está corriendo: `Test-NetConnection localhost -Port 8080`
- Verificar logs de la aplicación

### Error de validación
- Revisar que todas las fechas sean futuras
- Revisar que modalidad sea: "Presencial", "Híbrida" o "Remoto"
- Revisar que título tenga entre 5-200 caracteres

