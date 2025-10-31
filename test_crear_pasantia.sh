#!/bin/bash

# Script de test para crear una pasantía
# Endpoint: POST /api/pasantias/crear

echo "=========================================="
echo "TEST: Crear Pasantía"
echo "=========================================="
echo ""

# URL del endpoint
URL="http://localhost:8080/api/pasantias/crear"

# Datos de la pasantía a crear
REQUEST_BODY='{
  "titulo": "Desarrollador Backend Spring Boot",
  "puestoACubrir": "Desarrollador Junior Backend",
  "ciudad": "Santa Fe",
  "modalidad": "Híbrida",
  "asignacionEstimulo": 55000.0,
  "cantidadDePasantes": 2,
  "fechaPublicacion": "2025-11-01",
  "fechaCaducidad": "2026-02-01",
  "idEmpresa": 1,
  "idsCarreras": [6],
  "emailContacto": "rrhh@biofarmaweb.com.ar"
}'

echo "📋 Request Body:"
echo "$REQUEST_BODY" | jq '.'
echo ""

echo "🚀 Enviando request a: $URL"
echo ""

# Hacer la petición POST
RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$URL" \
  -H "Content-Type: application/json" \
  -d "$REQUEST_BODY")

# Separar el body del status code
HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
BODY=$(echo "$RESPONSE" | sed '$d')

echo "📥 HTTP Status Code: $HTTP_CODE"
echo ""

# Mostrar respuesta formateada
if [ "$HTTP_CODE" = "201" ]; then
    echo "✅ SUCCESS - Pasantía creada correctamente"
    echo ""
    echo "📄 Response Body:"
    echo "$BODY" | jq '.'
    echo ""
    
    # Extraer y mostrar datos importantes
    ID_PASANTIA=$(echo "$BODY" | jq -r '.data.idPasantia')
    ESTADO=$(echo "$BODY" | jq -r '.data.estado')
    TITULO=$(echo "$BODY" | jq -r '.data.titulo')
    
    echo "📊 Datos de la pasantía creada:"
    echo "   ID: $ID_PASANTIA"
    echo "   Título: $TITULO"
    echo "   Estado: $ESTADO"
    echo ""
    
    if [ "$ESTADO" = "PENDIENTE_DE_APROBACION" ]; then
        echo "✅ Estado inicial correcto: PENDIENTE_DE_APROBACION"
    else
        echo "❌ Estado inicial incorrecto: $ESTADO (esperado: PENDIENTE_DE_APROBACION)"
    fi
    
elif [ "$HTTP_CODE" = "400" ]; then
    echo "❌ BAD REQUEST - Error de validación"
    echo ""
    echo "📄 Response Body:"
    echo "$BODY" | jq '.'
    
elif [ "$HTTP_CODE" = "409" ]; then
    echo "❌ CONFLICT - Error de regla de negocio"
    echo ""
    echo "📄 Response Body:"
    echo "$BODY" | jq '.'
    
elif [ "$HTTP_CODE" = "500" ]; then
    echo "❌ INTERNAL SERVER ERROR"
    echo ""
    echo "📄 Response Body:"
    echo "$BODY" | jq '.'
    
else
    echo "❓ Respuesta inesperada"
    echo ""
    echo "📄 Response Body:"
    echo "$BODY"
fi

echo ""
echo "=========================================="
echo "Fin del test"
echo "=========================================="
