package com.edu.esuelaing.arep.controllers;

import com.edu.esuelaing.arep.annotations.GetMapping;
import com.edu.esuelaing.arep.annotations.PostMapping;
import com.edu.esuelaing.arep.annotations.RequestParam;
import com.edu.esuelaing.arep.annotations.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public String greetingGet(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format(template + " (via GET)", name);
    }

    @PostMapping("/greeting")
    public String greetingPost(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format(template + " (via POST)", name);
    }
}