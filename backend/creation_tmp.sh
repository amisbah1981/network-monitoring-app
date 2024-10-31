#!/bin/bash

# Function to create a Spring Boot project using curl
create_spring_project() {
    local project_name=$1
    local dependencies=$2

    # Download the project zip file
    curl https://start.spring.io/starter.zip \
        -d dependencies=$dependencies \
        -d type=gradle-project \
        -d baseDir=$project_name \
        -d javaVersion=21 \
        -o $project_name.zip

    # Unzip the project
    unzip $project_name.zip -d .

    # Remove the zip file
    rm $project_name.zip
}

# Create the edge-devices-service project
create_spring_project "edge-devices-service" "web,actuator,webflux"

# Navigate into the edge-devices-service directory
cd edge-devices-service

# Update build.gradle to use Java 21
sed -i 's/sourceCompatibility = .*/sourceCompatibility = 21/' build.gradle

# Ensure only one main class is present
rm -rf src/main/java/com/example/demo

# Create the main application class
mkdir -p src/main/java/com/example/edgedevices
cat <<EOL > src/main/java/com/example/edgedevices/EdgeDevicesServiceApplication.java
package com.example.edgedevices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EdgeDevicesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdgeDevicesServiceApplication.class, args);
    }
}
EOL

# Create the EdgeDeviceService class
mkdir -p src/main/java/com/example/edgedevices/service
cat <<EOL > src/main/java/com/example/edgedevices/service/EdgeDeviceService.java
package com.example.edgedevices.service;

import com.example.edgedevices.model.TrafficData;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class EdgeDeviceService {

    private final WebClient webClient;

    public EdgeDeviceService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082").build();
    }

    public List<TrafficData> generateAndSendTraffic() {
        List<TrafficData> trafficDataList = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            TrafficData trafficData = new TrafficData();
            trafficData.setDeviceId("Device-" + (random.nextInt(10) + 1));
            trafficData.setTrafficType("Benign");
            trafficData.setTimestamp(System.currentTimeMillis());
            trafficDataList.add(trafficData);
        }

        webClient.post()
                .uri("/generateTraffic")
                .bodyValue(trafficDataList)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        return trafficDataList;
    }
}
EOL

# Create the TrafficData model class
mkdir -p src/main/java/com/example/edgedevices/model
cat <<EOL > src/main/java/com/example/edgedevices/model/TrafficData.java
package com.example.edgedevices.model;

public class TrafficData {

    private String deviceId;
    private String trafficType;
    private long timestamp;

    // Getters and Setters

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTrafficType() {
        return trafficType;
    }

    public void setTrafficType(String trafficType) {
        this.trafficType = trafficType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
EOL

# Create the EdgeDeviceController class
mkdir -p src/main/java/com/example/edgedevices/controller
cat <<EOL > src/main/java/com/example/edgedevices/controller/EdgeDeviceController.java
package com.example.edgedevices.controller;

import com.example.edgedevices.model.TrafficData;
import com.example.edgedevices.service.EdgeDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EdgeDeviceController {

    @Autowired
    private EdgeDeviceService edgeDeviceService;

    @GetMapping("/generateAndSendTraffic")
    public List<TrafficData> generateAndSendTraffic() {
        return edgeDeviceService.generateAndSendTraffic();
    }
}
EOL

# Update application.properties
cat <<EOL > src/main/resources/application.properties
server.port=8083
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
EOL

# Navigate back to the parent directory
cd ..

# Create the malicious-devices-service project
create_spring_project "malicious-devices-service" "web,actuator,webflux"

# Navigate into the malicious-devices-service directory
cd malicious-devices-service

# Update build.gradle to use Java 21
sed -i 's/sourceCompatibility = .*/sourceCompatibility = 21/' build.gradle

# Ensure only one main class is present
rm -rf src/main/java/com/example/demo

# Create the main application class
mkdir -p src/main/java/com/example/maliciousdevices
cat <<EOL > src/main/java/com/example/maliciousdevices/MaliciousDevicesServiceApplication.java
package com.example.maliciousdevices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MaliciousDevicesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaliciousDevicesServiceApplication.class, args);
    }
}
EOL

# Create the MaliciousDeviceService class
mkdir -p src/main/java/com/example/maliciousdevices/service
cat <<EOL > src/main/java/com/example/maliciousdevices/service/MaliciousDeviceService.java
package com.example.maliciousdevices.service;

import com.example.maliciousdevices.model.TrafficData;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class MaliciousDeviceService {

    private final WebClient webClient;

    public MaliciousDeviceService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082").build();
    }

    public List<TrafficData> generateAndSendMaliciousTraffic() {
        List<TrafficData> trafficDataList = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            TrafficData trafficData = new TrafficData();
            trafficData.setDeviceId("Malicious-Device-" + (random.nextInt(10) + 1));
            trafficData.setTrafficType(generateMaliciousTrafficType(random));
            trafficData.setTimestamp(System.currentTimeMillis());
            trafficDataList.add(trafficData);
        }

        webClient.post()
                .uri("/generateTraffic")
                .bodyValue(trafficDataList)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        return trafficDataList;
    }

    private String generateMaliciousTrafficType(Random random) {
        String[] maliciousTypes = {"DDoS", "Port Scanning", "Data Exfiltration"};
        return maliciousTypes[random.nextInt(maliciousTypes.length)];
    }
}
EOL

# Create the TrafficData model class
mkdir -p src/main/java/com/example/maliciousdevices/model
cat <<EOL > src/main/java/com/example/maliciousdevices/model/TrafficData.java
package com.example.maliciousdevices.model;

