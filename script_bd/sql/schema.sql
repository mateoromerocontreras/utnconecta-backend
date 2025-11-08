-- Elimina la base de datos si ya existe para empezar de cero
DROP DATABASE IF EXISTS db_pasantias;
CREATE DATABASE db_pasantias CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
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

--
-- Estructura de la tabla `Pasantia`
--
CREATE TABLE Pasantia (
    id_pasantia INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    puesto_a_cubrir VARCHAR(150) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    modalidad VARCHAR(50) NOT NULL,
    asignacion_estimulo FLOAT,
    cantidad_de_pasantes INT NOT NULL DEFAULT 1,
    fecha_publicacion DATE NOT NULL,
    fecha_caducidad DATE NOT NULL,
    estado ENUM('PUBLICADA', 'FINALIZADA', 'DADA_DE_BAJA', 'PENDIENTE_DE_APROBACION', 'EXPIRADA') NOT NULL DEFAULT 'PENDIENTE_DE_APROBACION',
    email_contacto VARCHAR(100) NOT NULL,
    conocimientos TEXT,
    otros_requisitos TEXT,
    beneficios TEXT,
    id_empresa INT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_empresa) REFERENCES Empresa(id_empresa) ON DELETE CASCADE
);

--
-- Tabla intermedia `Pasantia_Carrera` (relación N:N entre Pasantia y Carrera)
--
CREATE TABLE Pasantia_Carrera (
    id_pasantia INT NOT NULL,
    id_carrera INT NOT NULL,
    PRIMARY KEY (id_pasantia, id_carrera),
    FOREIGN KEY (id_pasantia) REFERENCES Pasantia(id_pasantia) ON DELETE CASCADE,
    FOREIGN KEY (id_carrera) REFERENCES Carrera(id_carrera) ON DELETE CASCADE
);

