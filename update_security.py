import os

# Define the services directories and their respective ports
services_ports = {
    'network-monitoring-service': 8081,
    'data-transformation-service': 8082,
    'model-training-service': 8083,
    'model-aggregation-service': 8084,
    'ids-ips-service': 8085
}

# Define the base directory
base_dir = 'C:/Users/10059890/Desktop/network-monitoring-app/backend'

# Define the content for SecurityConfig.java
security_config_content = """
package com.example;

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
"""

# Define the content for application.properties
def application_properties_content(port):
    return f"""
server.port={port}
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
"""

# Function to create or update SecurityConfig.java
def create_or_update_security_config(service_dir):
    package_dir = os.path.join(service_dir, 'src/main/java/com/example')
    security_config_path = os.path.join(package_dir, 'SecurityConfig.java')
    if not os.path.exists(package_dir):
        os.makedirs(package_dir, exist_ok=True)
    with open(security_config_path, 'w') as file:
        file.write(security_config_content)

# Function to update application.properties with the specified port
def update_application_properties(service_dir, port):
    application_properties_path = os.path.join(service_dir, 'src/main/resources/application.properties')
    if not os.path.exists(application_properties_path):
        os.makedirs(os.path.dirname(application_properties_path), exist_ok=True)
    with open(application_properties_path, 'w') as file:
        file.write(application_properties_content(port))

# Update each service with the security configuration and specified port
for service, port in services_ports.items():
    service_dir = os.path.join(base_dir, service)
    create_or_update_security_config(service_dir)
    update_application_properties(service_dir, port)

print("Security configuration and ports updated for all services.")