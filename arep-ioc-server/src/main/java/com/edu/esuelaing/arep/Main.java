package com.edu.esuelaing.arep;

import com.edu.esuelaing.arep.ioc.MicroSpringBoot;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                System.out.println("Cargando POJO específico: " + args[0]);
                MicroSpringBoot.run(args[0]);
            } else {
                System.out.println("Iniciando con auto-descubrimiento de controladores...");
                MicroSpringBoot microSpringBoot = new MicroSpringBoot();
                microSpringBoot.start("com.edu.esuelaing.arep.controllers");
            }
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}