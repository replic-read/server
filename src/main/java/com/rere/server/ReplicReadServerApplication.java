package com.rere.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Spring application class.
 */
@Configuration
@SpringBootApplication
public class ReplicReadServerApplication {

    /**
     * The main method that launches the spring application.
     * @param args The CLI-args.
     */
    public static void main(String[] args) {
        SpringApplication.run(ReplicReadServerApplication.class, args);
    }

    /**
     * Provides a UTC-clock system-wide.
     * @return A UTC-clock.
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

}
