#!/bin/bash

# Script de prueba para el endpoint /api/pasantias/crear CON AUTENTICACIÓN
# Verifica la seguridad:
# 1. Usuario EMPRESA solo puede crear pasantías para su propia empresa
# 2. Usuario ADMINISTRADOR puede crear pasantías para cualquier empresa
# 3. Pasantías se crean siempre en estado PENDIENTE_DE_APROBACION

BASE_URL="http://localhost:8080"
API_URL="${BASE_URL}/api/pasantias"
AUTH_URL="${BASE_URL}/auth"

echo "=========================================="
echo "  TEST: Crear Pasantía CON SEGURIDAD"
echo "=========================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# ====================================================================================
# PASO 1: Login como usuario EMPRESA (biofarma_user)
# ====================================================================================
echo -e "${BLUE}PASO 1: Login como usuario EMPRESA (biofarma_user)${NC}"
echo "Usuario: biofarma_user"
echo "Empresa asociada: BIOFARMA S.A (id_empresa=1)"
echo ""

LOGIN_EMPRESA=$(curl -s -X POST "${AUTH_URL}/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "biofarma_user",
    "password": "biofarma123"
  }')

echo "Respuesta de login:"
echo "$LOGIN_EMPRESA" | jq '.'
echo ""

# Extraer token
TOKEN_EMPRESA=$(echo "$LOGIN_EMPRESA" | jq -r '.token // .data.token // empty')

if [ -z "$TOKEN_EMPRESA" ] || [ "$TOKEN_EMPRESA" = "null" ]; then
  echo -e "${RED}❌ ERROR: No se pudo obtener el token de autenticación${NC}"
  echo "Respuesta completa:"
  echo "$LOGIN_EMPRESA"
  exit 1
fi

echo -e "${GREEN}✅ Token obtenido exitosamente${NC}"
echo "Token: ${TOKEN_EMPRESA:0:20}..."
echo ""

# ====================================================================================
# PASO 2: Usuario EMPRESA crea pasantía para SU PROPIA empresa (DEBE FUNCIONAR)
# ====================================================================================
echo -e "${BLUE}PASO 2: Usuario EMPRESA crea pasantía para su propia empresa (id_empresa=1)${NC}"
echo "Esperado: 201 Created - Pasantía creada en estado PENDIENTE_DE_APROBACION"
echo ""

RESPONSE_PROPIA=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "${API_URL}/crear" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN_EMPRESA}" \
  -d '{
    "titulo": "Desarrollador Backend Java - BIOFARMA",
    "puestoACubrir": "Desarrollador Junior Backend",
    "ciudad": "Santa Fe",
    "modalidad": "Híbrida",
    "asignacionEstimulo": 65000.0,
    "cantidadDePasantes": 2,
    "fechaPublicacion": "2025-02-01",
    "fechaCaducidad": "2025-05-01",
    "idEmpresa": 1,
    "idsCarreras": [1, 2],
    "emailContacto": "rrhh@biofarmaweb.com.ar"
  }')

HTTP_STATUS_PROPIA=$(echo "$RESPONSE_PROPIA" | grep -o "HTTP_STATUS:[0-9]*" | cut -d':' -f2)
BODY_PROPIA=$(echo "$RESPONSE_PROPIA" | sed 's/HTTP_STATUS:[0-9]*$//')

echo "Status Code: $HTTP_STATUS_PROPIA"
echo "Respuesta:"
echo "$BODY_PROPIA" | jq '.'
echo ""

if [ "$HTTP_STATUS_PROPIA" = "201" ]; then
  ESTADO=$(echo "$BODY_PROPIA" | jq -r '.data.estado')
  if [ "$ESTADO" = "PENDIENTE_DE_APROBACION" ]; then
    echo -e "${GREEN}✅ TEST EXITOSO: Pasantía creada para propia empresa en estado PENDIENTE_DE_APROBACION${NC}"
  else
    echo -e "${RED}❌ ERROR: Estado incorrecto. Esperado: PENDIENTE_DE_APROBACION, Obtenido: $ESTADO${NC}"
  fi
else
  echo -e "${RED}❌ ERROR: Status esperado 201, obtenido $HTTP_STATUS_PROPIA${NC}"
fi
echo ""

# ====================================================================================
# PASO 3: Usuario EMPRESA intenta crear pasantía para OTRA empresa (DEBE FALLAR)
# ====================================================================================
echo -e "${BLUE}PASO 3: Usuario EMPRESA intenta crear pasantía para otra empresa (id_empresa=2)${NC}"
echo "Esperado: 403 Forbidden - No tiene permiso"
echo ""

RESPONSE_OTRA=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "${API_URL}/crear" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN_EMPRESA}" \
  -d '{
    "titulo": "Desarrollador Frontend - Hospital Italiano",
    "puestoACubrir": "Desarrollador Frontend",
    "ciudad": "Buenos Aires",
    "modalidad": "Presencial",
    "asignacionEstimulo": 70000.0,
    "cantidadDePasantes": 1,
    "fechaPublicacion": "2025-02-01",
    "fechaCaducidad": "2025-05-01",
    "idEmpresa": 2,
    "idsCarreras": [1],
    "emailContacto": "seleccion@hospital-italiano.org.ar"
  }')

HTTP_STATUS_OTRA=$(echo "$RESPONSE_OTRA" | grep -o "HTTP_STATUS:[0-9]*" | cut -d':' -f2)
BODY_OTRA=$(echo "$RESPONSE_OTRA" | sed 's/HTTP_STATUS:[0-9]*$//')

echo "Status Code: $HTTP_STATUS_OTRA"
echo "Respuesta:"
echo "$BODY_OTRA" | jq '.'
echo ""

