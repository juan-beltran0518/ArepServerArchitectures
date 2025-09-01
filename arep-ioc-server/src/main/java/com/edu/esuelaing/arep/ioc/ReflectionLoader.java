package com.edu.esuelaing.arep.ioc;

import com.edu.esuelaing.arep.annotations.RestController;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReflectionLoader {

    /**
     * Carga todos los beans con @RestController del paquete especificado
     */
    public List<Object> loadBeans(String packageName) throws IOException, ClassNotFoundException {
        List<Object> beans = new ArrayList<>();
        String path = packageName.replace('.', '/');
        
        java.net.URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            System.err.println("Package not found: " + packageName);
            return beans;
        }
        
        File directory = new File(resource.getFile());
        
        if (directory.exists() && directory.isDirectory()) {
            System.out.println("Explorando directorio: " + directory.getPath());
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().replace(".class", "");
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(RestController.class)) {
                            System.out.println("Cargando controlador: " + className);
                            Constructor<?> constructor = clazz.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            beans.add(constructor.newInstance());
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading class: " + className + " - " + e.getMessage());
                    }
                }
            }
        }
        return beans;
    }

    /**
     * Encuentra todas las clases con una anotación específica
     */
    public static Set<Class<?>> findClassesWithAnnotation(Class<? extends Annotation> annotation) {
        Set<Class<?>> classes = new HashSet<>();
        String packageName = "com.edu.esuelaing.arep.controllers";
        String path = packageName.replace('.', '/');
        
        try {
            java.net.URL resource = ReflectionLoader.class.getClassLoader().getResource(path);
            if (resource != null) {
                File directory = new File(resource.getFile());
                if (directory.exists()) {
                    for (File file : directory.listFiles()) {
                        if (file.getName().endsWith(".class")) {
                            String className = packageName + '.' + file.getName().replace(".class", "");
                            try {
                                Class<?> clazz = Class.forName(className);
                                if (clazz.isAnnotationPresent(annotation)) {
                                    classes.add(clazz);
                                }
                            } catch (Exception e) {
                                System.err.println("Error loading class: " + className);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return classes;
    }

    /**
     * Carga una clase específica por su nombre
     */
    public Object loadSpecificBean(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}