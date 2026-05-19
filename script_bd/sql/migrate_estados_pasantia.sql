-- =============================================================================
-- Migración: Simplificar estados de pasantías a PUBLICADA / FINALIZADA
-- Fecha: 2026-05-19
-- 
-- Este script migra los datos existentes y actualiza el ENUM de PostgreSQL.
-- Ejecutar en una transacción para garantizar atomicidad.
-- =============================================================================

BEGIN;

-- Paso 1: Migrar datos existentes a los nuevos estados
-- PENDIENTE_DE_APROBACION → PUBLICADA (las que estaban pendientes se publican)
UPDATE Pasantia SET estado = 'PUBLICADA' WHERE estado = 'PENDIENTE_DE_APROBACION';

-- DADA_DE_BAJA → FINALIZADA
UPDATE Pasantia SET estado = 'FINALIZADA' WHERE estado = 'DADA_DE_BAJA';

-- EXPIRADA → FINALIZADA
UPDATE Pasantia SET estado = 'FINALIZADA' WHERE estado = 'EXPIRADA';

-- Paso 2: Cambiar el valor por defecto de la columna
ALTER TABLE Pasantia ALTER COLUMN estado SET DEFAULT 'PUBLICADA';

-- Paso 3: Recrear el ENUM sin los valores obsoletos
-- PostgreSQL no permite eliminar valores de un ENUM directamente,
-- así que hay que crear un nuevo tipo, migrar la columna y eliminar el viejo.

-- 3a. Crear nuevo ENUM
CREATE TYPE estado_pasantia_new AS ENUM('PUBLICADA', 'FINALIZADA');

-- 3b. Quitar el default temporalmente
ALTER TABLE Pasantia ALTER COLUMN estado DROP DEFAULT;

-- 3c. Migrar la columna al nuevo tipo
ALTER TABLE Pasantia 
    ALTER COLUMN estado TYPE estado_pasantia_new 
    USING estado::text::estado_pasantia_new;

-- 3d. Restaurar el default con el nuevo tipo
ALTER TABLE Pasantia ALTER COLUMN estado SET DEFAULT 'PUBLICADA';

-- 3e. Eliminar el ENUM viejo y renombrar el nuevo
DROP TYPE estado_pasantia;
ALTER TYPE estado_pasantia_new RENAME TO estado_pasantia;

COMMIT;

-- Verificación
SELECT estado, COUNT(*) FROM Pasantia GROUP BY estado;
