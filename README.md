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