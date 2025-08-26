# Project Statement: Web Framework Development for REST Services and Static File Management

## Descripción
Este proyecto hace parte del taller de **Arquitectura de Aplicaciones Distribuidas** en Ingeniería de Sistemas.  
Se implementa un **framework web en Java** que permite definir servicios REST mediante funciones lambda, gestionar parámetros de consulta y servir archivos estáticos (HTML, CSS, JS, imágenes) desde una carpeta configurable, sin emplear frameworks externos.

---

## Requisitos
- Java 17 o superior
- Maven
- Navegador web moderno para probar la aplicación
- (Opcional) Postman o curl para pruebas REST

---

## Instalación y Ejecución

1. Clonar el repositorio:
```bash
git clone https://github.com/juan-beltran0518/ArepJavawebserverTaller.git
```

2. Compilar con Maven:
```bash
mvn clean package
```

3. Ejecutar el servidor:
```bash
java -cp target/classes com.edu.esuelaing.arep.Main
```

4. Abrir un navegador y acceder a:
```bash
http://localhost:35000/
```

---

## Estructura del proyecto
```bash
arep-java-webserver-taller/
│
├── src/
│   ├── main/
│   │   ├── java/                     
│   │   │   └── com/edu/esuelaing/arep/
│   │   │       ├── Main.java
│   │   │       ├── SimpleHttpServer.java
│   │   │       ├── RouteHandler.java
│   │   │       ├── Request.java
│   │   │       └── Response.java
│   │   └── resources/
│   │       └── public/               # Archivos estáticos: 
│   └── test/
│       └── java/
│           └── com/edu/esuelaing/arep/
│               └── SimpleHttpServerTest.java
├── pom.xml                           # Archivo de Maven
└── README.md                         # Documentación
```

---
## Arquitectura y características

- Servidor HTTP en Java:

  - Escucha en el puerto 35000 (configurable).

  - Permite definir rutas REST con funciones lambda usando get().

  - Extrae parámetros de consulta accesibles vía Request.getValues().

  - Sirve archivos estáticos desde la carpeta configurada con staticfiles().

  - Responde con 404 Not Found para rutas inexistentes.

- Cliente Web:

  - HTML con formularios para probar servicios GET y POST.

  - JavaScript para llamadas asíncronas (fetch/XMLHttpRequest).

  ---
  ## Ejemplo de uso
  ```bash
    public static void main(String[] args) {
      SimpleHttpServer.staticfiles("src/main/resources/public");
      
      SimpleHttpServer.get("/app/hello", (req, res) -> {
          String name = req.getValues("name").stream().findFirst().orElse("Mundo");
          return "{ \"mensaje\": \"Hola, " + name + "!\" }";
      });

      SimpleHttpServer.get("/pi", (req, res) -> String.valueOf(Math.PI));
  }
  ```

---

## Pruebas realizadas
 - Solicitudes GET a /app/hello?name=Pedro → responde { "mensaje": "Hola, Pedro!" }.

  - Solicitudes POST a /hellopost?name=Pedro → responde { "mensaje": "Hola (POST), Pedro!" }.

- Acceso a archivos estáticos: /, /index.html, /style.css, /app.js, /test-image.jpg.

- Validación de respuestas JSON desde el backend.

- Manejo correcto de rutas no encontradas (404).

- Pruebas automatizadas con SimpleHttpServerTest.

---
## Notas

- No se usaron frameworks web externos (Spark, Spring, etc.), solo Java estándar.

- Se recomienda ejecutar el servidor desde la raíz del proyecto para que los archivos estáticos se sirvan correctamente.

- Puedes probar los endpoints REST y archivos estáticos desde el navegador o herramientas como Postman.
