# AREP IoC Server

## Descripción
El proyecto AREP IoC Server es un servidor web implementado en Java que permite servir páginas HTML e imágenes PNG. Además, proporciona un marco de Inversión de Control (IoC) para la construcción de aplicaciones web a partir de POJOs (Plain Old Java Objects). Este servidor es capaz de manejar múltiples solicitudes no concurrentes y utiliza reflexión para cargar componentes de manera dinámica.

## Estructura del Proyecto
El proyecto está organizado de la siguiente manera:

```
arep-ioc-server
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── edu
│   │   │           └── esuelaing
│   │   │               └── arep
│   │   │                   ├── annotations
│   │   │                   │   ├── RestController.java
│   │   │                   │   ├── GetMapping.java
│   │   │                   │   └── RequestParam.java
│   │   │                   ├── ioc
│   │   │                   │   ├── MicroSpringBoot.java
│   │   │                   │   ├── BeanContainer.java
│   │   │                   │   └── ReflectionLoader.java
│   │   │                   ├── controllers
│   │   │                   │   ├── HelloController.java
│   │   │                   │   └── GreetingController.java
│   │   │                   ├── SimpleHttpServer.java
│   │   │                   ├── RouteHandler.java
│   │   │                   ├── Request.java
│   │   │                   └── Main.java
│   │   └── resources
│   │       └── public
│   │           ├── index.html
│   │           ├── style.css
│   │           ├── app.js
│   │           └── test-image.jpg
│   └── test
│       └── java
│           └── com
│               └── edu
│                   └── esuelaing
│                       └── arep
│                           ├── SimpleHttpServerTest.java
│                           └── IoCSampleTest.java
├── pom.xml
└── README.md
```

## Requisitos de Configuración
1. **Java JDK**: Asegúrate de tener instalado Java JDK 11 o superior.
2. **Maven**: Este proyecto utiliza Maven para la gestión de dependencias y construcción. Asegúrate de tener Maven instalado.

## Instrucciones de Uso
1. **Clonar el Repositorio**:
   ```bash
   git clone <URL_DEL_REPOSITORIO>
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
   Abre tu navegador y dirígete a `http://localhost:35000` para ver la aplicación en funcionamiento.

## Funcionalidades
- **Manejo de Rutas**: El servidor puede manejar rutas definidas mediante anotaciones como `@GetMapping`.
- **Inversión de Control**: Permite la carga de componentes POJO a través de un marco IoC.
- **Reflexión**: Utiliza reflexión para cargar clases y sus anotaciones dinámicamente.

## Contribuciones
Las contribuciones son bienvenidas. Si deseas contribuir, por favor abre un issue o envía un pull request.

## Licencia
Este proyecto está bajo la Licencia MIT. Consulta el archivo LICENSE para más detalles.