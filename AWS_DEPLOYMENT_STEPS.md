# Deploying MicroSpringBoot to AWS Academy

Este archivo contiene el paso a paso exacto para desplegar el proyecto en una instancia EC2 de AWS Academy.
**Nota:** Este archivo está en el `.gitignore` por lo tanto no se subirá al repositorio público cómo fue solicitado.

## Paso 1: Lanzar la Instancia EC2
1. Inicia sesión en **AWS Academy Learner Lab**.
2. Ve al servicio **EC2** y haz clic en "Launch Instance" (Lanzar Instancia).
3. **Name**: `MicroSpringBoot-Server`
4. **AMI**: Selecciona `Amazon Linux 2023 AMI` (o Ubuntu si lo prefieres).
5. **Instance Type**: `t2.micro` (Apto para el lab gratuito).
6. **Key Pair**: Selecciona tu par de claves existente (ej. `vockey`) o crea uno nuevo y descarga el `.pem`.
7. **Network Settings**:
   - Auto-assign Public IP: `Enable`.
   - Crea un nuevo **Security Group** o edita el existente.
   - Agrega o asegúrate de que existan las siguientes reglas de **Inbound Traffic** (Reglas de entrada):
     - `SSH (22)` desde `Anywhere (0.0.0.0/0)` para poder conectarte.
     - `Custom TCP (8080)` desde `Anywhere (0.0.0.0/0)` para que podamos acceder a nuestro servidor web.
8. Haz clic en **Launch Instance**.

## Paso 2: Conectarse a la Instancia
1. Una vez la instancia esté en estado `Running`, copia la **Public IPv4 address**.
2. Si usas Linux/Mac/Windows(PowerShell), usa SSH en la ubicación donde guardaste tu llave `.pem`.

   ```bash
   chmod 400 labsuser.pem # En caso de que sea descargada nueva
   ssh -i "labsuser.pem" ec2-user@<TU_IP_PUBLICA>
   ```

## Paso 3: Instalar Java (JDK 17) y Git
Ejecuta los siguientes comandos dentro de la instancia EC2:

```bash
sudo yum update -y
sudo yum install java-17-amazon-corretto -y
sudo yum install git -y
```
Verifica la instalación:
```bash
java -version
git --version
```

## Paso 4: Instalar Maven
```bash
sudo wget https://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/6/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven
```
*(Alternativamente, puedes simplemente ejecutarlo bajando los binarios si el repositorio de yum da problemas).*
Verifica maven: `mvn -v`

## Paso 5: Clonar y Compilar el Proyecto
1. Clona tu repositorio de GitHub usando HTTPS:
   ```bash
   git clone <URL_DE_TU_REPOSITORIO>.git
   cd Taller-de-Arquitecturas-de-Servidores-de-Aplicaciones
   ```
2. Compila y empaqueta el código:
   ```bash
   mvn clean install
   ```

## Paso 6: Ejecutar el Servidor
1. Para ejecutar tu aplicación:
   ```bash
   java -cp target/classes co.edu.escuelaing.reflexionlab.MicroSpringBoot
   ```

*(Opcional: Si quieres que siga corriendo al cerrar la terminal, usa `nohup`)*:
```bash
nohup java -cp target/classes co.edu.escuelaing.reflexionlab.MicroSpringBoot > log.txt 2>&1 &
```

## Paso 7: Verificar
Abre tu navegador (o usa Postman) e ingresa la ruta:
```text
http://<TU_IP_PUBLICA>:8080/
http://<TU_IP_PUBLICA>:8080/greeting?name=AWS
http://<TU_IP_PUBLICA>:8080/index.html
```
