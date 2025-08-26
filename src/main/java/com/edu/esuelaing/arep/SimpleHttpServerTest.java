package com.edu.esuelaing.arep;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleHttpServerTest {
    public static void main(String[] args) throws Exception {
        Thread serverThread = new Thread(() -> {
            try {
                Main.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
        Thread.sleep(2000);
        String restResponse = httpGet("http://localhost:35000/hello?name=Test");
        System.out.println("/hello?name=Test => " + restResponse);
        assert restResponse.contains("Hola, Test") || restResponse.contains("Hola,Test");
        String piResponse = httpGet("http://localhost:35000/pi");
        System.out.println("/pi => " + piResponse);
        assert piResponse.contains("3.1415");
        String staticResponse = httpGet("http://localhost:35000/index.html");
        System.out.println("/index.html => " + staticResponse);
        assert staticResponse.contains("<html") || staticResponse.contains("DOCTYPE");

        System.out.println("Todas las pruebas pasaron correctamente.");
    }

    private static String httpGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(2000);
        conn.setReadTimeout(2000);
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            content.append(line).append("\n");
        }
        in.close();
        conn.disconnect();
        return content.toString();
    }
}
