package com.example.datatransformationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {DataTransformationServiceApplication.class, TestConfig.class})
class DataTransformationServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}