#!/bin/bash

# Script para iniciar solo el backend con Maven (desarrollo)
# La base de datos debe estar corriendo en Docker

echo "======================================"
echo "Iniciando Backend en modo desarrollo"
echo "======================================"
echo ""

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Verificar que la base de datos está corriendo
echo -e "${BLUE}Verificando base de datos...${NC}"
DB_CONTAINER="script_bd-db-1"

if docker ps --format '{{.Names}}' | grep -q "^${DB_CONTAINER}$"; then
    echo -e "${GREEN}✓ Base de datos corriendo en Docker${NC}"
else
    echo -e "${YELLOW}⚠ Base de datos no está corriendo${NC}"
    echo -e "${BLUE}Iniciando base de datos...${NC}"
    cd script_bd
    docker-compose up -d
    cd ..
    echo "Esperando 10 segundos para que la BD inicie..."
    sleep 10
fi

echo ""
echo -e "${BLUE}Iniciando Spring Boot con Maven...${NC}"
echo "Puerto: 8080"
echo "Para detener: Ctrl+C"
echo ""

cd pasantias
./mvnw spring-boot:run
