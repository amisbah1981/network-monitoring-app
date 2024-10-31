package com.example.maliciousdevices.controller;

import com.example.maliciousdevices.service.MaliciousTrafficService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class MaliciousTrafficController {

    @Autowired
    private MaliciousTrafficService maliciousTrafficService;

    @PostMapping("/generateMaliciousTraffic")
    public Mono<ResponseEntity<String>> generateAndSendMaliciousTraffic(@RequestParam String attackType) {
        if (!isValidAttackType(attackType)) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid attack type. Please specify a valid attack type."));
        }
        return maliciousTrafficService.generateAndSendPcapFile(attackType)
                .map(result -> ResponseEntity.ok("Malicious traffic data sent successfully."))
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(500)
                            .body("Error generating malicious traffic: " + e.getMessage()));
                });
    }

    @GetMapping("/generateMaliciousTraffic")
    public Mono<ResponseEntity<String>> generateAndSendMaliciousTrafficGet(@RequestParam String attackType) {
        if (!isValidAttackType(attackType)) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid attack type. Please specify a valid attack type."));
        }
        return maliciousTrafficService.generateAndSendPcapFile(attackType)
                .map(result -> ResponseEntity.ok("Malicious traffic data sent successfully (via GET)."))
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(500)
                            .body("Error generating malicious traffic: " + e.getMessage()));
                });
    }

    private boolean isValidAttackType(String attackType) {
        switch (attackType.toUpperCase()) {
            case "DOS":
            case "BRUTEFORCE":
            case "SQLINJECTION":
            case "XSS":
            case "PORTSCAN":
            case "BOTNET":
            case "DDOS":
            case "INJECTION":
            case "MITM":
            case "PHISHING":
            case "RANSOMWARE":
            case "SPAM":
            case "SPOOFING":
            case "TROJAN":
            case "WORM":
            case "ZERO_DAY":
            case "BACKDOOR":
                return true;
            default:
                return false;
        }
    }
}
