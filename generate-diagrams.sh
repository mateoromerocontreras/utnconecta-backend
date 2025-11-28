#!/bin/bash

# Script para generar diagramas PlantUML con GraphViz
# Ubicación: raíz del proyecto

echo "======================================"
echo "Generador de Diagramas PlantUML"
echo "======================================"
echo ""

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Verificar que GraphViz esté instalado
if ! command -v dot &> /dev/null; then
    echo -e "${RED}Error: GraphViz no está instalado${NC}"
    echo "Por favor instala GraphViz primero:"
    echo "  sudo zypper install graphviz"
    exit 1
fi

echo -e "${GREEN}✓ GraphViz está instalado${NC}"
echo "Versión: $(dot -V 2>&1)"
echo ""

# Directorio de diagramas
DIAGRAMS_DIR="docs/diagrams"
OUTPUT_DIR="docs/diagrams/generated"

# Verificar que exista el directorio de diagramas
if [ ! -d "$DIAGRAMS_DIR" ]; then
    echo -e "${RED}Error: No existe el directorio $DIAGRAMS_DIR${NC}"
    exit 1
fi

# Crear directorio de salida si no existe
mkdir -p "$OUTPUT_DIR"

# Método 1: Usando Maven (recomendado)
echo -e "${YELLOW}Método 1: Generando diagramas con Maven...${NC}"
cd pasantias
if mvn plantuml:generate -q; then
    echo -e "${GREEN}✓ Diagramas generados exitosamente con Maven${NC}"
    cd ..
else
    echo -e "${YELLOW}⚠ No se pudo generar con Maven, intentando método alternativo...${NC}"
    cd ..
    
    # Método 2: Usando PlantUML JAR directamente
    echo -e "${YELLOW}Método 2: Generando diagramas con PlantUML JAR...${NC}"
    
    # Descargar PlantUML si no existe
    PLANTUML_JAR="plantuml.jar"
    if [ ! -f "$PLANTUML_JAR" ]; then
        echo "Descargando PlantUML..."
        wget -q https://github.com/plantuml/plantuml/releases/download/v1.2024.8/plantuml-1.2024.8.jar -O "$PLANTUML_JAR"
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✓ PlantUML descargado${NC}"
        else
            echo -e "${RED}Error al descargar PlantUML${NC}"
            exit 1
        fi
    fi
    
    # Generar diagramas
    echo "Generando diagramas PNG..."
    java -jar "$PLANTUML_JAR" -o "../generated" "$DIAGRAMS_DIR/*.puml"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Diagramas generados exitosamente${NC}"
    else
        echo -e "${RED}Error al generar diagramas${NC}"
        exit 1
    fi
fi

# Listar archivos generados
echo ""
echo -e "${GREEN}Diagramas generados:${NC}"
ls -lh "$OUTPUT_DIR"/*.png 2>/dev/null || echo "No se encontraron archivos PNG"

echo ""
echo -e "${GREEN}======================================"
echo "Proceso completado"
echo "======================================${NC}"
echo ""
echo "Los diagramas se encuentran en: $OUTPUT_DIR"
echo ""
