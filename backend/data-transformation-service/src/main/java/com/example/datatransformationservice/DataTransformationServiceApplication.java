package com.example.datatransformationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
@RequestMapping("/api/transform")
public class DataTransformationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataTransformationServiceApplication.class, args);
	}

	@PostMapping("/uppercase")
	public String transformToUppercase(@RequestBody String input) {
		return input.toUpperCase();
	}

	@PostMapping("/lowercase")
	public String transformToLowercase(@RequestBody String input) {
		return input.toLowerCase();
	}

	@PostMapping("/reverse")
	public String transformReverse(@RequestBody String input) {
		return new StringBuilder(input).reverse().toString();
	}
}
