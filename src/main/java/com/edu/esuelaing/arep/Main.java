package com.edu.esuelaing.arep;

public class Main {
    public static void main(String[] args) {
        // Configurar carpeta de archivos estáticos
        SimpleHttpServer.staticfiles("src/main/resources/public");

        // Ruta /hello
        SimpleHttpServer.get("/hello", (req, res) -> {
            String name = req.getQueryParam("name");
            if (name == null) name = "mundo";
            return "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nHola, " + name + "!";
        });

        // Ruta /pi
        SimpleHttpServer.get("/pi", (req, res) -> {
            return "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\n" + Math.PI;
        });

        // Iniciar el servidor
        try {
            SimpleHttpServer.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
