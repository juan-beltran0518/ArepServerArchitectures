package com.edu.esuelaing.arep.ioc;

import java.util.HashMap;
import java.util.Map;

public class BeanContainer {
    private Map<Class<?>, Object> beans = new HashMap<>();

    public <T> void registerBean(Class<T> clazz, T instance) {
        beans.put(clazz, instance);
    }

    public <T> T getBean(Class<T> clazz) {
        return clazz.cast(beans.get(clazz));
    }

    public boolean containsBean(Class<?> clazz) {
        return beans.containsKey(clazz);
    }
}