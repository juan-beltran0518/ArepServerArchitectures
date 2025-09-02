# AREP IoC Server

## Descripción
El proyecto AREP IoC Server es un servidor web implementado en Java que permite servir páginas HTML e imágenes PNG. Además, proporciona un marco de Inversión de Control (IoC) para la construcción de aplicaciones web a partir de POJOs (Plain Old Java Objects). Este servidor es capaz de manejar múltiples solicitudes no concurrentes y utiliza reflexión para cargar componentes de manera dinámica.

## Estructura del Proyecto
El proyecto está organizado de la siguiente manera:

```
ArepServerArchitectures
├── .DS_Store
├── .git/
├── .gitignore
├── README.md
├── server.log
└── arep-ioc-server/
    ├── pom.xml
    ├── run.sh
    └── src/
        ├── main/
        │   ├── java/
        │   │   └── com/
        │   │       └── edu/
        │   │           └── esuelaing/
        │   │               └── arep/
        │   │                   ├── annotations/
        │   │                   │   ├── GetMapping.java
        │   │                   │   ├── PostMapping.java
        │   │                   │   ├── RequestParam.java
        │   │                   │   └── RestController.java
        │   │                   ├── controllers/
        │   │                   │   ├── GreetingController.java
        │   │                   │   └── HelloController.java
        │   │                   ├── ioc/
        │   │                   │   ├── BeanContainer.java
        │   │                   │   ├── MicroSpringBoot.java
        │   │                   │   └── ReflectionLoader.java
        │   │                   ├── Main.java
        │   │                   ├── Request.java
        │   │                   ├── RouteHandler.java
        │   │                   └── SimpleHttpServer.java
        │   └── resources/
        │       └── public/
        │           ├── app.js
        │           ├── index.html
        │           ├── style.css
        │           └── test-image.jpg
        └── test/
            └── java/
                └── com/
                    └── edu/
                        └── esuelaing/
                            └── arep/
                                ├── IoCSampleTest.java
                                └── SimpleHttpServerTest.java
```

## Requisitos de Configuración
1. **Java JDK**: Asegúrate de tener instalado Java JDK 11 o superior.
2. **Maven**: Este proyecto utiliza Maven para la gestión de dependencias y construcción. Asegúrate de tener Maven instalado.

## Instrucciones de Uso
1. **Clonar el Repositorio**:
   ```bash
   git clone https://github.com/juan-beltran0518/ArepServerArchitectures.git
   cd arep-ioc-server
   ```

2. **Construir el Proyecto**:
   Ejecuta el siguiente comando para compilar el proyecto y descargar las dependencias:
   ```bash
   mvn clean install
   ```

3. **Ejecutar el Servidor**:
   Para iniciar el servidor, ejecuta el siguiente comando:
   ```bash
   java -cp target/classes com.edu.esuelaing.arep.Main
   ```

4. **Acceder a la Aplicación Web**:
   Abre tu navegador y dirígete a `http://localhost:8080` para ver la aplicación en funcionamiento.

## Pruebas
- http://localhost:8080/api/hello
- http://localhost:8080
- http://localhost:8080/app/hello?name=Beltran
- http://localhost:8080/greeting
- http://localhost:8080/app/hello?name

## Funcionalidades
- **Manejo de Rutas**: El servidor puede manejar rutas definidas mediante anotaciones como `@GetMapping`.
- **Inversión de Control**: Permite la carga de componentes POJO a través de un marco IoC.
- **Reflexión**: Utiliza reflexión para cargar clases y sus anotaciones dinámicamente.

