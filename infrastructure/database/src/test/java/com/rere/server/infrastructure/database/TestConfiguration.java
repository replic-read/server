package com.rere.server.infrastructure.database;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@AutoConfigurationPackage
@SpringBootApplication
@EntityScan(basePackages = "com.rere.server.infrastructure.database.table")
@EnableJpaRepositories(basePackages = "com.rere.server.infrastructure.database.repository.jpa")
public class TestConfiguration {
}