package com.edu.esuelaing.arep;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SimpleHttpServerTest {

    private static Thread serverThread;

    @BeforeAll
    static void startServer() throws Exception {
        serverThread = new Thread(() -> {
            try {
                Main.main(new String[] {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
        Thread.sleep(2000);
    }

    @AfterAll
    static void stopServer() throws Exception {
        // Logic to stop the server if necessary
    }

    @Test
    void helloEndpointShouldReturnGreeting() throws Exception {
        String restResponse = httpGet("http://localhost:35000/app/hello?name=Test");
        assertTrue(restResponse.contains("Hola, Test") || restResponse.contains("Hola,Test"), restResponse);
    }

    @Test
    void indexShouldBeServed() throws Exception {
        String staticResponse = httpGet("http://localhost:35000/index.html");
        assertTrue(staticResponse.contains("<html") || staticResponse.contains("DOCTYPE"), staticResponse);
    }

    private static String httpGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } finally {
            conn.disconnect();
        }
    }
}