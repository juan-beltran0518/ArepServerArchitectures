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

    public List<Object> loadBeans(String packageName) throws IOException, ClassNotFoundException {
        List<Object> beans = new ArrayList<>();
        String path = packageName.replace('.', '/');
        File directory = new File(getClass().getClassLoader().getResource(path).getFile());

        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().replace(".class", "");
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(RestController.class)) {
                        try {
                            Constructor<?> constructor = clazz.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            beans.add(constructor.newInstance());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return beans;
    }

    public static Set<Class<?>> findClassesWithAnnotation(Class<? extends Annotation> annotation) {
        Set<Class<?>> classes = new HashSet<>();
        try {
            String packageName = "com.edu.esuelaing.arep.controllers";
            String path = packageName.replace('.', '/');
            File directory = new File(ReflectionLoader.class.getClassLoader().getResource(path).getFile());
            
            if (directory.exists()) {
                for (File file : directory.listFiles()) {
                    if (file.getName().endsWith(".class")) {
                        String className = packageName + '.' + file.getName().replace(".class", "");
                        try {
                            Class<?> clazz = Class.forName(className);
                            if (clazz.isAnnotationPresent(annotation)) {
                                classes.add(clazz);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }
}