--
-- Estructura de la tabla `Postulacion` (actualizada)
--
CREATE TABLE Postulacion (
    id_postulacion INT PRIMARY KEY AUTO_INCREMENT,
    fecha_postulacion DATE NOT NULL,
    fecha_inicio_contrato DATE,
    duracion_meses INT,
    estado ENUM('BORRADOR', 'PENDIENTE_APROBACION', 'PUBLICADA', 'CUBIERTA', 'FINALIZADA') NOT NULL DEFAULT 'BORRADOR',
    id_pasantia INT NOT NULL,
    id_estudiante INT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_postulacion_pasantia
        FOREIGN KEY (id_pasantia)
        REFERENCES Pasantia(id_pasantia)
        ON DELETE CASCADE,
    CONSTRAINT fk_postulacion_estudiante
        FOREIGN KEY (id_estudiante)
        REFERENCES Estudiante(id_estudiante)
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
('empresa1', 'empresa1@empresas.com', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE), -- Contraseña: empresa123
('biofarma_user', 'rrhh@biofarmaweb.com.ar', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE), -- Contraseña: empresa123
('hospital_user', 'seleccion@hospital-italiano.org.ar', '$2a$10$XxM932luMMMgVuTyTCOaj.VqnTNK/yWtRy3dR7BUJuaqh76TE10gO', 3, TRUE); -- Contraseña: empresa123

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
INSERT INTO Empresa (nombre, ciudad, calle, nro_calle, piso, departamento, barrio, email, cuit, razon_social, id_usuario) VALUES
('BIOFARMA S.A', 'Córdoba', 'Bv. de los Polacos', 6446, NULL, NULL, 'Los Boulevares', 'rrhh@biofarmaweb.com.ar','30-76543210-3', 'BIOFARMA S.A.', 4),
('INDACOR S.A.', 'JUAREZ CELMAN', 'Ruta 9 norte km 721', NULL, NULL, NULL, NULL, 'aracelipenaflor@pollosindacor.com.ar', '33-12345678-9', 'INDACOR S.A.', NULL),
('Soc. de Beneficencia Hospital Italiano', 'Córdoba', 'Roma', 577, NULL, NULL, NULL, 'seleccion@hospital-italiano.org.ar', NULL, 'Soc. de Beneficencia Hospital Italiano', 5),
('SOWIC S.A', 'Córdoba', 'Av. La Voz del Interior', 7000, NULL, NULL, 'Torre Miragolf Oeste', 'seleccion@sowic.com.ar', NULL, 'SOWIC S.A', NULL),
('LOS MOLINOS SRL (ELECTROALEM)', 'Córdoba Capital', 'RN19 Ex Km 12', NULL, NULL, NULL, 'Malvinas Argentinas', 'rrhhelectroalem@gmail.com', NULL, 'LOS MOLINOS SRL', NULL),
('HARRIAGUE Y ASOCIADOS SRL (Avenga)', 'Córdoba', NULL, NULL, NULL, NULL, 'Capitalinas', 'natalia.barrionuevo@avenga.com', NULL, 'HARRIAGUE Y ASOCIADOS SRL', NULL),
('Universidad Nacional de Córdoba - FFyH', 'Córdoba', 'Pabellón Agustín Tosco', NULL, NULL, NULL, 'Ciudad Universitaria', 'biblio@ffyh.unc.edu.ar', NULL, 'Universidad Nacional de Córdoba', NULL),
('Grupo Kersia', 'Córdoba', NULL, NULL, NULL, NULL, NULL, 'constanza.diebel@kersia-group.com', NULL, 'Grupo Kersia', NULL),
('ELECTROMECÁNICA DICK COSTANTINO SA - WEDO', 'Río Tercero', NULL, NULL, NULL, NULL, NULL, 'rrhh@dickcostantinosa.com.ar', NULL, 'ELECTROMECÁNICA DICK COSTANTINO SA', NULL),
('Prosecretaría de Informática - UNC', 'Córdoba', 'Av Haya de la Torre S/N Pabellón Argentina', NULL, '1er piso', NULL, 'Ciudad Universitaria', 'contable@informatica.unc.edu.ar', NULL, 'Prosecretaría de Informática - UNC', NULL),
('IMPRO SRL', 'Córdoba', NULL, NULL, NULL, NULL, 'Est. Flores', 'rrhhimpro1@gmail.com', NULL, 'IMPRO SRL', NULL),
('Fumiscor S.A', 'Córdoba', 'Avenida circunvalación km 4 y medio', NULL, NULL, NULL, 'Los Olmos Sur', 'gestiondetalento.gm@grupomarma.com.ar', NULL, 'Fumiscor S.A', NULL),
('IVECO ARGENTINA S.A.', 'Córdoba', NULL, NULL, NULL, NULL, 'Ferreyra', 'talentos.ivg-argentina@ivecogroup.com', NULL, 'IVECO ARGENTINA S.A.', NULL),
('BANCO ROELA', 'Córdoba', 'Rosario de Santa Fé', 275, NULL, NULL, NULL, 'rrhh@bancoroela.com.ar', NULL, 'BANCO ROELA', NULL),
('SPINOZZI SAS', 'Córdoba', NULL, NULL, NULL, NULL, NULL, 'SPINOZZIRRHH@GMAIL.COM', NULL, 'SPINOZZI SAS', NULL),
('HELIOS ENERGÍA LIMPIA S.A.', 'Estación General Paz', 'Córdoba', NULL, NULL, NULL, NULL, 'seleccioncaphumano@selenesoluciones.com', NULL, 'HELIOS ENERGÍA LIMPIA S.A.', NULL),
('MAXION MONTICH S.A.', 'Córdoba', 'Av 11 de septiembre', 3768, NULL, NULL, NULL, 'atorres@montich.com.ar', NULL, 'MAXION MONTICH S.A.', NULL),
('Adecco Argentina/Stellantis', 'Córdoba', NULL, NULL, NULL, NULL, 'Ferreyra', 'micaela.cardus@adecco.com', NULL, 'Adecco Argentina/Stellantis', NULL),
('AMX ARGENTINA S.A. (CLARO ARGENTINA)', 'Córdoba', 'Av. Sabattini', 1417, NULL, NULL, NULL, 'rrhh@claro.com.ar', NULL, 'AMX ARGENTINA S.A.', NULL),
('AVENUE SA', 'Córdoba', 'Avenida Castro barros', 1155, NULL, NULL, NULL, 'rocio.suarez@grupoquijada.com.ar', NULL, 'AVENUE SA', NULL),
('Dayco Argentina S.A.', 'Córdoba', 'Juan R. Estomba', NULL, NULL, NULL, 'Parque Industrial Ferreyra', 'alfonsina.gioino@dayco.com', NULL, 'Dayco Argentina S.A.', NULL),
('Hospital Privado Universitario de Córdoba', 'Córdoba', 'Santa Rosa', 770, NULL, NULL, NULL, 'azul.merlo@hospitalprivado.com.ar', NULL, 'Hospital Privado Universitario de Córdoba', NULL);

--
-- Inserta datos de ejemplo para Contacto
--
INSERT INTO Contacto (nombre, apellido, email_responsable, telefono_responsable, id_empresa) VALUES
('Juan', 'Pérez', 'juan.perez@biofarmaweb.com.ar', '351-123-4567', 1),
('María', 'González', 'maria.gonzalez@pollosindacor.com.ar', '351-987-6543', 2),
('Ana Clara', 'Casullo', 'seleccion@hospital-italiano.org.ar', NULL, 3),
('Carolina', 'Lamelas', 'seleccion@sowic.com.ar', NULL, 4),
('RRHH', 'Electroalem', 'rrhhelectroalem@gmail.com', NULL, 5),
('Natalia', 'Barrionuevo', 'natalia.barrionuevo@avenga.com', NULL, 6),
('Alejandra', 'Greiff', 'biblio@ffyh.unc.edu.ar', '5353610', 7),
('Constanza', 'Diebel', 'constanza.diebel@kersia-group.com', NULL, 8),
('Agostina', 'Peralta', 'rrhh@dickcostantinosa.com.ar', NULL, 9),
('Contacto', 'Informática', 'contable@informatica.unc.edu.ar', NULL, 10),
('Agustin', 'Peirone', 'rrhhimpro1@gmail.com', NULL, 11),
('María Belén', 'Sánchez', 'gestiondetalento.gm@grupomarma.com.ar', NULL, 12),
('Paula', 'Gyurgyev', 'talentos.ivg-argentina@ivecogroup.com', NULL, 13),
('RRHH', 'Banco Roela', 'rrhh@bancoroela.com.ar', NULL, 14),
('Belen', 'Gilli', 'Seleccion@hospital-italiano.org.ar', NULL, 3),
('Mariano', 'Fernandez', 'SPINOZZIRRHH@GMAIL.COM', NULL, 15),
('Regina', 'Gerlach', 'seleccioncaphumano@selenesoluciones.com', NULL, 16),
('Anabella', 'Torres', 'atorres@montich.com.ar', NULL, 17),
('María Sol', 'Benedetic', 'micaela.cardus@adecco.com', NULL, 18),
('Juan Manuel', 'Bachmann', 'rrhh@claro.com.ar', NULL, 19),
('Rocio', 'Suarez', 'rocio.suarez@grupoquijada.com.ar', NULL, 20),
('Alfonsina', 'Gioino', 'alfonsina.gioino@dayco.com', NULL, 21),
('Yanina', 'Giordano', 'azul.merlo@hospitalprivado.com.ar', NULL, 22);



--
-- Inserta datos de ejemplo para Estudiante
--
INSERT INTO Estudiante (dni, apellido, nombre, especialidad, nro_legajo, email, tel_celular, id_usuario, activo) VALUES
('12345678', 'García', 'Juan', 'Ingeniería en Sistemas', 'L001', 'juan.garcia@estudiantes.com', '351-1234567', 2, TRUE);

--
-- Inserta datos de ejemplo para Pasantia
--
INSERT INTO Pasantia (titulo, puesto_a_cubrir, ciudad, modalidad, asignacion_estimulo, cantidad_de_pasantes, fecha_publicacion, fecha_caducidad, estado, email_contacto, conocimientos, otros_requisitos, beneficios, id_empresa) VALUES
('Desarrollador Backend Java', 'Desarrollador Junior', 'Córdoba', 'Híbrida', 50000.0, 2, '2025-11-01', '2026-02-01', 'PUBLICADA', 'rrhh@biofarmaweb.com.ar', NULL, NULL, NULL, 1),
('Analista de Sistemas', 'Analista Junior', 'Córdoba', 'Presencial', 45000.0, 1, '2025-11-01', '2026-01-15', 'PUBLICADA', 'seleccion@hospital-italiano.org.ar', NULL, NULL, NULL, 3),
('Desarrollador Frontend React', 'Desarrollador Frontend', 'Córdoba', 'Remoto', 48000.0, 3, '2025-10-15', '2025-12-31', 'PENDIENTE_DE_APROBACION', 'natalia.barrionuevo@avenga.com', NULL, NULL, NULL, 6),
('Pasante IT', 'Pasante IT', 'Córdoba', 'Híbrida', 725888.0, 3, '2025-12-01', '2026-03-01', 'PENDIENTE_DE_APROBACION', 'rrhh@claro.com.ar', 'Conocimientos de lenguaje de base de datos Oracle\nConocimientos en metodologías ágiles y DevOps\nConocimientos de Linux\nPoder dar soporte a ejecuciones realizadas por los usuarios que usan las herramientas de IC/DC\nInglés (nivel medio)', 'Simplicidad en tu forma de pensar y hacer\nVocación de servicio', 'OSDE 210', 19),
('Pasante de Garantía', 'Pasante de Garantía', 'Córdoba', 'Presencial', 712656.0, 2, '2025-12-01', '2026-03-01', 'PENDIENTE_DE_APROBACION', 'rocio.suarez@grupoquijada.com.ar', 'Estudiantes que estén cursando 3ro o 4to año tendrían los conocimientos necesarios para poder desempeñarse en la posición', 'Manejo de Excel (Básico)', 'SWISS MEDICAL SMG 20', 20),
('Pasante en Seguridad y Ambiente', 'Pasante en Seguridad y Ambiente', 'Córdoba', 'Presencial', 473101.0, 1, '2025-12-01', '2026-03-01', 'PENDIENTE_DE_APROBACION', 'alfonsina.gioino@dayco.com', 'Conocimiento Excel avanzado', NULL, NULL, 21),
('Auxiliar de soporte técnico', 'Auxiliar de soporte técnico', 'Córdoba', 'Presencial', 532411.18, 1, '2025-12-01', '2026-03-01', 'PENDIENTE_DE_APROBACION', 'azul.merlo@hospitalprivado.com.ar', 'Conocimientos en reparación y armado de computadoras, redes, impresoras, entre otras. Buen manejo de Word y Excel.', NULL, 'Ninguno', 22);

--
-- Inserta relaciones Pasantia-Carrera
--
INSERT INTO Pasantia_Carrera (id_pasantia, id_carrera) VALUES
(1, 6), -- Desarrollador Backend Java -> Ingeniería en Sistemas
(2, 6), -- Analista de Sistemas -> Ingeniería en Sistemas
(2, 2), -- Analista de Sistemas -> Ingeniería Industrial
(3, 6), -- Desarrollador Frontend -> Ingeniería en Sistemas
(4, 6), -- Pasante IT (AMX/CLARO) -> Ingeniería en Sistemas
(5, 2), -- Pasante de Garantía (AVENUE) -> Ingeniería Industrial
(5, 4), -- Pasante de Garantía (AVENUE) -> Ingeniería Mecánica
(7, 6); -- Auxiliar de soporte técnico (Hospital Privado) -> Ingeniería en Sistemas
-- Nota: Pasante en Seguridad y Ambiente (Dayco) requiere "Tecnicatura en higiene y seguridad" que no existe en la tabla Carrera

--
-- Inserta datos de ejemplo para Postulacion
--
-- INSERT INTO Postulacion (fecha_postulacion, estado, id_pasantia, id_estudiante) VALUES
-- ('2025-11-02', 'BORRADOR', 1, 1);