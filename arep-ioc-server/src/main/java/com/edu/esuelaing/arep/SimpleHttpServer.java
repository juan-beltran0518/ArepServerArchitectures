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

                    if (!serveStaticFile(requesturi, clientSocket)) {
                        if (!handleRoute(method, requesturi, out)) {
                            out.println(notFoundResponse());
                        }
                    }
                }
            }
        }
    }

    private static boolean handleRoute(String method, URI requesturi, PrintWriter out) {
        if (requesturi == null || method == null)
            return false;
        String m = method.toUpperCase();
        Map<String, RouteHandler> routes = "GET".equals(m) ? getRoutes : postRoutes;
        RouteHandler handler = routes.get(requesturi.getPath());
        if (handler == null)
            return false;

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

    private static boolean serveStaticFile(URI requesturi, Socket clientSocket) throws IOException {
        String staticPath = requesturi != null ? requesturi.getPath() : "/";
        if (staticPath.equals("/") || staticPath.isEmpty()) {
            staticPath = "/index.html";
        }

        String resourcePath = "/public" + staticPath;
        try (java.io.InputStream inputStream = SimpleHttpServer.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                return false;
            }

            byte[] fileContent = inputStream.readAllBytes();

            String contentType = getContentType(staticPath);
            String header = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + fileContent.length + "\r\n" +
                    "\r\n";

            clientSocket.getOutputStream().write(header.getBytes());
            clientSocket.getOutputStream().write(fileContent);
            clientSocket.getOutputStream().flush();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query == null || query.isEmpty())
            return queryParams;
        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            queryParams.put(kv[0], kv.length == 2 ? kv[1] : "");
        }
        return queryParams;
    }

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
        if (path.endsWith(".ico"))
            return "image/x-icon";
        return "text/plain";
    }

    private static String notFoundResponse() {
        return "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/plain\r\n\r\n" +
                "Recurso no encontrado";
    }
}