package co.edu.escuelaing.reflexionlab;

import co.edu.escuelaing.reflexionlab.framework.ComponentScanner;
import co.edu.escuelaing.reflexionlab.server.HttpServer;

public class MicroSpringBoot {

    public static void main(String[] args) {
        try {
            System.out.println("Starting MicroSpringBoot Application...");
            ComponentScanner.scanAndRegister(args);
            HttpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
