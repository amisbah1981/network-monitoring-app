package com.example.networkmonitoring.controller;

import com.example.networkmonitoring.model.TrafficData;
import com.example.networkmonitoring.service.NetworkMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class NetworkMonitoringServiceController {

    private static final Logger logger = LoggerFactory.getLogger(NetworkMonitoringServiceController.class);

    @Autowired
    private NetworkMonitoringService networkMonitoringService;

    @PostMapping(value = "/monitor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> handlePcapFileUpload(@RequestParam("packetData") MultipartFile packetData,
                                            @RequestParam("trafficType") String trafficType) {
        logger.debug("Received PCAP file with traffic type: {}", trafficType);
        return networkMonitoringService.processAndStorePackets(packetData, trafficType);
    }

    @GetMapping(value = "/traffic-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<TrafficData> getAllTrafficData() {
        return networkMonitoringService.getAllTrafficData();
    }
}
