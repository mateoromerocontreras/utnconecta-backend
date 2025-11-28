# 🐳 Docker Setup Guide

## 📋 Requisitos Previos

- [Docker](https://docs.docker.com/get-docker/) instalado
- [Docker Compose](https://docs.docker.com/compose/install/) instalado

## 🚀 Inicio Rápido

### **Opción 1: Usando el script de inicio (Linux/Mac)**

```bash
./start-app.sh
```

### **Opción 2: Usando el script de inicio (Windows)**

```cmd
start-app.bat
```

### **Opción 3: Manual**

```bash
# Desde la raíz del proyecto
cd script_bd

# Iniciar todos los servicios
docker-compose up -d --build

# Ver logs en tiempo real
docker-compose logs -f

# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes (reset completo de la BD)
docker-compose down -v
```

## 🌐 Servicios Disponibles

Una vez iniciados los contenedores, los servicios estarán disponibles en:

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **Frontend** | http://localhost:5173 | Interfaz de usuario React + Vite |
| **Backend** | http://localhost:8080 | API REST Spring Boot |
| **Swagger UI** | http://localhost:8080/swagger-ui/index.html | Documentación interactiva de la API |
| **MySQL** | localhost:3306 | Base de datos MySQL 8.0 |

### Credenciales de Base de Datos

- **Usuario:** `root`
- **Contraseña:** `my-secret-pw`
- **Base de datos:** `db_pasantias`

## 📦 Arquitectura de Contenedores

```
┌─────────────────────────────────────────────┐
│          pasantias_frontend                 │
│        (Node 20 + Vite + React)             │
│            Port: 5173                       │
└─────────────────┬───────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────┐
│          pasantias_backend                  │
│     (Java 21 + Spring Boot + Maven)         │
│            Port: 8080                       │
└─────────────────┬───────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────┐
│            db_pasantias                     │
│            (MySQL 8.0)                      │
│            Port: 3306                       │
└─────────────────────────────────────────────┘
```

## 🛠️ Comandos Útiles

### Ver estado de los contenedores

```bash
docker-compose ps
```

### Ver logs de un servicio específico

```bash
# Backend
docker-compose logs -f backend

# Frontend
docker-compose logs -f frontend

# Base de datos
docker-compose logs -f db
```

### Reiniciar un servicio específico

```bash
# Reiniciar backend
docker-compose restart backend

# Reiniciar frontend
docker-compose restart frontend
```

### Reconstruir un servicio

```bash
# Reconstruir backend sin caché
docker-compose build --no-cache backend

# Reconstruir frontend sin caché
docker-compose build --no-cache frontend
```

### Ejecutar comandos dentro de un contenedor

```bash
# Acceder al contenedor del backend
docker-compose exec backend bash

# Acceder al contenedor de MySQL
docker-compose exec db mysql -u root -p
```

### Limpiar recursos Docker

```bash
# Detener y eliminar contenedores, redes y volúmenes
docker-compose down -v

# Limpiar imágenes no utilizadas
docker image prune -a

# Limpiar todo (contenedores, redes, volúmenes, imágenes)
docker system prune -a --volumes
```

## 🔧 Desarrollo Local

### Modificar código en tiempo real

Los contenedores están configurados con volúmenes que permiten hot-reload:

- **Frontend**: Los cambios en el código se reflejan automáticamente
- **Backend**: Necesita reiniciar el contenedor para ver los cambios

```bash
# Después de cambiar código del backend
docker-compose restart backend
```

### Conectarse a la base de datos desde un cliente externo

Puedes usar herramientas como MySQL Workbench, DBeaver, o la línea de comandos:

```bash
mysql -h 127.0.0.1 -P 3306 -u root -p
# Password: my-secret-pw
```

## 🐛 Troubleshooting

### El puerto 3306/8080/5173 ya está en uso

```bash
# Ver qué proceso está usando el puerto
sudo lsof -i :3306
sudo lsof -i :8080
sudo lsof -i :5173

# Detener el proceso o cambiar el puerto en docker-compose.yml
```

### El backend no puede conectarse a la base de datos

1. Verificar que el contenedor de MySQL esté saludable:
   ```bash
   docker-compose ps
   ```

2. Ver logs del backend:
   ```bash
   docker-compose logs backend
   ```

3. Reiniciar servicios en orden:
   ```bash
   docker-compose down
   docker-compose up -d db
   # Esperar 30 segundos
   docker-compose up -d backend
   docker-compose up -d frontend
   ```

### Resetear completamente la base de datos

```bash
# Detener y eliminar volúmenes
docker-compose down -v

# Reiniciar servicios (se ejecutará schema.sql nuevamente)
docker-compose up -d
```

### Error de permisos en Linux

Si encuentras errores de permisos, asegúrate de que tu usuario esté en el grupo docker:

```bash
sudo usermod -aG docker $USER
# Cerrar sesión y volver a iniciar
```

## 🌍 Variables de Entorno

Las variables de entorno se pueden configurar en un archivo `.env` en la raíz del proyecto:

```bash
# Copiar el archivo de ejemplo
cp .env.example .env

# Editar con tus valores
nano .env
```

Variables disponibles:

- `MYSQL_ROOT_PASSWORD`: Contraseña del usuario root de MySQL
- `MYSQL_DATABASE`: Nombre de la base de datos
- `JWT_SECRET`: Clave secreta para JWT (cambiar en producción)
- `JWT_EXPIRATION`: Tiempo de expiración del token en segundos
- `VITE_API_URL`: URL del backend para el frontend

## 📚 Recursos Adicionales

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot with Docker](https://spring.io/guides/gs/spring-boot-docker/)
- [Vite Docker Configuration](https://vitejs.dev/guide/static-deploy.html)

## 🤝 Equipo de Desarrollo

Para nuevos miembros del equipo:

1. Clonar el repositorio
2. Copiar `.env.example` a `.env`
3. Ejecutar `./start-app.sh` o `start-app.bat`
4. ¡Listo para desarrollar! 🎉
