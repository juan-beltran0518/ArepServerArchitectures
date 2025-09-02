package com.edu.esuelaing.arep.controllers;

import com.edu.esuelaing.arep.annotations.GetMapping;
import com.edu.esuelaing.arep.annotations.RequestParam;
import com.edu.esuelaing.arep.annotations.RestController;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public String index() {
        return "Greetings from the HelloController!";
    }

    @GetMapping("/app/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "{\"mensaje\": \"Hola, " + name + "\"}";
    }
}