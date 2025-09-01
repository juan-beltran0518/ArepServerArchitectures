#!/bin/bash
echo "=== Compilando proyecto ==="
mvn clean compile

echo "=== Ejecutando con parámetro específico ==="
echo "java -cp target/classes com.edu.esuelaing.arep.Main com.edu.esuelaing.arep.controllers.HelloController"
java -cp target/classes com.edu.esuelaing.arep.Main com.edu.esuelaing.arep.controllers.HelloController

# Para ejecutar con auto-descubrimiento, usar:
# java -cp target/classes com.edu.esuelaing.arep.Main
