import os

# Define the services directories
services = [
    'network-monitoring-service',
    'data-transformation-service',
    'model-training-service',
    'model-aggregation-service',
    'ids-ips-service'
]

# Define the base directory
base_dir = 'C:/Users/10059890/Desktop/network-monitoring-app/backend'

# Define the content to add to build.gradle
build_gradle_content = """
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.1.4'
    id 'io.spring.dependency-management' version '1.0.15.RELEASE'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    runtimeOnly 'com.h2database:h2'
}

tasks.named('test') {
    useJUnitPlatform()
}
"""

# Define the content to add to application.properties
application_properties_content = """
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
"""

# Define the content to add to application-test.properties
application_test_properties_content = """
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
"""

# Define the content for TestConfig.java
test_config_content = """
package com.example.datatransformationservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb");
        dataSource.setUsername("sa");
        dataSource.setPassword("password");
        return dataSource;
    }
}
"""

# Function to update build.gradle
def update_build_gradle(service_dir):
    build_gradle_path = os.path.join(service_dir, 'build.gradle')
    with open(build_gradle_path, 'w') as file:
        file.write(build_gradle_content)

# Function to update application.properties
def update_application_properties(service_dir):
    application_properties_path = os.path.join(service_dir, 'src/main/resources/application.properties')
    if not os.path.exists(application_properties_path):
        os.makedirs(os.path.dirname(application_properties_path), exist_ok=True)
    with open(application_properties_path, 'w') as file:
        file.write(application_properties_content)

# Function to update application-test.properties
def update_application_test_properties(service_dir):
    application_test_properties_path = os.path.join(service_dir, 'src/main/resources/application-test.properties')
    if not os.path.exists(application_test_properties_path):
        os.makedirs(os.path.dirname(application_test_properties_path), exist_ok=True)
    with open(application_test_properties_path, 'w') as file:
        file.write(application_test_properties_content)

# Function to create TestConfig.java
def create_test_config(service_dir):
    test_config_dir = os.path.join(service_dir, 'src/test/java/com/example/datatransformationservice')
    test_config_path = os.path.join(test_config_dir, 'TestConfig.java')
    if not os.path.exists(test_config_dir):
        os.makedirs(test_config_dir, exist_ok=True)
    with open(test_config_path, 'w') as file:
        file.write(test_config_content)

# Update each service
for service in services:
    service_dir = os.path.join(base_dir, service)
    update_build_gradle(service_dir)
    update_application_properties(service_dir)
    update_application_test_properties(service_dir)
    create_test_config(service_dir)

print("Updates applied to all services.")