package co.edu.escuelaing.reflexionlab;

import co.edu.escuelaing.reflexionlab.example.GreetingController;
import co.edu.escuelaing.reflexionlab.example.HelloController;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MicroSpringBootTest {

    @Test
    public void testHelloController() {
        HelloController controller = new HelloController();
        String response = controller.index();
        assertEquals("Greetings from MicroSpringBoot!", response);
    }

    @Test
    public void testGreetingControllerDefaultParam() {
        GreetingController controller = new GreetingController();
        String response = controller.greeting("World");
        assertTrue(response.contains("Hola World"));
    }

    @Test
    public void testGreetingControllerCustomParam() {
        GreetingController controller = new GreetingController();
        String response = controller.greeting("AWS");
        assertTrue(response.contains("Hola AWS"));
    }
}
