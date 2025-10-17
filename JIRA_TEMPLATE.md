# 🎫 Template para Jira - Seguridad API Pasantías

## 📋 **Instrucciones de Uso**

1. **Copia y pega** cada sección en Jira según el tipo de ticket
2. **Adapta** las estimaciones según tu equipo
3. **Asigna** los componentes apropiados de tu proyecto
4. **Vincula** los tickets usando las relaciones especificadas

---

## 🎯 **EPIC: Implementación de Matriz de Seguridad**

### **Información Básica:**
```
Título: Implementar Matriz de Seguridad por Roles - API Pasantías
Tipo: Epic
Componente: Backend, Security
Prioridad: High
Labels: security, backend, api, critical

Estado inicial: To Do
Asignado: Backend Team Lead
```

### **Descripción:**
```
## 🎯 Objetivo
Implementar una configuración de seguridad robusta que proteja todos los endpoints de la API según los roles de usuario definidos.

## 🚨 Problema Actual
- Endpoints críticos están protegidos de forma genérica
- No hay diferenciación por roles de usuario
- Falta testing automatizado de autorización
- Riesgo de escalación de privilegios

## 💡 Solución Propuesta
Implementar matriz de seguridad granular con:
- Configuración específica por rol (ADMIN, EMPRESA, ESTUDIANTE, SUPERVISOR)
- Tests automatizados de autorización
- Documentación completa de permisos

## 📊 Criterios de Aceptación del Epic
- [ ] Todos los endpoints críticos requieren autorización apropiada
- [ ] Tests de seguridad cubren todos los escenarios
- [ ] Documentación de permisos actualizada
- [ ] Auditoría de seguridad aprobada

## 📈 Valor de Negocio
- Protección de datos sensibles
- Cumplimiento de estándares de seguridad
- Confianza de usuarios en la plataforma
- Prevención de vulnerabilidades críticas

## 📋 Definition of Done
- Configuración implementada y funcionando
- Tests pasando en CI/CD
- Documentación actualizada
- Review de seguridad completado
```

### **Historias de Usuario Relacionadas:**
```
- SEC-US-001: Protección de Endpoints Críticos
- SEC-US-002: Testing de Autorización
- SEC-US-003: Configuración por Roles
- SEC-US-004: Documentación de Seguridad
```

---

## 👥 **USER STORIES**

### **[SEC-US-001] Protección de Endpoints Críticos**

```
Título: Como administrador del sistema quiero que solo usuarios autorizados accedan a endpoints críticos
Tipo: Story
Epic: Implementar Matriz de Seguridad por Roles
Componente: Backend, Security
Prioridad: Critical
Labels: security, authorization, critical
Story Points: 8

Asignado: Backend Developer
Sprint: Sprint 1
```

**Descripción:**
```
## 📝 Como...
Administrador del sistema

## 🎯 Quiero...
Que solo usuarios con roles apropiados puedan acceder a endpoints críticos como eliminación de usuarios y gestión de roles

## 💡 Para que...
El sistema esté protegido contra accesos no autorizados y escalación de privilegios

## 📋 Criterios de Aceptación
- [ ] Solo usuarios con rol ADMINISTRADOR pueden acceder a /usuarios/eliminarUsuario
- [ ] Solo usuarios con rol ADMINISTRADOR pueden gestionar roles (/roles/registrarRol, /roles/modificarRol, /roles/eliminarRol)
- [ ] Usuarios ESTUDIANTE y EMPRESA reciben error 403 al intentar acceder a endpoints de admin
- [ ] Configuración implementada en SecurityConfig

## 🧪 Casos de Prueba
### Caso 1: Usuario ESTUDIANTE intenta eliminar usuario
- Input: Token de ESTUDIANTE + POST /usuarios/eliminarUsuario
- Expected: HTTP 403 Forbidden

### Caso 2: Usuario ADMINISTRADOR elimina usuario
- Input: Token de ADMINISTRADOR + POST /usuarios/eliminarUsuario
- Expected: HTTP 200 + operación exitosa

### Caso 3: Usuario sin token intenta gestionar roles
- Input: Sin token + POST /roles/registrarRol
- Expected: HTTP 401 Unauthorized

## 🔗 Enlaces
- Documentación: [SECURITY.md](https://github.com/proyecto/SECURITY.md)
- Matriz de endpoints: [ENDPOINTS_SECURITY.md](https://github.com/proyecto/ENDPOINTS_SECURITY.md)
```

**Tasks Relacionados:**
```
- SEC-T-001: Actualizar SecurityConfig con configuración por roles
- SEC-T-002: Implementar tests de autorización para endpoints críticos
- SEC-T-003: Validar configuración en ambiente de testing
```

---

### **[SEC-US-002] Testing de Autorización**

```
Título: Como desarrollador quiero tests automatizados que validen la autorización de endpoints
Tipo: Story
Epic: Implementar Matriz de Seguridad por Roles
Componente: Backend, Testing
Prioridad: High
Labels: security, testing, automation
Story Points: 5

Asignado: Backend Developer
Sprint: Sprint 1
```

