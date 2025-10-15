#!/bin/bash

echo "Testing login with admin credentials..."

curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@pasantias.com",
    "password": "admin123"
  }' \
  -v

echo ""
echo "Testing login with wrong password..."

curl -X POST http://localhost:8080/auth/iniciarSesion \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@pasantias.com",
    "password": "wrongpassword"
  }' \
  -v