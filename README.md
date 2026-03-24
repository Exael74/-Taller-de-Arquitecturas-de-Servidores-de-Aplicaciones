# MicroSpringBoot Framework

Servidor web HTTP construido en Java puro (sin frameworks externos) con un motor de Inversión de Control (IoC) basado en Reflexión de Java. Simula el comportamiento básico de Spring Boot mediante anotaciones personalizadas, entregando tanto rutas dinámicas como archivos estáticos.

## ¿Qué implementa?

### 1. Servidor HTTP
- Servidor basado en `ServerSocket` escuchando en el puerto **8080**.
- Soporte del método `GET`.
- Enrutamiento dinámico hacia métodos anotados con `@GetMapping`.
- Entrega de archivos estáticos desde `src/main/resources/public/`.
- Tipos MIME soportados: `text/html`, `image/png`, `text/css`, `application/javascript`.

### 2. Framework IoC por Reflexión
Anotaciones implementadas:
- `@RestController` — Marca una clase como componente web.
- `@GetMapping(value)` — Asocia un método a una ruta HTTP GET.
- `@RequestParam(value, defaultValue)` — Extrae parámetros de la query string.

Capacidades:
- **Carga explícita** de un POJO por línea de comandos (similar a frameworks de testing).
- **Escaneo automático** del classpath para detectar todas las clases con `@RestController`.
- Instanciación vía reflexión: `getDeclaredConstructor().newInstance()`.
- Invocación reflexiva de métodos: `Method.invoke(instance, args)`.

### 3. Aplicación Web de Ejemplo
- `HelloController` con ruta `/hello`.
- `GreetingController` con ruta `/greeting` y soporte de `@RequestParam`.
- Página `index.html` de navegación entre endpoints.
- Imagen `logo.png` para validar entrega de recursos estáticos PNG.

---

## Estructura del Proyecto

```
-Taller-de-Arquitecturas-de-Servidores-de-Aplicaciones/
├── pom.xml
├── .gitignore
├── README.md
└── src/
    ├── main/
    │   ├── java/co/edu/escuelaing/reflexionlab/
    │   │   ├── MicroSpringBoot.java          <- Punto de entrada / contenedor IoC
    │   │   ├── annotations/
    │   │   │   ├── RestController.java
    │   │   │   ├── GetMapping.java
    │   │   │   └── RequestParam.java
    │   │   ├── framework/
    │   │   │   └── ComponentScanner.java     <- Escáner de classpath (Reflexión)
    │   │   ├── server/
    │   │   │   └── HttpServer.java           <- Servidor HTTP (ServerSocket)
    │   │   └── example/
    │   │       ├── HelloController.java
    │   │       └── GreetingController.java
    │   └── resources/
    │       └── public/
    │           ├── index.html
    │           └── logo.png
    └── test/
        └── java/co/edu/escuelaing/reflexionlab/
            └── MicroSpringBootTest.java
```

---

## Requisitos
- Java 17 o superior
- Maven 3.6 o superior
- Git

---

## ¿Cómo ejecutarlo?

### Compilar
```bash
mvn clean install
```

### Modo escaneo automático (detecta todos los `@RestController` del classpath)
```bash
java -cp target/classes co.edu.escuelaing.reflexionlab.MicroSpringBoot
```

### Modo clase explícita (como frameworks de testing)
```bash
java -cp target/classes co.edu.escuelaing.reflexionlab.MicroSpringBoot co.edu.escuelaing.reflexionlab.example.HelloController
```

### Endpoints disponibles
| Tipo | URL | Descripción |
|---|---|---|
| Estático | `http://localhost:8080/` | index.html |
| Dinámico | `http://localhost:8080/hello` | HelloController |
| Dinámico | `http://localhost:8080/greeting` | GreetingController (World por defecto) |
| Dinámico | `http://localhost:8080/greeting?name=Juan` | GreetingController con parámetro |
| Estático | `http://localhost:8080/logo.png` | Imagen PNG |

---

## Pruebas Automatizadas

```bash
mvn test
```

**Resultado:**

<!-- Inserta aquí una captura de pantalla del resultado de mvn test -->

---

## Despliegue en AWS EC2

### Paso 1 – Lanzar instancia
Crear una instancia EC2 `t2.micro` con `Amazon Linux 2023` y habilitar en el Security Group:
- `SSH (22)` desde `0.0.0.0/0`
- `Custom TCP (8080)` desde `0.0.0.0/0`

### Paso 2 – Conectarse
```bash
ssh -i "labsuser.pem" ec2-user@<PUBLIC-IP>
```

### Paso 3 – Instalar dependencias
```bash
sudo yum update -y
sudo yum install java-17-amazon-corretto git -y
sudo yum install -y apache-maven
```

### Paso 4 – Clonar y compilar
```bash
git clone <URL_DEL_REPOSITORIO>.git
cd -Taller-de-Arquitecturas-de-Servidores-de-Aplicaciones
mvn clean install
```

### Paso 5 – Ejecutar el servidor
```bash
nohup java -cp target/classes co.edu.escuelaing.reflexionlab.MicroSpringBoot > log.txt 2>&1 &
```

### Paso 6 – Verificar
```
http://<PUBLIC-IP>:8080/
http://<PUBLIC-IP>:8080/hello
http://<PUBLIC-IP>:8080/greeting?name=AWS
```

**Evidencia del despliegue:**

<!-- Inserta aquí una captura de pantalla del servidor corriendo en EC2 -->
