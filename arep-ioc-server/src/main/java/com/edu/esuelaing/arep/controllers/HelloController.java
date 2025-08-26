package com.edu.esuelaing.arep.controllers;

import com.edu.esuelaing.arep.annotations.GetMapping;
import com.edu.esuelaing.arep.annotations.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String index() {
        return "Greetings from the HelloController!";
    }
}