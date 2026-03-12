package co.edu.escuelaing.reflexionlab.example;

import co.edu.escuelaing.reflexionlab.annotations.GetMapping;
import co.edu.escuelaing.reflexionlab.annotations.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String index() {
        return "Greetings from MicroSpringBoot!";
    }
}
