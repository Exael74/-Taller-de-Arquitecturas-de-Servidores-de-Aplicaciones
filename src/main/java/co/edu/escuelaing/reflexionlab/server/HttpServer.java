package co.edu.escuelaing.reflexionlab.server;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import co.edu.escuelaing.reflexionlab.annotations.RequestParam;

public class HttpServer {

    private static final int PORT = 8080;
    private static Map<String, Route> routes = new HashMap<>();

    public static void registerRoute(String path, Object controller, Method method) {
        routes.put(path, new Route(controller, method));
    }

    public static void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("MicroSpringBoot server listening on port " + PORT + "...");

        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                handleRequest(clientSocket);
            } catch (IOException e) {
                System.err.println("Accept failed.");
            }
        }
    }

    private static void handleRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream();
             PrintWriter pw = new PrintWriter(out, true)) {

            String inputLine = in.readLine();
            if (inputLine == null || inputLine.isEmpty()) return;

            String[] requestParts = inputLine.split(" ");
            String method = requestParts[0];
            String uri = requestParts[1];

            System.out.println("Request Received: " + inputLine);

            String path = uri;
            String query = null;
            if (uri.contains("?")) {
                int qIndex = uri.indexOf("?");
                path = uri.substring(0, qIndex);
                query = uri.substring(qIndex + 1);
            }

            if (routes.containsKey(path)) {
                handleDynamicRoute(path, query, pw, out);
            } else {
                handleStaticFile(path, pw, out);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleDynamicRoute(String path, String query, PrintWriter pw, OutputStream out) throws Exception {
        Route route = routes.get(path);
        Map<String, String> queryParams = parseQueryParams(query);

        Method method = route.method();
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            if (param.isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = param.getAnnotation(RequestParam.class);
                String paramName = requestParam.value();
                String defaultValue = requestParam.defaultValue();

                String value = queryParams.get(paramName);
                if (value == null) {
                    value = defaultValue;
                }
                args[i] = value;
            } else {
                args[i] = null;
            }
        }

        try {
            Object result = method.invoke(route.controller(), args);
            sendResponse(pw, out, "200 OK", "text/plain", result.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(pw, out, "500 Internal Server Error", "text/plain", "500 Internal Server Error".getBytes(StandardCharsets.UTF_8));
        }
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) return params;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            } else if (kv.length == 1) {
                params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), "");
            }
        }
        return params;
    }

    private static void handleStaticFile(String path, PrintWriter pw, OutputStream out) throws IOException {
        if (path.equals("/")) {
            path = "/index.html";
        }

        Path filePath = Paths.get("src/main/resources/public", path);
        if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
            String contentType = guessContentType(filePath);
            byte[] fileBytes = Files.readAllBytes(filePath);
            sendResponse(pw, out, "200 OK", contentType, fileBytes);
        } else {
            sendResponse(pw, out, "404 Not Found", "text/html", "<h1>404 Not Found</h1>".getBytes(StandardCharsets.UTF_8));
        }
    }

    private static String guessContentType(Path filePath) {
        String name = filePath.getFileName().toString().toLowerCase();
        if (name.endsWith(".html") || name.endsWith(".htm")) return "text/html";
        if (name.endsWith(".css")) return "text/css";
        if (name.endsWith(".js")) return "application/javascript";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream";
    }

    private static void sendResponse(PrintWriter pw, OutputStream out, String status, String contentType, byte[] body) throws IOException {
        pw.print("HTTP/1.1 " + status + "\r\n");
        pw.print("Content-Type: " + contentType + "\r\n");
        pw.print("Content-Length: " + body.length + "\r\n");
        pw.print("\r\n");
        pw.flush();
        out.write(body);
        out.flush();
    }

    private record Route(Object controller, Method method) {}
}
