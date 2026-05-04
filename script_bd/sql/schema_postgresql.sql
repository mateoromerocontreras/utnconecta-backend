-- PostgreSQL Schema for Render
-- Database already exists, only creating tables

-- Create ENUM types
CREATE TYPE estado_pasantia AS ENUM('PUBLICADA', 'FINALIZADA', 'DADA_DE_BAJA', 'PENDIENTE_DE_APROBACION', 'EXPIRADA');
CREATE TYPE estado_postulacion AS ENUM('BORRADOR', 'PENDIENTE_APROBACION', 'PUBLICADA', 'CUBIERTA', 'FINALIZADA');

-- Table: Rol
CREATE TABLE Rol (
    id_rol SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: Usuario
CREATE TABLE Usuario (
    id_usuario SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_rol) REFERENCES Rol(id_rol)
);

CREATE TABLE EmailVerificationToken (
    id_token SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    fecha_expiracion TIMESTAMP NOT NULL,
    usado BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- Table: Empresa
CREATE TABLE Empresa (
    id_empresa SERIAL PRIMARY KEY,
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

-- Table: Carrera
CREATE TABLE Carrera (
    id_carrera SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE
);

-- Table: Contacto
CREATE TABLE Contacto (
    id_contacto SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    email_responsable VARCHAR(255) NOT NULL,
    telefono_responsable VARCHAR(50),
    id_empresa INT,
    FOREIGN KEY (id_empresa) REFERENCES Empresa(id_empresa) ON DELETE CASCADE
);

-- Table: Estudiante
CREATE TABLE Estudiante (
    id_estudiante SERIAL PRIMARY KEY,
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

-- Table: CV
CREATE TABLE CV (
    id_cv SERIAL PRIMARY KEY,
    nombre_archivo VARCHAR(255) NOT NULL,
    datos_cv BYTEA NOT NULL,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_estudiante INT NOT NULL,
    FOREIGN KEY (id_estudiante) REFERENCES Estudiante(id_estudiante) ON DELETE CASCADE
);

-- Table: Pasantia
CREATE TABLE Pasantia (
    id_pasantia SERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    puesto_a_cubrir VARCHAR(150) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    modalidad VARCHAR(50) NOT NULL,
    asignacion_estimulo FLOAT,
    cantidad_de_pasantes INT NOT NULL DEFAULT 1,
    fecha_publicacion DATE NOT NULL,
    fecha_caducidad DATE NOT NULL,
    estado estado_pasantia NOT NULL DEFAULT 'PENDIENTE_DE_APROBACION',
    email_contacto VARCHAR(100) NOT NULL,
    conocimientos TEXT,
    otros_requisitos TEXT,
    beneficios TEXT,
    id_empresa INT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_empresa) REFERENCES Empresa(id_empresa) ON DELETE CASCADE
);

-- Trigger function for auto-updating fecha_actualizacion
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_pasantia_updated_at BEFORE UPDATE ON Pasantia
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Table: Pasantia_Carrera
CREATE TABLE Pasantia_Carrera (
    id_pasantia INT NOT NULL,
    id_carrera INT NOT NULL,
    PRIMARY KEY (id_pasantia, id_carrera),
    FOREIGN KEY (id_pasantia) REFERENCES Pasantia(id_pasantia) ON DELETE CASCADE,
    FOREIGN KEY (id_carrera) REFERENCES Carrera(id_carrera) ON DELETE CASCADE
);

-- Table: Postulacion
CREATE TABLE Postulacion (
    id_postulacion SERIAL PRIMARY KEY,
    fecha_postulacion DATE NOT NULL,
    fecha_inicio_contrato DATE,
    duracion_meses INT,
    estado estado_postulacion NOT NULL DEFAULT 'BORRADOR',
    observaciones TEXT,
    id_pasantia INT NOT NULL,
    estudiante_id INT NOT NULL,
    id_cv INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_postulacion_pasantia FOREIGN KEY (id_pasantia) REFERENCES Pasantia(id_pasantia) ON DELETE CASCADE,
    CONSTRAINT fk_postulacion_estudiante FOREIGN KEY (estudiante_id) REFERENCES Estudiante(id_estudiante) ON DELETE CASCADE,
    CONSTRAINT fk_postulacion_cv FOREIGN KEY (id_cv) REFERENCES CV(id_cv) ON DELETE SET NULL
);

CREATE TRIGGER update_postulacion_updated_at BEFORE UPDATE ON Postulacion
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert initial data
INSERT INTO Rol (nombre, descripcion, activo) VALUES
('ADMINISTRADOR', 'Administrador del sistema con acceso completo', TRUE),
('ESTUDIANTE', 'Estudiante que busca pasantías', TRUE),
('EMPRESA', 'Empresa que ofrece pasantías', TRUE),
('SUPERVISOR', 'Supervisor de pasantías', TRUE);

INSERT INTO Usuario (username, email, password, id_rol, activo) VALUES
('admin', 'admin@pasantias.com', '$2a$10$7twR9AUAYwkpeDMQl/RUR.bD1u73FBWoBqX0F7E7qVAdTcxnXLEKO', 1, TRUE),
('estudiante1', 'estudiante1@estudiantes.com', '$2a$10$NRCLtCsvTEz8F7u1JmX5K.M6aY7P7pgWOHuFLvLh7Lc0ZLCaIrat2', 2, TRUE),
('empresa1', 'empresa1@empresas.com', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('biofarma_user', 'rrhh@biofarmaweb.com.ar', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('hospital_user', 'seleccion@hospital-italiano.org.ar', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('indacor_user', 'aracelipenaflor@pollosindacor.com.ar', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('sowic_user', 'seleccion@sowic.com.ar', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('losmolinos_user', 'rrhhelectroalem@gmail.com', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('harriague_user', 'natalia.barrionuevo@avenga.com', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('unc_ffyh_user', 'biblio@ffyh.unc.edu.ar', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('grupo_kersia_user', 'constanza.diebel@kersia-group.com', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('dick_costantino_user', 'rrhh@dickcostantinosa.com.ar', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('proinfo_unc_user', 'contable@informatica.unc.edu.ar', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('impro_user', 'rrhhimpro1@gmail.com', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('fumiscor_user', 'gestiondetalento.gm@grupomarma.com.ar', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('iveco_user', 'talentos.ivg-argentina@ivecogroup.com', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('banco_roela_user', 'rrhh@bancoroela.com.ar', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('spinozzi_user', 'spinozzirrhh@gmail.com', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('helios_user', 'seleccioncaphumano@selenesoluciones.com', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('maxion_montich_user', 'atorres@montich.com.ar', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('adecco_user', 'micaela.cardus@adecco.com', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('amx_claro_user', 'rrhh@claro.com.ar', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('avenue_user', 'rocio.suarez@grupoquijada.com.ar', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('dayco_user', 'alfonsina.gioino@dayco.com', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE),
('hospital_privado_user', 'azul.merlo@hospitalprivado.com.ar', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE);

INSERT INTO Carrera (nombre) VALUES
('Ingeniería Civil'),
('Ingeniería Industrial'),
('Ingeniería Electrónica'),
('Ingeniería Mecánica'),
('Ingeniería Química'),
('Ingeniería en Sistemas'),
('Ingeniería Eléctrica'),
('Ingeniería Aeroespacial');

-- Continue with remaining INSERT statements from original schema...
-- (I can provide the full version if needed, but this covers the structure)