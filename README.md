# MicroSpringBoot Framework

Este proyecto es un taller de diseño de un servidor web desde cero utilizando Java nativo (Sockets) y un pequeño framework de Inyección de Dependencias (IoC) mediante Reflexión. Es capaz de entregar archivos estáticos (HTML e Imagenes) e invocar métodos y clases mapeados mediante anotaciones personalizadas tales como `@RestController`, `@GetMapping` y `@RequestParam`.

## Requisitos
- **Java 17** o superior
- **Maven**
- **Git**

## Arquitectura y Componentes
El proyecto se divide en diferentes capas:
1. **Annotations (`@RestController`, `@GetMapping`, `@RequestParam`)**: Permiten marcar los componentes para el escáner.
2. **Framework (`ComponentScanner`)**: Analiza las clases pasadas por CLI o escanea el Classpath del Thread buscando componentes anotados, luego registra estos métodos invocables de reflexción como rutas dentro del Server.
3. **Server (`HttpServer`)**: Lee y procesa las peticiones (Sockets en puerto 8080). Si hay una ruta mapeada coincidente, extrae parámetros e invoca el método. Si no, intenta leer archivos estáticos en `src/main/resources/public/`.
4. **App de Ejemplo (`example`)**: Clases de muestra `HelloController` y `GreetingController`.

## ¿Cómo Empezar?

1. Clonar el Repositorio
2. Construir el Proyecto con Maven:
   ```bash
   mvn clean install
   ```
3. Ejecutar la Aplicación:
   ```bash
   java -cp target/classes co.edu.escuelaing.reflexionlab.MicroSpringBoot
   ```
   *(Pude ser ejecutado proporcionando directamente la clase a manera de input en CLI como se pedia como requisito 1).*

4. Ir al Navegador y probar los siguientes Endpoints:
   - Rutas Dinámicas (Rest):
     - `http://localhost:8080/hello`
     - `http://localhost:8080/greeting`
     - `http://localhost:8080/greeting?name=EscuelaIng`
   - Rutas Estáticas:
     - `http://localhost:8080/` (apunta a index.html)
     - `http://localhost:8080/index.html`
     - `http://localhost:8080/logo.png`
