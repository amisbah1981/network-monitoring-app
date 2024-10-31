package com.example.edgedevices.service;

import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.*;
import org.pcap4j.util.MacAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class EdgeDeviceService {

    private static final Logger logger = LoggerFactory.getLogger(EdgeDeviceService.class);
    private final WebClient webClient;

    public EdgeDeviceService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081")
            .defaultHeaders(headers -> headers.setBasicAuth("admin", "admin"))
            .build();
    }

    public Mono<String> generateAndSendPcapFile(String trafficType) {
        return Mono.fromCallable(() -> {
            Path tempPcapFile = null;
            PcapHandle handle = null;

            try {
                tempPcapFile = Files.createTempFile("traffic", ".pcap");
                handle = Pcaps.openDead(DataLinkType.EN10MB, 65536);
                
                try (PcapDumper dumper = handle.dumpOpen(tempPcapFile.toString())) {
                    Packet packet = createSyntheticPacket(trafficType);
                    if (packet != null) {
                        dumper.dump(packet, new java.sql.Timestamp(System.currentTimeMillis()));
                        return tempPcapFile;
                    }
                    return null;
                }
            } finally {
                if (handle != null) {
                    handle.close();
                }
            }
        })
        .flatMap(path -> {
            if (path == null) {
                return Mono.just("Packet creation failed");
            }
            return sendPcapFile(path.toFile(), trafficType)
                    .doFinally(signalType -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            logger.error("Failed to delete temporary file", e);
                        }
                    });
        })
        .subscribeOn(Schedulers.boundedElastic());
    }
 
    private Mono<String> sendPcapFile(File pcapFile, String trafficType) {
        logger.debug("Sending PCAP file with traffic type: {}", trafficType);
        return webClient.post()
                .uri("/monitor")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(fromFile(pcapFile, trafficType)))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> {
                    logger.error("Error sending PCAP file: {}", e.getMessage());
                    e.printStackTrace();
                });
    }
    
    private MultiValueMap<String, HttpEntity<?>> fromFile(File file, String trafficType) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        
        // Attach the pcap file part with the name 'packetData'
        builder.part("packetData", new FileSystemResource(file));
        
        // Attach trafficType as a simple string part
        builder.part("trafficType", trafficType);
    
        return builder.build();
    }
    
    private Packet createSyntheticPacket(String trafficType) {
        try {
            MacAddress srcMac = MacAddress.getByName("aa:bb:cc:dd:ee:ff");
            MacAddress dstMac = MacAddress.getByName("ff:ee:dd:cc:bb:aa");
            Inet4Address srcAddr = (Inet4Address) InetAddress.getByName("192.168.1.1");
            Inet4Address dstAddr = (Inet4Address) InetAddress.getByName("192.168.1.2");

            EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder()
                .srcAddr(srcMac)
                .dstAddr(dstMac)
                .type(EtherType.IPV4)
                .paddingAtBuild(true);

            IpV4Packet.Builder ipV4Builder = new IpV4Packet.Builder()
                .version(IpVersion.IPV4)
                .tos(IpV4Rfc791Tos.newInstance((byte) 0))
                .ttl((byte) 64)
                .srcAddr(srcAddr)
                .dstAddr(dstAddr)
                .protocol(IpNumber.TCP)
                .correctChecksumAtBuild(true)
                .correctLengthAtBuild(true);

            if ("TCP".equalsIgnoreCase(trafficType)) {
                TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
                    .srcPort(TcpPort.HTTP)
                    .dstPort(TcpPort.getInstance((short) 8080))
                    .sequenceNumber(100)
                    .acknowledgmentNumber(0)
                    .dataOffset((byte) 5)
                    .window((short) 1024)
                    .srcAddr(srcAddr)
                    .dstAddr(dstAddr)
                    .correctChecksumAtBuild(true)
                    .correctLengthAtBuild(true)
                    .payloadBuilder(new UnknownPacket.Builder().rawData("Sample TCP Payload".getBytes()));

                ipV4Builder.protocol(IpNumber.TCP)
                           .payloadBuilder(tcpBuilder);
            } else if ("UDP".equalsIgnoreCase(trafficType)) {
                UdpPacket.Builder udpBuilder = new UdpPacket.Builder()
                    .srcPort(UdpPort.DOMAIN)
                    .dstPort(UdpPort.DOMAIN)
                    .srcAddr(srcAddr)
                    .dstAddr(dstAddr)
                    .correctChecksumAtBuild(true)
                    .correctLengthAtBuild(true)
                    .payloadBuilder(new UnknownPacket.Builder().rawData("Sample UDP Payload".getBytes()));

                ipV4Builder.protocol(IpNumber.UDP)
                           .payloadBuilder(udpBuilder);
            } else {
                logger.warn("Unsupported traffic type: {}", trafficType);
                return null;
            }

            etherBuilder.payloadBuilder(ipV4Builder);
            return etherBuilder.build();

        } catch (Exception e) {
            logger.error("Error building packet: ", e);
            return null;
        }
    }
}
