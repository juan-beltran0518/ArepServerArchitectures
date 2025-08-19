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

public class SimpleHttpServer {
    public static void main(String[] args) throws Exception {
        int port = 35000;
        ServerSocket serverSocket = new ServerSocket(port);
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

                String outputLine;
                if (requesturi != null && requesturi.getPath().startsWith("/hellopost")) {
                    if (method.equals("POST")) {
                        String postResp = helloPostService(requesturi);
                        rawOut.write(postResp.getBytes());
                        rawOut.flush();
                    } else {
                        rawOut.write(notFoundResponse());
                        rawOut.flush();
                    }
                } else if (requesturi != null && requesturi.getPath().startsWith("/app/hello")) {
                    outputLine = helloService(requesturi);
                    out.println(outputLine);
                } else {
                    // Serve static files from /public
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

    private static String helloService(URI requesturi) {
        String response = "HTTP/1.1 200 OK\r\n" +
                "content-type: application/json\r\n" +
                "\r\n";
        String name = "";
        String query = requesturi.getQuery();
        if (query != null && query.contains("=")) {
            name = query.split("=")[1];
        }
        response += "{\"mensaje\": \"Hola " + name + "\"}";
        return response;
    }

    private static boolean serveStaticFileRaw(String path, java.io.OutputStream rawOut) {
        String basePath = "src/main/resources/public";
        File file = new File(basePath + path);
        if (!file.exists() || file.isDirectory()) {
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

    private static byte[] notFoundResponse() {
        String response = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/plain\r\n\r\n" +
                "Recurso no encontrado";
        return response.getBytes();
    }

    private static String helloPostService(URI requesturi) {
        String name = "";
        String query = requesturi.getQuery();
        if (query != null && query.contains("=")) {
            name = query.split("=")[1];
        }
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n\r\n" +
                "{\"mensaje\": \"POST Hola " + name + "\"}";
        return response;
    }
}
