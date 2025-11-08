-- Script para verificar que la pasantía se registró correctamente
-- Ejecutar después de crear una pasantía

USE db_pasantias;

-- Ver la última pasantía creada
SELECT 
    id_pasantia,
    titulo,
    puesto_a_cubrir,
    ciudad,
    modalidad,
    asignacion_estimulo,
    cantidad_de_pasantes,
    fecha_publicacion,
    fecha_caducidad,
    estado,
    email_contacto,
    conocimientos,
    otros_requisitos,
    beneficios,
    id_empresa,
    fecha_creacion
FROM Pasantia 
ORDER BY id_pasantia DESC 
LIMIT 1;

-- Verificar que los nuevos campos no son NULL (si se enviaron)
SELECT 
    id_pasantia,
    titulo,
    CASE 
        WHEN conocimientos IS NULL THEN 'NULL'
        WHEN conocimientos = '' THEN 'VACIO'
        ELSE 'TIENE VALOR'
    END AS estado_conocimientos,
    CASE 
        WHEN otros_requisitos IS NULL THEN 'NULL'
        WHEN otros_requisitos = '' THEN 'VACIO'
        ELSE 'TIENE VALOR'
    END AS estado_otros_requisitos,
    CASE 
        WHEN beneficios IS NULL THEN 'NULL'
        WHEN beneficios = '' THEN 'VACIO'
        ELSE 'TIENE VALOR'
    END AS estado_beneficios
FROM Pasantia 
ORDER BY id_pasantia DESC 
LIMIT 1;

-- Verificar relaciones con carreras
SELECT 
    p.id_pasantia,
    p.titulo,
    c.id_carrera,
    c.nombre AS nombre_carrera
FROM Pasantia p
INNER JOIN Pasantia_Carrera pc ON p.id_pasantia = pc.id_pasantia
INNER JOIN Carrera c ON pc.id_carrera = c.id_carrera
WHERE p.id_pasantia = (SELECT MAX(id_pasantia) FROM Pasantia);

