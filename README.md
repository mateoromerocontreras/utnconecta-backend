# 🎓 Seminario Integrador 2025 - Sistema de Pasantías

Sistema completo para gestión de pasantías universitarias con frontend React y backend Spring Boot.

## 🔐 **IMPORTANTE - Configuración de Seguridad**

⚠️ **ESTADO ACTUAL**: Este proyecto está en desarrollo activo. La configuración de seguridad requiere mejoras antes de producción.

📋 **Ver documentación completa de seguridad**: 
- [🛡️ SECURITY.md](SECURITY.md) - Estado actual y recomendaciones críticas
- [🎯 ENDPOINTS_SECURITY.md](ENDPOINTS_SECURITY.md) - Matriz de permisos detallada  
- [🛣️ SECURITY_ROADMAP.md](SECURITY_ROADMAP.md) - Plan de implementación
- [🎫 JIRA_TEMPLATE.md](JIRA_TEMPLATE.md) - Templates para seguimiento en Jira

### 🚨 **Issues Críticos Identificados:**
- Endpoints de eliminación requieren configuración por roles
- Gestión de roles necesita protección de ADMINISTRADOR
- Tests de autorización pendientes de implementación

## 🚀 **Instalación y Ejecución**

### **Frontend (React + Vite)**
```bash
cd frontend
npm install
npm run dev
```

### **Backend (Spring Boot + MySQL)**
```bash
# 1. Iniciar base de datos
cd script_bd
docker-compose up -d

# 2. Compilar y ejecutar aplicación
cd ../pasantias
mvn clean package -DskipTests
java -jar target/pasantias-0.0.1-SNAPSHOT.jar
```

### **Base de Datos**
```bash
cd script_bd
docker-compose up -d
# MySQL disponible en localhost:3306
# Usuario: root / Password: my-secret-pw
# Base de datos: db_pasantias
```

## 📚 **Documentación de API**

### **Endpoints Implementados:**
- ✅ `POST /usuarios/registrarUsuario` - [Tests](pasantias/TEST_REGISTRAR_USUARIO.md)
- ✅ `POST /auth/iniciarSesion` - [Tests](pasantias/TESTS_INICIAR_SESION.md)
- ✅ `POST /auth/login` - Autenticación con JWT
- ⚠️ Otros endpoints requieren configuración de seguridad

### **Swagger UI:**
```
http://localhost:8080/swagger-ui/index.html
```

## 🧪 **Testing**

### **Tests de Endpoints Disponibles:**
- [📝 Registro de Usuario](pasantias/TEST_REGISTRAR_USUARIO.md)
- [🔐 Inicio de Sesión](pasantias/TESTS_INICIAR_SESION.md)

### **Usuarios de Prueba:**
| Username | Password | Rol |
|----------|----------|-----|
| `admin` | `admin123` | ADMINISTRADOR |
| `estudiante1` | `estudiante123` | ESTUDIANTE |
| `empresa1` | `empresa123` | EMPRESA |

## 📈 **Code Quality Metrics (SAST / SonarQube / SonarCloud)**

Este proyecto incluye configuración para ejecutar **SonarScanner (Maven)** y subir métricas como:
- Complejidad ciclomática y cognitiva (calculadas por Sonar)
- Technical Debt (Sonar)
- Cobertura (JaCoCo XML)

### **Ejecutar SonarQube local**
1) Levantar SonarQube (ejemplo con Docker):

```bash
docker run --rm -p 9000:9000 --name sonarqube sonarqube:lts-community
```

2) Crear el proyecto en SonarQube y obtener:
- `SONAR_PROJECT_KEY`
- `SONAR_TOKEN`

3) Correr el análisis (desde la raíz del repo):

```bash
export SONAR_TOKEN="..."
export SONAR_PROJECT_KEY="..."

cd pasantias
mvn -Psonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login="$SONAR_TOKEN" -Dsonar.projectKey="$SONAR_PROJECT_KEY" clean verify sonar:sonar
```

### **Ejecutar SonarCloud**
En CI o local, seteá:
- `SONAR_TOKEN`
- `sonar.organization`
- `sonar.projectKey`

Ejemplo:

```bash
export SONAR_TOKEN="..."
cd pasantias
mvn -Psonar -Dsonar.login="$SONAR_TOKEN" -Dsonar.organization="TU_ORG" -Dsonar.projectKey="TU_KEY" clean verify sonar:sonar
```

### **Quality Gates (complejidad)**
El umbral de complejidad (ej. **15**) se configura en SonarQube/SonarCloud como **Quality Gate** (no en el `pom.xml`).
Cuando el Quality Gate está activo, el job/pipeline queda en rojo si la rama no lo cumple.

## 🔥 **Code Churn / Hotspots**
Para detectar archivos con alto “churn” (cambian mucho), corré:

```bash
chmod +x ./scripts/code-churn-hotspots.sh
./scripts/code-churn-hotspots.sh "90 days ago"
```

Priorización recomendada: **alto churn + alta complejidad (Sonar)** ⇒ mejores candidatos a refactor.

## 🏗️ **Arquitectura**

```
proyecto/
├── frontend/          # React + Vite
├── pasantias/         # Spring Boot API
├── script_bd/         # Docker MySQL + Schema
├── SECURITY.md        # 🔐 Configuración de seguridad
├── ENDPOINTS_SECURITY.md  # 🎯 Matriz de permisos
└── SECURITY_ROADMAP.md    # 🛣️ Plan de implementación
```

## 👥 **Equipo de Desarrollo**

- **Backend**: Spring Boot + MySQL + JWT Authentication
- **Frontend**: React + Vite + CSS Modules  
- **Base de Datos**: MySQL 8.0 en Docker
- **Seguridad**: Spring Security + BCrypt

## 📋 **Estado del Proyecto**

### **✅ Completado:**
- Base de datos MySQL con esquema completo
- Autenticación JWT básica
- Registro de usuarios con validaciones
- Endpoints de login funcionando
- Tests de API documentados

### **🔄 En Desarrollo:**
- Configuración de seguridad por roles
- Tests automatizados de autorización
- Frontend integración con backend

### **📅 Próximo Sprint:**
- Implementar matriz de seguridad (Ver [SECURITY_ROADMAP.md](SECURITY_ROADMAP.md))
- Crear tests automatizados de autorización
- Configurar CORS para producción

## 🔗 **Enlaces Útiles**

- **API Documentation**: http://localhost:8080/swagger-ui/index.html
- **Base de Datos**: http://localhost:3306 (MySQL Workbench)
- **Frontend**: http://localhost:5173 (Vite dev server)

## 📞 **Soporte**

Para consultas técnicas o reportar issues de seguridad:
- **Repository**: GitHub Issues
- **Security**: Ver [SECURITY.md](SECURITY.md) para reportar vulnerabilidades
- **Jira**: Usar templates de [JIRA_TEMPLATE.md](JIRA_TEMPLATE.md)

---

**Última actualización**: 8 de octubre de 2025  
**Estado**: 🔄 En desarrollo activo  
**Versión**: 0.1.0-SNAPSHOT