public class TrafficData {

    private String deviceId;
    private String trafficType;
    private long timestamp;

    // Getters and Setters

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTrafficType() {
        return trafficType;
    }

    public void setTrafficType(String trafficType) {
        this.trafficType = trafficType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
EOL

# Create the MaliciousDeviceController class
mkdir -p src/main/java/com/example/maliciousdevices/controller
cat <<EOL > src/main/java/com/example/maliciousdevices/controller/MaliciousDeviceController.java
package com.example.maliciousdevices.controller;

import com.example.maliciousdevices.model.TrafficData;
import com.example.maliciousdevices.service.MaliciousDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MaliciousDeviceController {

    @Autowired
    private MaliciousDeviceService maliciousDeviceService;

    @GetMapping("/generateAndSendMaliciousTraffic")
    public List<TrafficData> generateAndSendMaliciousTraffic() {
        return maliciousDeviceService.generateAndSendMaliciousTraffic();
    }
}
EOL

# Update application.properties
cat <<EOL > src/main/resources/application.properties
server.port=8084
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
EOL

# Navigate back to the parent directory
cd ..

# Create the network-monitoring-service project
create_spring_project "network-monitoring-service" "web,actuator,security,webflux,data-jpa,h2"

# Navigate into the network-monitoring-service directory
cd network-monitoring-service

# Update build.gradle to use Java 21
cat <<EOL > build.gradle
plugins {
    id 'org.springframework.boot' version '3.1.4'
    id 'io.spring.dependency-management' version '1.0.13.RELEASE'
    id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '21'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.h2database:h2'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
}

test {
    useJUnitPlatform()
}
EOL

# Ensure only one main class is present
rm -rf src/main/java/com/example/demo

# Create the main application class
mkdir -p src/main/java/com/example/networkmonitoring
cat <<EOL > src/main/java/com/example/networkmonitoring/NetworkMonitoringServiceApplication.java
package com.example.networkmonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NetworkMonitoringServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetworkMonitoringServiceApplication.class, args);
    }
}
EOL

# Create the TrafficData entity class
mkdir -p src/main/java/com/example/networkmonitoring/model
cat <<EOL > src/main/java/com/example/networkmonitoring/model/TrafficData.java
package com.example.networkmonitoring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TrafficData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deviceId;
    private String trafficType;
    private long timestamp;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTrafficType() {
        return trafficType;
    }

    public void setTrafficType(String trafficType) {
        this.trafficType = trafficType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
EOL

# Create the TrafficDataRepository interface
mkdir -p src/main/java/com/example/networkmonitoring/repository
cat <<EOL > src/main/java/com/example/networkmonitoring/repository/TrafficDataRepository.java
package com.example.networkmonitoring.repository;

import com.example.networkmonitoring.model.TrafficData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficDataRepository extends JpaRepository<TrafficData, Long> {
}
EOL

# Create the NetworkMonitoringService class
mkdir -p src/main/java/com/example/networkmonitoring/service
cat <<EOL > src/main/java/com/example/networkmonitoring/service/NetworkMonitoringService.java
package com.example.networkmonitoring.service;

import com.example.networkmonitoring.model.TrafficData;
import com.example.networkmonitoring.repository.TrafficDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class NetworkMonitoringService {

    private final WebClient webClient;
    private final TrafficDataRepository trafficDataRepository;

    @Autowired
    public NetworkMonitoringService(WebClient.Builder webClientBuilder, TrafficDataRepository trafficDataRepository) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082").build();
        this.trafficDataRepository = trafficDataRepository;
    }

    public List<TrafficData> monitorNetwork() {
        List<TrafficData> trafficDataList = webClient.get()
                .uri("/allTrafficData")
                .retrieve()
                .bodyToFlux(TrafficData.class)
                .collectList()
                .block();

        return trafficDataRepository.saveAll(trafficDataList);
    }

    public List<TrafficData> getAllTrafficData() {
        return trafficDataRepository.findAll();
    }
}
EOL

# Create the NetworkMonitoringController class
mkdir -p src/main/java/com/example/networkmonitoring/controller
cat <<EOL > src/main/java/com/example/networkmonitoring/controller/NetworkMonitoringController.java
package com.example.networkmonitoring.controller;

import com.example.networkmonitoring.model.TrafficData;
import com.example.networkmonitoring.service.NetworkMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NetworkMonitoringController {

    @Autowired
    private NetworkMonitoringService networkMonitoringService;

    @GetMapping("/monitor")
    public List<TrafficData> monitorNetwork() {
        return networkMonitoringService.monitorNetwork();
    }

    @GetMapping("/allTrafficData")
    public List<TrafficData> getAllTrafficData() {
        return networkMonitoringService.getAllTrafficData();
    }
}
EOL

# Create the SecurityConfig class
mkdir -p src/main/java/com/example/networkmonitoring
cat <<EOL > src/main/java/com/example/networkmonitoring/SecurityConfig.java
package com.example.networkmonitoring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/actuator/**").permitAll()  // Allow access to actuator endpoints
                .anyRequest().authenticated()
            )
            .csrf().disable()  // Disable CSRF for simplicity
            .httpBasic();
        return http.build();
    }
}
EOL

# Update application.properties
cat <<EOL > src/main/resources/application.properties
server.port=8081
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.security.user.name=admin
spring.security.user.password=admin
EOL

# Print completion message
echo "Edge Devices Service, Malicious Devices Service, and Network Monitoring Service setup is complete."