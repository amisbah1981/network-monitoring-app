package com.example.maliciousdevices.service;

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
public class MaliciousTrafficService {

    private static final Logger logger = LoggerFactory.getLogger(MaliciousTrafficService.class);
    private final WebClient webClient;

    public MaliciousTrafficService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081")
            .defaultHeaders(headers -> headers.setBasicAuth("admin", "admin"))
            .build();
    }

    public Mono<String> generateAndSendPcapFile(String trafficType) {
    return Mono.fromCallable(() -> {
        Path tempPcapFile = null;
        PcapHandle handle = null;

        try {
            tempPcapFile = Files.createTempFile("malicious_traffic", ".pcap");
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

    private Mono<String> sendPcapFile(File pcapFile, String attackType) {
        logger.debug("Sending PCAP file with attack type: {}", attackType);
        return webClient.post()
                .uri("/monitor")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(fromFile(pcapFile, attackType)))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> {
                    logger.error("Error sending PCAP file: {}", e.getMessage());
                    e.printStackTrace();
                });
    }
    
    private MultiValueMap<String, HttpEntity<?>> fromFile(File file, String attackType) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        
        // Attach packetData file part
        builder.part("packetData", new FileSystemResource(file));
        
        // Attach trafficType part as a string
        builder.part("trafficType", attackType);
    
        return builder.build();
    }       
   
    private Packet createSyntheticPacket(String attackType) {
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

            switch (attackType.toUpperCase()) {
                case "DOS":
                    return createDoSPacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "BRUTEFORCE":
                    return createBruteForcePacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "SQLINJECTION":
                    return createSQLInjectionPacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "XSS":
                    return createXSSPacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "PORTSCAN":
                    return createPortScanPacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "BOTNET":
                    return createBotnetPacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "DDOS":
                    return createDDoSPacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "INJECTION":
                    return createInjectionPacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "MITM":
                    return createMITMPacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "PHISHING":
                    return createPhishingPacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "RANSOMWARE":
                    return createRansomwarePacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "SPAM":
                    return createSpamPacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "SPOOFING":
                    return createSpoofingPacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "TROJAN":
                    return createTrojanPacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "WORM":
                    return createWormPacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "ZERO_DAY":
                    return createZeroDayPacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                case "BACKDOOR":
                    return createBackdoorPacket(etherBuilder, ipV4Builder, srcAddr, dstAddr);
                default:
                    logger.warn("Unsupported attack type: {}", attackType);
                    return null;
            }
        } catch (Exception e) {
            logger.error("Error building packet: ", e);
            return null;
        }
    }

    private Packet createDoSPacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("DoS Attack Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createBruteForcePacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 22))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("Brute Force Attack Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createSQLInjectionPacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("SQL Injection Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createXSSPacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("XSS Attack Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createPortScanPacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("Port Scan Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createBotnetPacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("Botnet Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createDDoSPacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("DDoS Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createInjectionPacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("Injection Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createMITMPacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("MITM Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createPhishingPacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("Phishing Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createRansomwarePacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("Ransomware Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createSpamPacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("Spam Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createSpoofingPacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("Spoofing Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createTrojanPacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("Trojan Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createWormPacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("Worm Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createZeroDayPacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("Zero Day Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }

    private Packet createBackdoorPacket(EthernetPacket.Builder etherBuilder, IpV4Packet.Builder ipV4Builder, 
            Inet4Address srcAddr, Inet4Address dstAddr) {
        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder()
            .srcPort(TcpPort.HTTP)
            .dstPort(TcpPort.getInstance((short) 80))
            .sequenceNumber(100)
            .acknowledgmentNumber(0)
            .dataOffset((byte) 5)
            .window((short) 1024)
            .srcAddr(srcAddr)
            .dstAddr(dstAddr)
            .correctChecksumAtBuild(true)
            .correctLengthAtBuild(true)
            .payloadBuilder(new UnknownPacket.Builder().rawData("Backdoor Payload".getBytes()));

        ipV4Builder.protocol(IpNumber.TCP)
                   .payloadBuilder(tcpBuilder);

        etherBuilder.payloadBuilder(ipV4Builder);
        return etherBuilder.build();
    }
}
