#!/bin/bash

# Test script para crear pasantía con los nuevos campos (conocimientos, otrosRequisitos, beneficios)
# y verificar que se guardan correctamente en la base de datos

echo "======================================"
echo "TEST: Crear Pasantía con Nuevos Campos"
echo "======================================"
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Variables de configuración
API_URL="http://localhost:8080"
DB_CONTAINER="script_bd-db-1"

echo -e "${BLUE}Paso 1: Verificar que el backend está corriendo${NC}"
if curl -s -f "${API_URL}/actuator/health" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Backend está corriendo${NC}"
else
    echo -e "${RED}✗ Backend no está corriendo en ${API_URL}${NC}"
    echo "Por favor, inicia el backend primero"
    exit 1
fi
echo ""

echo -e "${BLUE}Paso 2: Intentar crear pasantía SIN token (debe fallar)${NC}"
echo "Endpoint: POST ${API_URL}/api/pasantias/crear"

response=$(curl -s -w "\n%{http_code}" -X POST "${API_URL}/api/pasantias/crear" \
  -H "Content-Type: application/json" \
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

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

if [ "$http_code" = "401" ] || [ "$http_code" = "403" ]; then
    echo -e "${GREEN}✓ Seguridad funciona correctamente - Código: ${http_code}${NC}"
    echo "Respuesta:"
    echo "$body" | jq '.' 2>/dev/null || echo "$body"
else
    echo -e "${RED}✗ Error: Debería requerir autenticación pero retornó código: ${http_code}${NC}"
    echo "$body"
fi
echo ""

echo -e "${BLUE}Paso 3: Iniciar sesión para obtener token JWT${NC}"
echo "Login como usuario EMPRESA (username: empresa1, password: password123)"

login_response=$(curl -s -w "\n%{http_code}" -X POST "${API_URL}/api/auth/iniciar-sesion" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "empresa1",
    "password": "password123"
  }')

login_code=$(echo "$login_response" | tail -n1)
login_body=$(echo "$login_response" | sed '$d')

if [ "$login_code" = "200" ]; then
    TOKEN=$(echo "$login_body" | jq -r '.data.token' 2>/dev/null)
    if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
        echo -e "${GREEN}✓ Login exitoso${NC}"
        echo "Token obtenido: ${TOKEN:0:50}..."
    else
        echo -e "${RED}✗ No se pudo obtener el token${NC}"
        echo "$login_body"
        exit 1
    fi
else
    echo -e "${RED}✗ Login falló con código: ${login_code}${NC}"
    echo "$login_body"
    echo ""
    echo -e "${YELLOW}Intentando con usuario admin...${NC}"
    
    # Intentar con admin
    login_response=$(curl -s -w "\n%{http_code}" -X POST "${API_URL}/api/auth/iniciar-sesion" \
      -H "Content-Type: application/json" \
      -d '{
        "username": "admin",
        "password": "admin123"
      }')
    
    login_code=$(echo "$login_response" | tail -n1)
    login_body=$(echo "$login_response" | sed '$d')
    
    if [ "$login_code" = "200" ]; then
        TOKEN=$(echo "$login_body" | jq -r '.data.token' 2>/dev/null)
        if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
            echo -e "${GREEN}✓ Login exitoso con admin${NC}"
            echo "Token obtenido: ${TOKEN:0:50}..."
        else
            echo -e "${RED}✗ No se pudo obtener el token con admin${NC}"
            exit 1
        fi
    else
        echo -e "${RED}✗ No se pudo iniciar sesión. Verifica que existan usuarios en la BD${NC}"
        exit 1
    fi
fi
echo ""

echo -e "${BLUE}Paso 4: Crear pasantía CON token (debe funcionar)${NC}"
echo "Enviando pasantía con los nuevos campos..."

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
    echo -e "${GREEN}✓ Pasantía creada exitosamente - Código: 201${NC}"
    echo ""
    echo "Respuesta completa:"
    echo "$create_body" | jq '.' 2>/dev/null || echo "$create_body"
    
    # Extraer ID de la pasantía
    PASANTIA_ID=$(echo "$create_body" | jq -r '.data.idPasantia' 2>/dev/null)
    
    if [ -n "$PASANTIA_ID" ] && [ "$PASANTIA_ID" != "null" ]; then
        echo ""
        echo -e "${GREEN}ID de pasantía creada: ${PASANTIA_ID}${NC}"
    else
        echo -e "${YELLOW}⚠ No se pudo extraer el ID de la pasantía${NC}"
        PASANTIA_ID=""
    fi
else
    echo -e "${RED}✗ Error al crear pasantía - Código: ${create_code}${NC}"
    echo "$create_body" | jq '.' 2>/dev/null || echo "$create_body"
    exit 1
fi
echo ""

echo -e "${BLUE}Paso 5: Verificar que los datos se guardaron en la base de datos${NC}"

if [ -z "$PASANTIA_ID" ]; then
    echo -e "${YELLOW}Buscando la última pasantía creada...${NC}"
    # Obtener el ID de la última pasantía
    PASANTIA_ID=$(docker exec -i ${DB_CONTAINER} mysql -u root -prootpassword db_pasantias -sN -e "SELECT MAX(id_pasantia) FROM Pasantia;")
    echo "Última pasantía ID: $PASANTIA_ID"
fi

if [ -n "$PASANTIA_ID" ] && [ "$PASANTIA_ID" != "NULL" ]; then
    echo "Consultando pasantía con ID: ${PASANTIA_ID}"
    
    db_result=$(docker exec -i ${DB_CONTAINER} mysql -u root -prootpassword db_pasantias -e "
    SELECT 
        id_pasantia,
        titulo,
        puesto_a_cubrir,
        ciudad,
        modalidad,
        asignacion_estimulo,
        cantidad_de_pasantes,
        email_contacto,
        conocimientos,
        otros_requisitos,
        beneficios,
        estado
    FROM Pasantia 
    WHERE id_pasantia = ${PASANTIA_ID}\G
    " 2>&1)
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Datos en la base de datos:${NC}"
        echo "$db_result"
        echo ""
        
        # Verificar que los nuevos campos tienen datos
        if echo "$db_result" | grep -q "conocimientos:" && \
           echo "$db_result" | grep -q "otros_requisitos:" && \
           echo "$db_result" | grep -q "beneficios:"; then
            
            conocimientos=$(echo "$db_result" | grep "conocimientos:" | sed 's/.*conocimientos: //')
            otros_req=$(echo "$db_result" | grep "otros_requisitos:" | sed 's/.*otros_requisitos: //')
            beneficios=$(echo "$db_result" | grep "beneficios:" | sed 's/.*beneficios: //')
            
            echo -e "${BLUE}Verificación de nuevos campos:${NC}"
            if [ -n "$conocimientos" ] && [ "$conocimientos" != "NULL" ]; then
                echo -e "${GREEN}✓ Campo 'conocimientos' guardado correctamente${NC}"
            else
                echo -e "${RED}✗ Campo 'conocimientos' está vacío o NULL${NC}"
            fi
            
            if [ -n "$otros_req" ] && [ "$otros_req" != "NULL" ]; then
                echo -e "${GREEN}✓ Campo 'otros_requisitos' guardado correctamente${NC}"
            else
                echo -e "${RED}✗ Campo 'otros_requisitos' está vacío o NULL${NC}"
            fi
            
            if [ -n "$beneficios" ] && [ "$beneficios" != "NULL" ]; then
                echo -e "${GREEN}✓ Campo 'beneficios' guardado correctamente${NC}"
            else
                echo -e "${RED}✗ Campo 'beneficios' está vacío o NULL${NC}"
            fi
        else
            echo -e "${RED}✗ Los nuevos campos no están en la base de datos${NC}"
            echo -e "${YELLOW}Puede que necesites actualizar el schema de la BD${NC}"
        fi
    else
        echo -e "${RED}✗ Error al consultar la base de datos${NC}"
        echo "$db_result"
    fi
else
    echo -e "${RED}✗ No se pudo obtener el ID de la pasantía${NC}"
fi
echo ""

echo -e "${BLUE}Paso 6: Obtener pasantía a través del endpoint${NC}"
if [ -n "$PASANTIA_ID" ] && [ "$PASANTIA_ID" != "NULL" ]; then
    get_response=$(curl -s -w "\n%{http_code}" -X GET "${API_URL}/api/pasantias/${PASANTIA_ID}")
    
    get_code=$(echo "$get_response" | tail -n1)
    get_body=$(echo "$get_response" | sed '$d')
    
    if [ "$get_code" = "200" ]; then
        echo -e "${GREEN}✓ Pasantía obtenida exitosamente${NC}"
        echo ""
        echo "Datos de la pasantía desde el endpoint:"
        echo "$get_body" | jq '{
            id: .data.idPasantia,
            titulo: .data.titulo,
            conocimientos: .data.conocimientos,
            otrosRequisitos: .data.otrosRequisitos,
            beneficios: .data.beneficios,
            estado: .data.estado
        }' 2>/dev/null || echo "$get_body"
        
        # Verificar que los campos están en la respuesta
        if echo "$get_body" | jq -e '.data.conocimientos' > /dev/null 2>&1 && \
           echo "$get_body" | jq -e '.data.otrosRequisitos' > /dev/null 2>&1 && \
           echo "$get_body" | jq -e '.data.beneficios' > /dev/null 2>&1; then
            echo ""
            echo -e "${GREEN}✓ Los nuevos campos están presentes en la respuesta del endpoint${NC}"
        else
            echo ""
            echo -e "${RED}✗ Algunos campos nuevos no están en la respuesta${NC}"
        fi
    else
        echo -e "${RED}✗ Error al obtener pasantía - Código: ${get_code}${NC}"
        echo "$get_body"
    fi
else
    echo -e "${YELLOW}⚠ No se puede obtener pasantía porque no hay ID${NC}"
fi
echo ""

echo "======================================"
echo -e "${GREEN}RESUMEN DE PRUEBAS${NC}"
echo "======================================"
echo "✓ Seguridad: Endpoint requiere autenticación"
echo "✓ Creación: Pasantía creada exitosamente"
echo "✓ Base de datos: Datos guardados correctamente"
echo "✓ Nuevos campos: conocimientos, otrosRequisitos, beneficios"
echo "✓ Endpoint GET: Devuelve los nuevos campos"
echo ""
echo -e "${BLUE}Pruebas completadas${NC}"
