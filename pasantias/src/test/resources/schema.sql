-- H2 Database Schema for Tests
-- Adapted from MySQL schema for H2 compatibility

-- Estructura de la tabla `Rol`
CREATE TABLE IF NOT EXISTS Rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Estructura de la tabla `Usuario`
CREATE TABLE IF NOT EXISTS Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_rol) REFERENCES Rol(id_rol)
);

CREATE TABLE IF NOT EXISTS EmailVerificationToken (
    id_token INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    fecha_expiracion TIMESTAMP NOT NULL,
    usado BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- Estructura de la tabla `Empresa`
CREATE TABLE IF NOT EXISTS Empresa (
    id_empresa INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    ciudad VARCHAR(100),
    calle VARCHAR(255),
    nro_calle INT,
    piso VARCHAR(20),
    departamento VARCHAR(20),
    barrio VARCHAR(100),
    email VARCHAR(255),
    cuit VARCHAR(100),
    razon_social VARCHAR(255),
    id_usuario INT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE SET NULL
);

-- Estructura de la tabla `Carrera`
CREATE TABLE IF NOT EXISTS Carrera (
    id_carrera INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE
);

-- Estructura de la tabla `Contacto`
CREATE TABLE IF NOT EXISTS Contacto (
    id_contacto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    email_responsable VARCHAR(255) NOT NULL,
    telefono_responsable VARCHAR(50),
    id_empresa INT,
    FOREIGN KEY (id_empresa) REFERENCES Empresa(id_empresa) ON DELETE CASCADE
);

-- Estructura de la tabla `Estudiante`
CREATE TABLE IF NOT EXISTS Estudiante (
    id_estudiante INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(20),
    apellido VARCHAR(255),
    nombre VARCHAR(255),
    especialidad VARCHAR(255),
    nro_legajo VARCHAR(50),
    calle VARCHAR(255),
    nro_calle INT,
    barrio VARCHAR(100),
    localidad VARCHAR(100),
    provincia VARCHAR(100),
    email VARCHAR(255) NOT NULL,
    tel_celular VARCHAR(50),
    tel_fijo VARCHAR(50),
    id_usuario INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- Estructura de la tabla `CV`
CREATE TABLE IF NOT EXISTS CV (
    id_cv INT AUTO_INCREMENT PRIMARY KEY,
    nombre_archivo VARCHAR(255) NOT NULL,
    datos_cv BLOB NOT NULL,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_estudiante INT NOT NULL,
    FOREIGN KEY (id_estudiante) REFERENCES Estudiante(id_estudiante) ON DELETE CASCADE
);

-- Estructura de la tabla `Pasantia`
CREATE TABLE IF NOT EXISTS Pasantia (
    id_pasantia INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    puesto_a_cubrir VARCHAR(150) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    modalidad VARCHAR(50) NOT NULL,
    asignacion_estimulo FLOAT,
    cantidad_de_pasantes INT NOT NULL DEFAULT 1,
    fecha_publicacion DATE NOT NULL,
    fecha_caducidad DATE NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE_DE_APROBACION',
    email_contacto VARCHAR(100) NOT NULL,
    conocimientos TEXT,
    otros_requisitos TEXT,
    beneficios TEXT,
    id_empresa INT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_empresa) REFERENCES Empresa(id_empresa) ON DELETE CASCADE
);

-- Tabla intermedia `Pasantia_Carrera`
CREATE TABLE IF NOT EXISTS Pasantia_Carrera (
    id_pasantia INT NOT NULL,
    id_carrera INT NOT NULL,
    PRIMARY KEY (id_pasantia, id_carrera),
    FOREIGN KEY (id_pasantia) REFERENCES Pasantia(id_pasantia) ON DELETE CASCADE,
    FOREIGN KEY (id_carrera) REFERENCES Carrera(id_carrera) ON DELETE CASCADE
);

-- Estructura de la tabla `Postulacion`
CREATE TABLE IF NOT EXISTS Postulacion (
    id_postulacion INT PRIMARY KEY AUTO_INCREMENT,
    fecha_postulacion DATE NOT NULL,
    fecha_inicio_contrato DATE,
    duracion_meses INT,
    estado VARCHAR(50) NOT NULL DEFAULT 'BORRADOR',
    observaciones TEXT,
    id_pasantia INT NOT NULL,
    estudiante_id INT NOT NULL,
    id_cv INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pasantia) REFERENCES Pasantia(id_pasantia) ON DELETE CASCADE,
    FOREIGN KEY (estudiante_id) REFERENCES Estudiante(id_estudiante) ON DELETE CASCADE,
    FOREIGN KEY (id_cv) REFERENCES CV(id_cv) ON DELETE SET NULL
);

