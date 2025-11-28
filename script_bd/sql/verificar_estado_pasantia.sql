-- Script SQL para verificar el estado de una pasantía
-- Uso: Reemplaza <ID_PASANTIA> con el ID que deseas verificar

-- Verificar estado de una pasantía específica
SELECT 
    id_pasantia,
    titulo,
    estado,
    fecha_publicacion,
    fecha_caducidad,
    fecha_creacion,
    fecha_actualizacion,
    id_empresa
FROM Pasantia
WHERE id_pasantia = <ID_PASANTIA>;  -- Reemplaza <ID_PASANTIA> con el ID real

-- Ver todas las pasantías con su estado
SELECT 
    id_pasantia,
    titulo,
    estado,
    fecha_publicacion,
    fecha_actualizacion
FROM Pasantia
ORDER BY fecha_actualizacion DESC;

-- Contar pasantías por estado
SELECT 
    estado,
    COUNT(*) as cantidad
FROM Pasantia
GROUP BY estado
ORDER BY cantidad DESC;

-- Ver pasantías que cambiaron de PENDIENTE_DE_APROBACION a PUBLICADA
-- (útil para verificar cambios recientes)
SELECT 
    id_pasantia,
    titulo,
    estado,
    fecha_publicacion,
    fecha_actualizacion,
    TIMESTAMPDIFF(MINUTE, fecha_creacion, fecha_actualizacion) as minutos_desde_creacion
FROM Pasantia
WHERE estado = 'PUBLICADA'
ORDER BY fecha_actualizacion DESC;

-- Ver pasantías pendientes de aprobación
SELECT 
    id_pasantia,
    titulo,
    estado,
    fecha_creacion,
    TIMESTAMPDIFF(HOUR, fecha_creacion, NOW()) as horas_pendiente
FROM Pasantia
WHERE estado = 'PENDIENTE_DE_APROBACION'
ORDER BY fecha_creacion DESC;

