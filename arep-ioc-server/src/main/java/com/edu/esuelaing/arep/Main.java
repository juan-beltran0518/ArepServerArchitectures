package com.edu.esuelaing.arep;

import com.edu.esuelaing.arep.ioc.MicroSpringBoot;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please provide the fully qualified name of the POJO class.");
            System.exit(1);
        }

        String pojoClassName = args[0];
        try {
            MicroSpringBoot.run(pojoClassName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}