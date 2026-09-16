package cl.mapuescuela;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MapuescuelaApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                MapuescuelaApplication.class,
                args);
    }
}