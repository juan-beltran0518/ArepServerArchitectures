package com.edu.esuelaing.arep.ioc;

import com.edu.esuelaing.arep.SimpleHttpServer;
import com.edu.esuelaing.arep.annotations.RestController;
import com.edu.esuelaing.arep.annotations.GetMapping;

import java.lang.reflect.Method;
import java.util.Set;

public class MicroSpringBoot {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please provide the fully qualified name of the POJO class.");
            System.exit(1);
        }

        String pojoClassName = args[0];
        try {
            Class<?> pojoClass = Class.forName(pojoClassName);
            BeanContainer beanContainer = new BeanContainer();
            beanContainer.registerBean(pojoClass.getDeclaredConstructor().newInstance());

            // Load REST controllers
            loadRestControllers(beanContainer);

            // Start the HTTP server
            SimpleHttpServer.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadRestControllers(BeanContainer beanContainer) {
        Set<Class<?>> controllerClasses = ReflectionLoader.findClassesWithAnnotation(RestController.class);
        for (Class<?> controllerClass : controllerClasses) {
            try {
                Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
                for (Method method : controllerClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        String path = method.getAnnotation(GetMapping.class).value();
                        SimpleHttpServer.get(path, request -> {
                            try {
                                return (String) method.invoke(controllerInstance, request);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return "Error processing request";
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}