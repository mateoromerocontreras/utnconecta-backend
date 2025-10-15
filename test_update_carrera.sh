#!/bin/bash

echo "Testing updateCarrera endpoint..."

# Test 1: Actualizar carrera existente
curl -X POST http://localhost:8080/carreras/updateCarrera \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "nombre": "Ingeniería Civil Actualizada"
  }' \
  -v

echo ""
echo ""

# Test 2: Verificar que se actualizó
curl -X GET "http://localhost:8080/carreras/consultarCarrera" \
  -H "Content-Type: application/json" \
  -v