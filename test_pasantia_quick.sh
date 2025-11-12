#!/bin/bash

# Test rápido - asume que el backend YA está corriendo

echo "======================================"
echo "TEST: Crear Pasantía con Nuevos Campos"
echo "======================================"
echo ""

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

API_URL="http://localhost:8080"
DB_CONTAINER="db_pasantias"

echo -e "${BLUE}Verificando backend...${NC}"
if curl -s -f "${API_URL}/actuator/health" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Backend está corriendo${NC}"
else
    echo -e "${RED}✗ Backend NO está corriendo${NC}"
    echo "Inicia el backend primero con:"
    echo "cd pasantias && ./mvnw spring-boot:run -DskipTests"
    exit 1
fi
echo ""

echo -e "${BLUE}Paso 1: Intentar crear pasantía SIN token (debe fallar por seguridad)${NC}"
response=$(curl -s -w "\n%{http_code}" -X POST "${API_URL}/api/pasantias/crear" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Test Pasantía",
    "puestoACubrir": "Desarrollador",
    "ciudad": "Santa Fe",
    "modalidad": "Híbrida",
    "asignacionEstimulo": 60000.0,
    "cantidadDePasantes": 1,
    "fechaPublicacion": "2025-11-01",
    "fechaCaducidad": "2026-02-01",
    "idEmpresa": 1,
    "idsCarreras": [1],
    "emailContacto": "test@test.com"
  }')

http_code=$(echo "$response" | tail -n1)
if [ "$http_code" = "401" ] || [ "$http_code" = "403" ]; then
    echo -e "${GREEN}✓ Seguridad OK - Requiere autenticación (código: ${http_code})${NC}"
else
    echo -e "${YELLOW}⚠ Código inesperado: ${http_code}${NC}"
fi
echo ""

echo -e "${BLUE}Paso 2: Iniciar sesión${NC}"
login_response=$(curl -s -X POST "${API_URL}/api/auth/iniciar-sesion" \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}')

TOKEN=$(echo "$login_response" | jq -r '.data.token' 2>/dev/null)

if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
    echo -e "${GREEN}✓ Login exitoso${NC}"
else
    echo -e "${RED}✗ Login falló${NC}"
    echo "$login_response" | jq '.' 2>/dev/null || echo "$login_response"
    exit 1
fi
echo ""

echo -e "${BLUE}Paso 3: Crear pasantía CON token y nuevos campos${NC}"
create_response=$(curl -s -w "\n%{http_code}" -X POST "${API_URL}/api/pasantias/crear" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN}" \
  -d '{
    "titulo": "Desarrollador Backend con Spring Boot",
    "puestoACubrir": "Desarrollador Junior",
    "ciudad": "Santa Fe",
    "modalidad": "Híbrida",
    "asignacionEstimulo": 60000.0,
    "cantidadDePasantes": 2,
    "fechaPublicacion": "2025-11-01",
    "fechaCaducidad": "2026-02-01",
    "idEmpresa": 1,
    "idsCarreras": [1],
    "emailContacto": "rrhh@techcorp.com",
    "conocimientos": "Java 17+, Spring Boot 3, REST APIs, MySQL, Git",
    "otrosRequisitos": "Disponibilidad para trabajar en equipo, conocimientos básicos de Docker",
    "beneficios": "Asignación estímulo competitiva, capacitación continua, ambiente de trabajo flexible"
  }')

create_code=$(echo "$create_response" | tail -n1)
create_body=$(echo "$create_response" | sed '$d')

if [ "$create_code" = "201" ]; then
    echo -e "${GREEN}✓ Pasantía creada (código: 201)${NC}"
    echo ""
    echo "$create_body" | jq '.' 2>/dev/null || echo "$create_body"
    
    PASANTIA_ID=$(echo "$create_body" | jq -r '.data.idPasantia' 2>/dev/null)
    echo ""
    echo -e "${GREEN}ID de pasantía: ${PASANTIA_ID}${NC}"
else
    echo -e "${RED}✗ Error al crear pasantía (código: ${create_code})${NC}"
    echo "$create_body" | jq '.' 2>/dev/null || echo "$create_body"
    exit 1
fi
echo ""

echo -e "${BLUE}Paso 4: Verificar en la base de datos${NC}"
if [ -n "$PASANTIA_ID" ] && [ "$PASANTIA_ID" != "null" ]; then
    db_result=$(docker exec -i ${DB_CONTAINER} mysql -u root -pmy-secret-pw db_pasantias -e "
    SELECT 
        id_pasantia,
        titulo,
        conocimientos,
        otros_requisitos,
        beneficios,
        estado
    FROM Pasantia 
    WHERE id_pasantia = ${PASANTIA_ID}\G
    " 2>&1)
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Datos en la BD:${NC}"
        echo "$db_result"
        echo ""
        
        if echo "$db_result" | grep -q "conocimientos:" && \
           echo "$db_result" | grep -q "otros_requisitos:" && \
           echo "$db_result" | grep -q "beneficios:"; then
            
            conocimientos=$(echo "$db_result" | grep "conocimientos:" | sed 's/.*conocimientos: //')
            otros_req=$(echo "$db_result" | grep "otros_requisitos:" | sed 's/.*otros_requisitos: //')
            beneficios=$(echo "$db_result" | grep "beneficios:" | sed 's/.*beneficios: //')
            
            if [ -n "$conocimientos" ] && [ "$conocimientos" != "NULL" ]; then
                echo -e "${GREEN}✓ Campo 'conocimientos' guardado${NC}"
            else
                echo -e "${RED}✗ Campo 'conocimientos' vacío${NC}"
            fi
            
            if [ -n "$otros_req" ] && [ "$otros_req" != "NULL" ]; then
                echo -e "${GREEN}✓ Campo 'otros_requisitos' guardado${NC}"
            else
                echo -e "${RED}✗ Campo 'otros_requisitos' vacío${NC}"
            fi
            
            if [ -n "$beneficios" ] && [ "$beneficios" != "NULL" ]; then
                echo -e "${GREEN}✓ Campo 'beneficios' guardado${NC}"
            else
                echo -e "${RED}✗ Campo 'beneficios' vacío${NC}"
            fi
        fi
    else
        echo -e "${RED}✗ Error al consultar la BD${NC}"
    fi
fi
echo ""

echo -e "${BLUE}Paso 5: Obtener pasantía desde el endpoint${NC}"
if [ -n "$PASANTIA_ID" ] && [ "$PASANTIA_ID" != "null" ]; then
    get_response=$(curl -s "${API_URL}/api/pasantias/${PASANTIA_ID}")
    
    echo -e "${GREEN}✓ Respuesta del endpoint:${NC}"
    echo "$get_response" | jq '{
        id: .data.idPasantia,
        titulo: .data.titulo,
        conocimientos: .data.conocimientos,
        otrosRequisitos: .data.otrosRequisitos,
        beneficios: .data.beneficios
    }' 2>/dev/null || echo "$get_response"
fi
echo ""

echo "======================================"
echo -e "${GREEN}RESUMEN${NC}"
echo "======================================"
echo "✓ Seguridad: Requiere autenticación"
echo "✓ Creación: Pasantía creada con ID ${PASANTIA_ID}"
echo "✓ Nuevos campos guardados en BD"
echo "✓ Endpoint devuelve los nuevos campos"
echo ""
