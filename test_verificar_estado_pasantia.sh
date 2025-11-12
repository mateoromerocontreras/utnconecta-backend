#!/bin/bash

# Script para verificar el cambio de estado de una pasantía
# de PENDIENTE_DE_APROBACION a PUBLICADA

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

API_URL="http://localhost:8080"
DB_CONTAINER="db_pasantias"

echo "======================================"
echo "VERIFICAR CAMBIO DE ESTADO DE PASANTÍA"
echo "======================================"
echo ""

# Verificar que el backend esté corriendo
echo -e "${BLUE}Verificando backend...${NC}"
if curl -s -f "${API_URL}/actuator/health" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Backend está corriendo${NC}"
else
    echo -e "${RED}✗ Backend NO está corriendo${NC}"
    exit 1
fi
echo ""

# Solicitar ID de pasantía
read -p "Ingresa el ID de la pasantía a verificar: " PASANTIA_ID

if [ -z "$PASANTIA_ID" ]; then
    echo -e "${RED}✗ ID de pasantía requerido${NC}"
    exit 1
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}MÉTODO 1: Verificar vía API (GET /pasantias/{id})${NC}"
echo -e "${BLUE}========================================${NC}"

response=$(curl -s -w "\n%{http_code}" "${API_URL}/pasantias/${PASANTIA_ID}" \
  -H "Accept: application/json;charset=UTF-8")

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" = "200" ]; then
    estado=$(echo "$body" | grep -o '"estado":"[^"]*"' | cut -d'"' -f4)
    titulo=$(echo "$body" | grep -o '"titulo":"[^"]*"' | cut -d'"' -f4)
    
    echo -e "${GREEN}✓ Pasantía encontrada${NC}"
    echo "  ID: $PASANTIA_ID"
    echo "  Título: $titulo"
    echo "  Estado actual: $estado"
    
    if [ "$estado" = "PUBLICADA" ]; then
        echo -e "${GREEN}✓ Estado correcto: PUBLICADA${NC}"
    elif [ "$estado" = "PENDIENTE_DE_APROBACION" ]; then
        echo -e "${YELLOW}⚠ Estado aún es: PENDIENTE_DE_APROBACION${NC}"
        echo "  La pasantía aún no ha sido aprobada"
    else
        echo -e "${YELLOW}⚠ Estado: $estado${NC}"
    fi
else
    echo -e "${RED}✗ Error al obtener pasantía (código: ${http_code})${NC}"
    echo "$body"
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}MÉTODO 2: Verificar vía API (GET /pasantias - lista completa)${NC}"
echo -e "${BLUE}========================================${NC}"

response=$(curl -s "${API_URL}/pasantias" \
  -H "Accept: application/json;charset=UTF-8")

if echo "$response" | grep -q "\"idPasantia\":${PASANTIA_ID}"; then
    estado=$(echo "$response" | grep -A 20 "\"idPasantia\":${PASANTIA_ID}" | grep -o '"estado":"[^"]*"' | head -1 | cut -d'"' -f4)
    echo -e "${GREEN}✓ Pasantía encontrada en la lista${NC}"
    echo "  Estado: $estado"
else
    echo -e "${YELLOW}⚠ Pasantía no encontrada en la lista${NC}"
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}MÉTODO 3: Verificar directamente en la base de datos${NC}"
echo -e "${BLUE}========================================${NC}"

# Verificar si el contenedor de DB está corriendo
if docker ps | grep -q "$DB_CONTAINER"; then
    echo -e "${GREEN}✓ Contenedor de base de datos encontrado${NC}"
    
    query="SELECT id_pasantia, titulo, estado, fecha_actualizacion FROM Pasantia WHERE id_pasantia = ${PASANTIA_ID};"
    
    result=$(docker exec -i "$DB_CONTAINER" mysql -uroot -proot db_pasantias -e "$query" 2>/dev/null)
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "Resultado de la consulta:"
        echo "$result" | column -t
        echo ""
        
        estado_db=$(echo "$result" | grep -v "id_pasantia" | awk '{print $3}')
        if [ "$estado_db" = "PUBLICADA" ]; then
            echo -e "${GREEN}✓ Estado en BD: PUBLICADA${NC}"
        elif [ "$estado_db" = "PENDIENTE_DE_APROBACION" ]; then
            echo -e "${YELLOW}⚠ Estado en BD: PENDIENTE_DE_APROBACION${NC}"
        else
            echo -e "${YELLOW}⚠ Estado en BD: $estado_db${NC}"
        fi
    else
        echo -e "${RED}✗ Error al consultar la base de datos${NC}"
    fi
else
    echo -e "${YELLOW}⚠ Contenedor de base de datos no encontrado${NC}"
    echo "  Para verificar manualmente, ejecuta:"
    echo "  docker exec -it $DB_CONTAINER mysql -uroot -proot db_pasantias"
    echo "  SELECT id_pasantia, titulo, estado, fecha_actualizacion FROM Pasantia WHERE id_pasantia = ${PASANTIA_ID};"
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}MÉTODO 4: Consulta SQL directa${NC}"
echo -e "${BLUE}========================================${NC}"
echo "Ejecuta esta consulta en tu base de datos:"
echo ""
echo -e "${YELLOW}SELECT id_pasantia, titulo, estado, fecha_publicacion, fecha_actualizacion"
echo "FROM Pasantia"
echo "WHERE id_pasantia = ${PASANTIA_ID};${NC}"
echo ""

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}MÉTODO 5: Verificar con curl (ejemplo)${NC}"
echo -e "${BLUE}========================================${NC}"
echo "Comando curl para verificar:"
echo ""
echo -e "${YELLOW}curl -X GET \"${API_URL}/pasantias/${PASANTIA_ID}\" \\"
echo "  -H \"Accept: application/json;charset=UTF-8\" | jq '.estado'${NC}"
echo ""