if [ "$HTTP_STATUS_OTRA" = "403" ]; then
  echo -e "${GREEN}✅ TEST EXITOSO: Se bloqueó correctamente la creación para otra empresa${NC}"
else
  echo -e "${RED}❌ ERROR: Status esperado 403, obtenido $HTTP_STATUS_OTRA${NC}"
fi
echo ""

# ====================================================================================
# PASO 4: Login como ADMINISTRADOR
# ====================================================================================
echo -e "${BLUE}PASO 4: Login como usuario ADMINISTRADOR${NC}"
echo "Usuario: admin"
echo ""

LOGIN_ADMIN=$(curl -s -X POST "${AUTH_URL}/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }')

echo "Respuesta de login:"
echo "$LOGIN_ADMIN" | jq '.'
echo ""

TOKEN_ADMIN=$(echo "$LOGIN_ADMIN" | jq -r '.token // .data.token // empty')

if [ -z "$TOKEN_ADMIN" ] || [ "$TOKEN_ADMIN" = "null" ]; then
  echo -e "${RED}❌ ERROR: No se pudo obtener el token de administrador${NC}"
  exit 1
fi

echo -e "${GREEN}✅ Token de administrador obtenido${NC}"
echo ""

# ====================================================================================
# PASO 5: ADMINISTRADOR crea pasantía para CUALQUIER empresa (DEBE FUNCIONAR)
# ====================================================================================
echo -e "${BLUE}PASO 5: Usuario ADMINISTRADOR crea pasantía para empresa id=2${NC}"
echo "Esperado: 201 Created - Pasantía creada en estado PENDIENTE_DE_APROBACION"
echo ""

RESPONSE_ADMIN=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "${API_URL}/crear" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN_ADMIN}" \
  -d '{
    "titulo": "Analista de Datos - Hospital Italiano",
    "puestoACubrir": "Analista de Datos Junior",
    "ciudad": "Buenos Aires",
    "modalidad": "Presencial",
    "asignacionEstimulo": 72000.0,
    "cantidadDePasantes": 1,
    "fechaPublicacion": "2025-02-01",
    "fechaCaducidad": "2025-05-01",
    "idEmpresa": 2,
    "idsCarreras": [3],
    "emailContacto": "seleccion@hospital-italiano.org.ar"
  }')

HTTP_STATUS_ADMIN=$(echo "$RESPONSE_ADMIN" | grep -o "HTTP_STATUS:[0-9]*" | cut -d':' -f2)
BODY_ADMIN=$(echo "$RESPONSE_ADMIN" | sed 's/HTTP_STATUS:[0-9]*$//')

echo "Status Code: $HTTP_STATUS_ADMIN"
echo "Respuesta:"
echo "$BODY_ADMIN" | jq '.'
echo ""

if [ "$HTTP_STATUS_ADMIN" = "201" ]; then
  ESTADO_ADMIN=$(echo "$BODY_ADMIN" | jq -r '.data.estado')
  if [ "$ESTADO_ADMIN" = "PENDIENTE_DE_APROBACION" ]; then
    echo -e "${GREEN}✅ TEST EXITOSO: Administrador puede crear para cualquier empresa${NC}"
  else
    echo -e "${RED}❌ ERROR: Estado incorrecto${NC}"
  fi
else
  echo -e "${RED}❌ ERROR: Status esperado 201, obtenido $HTTP_STATUS_ADMIN${NC}"
fi
echo ""

# ====================================================================================
# PASO 6: Intento sin autenticación (DEBE FALLAR)
# ====================================================================================
echo -e "${BLUE}PASO 6: Intento de crear pasantía sin token JWT${NC}"
echo "Esperado: 401 Unauthorized"
echo ""

RESPONSE_NO_AUTH=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "${API_URL}/crear" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Pasantía sin autenticación",
    "puestoACubrir": "Desarrollador",
    "ciudad": "Santa Fe",
    "modalidad": "Remoto",
    "asignacionEstimulo": 50000.0,
    "cantidadDePasantes": 1,
    "fechaPublicacion": "2025-02-01",
    "fechaCaducidad": "2025-05-01",
    "idEmpresa": 1,
    "idsCarreras": [1],
    "emailContacto": "test@test.com"
  }')

HTTP_STATUS_NO_AUTH=$(echo "$RESPONSE_NO_AUTH" | grep -o "HTTP_STATUS:[0-9]*" | cut -d':' -f2)

echo "Status Code: $HTTP_STATUS_NO_AUTH"
echo ""

if [ "$HTTP_STATUS_NO_AUTH" = "401" ] || [ "$HTTP_STATUS_NO_AUTH" = "403" ]; then
  echo -e "${GREEN}✅ TEST EXITOSO: Se bloqueó correctamente el acceso sin autenticación${NC}"
else
  echo -e "${RED}❌ ERROR: Status esperado 401 o 403, obtenido $HTTP_STATUS_NO_AUTH${NC}"
fi
echo ""

# ====================================================================================
# RESUMEN FINAL
# ====================================================================================
echo -e "${YELLOW}=========================================="
echo "          RESUMEN DE PRUEBAS"
echo "==========================================${NC}"
echo ""
echo "✓ Usuario EMPRESA puede crear para su empresa"
echo "✓ Usuario EMPRESA NO puede crear para otra empresa"
echo "✓ Usuario ADMINISTRADOR puede crear para cualquier empresa"
echo "✓ Todas las pasantías se crean en estado PENDIENTE_DE_APROBACION"
echo "✓ Acceso sin autenticación es bloqueado"
echo ""
echo -e "${GREEN}Tests de seguridad completados${NC}"
