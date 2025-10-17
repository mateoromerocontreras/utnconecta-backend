-- Elimina la base de datos si ya existe para empezar de cero
DROP DATABASE IF EXISTS db_pasantias;
CREATE DATABASE db_pasantias;
USE db_pasantias;

--
-- Estructura de la tabla `Rol`
--
CREATE TABLE Rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--
-- Estructura de la tabla `Usuario`
--
CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_rol) REFERENCES Rol(id_rol)
);

--
-- Estructura de la tabla `Empresa`
--
CREATE TABLE Empresa (
    id_empresa INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    ciudad VARCHAR(100),
    direccion TEXT,
    email VARCHAR(255),
    cuit VARCHAR(100),
    razon_social VARCHAR(255)
);

--
-- Estructura de la tabla `Carrera`
--
CREATE TABLE Carrera (
    id_carrera INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE
);

--
-- Estructura de la tabla `Contacto`
--
CREATE TABLE Contacto (
    id_contacto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    email_responsable VARCHAR(255) NOT NULL,
    telefono_responsable VARCHAR(50),
    id_empresa INT,
    FOREIGN KEY (id_empresa) REFERENCES Empresa(id_empresa) ON DELETE CASCADE
);

--
-- Estructura de la tabla `Estudiante`
--
CREATE TABLE Estudiante (
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

CREATE TABLE postulacion (
    id_postulacion BIGINT PRIMARY KEY AUTO_INCREMENT,

    estudiante_id BIGINT NOT NULL,
    -- pasantia_id BIGINT,  -- (Comentado por ahora, se agregará cuando exista la entidad Pasantia)

    fecha_postulacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'Pendiente' CHECK (estado IN ('Pendiente', 'Aceptada', 'Rechazada', 'Cancelada')),
    observaciones TEXT,
    fecha_actualizacion TIMESTAMP NULL,

    CONSTRAINT fk_postulacion_estudiante
        FOREIGN KEY (estudiante_id)
        REFERENCES estudiante(id_estudiante)
        ON DELETE CASCADE
);

--
-- Inserta datos de ejemplo para Rol
--
INSERT INTO Rol (nombre, descripcion, activo) VALUES
('ADMINISTRADOR', 'Administrador del sistema con acceso completo', TRUE),
('ESTUDIANTE', 'Estudiante que busca pasantías', TRUE),
('EMPRESA', 'Empresa que ofrece pasantías', TRUE),
('SUPERVISOR', 'Supervisor de pasantías', TRUE);

--
-- Inserta datos de ejemplo para Usuario
--
INSERT INTO Usuario (username, email, password, id_rol, activo) VALUES
('admin', 'admin@pasantias.com', '$2a$10$7twR9AUAYwkpeDMQl/RUR.bD1u73FBWoBqX0F7E7qVAdTcxnXLEKO', 1, TRUE), -- Contraseña: admin123
('estudiante1', 'estudiante1@estudiantes.com', '$2a$10$NRCLtCsvTEz8F7u1JmX5K.M6aY7P7pgWOHuFLvLh7Lc0ZLCaIrat2', 2, TRUE), -- Contraseña: estudiante123
('empresa1', 'empresa1@empresas.com', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE); -- Contraseña: empresa123

--
-- Inserta datos de ejemplo para Carrera
--
INSERT INTO Carrera (nombre) VALUES
('Ingeniería Civil'),
('Ingeniería Industrial'),
('Ingeniería Electrónica'),
('Ingeniería Mecánica'),
('Ingeniería Química'),
('Ingeniería en Sistemas'),
('Ingeniería Eléctrica'),
('Ingeniería Aeroespacial');

--
-- Inserta datos de ejemplo
--
INSERT INTO Empresa (nombre, ciudad, direccion, email, cuit, razon_social) VALUES
('BIOFARMA S.A', 'Córdoba', 'Bv. de los Polacos 6446 Barrio Los Boulevares', 'rrhh@biofarmaweb.com.ar','30-76543210-3', 'BIOFARMA S.A.'),
('INDACOR S.A.', 'JUAREZ CELMAN', 'Ruta 9 norte km 721 – Juárez Celman', 'aracelipenaflor@pollosindacor.com.ar', '33-12345678-9', 'INDACOR S.A.');

--
-- Inserta datos de ejemplo para Contacto
--
INSERT INTO Contacto (nombre, apellido, email_responsable, telefono_responsable, id_empresa) VALUES
('Juan', 'Pérez', 'juan.perez@biofarmaweb.com.ar', '351-123-4567', 1),
('María', 'González', 'maria.gonzalez@pollosindacor.com.ar', '351-987-6543', 2);



--
-- Inserta datos de ejemplo para Postulacion
--
INSERT INTO postulacion (estudiante_id, estado, observaciones)
VALUES 
    (1, 'Pendiente', 'Postulación inicial de prueba'),
    (2, 'Aceptada', 'Postulación aprobada por recursos humanos');