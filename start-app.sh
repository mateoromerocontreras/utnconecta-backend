#!/bin/bash

echo "===================================="
echo " Iniciando Sistema de Pasantias"
echo "===================================="
echo ""

cd script_bd
echo "[1/3] Iniciando servicios con Docker..."
docker-compose up -d --build

echo ""
echo "[2/3] Esperando que los servicios estén listos..."
sleep 30

echo ""
echo "[3/3] Servicios disponibles:"
echo "  - Frontend: http://localhost:5173"
echo "  - Backend:  http://localhost:8080"
echo "  - Swagger:  http://localhost:8080/swagger-ui/index.html"
echo "  - MySQL:    localhost:3306"
echo ""
echo "Para ver logs: docker-compose logs -f"
echo "Para detener:  docker-compose down"
echo ""
