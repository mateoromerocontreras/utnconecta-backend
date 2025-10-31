-- Script para verificar pasantías en la base de datos

USE db_pasantias;

-- Ver todas las pasantías
SELECT 
    p.id_pasantia,
    p.titulo,
    p.puesto_a_cubrir,
    p.ciudad,
    p.modalidad,
    p.asignacion_estimulo,
    p.cantidad_de_pasantes,
    p.fecha_publicacion,
    p.fecha_caducidad,
    p.estado,
    p.email_contacto,
    e.nombre AS empresa,
    p.fecha_creacion
FROM Pasantia p
INNER JOIN Empresa e ON p.id_empresa = e.id_empresa
ORDER BY p.fecha_creacion DESC;

-- Ver carreras asociadas a cada pasantía
SELECT 
    p.id_pasantia,
    p.titulo,
    p.estado,
    c.nombre AS carrera
FROM Pasantia p
INNER JOIN Pasantia_Carrera pc ON p.id_pasantia = pc.id_pasantia
INNER JOIN Carrera c ON pc.id_carrera = c.id_carrera
ORDER BY p.id_pasantia;

-- Contar pasantías por estado
SELECT 
    estado,
    COUNT(*) AS cantidad
FROM Pasantia
GROUP BY estado;

-- Ver la última pasantía creada
SELECT 
    p.*,
    e.nombre AS empresa
FROM Pasantia p
INNER JOIN Empresa e ON p.id_empresa = e.id_empresa
ORDER BY p.fecha_creacion DESC
LIMIT 1;
