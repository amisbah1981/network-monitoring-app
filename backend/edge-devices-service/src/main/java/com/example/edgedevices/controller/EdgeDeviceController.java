package com.example.edgedevices.controller;

import com.example.edgedevices.service.EdgeDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class EdgeDeviceController {

    @Autowired
    private EdgeDeviceService edgeDeviceService;

    @PostMapping("/generateTraffic")
    public Mono<ResponseEntity<String>> generateAndSendTraffic(@RequestParam String trafficType) {
        if (!trafficType.equalsIgnoreCase("TCP") && !trafficType.equalsIgnoreCase("UDP")) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid traffic type. Please specify 'TCP' or 'UDP'."));
        }
        return edgeDeviceService.generateAndSendPcapFile(trafficType)
                .map(result -> ResponseEntity.ok("Traffic data sent successfully."))
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(500)
                            .body("Error generating traffic: " + e.getMessage()));
                });
    }

    @GetMapping("/generateTraffic")
    public Mono<ResponseEntity<String>> generateAndSendTrafficGet(@RequestParam String trafficType) {
        if (!trafficType.equalsIgnoreCase("TCP") && !trafficType.equalsIgnoreCase("UDP")) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid traffic type. Please specify 'TCP' or 'UDP'."));
        }
        return edgeDeviceService.generateAndSendPcapFile(trafficType)
                .map(result -> ResponseEntity.ok("Traffic data sent successfully (via GET)."))
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(500)
                            .body("Error generating traffic: " + e.getMessage()));
                });
    }
}