**Descripción:**
```
## 📝 Como...
Desarrollador del equipo

## 🎯 Quiero...
Tests automatizados que validen que cada endpoint tiene la autorización correcta implementada

## 💡 Para que...
Podamos detectar regresiones de seguridad automáticamente y tener confianza en los despliegues

## 📋 Criterios de Aceptación
- [ ] Tests que validen acceso denegado para usuarios sin permisos
- [ ] Tests que validen acceso permitido para usuarios autorizados
- [ ] Tests integrados en pipeline de CI/CD
- [ ] Cobertura de tests de seguridad > 80%

## 🧪 Casos de Prueba
### Suite de Tests de Autorización
- Tests para cada nivel de rol (ADMIN, EMPRESA, ESTUDIANTE, SUPERVISOR)
- Tests para endpoints públicos vs protegidos
- Tests para manejo de tokens inválidos/expirados
- Tests para escalación de privilegios

## 🔗 Enlaces
- Roadmap: [SECURITY_ROADMAP.md](https://github.com/proyecto/SECURITY_ROADMAP.md)
```

---

### **[SEC-US-003] Configuración por Roles**

```
Título: Como usuario del sistema quiero acceder solo a funcionalidades apropiadas para mi rol
Tipo: Story
Epic: Implementar Matriz de Seguridad por Roles
Componente: Backend, Security
Prioridad: High
Labels: security, roles, user-experience
Story Points: 8

Asignado: Backend Developer
Sprint: Sprint 2
```

**Descripción:**
```
## 📝 Como...
Usuario del sistema (ESTUDIANTE, EMPRESA, SUPERVISOR)

## 🎯 Quiero...
Acceder solo a las funcionalidades que corresponden a mi rol y recibir mensajes claros cuando no tengo permisos

## 💡 Para que...
La experiencia sea segura y clara, sin confusión sobre qué puedo hacer

## 📋 Criterios de Aceptación
- [ ] ESTUDIANTE puede ver/editar solo su propio perfil
- [ ] EMPRESA puede ver/editar solo su propio perfil
- [ ] SUPERVISOR puede ver perfiles de usuarios asignados
- [ ] ADMINISTRADOR tiene acceso completo
- [ ] Mensajes de error claros para accesos denegados

## 🧪 Casos de Prueba
### Validación por Rol
- ESTUDIANTE intenta ver perfil de otro usuario → 403
- EMPRESA intenta gestionar roles → 403
- SUPERVISOR ve usuarios asignados → 200
- ADMINISTRADOR accede a todo → 200
```

---

### **[SEC-US-004] Documentación de Seguridad**

```
Título: Como desarrollador quiero documentación clara de la configuración de seguridad
Tipo: Story
Epic: Implementar Matriz de Seguridad por Roles
Componente: Documentation
Prioridad: Medium
Labels: documentation, security, knowledge-sharing
Story Points: 3

Asignado: Backend Developer
Sprint: Sprint 4
```

---

## 🔧 **TASKS TÉCNICOS**

### **[SEC-T-001] Actualizar SecurityConfig**

```
Título: Actualizar SecurityConfig con configuración específica por roles
Tipo: Task
Parent: SEC-US-001
Componente: Backend
Prioridad: Critical
Labels: security, configuration, backend
Estimación: 4 horas

Asignado: Backend Developer
Sprint: Sprint 1
```

**Descripción:**
```
## 🎯 Objetivo
Modificar la clase SecurityConfig para implementar autorización granular por roles

## 📋 Tareas Específicas
- [ ] Reemplazar configuración genérica actual
- [ ] Implementar reglas específicas para endpoints críticos
- [ ] Configurar endpoints públicos necesarios
- [ ] Validar configuración con tests locales

## 🔧 Implementación Técnica
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // Endpoints públicos
            .requestMatchers("/auth/login", "/auth/iniciarSesion").permitAll()
            .requestMatchers("/usuarios/registrarUsuario").permitAll()
            .requestMatchers(HttpMethod.GET, "/roles/consultarRol").permitAll()
            
            // Endpoints solo ADMIN
            .requestMatchers("/usuarios/eliminarUsuario").hasRole("ADMINISTRADOR")
            .requestMatchers("/roles/registrarRol").hasRole("ADMINISTRADOR")
            .requestMatchers("/roles/modificarRol").hasRole("ADMINISTRADOR")
            .requestMatchers("/roles/eliminarRol").hasRole("ADMINISTRADOR")
            
            // Endpoints autenticados
            .requestMatchers("/usuarios/**").authenticated()
            .requestMatchers("/roles/**").authenticated()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
```

## ✅ Definition of Done
- [ ] Configuración implementada
- [ ] Tests unitarios pasando
- [ ] Validación manual completada
- [ ] Code review aprobado
```

---

### **[SEC-T-002] Implementar Tests de Autorización**

```
Título: Crear suite completa de tests de autorización
Tipo: Task
Parent: SEC-US-002
Componente: Backend, Testing
Prioridad: High
Labels: testing, security, automation
Estimación: 4 horas

