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
            run(pojoClassName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void run(String pojoClassName) throws Exception {
        Class<?> pojoClass = Class.forName(pojoClassName);
        BeanContainer beanContainer = new BeanContainer();
        Object pojoInstance = pojoClass.getDeclaredConstructor().newInstance();
        beanContainer.registerBean((Class<Object>) pojoClass, pojoInstance);

        loadRestControllers(beanContainer);

        String[] serverArgs = new String[0];
        SimpleHttpServer.main(serverArgs);
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