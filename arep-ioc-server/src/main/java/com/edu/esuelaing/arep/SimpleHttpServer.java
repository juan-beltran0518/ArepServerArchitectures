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
import java.util.HashMap;
import java.util.Map;

public class SimpleHttpServer {

    private static String staticBaseFolder = "src/main/resources/public";
    private static final Map<String, RouteHandler> getRoutes = new HashMap<>();
    private static final Map<String, RouteHandler> postRoutes = new HashMap<>();

    public static void staticfiles(String folder) {
        staticBaseFolder = folder;
    }

    public static void get(String path, RouteHandler handler) {
        getRoutes.put(path, handler);
    }

    public static void post(String path, RouteHandler handler) {
        postRoutes.put(path, handler);
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String inputLine;
                    boolean isFirstLine = true;
                    URI requesturi = null;
                    String method = "GET";

                    while ((inputLine = in.readLine()) != null) {
                        if (isFirstLine) {
                            String[] parts = inputLine.split(" ");
                            method = parts[0];
                            requesturi = new URI(parts[1]);
                            isFirstLine = false;
                        }
                        if (!in.ready()) {
                            break;
                        }
                    }

                    if (!serveStaticFile(requesturi, out)) {
                        if (!handleRoute(method, requesturi, out)) {
                            out.println(notFoundResponse());
                        }
                    }
                }
            }
        }
    }

    private static boolean handleRoute(String method, URI requesturi, PrintWriter out) {
        if (requesturi == null || method == null) return false;
        String m = method.toUpperCase();
        Map<String, RouteHandler> routes = "GET".equals(m) ? getRoutes : postRoutes;
        RouteHandler handler = routes.get(requesturi.getPath());
        if (handler == null) return false;

        Map<String, String> queryParams = parseQueryParams(requesturi.getQuery());
        Request req = new Request(m, requesturi.getPath(), queryParams, null);
        String resp = handler.handle(req);
        
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                             "Content-Type: text/plain\r\n" +
                             "Content-Length: " + resp.length() + "\r\n" +
                             "\r\n" +
                             resp;
        out.print(httpResponse);
        out.flush();
        return true;
    }

    private static boolean serveStaticFile(URI requesturi, PrintWriter out) throws IOException {
        String staticPath = requesturi != null ? requesturi.getPath() : "/";
        if (staticPath.equals("/") || staticPath.isEmpty()) {
            staticPath = "/index.html";
        }
        File file = new File(staticBaseFolder + staticPath);
        if (!file.exists() || file.isDirectory()) {
            return false; 
        }
        String contentType = getContentType(staticPath);
        String header = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + file.length() + "\r\n" +
                "\r\n";
        out.print(header);
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(new String(buffer, 0, bytesRead));
            }
        }
        return true; 
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query == null || query.isEmpty()) return queryParams;
        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            queryParams.put(kv[0], kv.length == 2 ? kv[1] : "");
        }
        return queryParams;
    }

    private static String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        return "text/plain";
    }

    private static String notFoundResponse() {
        return "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/plain\r\n\r\n" +
                "Recurso no encontrado";
    }
}