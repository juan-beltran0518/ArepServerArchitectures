package com.edu.esuelaing.arep;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class IoCSampleTest {

    private static MicroSpringBoot microSpringBoot;

    @BeforeAll
    static void setup() {
        microSpringBoot = new MicroSpringBoot();
        microSpringBoot.start("com.edu.esuelaing.arep.controllers");
    }

    @Test
    void testHelloController() {
        String response = microSpringBoot.handleRequest("GET", "/");
        assertEquals("Greetings from Spring Boot!", response);
    }

    @Test
    void testGreetingController() {
        String response = microSpringBoot.handleRequest("GET", "/greeting?name=John");
        assertEquals("Hola John", response);
    }

    @Test
    void testGreetingControllerDefault() {
        String response = microSpringBoot.handleRequest("GET", "/greeting");
        assertEquals("Hola World", response);
    }
}