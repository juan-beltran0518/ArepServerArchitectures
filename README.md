# AREP Java Web Server - Taller de Arquitectura Empresarial

## Descripción
Este proyecto corresponde a un taller de **Arquitectura de Aplicaciones Distribuidas** en Ingeniería de Sistemas.  
Se implementa un **servidor web en Java** que soporta múltiples solicitudes HTTP **seguida, no concurrentes**, sirve archivos estáticos (HTML, CSS, JS, imágenes) desde el disco local y permite comunicación asíncrona con servicios REST en el backend, sin usar frameworks web externos.

---

## Requisitos
- Java 17 o superior
- Maven
- Navegador web moderno para probar la aplicación
- Conexión a internet para probar servicios REST externos (opcional)

---

## Instalación y Ejecución

1. Clonar el repositorio:
```bash
git clone
```
2. Compilar con Maven:
```bash
mvn clean package
```
3. Ejecutar el servidor:
```bash
java -cp target/classes com.edu.esuelaing.arep.SimpleHttpServer
```
4. Abrir un navegador y navegar a:
```bash
http://localhost:35000
```

---

## Estructura del proyecto
```bash
arep-java-webserver-taller/
│
├── src/
│   ├── main/
│   │   ├── java/                     # Código fuente Java
│   │   │   └── com/edu/escuelaing/arep/
│   │   │       └── SimpleHttpServer.java
│   │   └── resources/
│   │       └── public/               # Archivos estáticos: HTML, CSS, JS, imágenes
├── pom.xml                            # Archivo de Maven
└── README.md                          # Documentación
```

---

## Arquitectura del prototipo
 - Servidor HTTP en Java que:
    - Escucha solicitudes HTTP en un puerto configurable
    - Detecta rutas y métodos (GET, POST)
    - Sirve archivos estáticos desde src/main/resources/public
    - Retorna respuestas JSON para servicios REST simulados (/app/hello)

  - Cliente Web:
      - HTML con formularios que llaman a servicios GET y POST
      - JavaScript para comunicación asíncrona usando XMLHttpRequest y fetch

---

## Pruebas realizadas

  - Solicitudes GET a páginas HTML, CSS, JS
        - Solicitudes POST simuladas desde formularios web
        - Acceso a archivos estáticos: imágenes, CSS, JS
        - Validación de respuestas JSON desde el backend
        - Manejo de rutas no encontradas (404)

---

## Notas
   - No se usaron frameworks web como Spark o Spring, solo Java y librerías estándar de red.
   - Se recomienda ejecutar desde la raíz del proyecto para que los archivos estáticos se detecten correctamente.

