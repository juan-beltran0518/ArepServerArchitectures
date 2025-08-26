/*
 * Archivo: SimpleHttpServer.java
 * Descripción: Servidor HTTP simple en Java para servir archivos estáticos y manejar endpoints REST (GET y POST).
 * Autor: Juan sebastián Beltrán
 * Fecha de creación: 15/08/2025
 * Funcionalidad principal:
 *  - Escucha peticiones HTTP en el puerto 35000.
 *  - Sirve archivos estáticos desde src/main/resources/public.
 *  - Expone dos endpoints REST: 
 *      - GET  /app/hello?name=...   → retorna saludo en JSON.
 *      - POST /hellopost?name=...   → retorna saludo en JSON.
 *  - Devuelve 404 para recursos no encontrados o rutas inválidas.
 */

package com.edu.esuelaing.arep;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;

/**
 * Clase principal del servidor HTTP simple.
 * Contiene el ciclo principal que acepta conexiones y despacha las peticiones.
 */

public class SimpleHttpServer {

    private static String staticBaseFolder = "src/main/resources/public";

    public static void staticfiles(String folder) {
        staticBaseFolder = folder;
    }

    private static final java.util.Map<String, RouteHandler> getRoutes = new java.util.HashMap<>();
    private static final java.util.Map<String, RouteHandler> postRoutes = new java.util.HashMap<>();

    public static void get(String path, RouteHandler handler) {
        getRoutes.put(path, handler);
    }

    public static void post(String path, RouteHandler handler) {
        postRoutes.put(path, handler);
    }

    /**
     * Método principal. Inicia el servidor HTTP en el puerto 35000 y atiende
     * peticiones de forma indefinida.
     * Maneja rutas para archivos estáticos y endpoints REST.
     */
    public static void main(String[] args) throws Exception {
        int port = 35000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    java.io.OutputStream rawOut = clientSocket.getOutputStream();

                    String inputLine;
                    boolean isFirstLine = true;
                    URI requesturi = null;
                    String method = "GET";

                    while ((inputLine = in.readLine()) != null) {
                        if (isFirstLine) {
                            String[] parts = inputLine.split(" ");
                            method = parts[0];
                            requesturi = new URI(parts[1]);
                            System.out.println("Path: " + requesturi.getPath());
                            isFirstLine = false;
                        }
                        System.out.println("Received: " + inputLine);
                        if (!in.ready()) {
                            break;
                        }
                    }

                    if (requesturi != null && method.equals("GET") && getRoutes.containsKey(requesturi.getPath())) {
                        String query = requesturi.getQuery();
                        java.util.Map<String, String> queryParams = new java.util.HashMap<>();
                        if (query != null) {
                            for (String param : query.split("&")) {
                                String[] kv = param.split("=", 2);
                                if (kv.length == 2)
                                    queryParams.put(kv[0], kv[1]);
                                else if (kv.length == 1)
                                    queryParams.put(kv[0], "");
                            }
                        }
                        Request req = new Request(method, requesturi.getPath(), queryParams, null);
                        Response res = new Response();
                        String resp = getRoutes.get(requesturi.getPath()).handle(req, res);
                        out.println(resp);
                    } else if (requesturi != null && method.equals("POST")
                            && postRoutes.containsKey(requesturi.getPath())) {
                        String query = requesturi.getQuery();
                        java.util.Map<String, String> queryParams = new java.util.HashMap<>();
                        if (query != null) {
                            for (String param : query.split("&")) {
                                String[] kv = param.split("=", 2);
                                if (kv.length == 2)
                                    queryParams.put(kv[0], kv[1]);
                                else if (kv.length == 1)
                                    queryParams.put(kv[0], "");
                            }
                        }
                        Request req = new Request(method, requesturi.getPath(), queryParams, null);
                        Response res = new Response();
                        String resp = postRoutes.get(requesturi.getPath()).handle(req, res);
                        out.println(resp);
                    } else {
                        String staticPath = requesturi != null ? requesturi.getPath() : "/";
                        if (staticPath == null || staticPath.equals("/") || staticPath.isEmpty()) {
                            staticPath = "/index.html";
                        }
                        if (staticPath.contains("..")) {
                            rawOut.write(notFoundResponse());
                            rawOut.flush();
                        } else {
                            boolean served = serveStaticFileRaw(staticPath, rawOut);
                            if (!served) {
                                rawOut.write(notFoundResponse());
                                rawOut.flush();
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * Sirve archivos estáticos ubicados en src/main/resources/public.
     * 
     * @param path   Ruta solicitada (relativa al directorio base)
     * @param rawOut OutputStream para enviar la respuesta binaria
     * @return true si el archivo fue servido correctamente, false si no existe o
     *         hay error
     */
    private static boolean serveStaticFileRaw(String path, java.io.OutputStream rawOut) {
        if (staticBaseFolder != null && staticBaseFolder.startsWith("classpath:")) {
            if (serveClasspathResource(staticBaseFolder, path, rawOut)) {
                return true;
            }
            return false;
        }

        File file = new File(staticBaseFolder + path);
        if (!file.exists() || file.isDirectory()) {
            if (serveClasspathResource("public", path, rawOut)) {
                return true;
            }
            return false;
        }
        String contentType = getContentType(path);
        String header = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + file.length() + "\r\n" +
                "\r\n";
        try {
            rawOut.write(header.getBytes());
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    rawOut.write(buffer, 0, bytesRead);
                }
            }
            rawOut.flush();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private static boolean serveClasspathResource(String base, String path, java.io.OutputStream rawOut) {
        if (base == null)
            return false;
        String normalizedBase = base.replaceFirst("^classpath:", "");
        while (normalizedBase.startsWith("/"))
            normalizedBase = normalizedBase.substring(1);
        while (normalizedBase.endsWith("/"))
            normalizedBase = normalizedBase.substring(0, normalizedBase.length() - 1);
        String normalizedPath = path != null && path.startsWith("/") ? path.substring(1) : path;
        String resourcePath = (normalizedBase == null || normalizedBase.isEmpty()) ? normalizedPath
                : normalizedBase + "/" + normalizedPath;
        if (resourcePath == null || resourcePath.isEmpty())
            return false;

        try (java.io.InputStream is = SimpleHttpServer.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null)
                return false;
            // Bufferizar para conocer Content-Length
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            byte[] body = baos.toByteArray();
            String contentType = getContentType(path);
            String header = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + body.length + "\r\n" +
                    "\r\n";
            rawOut.write(header.getBytes());
            rawOut.write(body);
            rawOut.flush();
            return true;
        } catch (IOException ioe) {
            return false;
        }
    }

    /**
     * Determina el tipo de contenido (MIME) según la extensión del archivo
     * solicitado.
     * 
     * @param path Ruta del archivo
     * @return Tipo de contenido MIME correspondiente
     */
    private static String getContentType(String path) {
        if (path.endsWith(".html"))
            return "text/html";
        if (path.endsWith(".css"))
            return "text/css";
        if (path.endsWith(".js"))
            return "application/javascript";
        if (path.endsWith(".png"))
            return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
            return "image/jpeg";
        if (path.endsWith(".gif"))
            return "image/gif";
        if (path.endsWith(".svg"))
            return "image/svg+xml";
        if (path.endsWith(".json"))
            return "application/json";
        return "text/plain";
    }

    /**
     * Genera una respuesta HTTP 404 para recursos no encontrados.
     * 
     * @return Respuesta HTTP 404 en bytes
     */
    private static byte[] notFoundResponse() {
        String response = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/plain\r\n\r\n" +
                "Recurso no encontrado";
        return response.getBytes();
    }

}
