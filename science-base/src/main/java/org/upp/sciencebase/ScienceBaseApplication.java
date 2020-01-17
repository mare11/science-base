package org.upp.sciencebase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ScienceBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScienceBaseApplication.class, args);
    }

}
