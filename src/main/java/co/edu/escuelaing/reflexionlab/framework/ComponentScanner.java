package co.edu.escuelaing.reflexionlab.framework;

import co.edu.escuelaing.reflexionlab.annotations.GetMapping;
import co.edu.escuelaing.reflexionlab.annotations.RestController;
import co.edu.escuelaing.reflexionlab.server.HttpServer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ComponentScanner {

    public static void scanAndRegister(String[] args) throws Exception {
        if (args.length > 0) {
            System.out.println("Loading component from CLI argument: " + args[0]);
            loadComponent(args[0]);
        } else {
            System.out.println("No CLI arguments, scanning classpath for @RestController...");
            scanClasspathForComponents("co.edu.escuelaing.reflexionlab");
        }
    }

    private static void loadComponent(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        if (clazz.isAnnotationPresent(RestController.class)) {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping getMapping = method.getAnnotation(GetMapping.class);
                    String path = getMapping.value();
                    HttpServer.registerRoute(path, instance, method);
                    System.out.println("Registered route: " + path + " -> " + method.getName());
                }
            }
        } else {
            System.err.println("Class " + className + " is not annotated with @RestController");
        }
    }

    private static void scanClasspathForComponents(String basePackage) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = basePackage.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(java.net.URLDecoder.decode(resource.getFile(), "UTF-8")));
        }
        
        for (File dir : dirs) {
            findClasses(dir, basePackage);
        }
    }

    private static void findClasses(File directory, String packageName) throws Exception {
        if (!directory.exists()) {
            return;
        }
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                findClasses(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(RestController.class)) {
                        loadComponent(className);
                    }
                } catch (Exception e) {
                    // Ignore missing classes in scanning
                }
            }
        }
    }
}