Asignado: Backend Developer
Sprint: Sprint 1
```

**Descripción:**
```
## 🎯 Objetivo
Crear tests automatizados que validen la configuración de autorización

## 📋 Tests a Implementar
- [ ] Test: endpoints públicos funcionan sin token
- [ ] Test: endpoints protegidos requieren token válido
- [ ] Test: endpoints de admin solo permiten rol ADMINISTRADOR
- [ ] Test: usuarios reciben 403 para endpoints sin permisos
- [ ] Test: tokens inválidos/expirados son rechazados

## 🔧 Implementación Técnica
```java
@SpringBootTest
@AutoConfigureTestDatabase
class SecurityConfigurationTests {

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    void testEstudianteCannotDeleteUsers() {
        // Implementación del test
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")  
    void testAdminCanDeleteUsers() {
        // Implementación del test
    }
}
```

## ✅ Definition of Done
- [ ] Todos los tests implementados y pasando
- [ ] Cobertura de tests de seguridad > 80%
- [ ] Tests integrados en CI/CD
- [ ] Documentación de tests actualizada
```

---

## 🐛 **BUGS DE SEGURIDAD**

### **[SEC-BUG-001] Endpoints críticos sin protección por rol**

```
Título: Endpoints de eliminación y gestión de roles no requieren rol específico
Tipo: Bug
Severidad: Critical
Prioridad: Critical
Componente: Backend, Security
Labels: security-vulnerability, critical, production-blocker

Asignado: Backend Developer
Sprint: Sprint 1 (Hotfix)
```

**Descripción:**
```
## 🚨 Problema
Los endpoints `/usuarios/eliminarUsuario` y `/roles/*` están configurados como `.authenticated()` en lugar de requerir rol ADMINISTRADOR específicamente.

## 💥 Impacto
- Cualquier usuario autenticado puede eliminar otros usuarios
- Cualquier usuario autenticado puede gestionar roles
- Riesgo crítico de escalación de privilegios

## 📊 Pasos para Reproducir
1. Obtener token JWT con rol ESTUDIANTE
2. Llamar POST /usuarios/eliminarUsuario con token
3. Observar que la operación es permitida (INCORRECTO)

## ✅ Comportamiento Esperado
Solo usuarios con rol ADMINISTRADOR deberían poder acceder a estos endpoints

## 🔧 Solución Propuesta
Actualizar SecurityConfig para usar `.hasRole("ADMINISTRADOR")` en lugar de `.authenticated()`

## 🎯 Prioridad Justificación
Este es un vulnerability crítico que debe ser arreglado inmediatamente antes de cualquier deployment.
```

---

## 📊 **TEMPLATE DE SPRINT**

### **Sprint Planning Template:**

```
Sprint: Sprint 1 - Configuración Crítica de Seguridad
Duración: 2 semanas
Objetivo: Proteger endpoints críticos contra accesos no autorizados

Stories Incluidas:
- SEC-US-001: Protección de Endpoints Críticos (8 SP)
- SEC-US-002: Testing de Autorización (5 SP)

Tasks Incluidas:
- SEC-T-001: Actualizar SecurityConfig (4h)
- SEC-T-002: Implementar Tests de Autorización (4h)
- SEC-T-003: Validar configuración en testing (2h)

Bugs Críticos:
- SEC-BUG-001: Endpoints críticos sin protección por rol

Total Story Points: 13
Total Estimación: 10 horas

Definition of Done del Sprint:
- [ ] Todos los endpoints críticos requieren rol ADMINISTRADOR
- [ ] Tests de autorización implementados y pasando
- [ ] Configuración validada en ambiente de testing
- [ ] Code review completado
- [ ] Documentación actualizada
```

---

## 🔗 **ENLACES Y DEPENDENCIAS**

### **Configuración de Enlaces en Jira:**
```
Epic "Implementar Matriz de Seguridad" está compuesto por:
├── SEC-US-001 (Story)
│   ├── SEC-T-001 (Task)
│   ├── SEC-T-002 (Task)
│   └── SEC-T-003 (Task)
├── SEC-US-002 (Story)
├── SEC-US-003 (Story)
└── SEC-US-004 (Story)

SEC-BUG-001 bloquea a SEC-US-001
SEC-T-001 es prerequisito para SEC-T-002
```

### **Labels Recomendados:**
```
- security (para todos los tickets de seguridad)
- critical (para vulnerabilidades críticas)
- backend (para trabajo de backend)
- testing (para tickets de testing)
- documentation (para tickets de documentación)
- production-blocker (para bugs que bloquean producción)
```

---

**Última actualización**: 8 de octubre de 2025  
**Creado por**: Backend Development Team  
**Para usar en**: Jira Software  
**Proyecto**: API Pasantías - Seminario Integrador 2025