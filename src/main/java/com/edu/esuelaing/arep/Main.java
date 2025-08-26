package com.edu.esuelaing.arep;

public class Main {
    public static void main(String[] args) {
        SimpleHttpServer.staticfiles("classpath:public");
        SimpleHttpServer.get("/app/hello", (req, res) -> {
            String name = req.getQueryParam("name");
            if (name == null || name.isEmpty())
                name = "mundo";
            String body = "{\"mensaje\":\"Hola, " + name + "!\"}";
            return "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + body.getBytes().length + "\r\n" +
                    "\r\n" +
                    body;
        });
        SimpleHttpServer.post("/hellopost", (req, res) -> {
            String name = req.getQueryParam("name");
            if (name == null || name.isEmpty())
                name = "mundo";
            String body = "{\"mensaje\":\"Hola (POST), " + name + "!\"}";
            return "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + body.getBytes().length + "\r\n" +
                    "\r\n" +
                    body;
        });

        try {
            SimpleHttpServer.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
