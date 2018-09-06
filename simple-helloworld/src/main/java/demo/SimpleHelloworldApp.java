package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@SpringBootApplication
public class SimpleHelloworldApp {

    public static void main(String[] args) {
        SpringApplication.run(SimpleHelloworldApp.class, args);
    }
}

@RestController
class GreetingController {

    @GetMapping("/")
    String greet(@RequestParam(defaultValue = "World") String name) {
        return String.format("Hello %s @ %s", name, Instant.now());
    }
}