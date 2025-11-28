-- Script para verificar la codificación UTF-8 en la base de datos
-- Ejecutar: docker exec -it db_pasantias mysql -uroot -proot db_pasantias < verify_encoding.sql

-- 1. Verificar charset y collation de la base de datos
SHOW CREATE DATABASE db_pasantias;

-- 2. Verificar charset y collation de la tabla Pasantia
SHOW CREATE TABLE Pasantia;

-- 3. Verificar charset de columnas específicas
SELECT 
    COLUMN_NAME,
    CHARACTER_SET_NAME,
    COLLATION_NAME
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'db_pasantias'
  AND TABLE_NAME = 'Pasantia'
  AND CHARACTER_SET_NAME IS NOT NULL;

-- 4. Inspeccionar datos problemáticos - ver representación hexadecimal
SELECT 
    id_pasantia,
    titulo,
    HEX(titulo) as hex_representation,
    LENGTH(titulo) as byte_length,
    CHAR_LENGTH(titulo) as char_length
FROM Pasantia 
WHERE titulo LIKE '%Garant%' 
   OR titulo LIKE '%%'
LIMIT 10;

-- 5. Verificar si hay caracteres que no son UTF-8 válidos
SELECT 
    id_pasantia,
    titulo,
    CASE 
        WHEN titulo REGEXP '[Ã][^a-zA-Z]' THEN 'Possible encoding issue'
        ELSE 'OK'
    END as encoding_status
FROM Pasantia
WHERE titulo REGEXP '[Ã]'
LIMIT 10;

-- 6. Verificar conexión actual
SHOW VARIABLES LIKE 'character_set%';
SHOW VARIABLES LIKE 'collation%';

