package com.example.edgedevices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EdgeDeviceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeDeviceServiceApplication.class, args);
    }
}
