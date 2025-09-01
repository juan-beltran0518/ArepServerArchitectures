package com.edu.esuelaing.arep.ioc;

import com.edu.esuelaing.arep.SimpleHttpServer;
import com.edu.esuelaing.arep.Request;
import com.edu.esuelaing.arep.annotations.RestController;
import com.edu.esuelaing.arep.annotations.GetMapping;
import com.edu.esuelaing.arep.annotations.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class MicroSpringBoot {

    private static BeanContainer beanContainer = new BeanContainer();
    private static Map<String, RouteHandler> routeHandlers = new HashMap<>();

    /**
     * Método estático para cargar un POJO específico desde línea de comandos
     */
    public static void run(String pojoClassName) throws Exception {
        System.out.println("=== MicroSpringBoot Starting ===");
        System.out.println("Cargando POJO: " + pojoClassName);

        ReflectionLoader loader = new ReflectionLoader();
        Object pojoInstance = loader.loadSpecificBean(pojoClassName);

        if (pojoInstance != null) {
            beanContainer.registerBean((Class<Object>) pojoInstance.getClass(), pojoInstance);

            registerControllerMethods(pojoInstance);
        }

        loadRestControllers();

        System.out.println("Iniciando servidor HTTP...");
        SimpleHttpServer.main(new String[] {});
    }

    /**
     * Método de instancia para auto-descubrimiento de controladores
     */
    public void start(String packageName) {
        try {
            System.out.println("=== MicroSpringBoot Starting (Auto-Discovery) ===");
            System.out.println("Explorando paquete: " + packageName);

            ReflectionLoader loader = new ReflectionLoader();
            List<Object> beans = loader.loadBeans(packageName);

            System.out.println("Controladores encontrados: " + beans.size());

            for (Object bean : beans) {
                beanContainer.registerBean((Class<Object>) bean.getClass(), bean);
                registerControllerMethods(bean);
            }

            System.out.println("Iniciando servidor HTTP...");
            SimpleHttpServer.main(new String[] {});

        } catch (Exception e) {
            System.err.println("Error durante el inicio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Maneja las peticiones HTTP (para testing)
     */
    public String handleRequest(String method, String pathWithQuery) {
        String[] parts = pathWithQuery.split("\\?", 2);
        String path = parts[0];
        String queryString = parts.length > 1 ? parts[1] : "";

        Map<String, String> queryParams = parseQueryParams(queryString);
        Request request = new Request(method, path, queryParams, null);

        String routeKey = method + ":" + path;
        if (routeHandlers.containsKey(routeKey)) {
            return routeHandlers.get(routeKey).handle(request);
        }

        return "404 Not Found";
    }

    /**
     * Carga automáticamente todos los controladores con @RestController
     */
    private static void loadRestControllers() throws IOException, ClassNotFoundException {
        ReflectionLoader loader = new ReflectionLoader();
        List<Object> controllers = loader.loadBeans("com.edu.esuelaing.arep.controllers");

        for (Object controller : controllers) {
            if (!beanContainer.hasBean(controller.getClass())) {
                beanContainer.registerBean((Class<Object>) controller.getClass(), controller);
                registerControllerMethods(controller);
            }
        }
    }

    /**
     * Registra todos los métodos de un controlador que tengan @GetMapping
     */
    private static void registerControllerMethods(Object controller) {
        Class<?> clazz = controller.getClass();
        System.out.println("Registrando métodos del controlador: " + clazz.getSimpleName());

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                String path = method.getAnnotation(GetMapping.class).value();
                System.out.println("  - Registrando ruta GET: " + path);

                SimpleHttpServer.get(path, request -> {
                    try {
                        return invokeControllerMethod(controller, method, request);
                    } catch (Exception e) {
                        System.err.println("Error procesando request: " + e.getMessage());
                        e.printStackTrace();
                        return "Error processing request: " + e.getMessage();
                    }
                });

                routeHandlers.put("GET:" + path, request -> {
                    try {
                        return invokeControllerMethod(controller, method, request);
                    } catch (Exception e) {
                        return "Error: " + e.getMessage();
                    }
                });
            }
        }
    }

    /**
     * Invoca un método del controlador con manejo de parámetros
     */
    private static String invokeControllerMethod(Object controller, Method method, Request request) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            if (param.isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = param.getAnnotation(RequestParam.class);
                String paramName = requestParam.value();
                String defaultValue = requestParam.defaultValue();

                String value = request.getQueryParam(paramName);
                args[i] = (value != null && !value.isEmpty()) ? value
                        : (!defaultValue.equals("") ? defaultValue : "World");
            } else if (param.getType() == Request.class) {
                args[i] = request;
            }
        }

        return (String) method.invoke(controller, args);
    }

    /**
     * Parsea query parameters de una URL
     */
    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query == null || query.isEmpty())
            return queryParams;

        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2) {
                queryParams.put(kv[0], kv[1]);
            } else {
                queryParams.put(kv[0], "");
            }
        }
        return queryParams;
    }

    /**
     * Interface para manejar rutas
     */
    @FunctionalInterface
    public interface RouteHandler {
        String handle(Request request);
    }
}