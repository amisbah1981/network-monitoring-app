package com.example.networkmonitoring.service;

import com.example.networkmonitoring.model.TrafficData;
import com.example.networkmonitoring.repository.TrafficDataRepository;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.EtherType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class NetworkMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(NetworkMonitoringService.class);
    private static final String EDGE_TRAFFIC_TOPIC = "edge-traffic";
    private static final String MALICIOUS_TRAFFIC_TOPIC = "malicious-traffic";
    private static final String[] TRAFFIC_CLASSES = {
            "ARP_Spoofing", "Benign", "MQTT-DDoS-Connect_Flood", "MQTT-DDoS-Publish_Flood",
            "MQTT-DoS-Connect_Flood", "MQTT-DoS-Publish_Flood", "MQTT-Malformed_Data",
            "Recon-OS_Scan", "Recon-Ping_Sweep", "Recon-Port_Scan", "Recon-VulScan",
            "TCP_IP-DDoS-ICMP", "TCP_IP-DDoS-SYN", "TCP_IP-DDoS-TCP", "TCP_IP-DDoS-UDP",
            "TCP_IP-DoS-ICMP", "TCP_IP-DoS-SYN", "TCP_IP-DoS-TCP", "TCP_IP-DoS-UDP"
    };

    @Autowired
    private TrafficDataRepository trafficDataRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final Random random = new Random();

    public Mono<String> processAndStorePackets(MultipartFile file, String trafficType) {
        return Mono.fromCallable(() -> {
            try {
                Path tempFile = Files.createTempFile("uploaded_packet_data", ".pcap");
                file.transferTo(tempFile);

                return processPackets(tempFile.toFile(), trafficType)
                        .doFinally(signal -> {
                            try {
                                Files.deleteIfExists(tempFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            } catch (IOException e) {
                throw new RuntimeException("Failed to process file upload", e);
            }
        }).flatMap(mono -> mono);
    }

    private Mono<String> processPackets(File pcapFile, String trafficType) {
        return Mono.fromCallable(() -> {
            try (PcapHandle handle = Pcaps.openOffline(pcapFile.getAbsolutePath())) {
                List<TrafficData> trafficDataList = new ArrayList<>();
                Packet packet;

                while ((packet = handle.getNextPacket()) != null) {
                    TrafficData trafficData = parsePacket(packet, trafficType);
                    if (trafficData != null) {
                        sendToKafka(trafficData, trafficType);
                        trafficDataList.add(trafficData);
                    }
                }

                if (!trafficDataList.isEmpty()) {
                    trafficDataRepository.saveAll(trafficDataList);
                }
                return "Success";
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<TrafficData> getAllTrafficData() {
        return Flux.fromIterable(trafficDataRepository.findAll())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> generateSyntheticData(int numRecords) {
        return Mono.fromCallable(() -> {
            List<TrafficData> syntheticDataList = new ArrayList<>();
            for (int i = 0; i < numRecords; i++) {
                TrafficData syntheticData = generateSyntheticTrafficData();
                syntheticDataList.add(syntheticData);
                sendToKafka(syntheticData, syntheticData.getTrafficType());
            }
            trafficDataRepository.saveAll(syntheticDataList);
            return "Synthetic Data Generation Success";
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private TrafficData generateSyntheticTrafficData() {
        TrafficData syntheticData = new TrafficData();

        syntheticData.setTimestamp(System.currentTimeMillis());
        syntheticData.setSrcIp(generateRandomIp());
        syntheticData.setDstIp(generateRandomIp());
        syntheticData.setSrcPort(random.nextInt(65535));
        syntheticData.setDstPort(random.nextInt(65535));
        syntheticData.setProtocol(random.nextBoolean() ? "TCP" : "UDP");
        syntheticData.setTrafficType(TRAFFIC_CLASSES[random.nextInt(TRAFFIC_CLASSES.length)]);

        // Set synthetic data for all 45 features
        syntheticData.setHeaderLength(random.nextDouble() * 200);
        syntheticData.setProtocolType(random.nextDouble() * 10);
        syntheticData.setDuration(random.nextDouble() * 100);
        syntheticData.setRate(random.nextDouble() * 50000);
        syntheticData.setSrate(random.nextDouble() * 50000);
        syntheticData.setDrate(random.nextDouble() * 50000);
        syntheticData.setFinFlagNumber(random.nextDouble() * 10);
        syntheticData.setSynFlagNumber(random.nextDouble() * 10);
        syntheticData.setRstFlagNumber(random.nextDouble() * 10);
        syntheticData.setPshFlagNumber(random.nextDouble() * 10);
        syntheticData.setAckFlagNumber(random.nextDouble() * 10);
        syntheticData.setEceFlagNumber(random.nextDouble() * 10);
        syntheticData.setCwrFlagNumber(random.nextDouble() * 10);
        syntheticData.setAckCount(random.nextDouble() * 20);
        syntheticData.setSynCount(random.nextDouble() * 20);
        syntheticData.setFinCount(random.nextDouble() * 20);
        syntheticData.setRstCount(random.nextDouble() * 20);
        syntheticData.setHttp(random.nextDouble());
        syntheticData.setHttps(random.nextDouble());
        syntheticData.setDns(random.nextDouble());
        syntheticData.setTelnet(random.nextDouble());
        syntheticData.setSmtp(random.nextDouble());
        syntheticData.setSsh(random.nextDouble());
        syntheticData.setIrc(random.nextDouble());
        syntheticData.setTcp(random.nextDouble());
        syntheticData.setUdp(random.nextDouble());
        syntheticData.setDhcp(random.nextDouble());
        syntheticData.setArp(random.nextDouble());
        syntheticData.setIcmp(random.nextDouble());
        syntheticData.setIgmp(random.nextDouble());
        syntheticData.setIpv(random.nextDouble());
        syntheticData.setLlc(random.nextDouble());
        syntheticData.setTotSum(random.nextDouble() * 1000);
        syntheticData.setMin(random.nextDouble() * 100);
        syntheticData.setMax(random.nextDouble() * 100);
        syntheticData.setAvg(random.nextDouble() * 100);
        syntheticData.setStd(random.nextDouble() * 50);
        syntheticData.setTotSize(random.nextDouble() * 500);
        syntheticData.setIat(random.nextDouble() * 100);
        syntheticData.setNumber(random.nextDouble() * 20);
        syntheticData.setMagnitude(random.nextDouble() * 20);
        syntheticData.setRadius(random.nextDouble() * 300);
        syntheticData.setCovariance(random.nextDouble() * 50000);
        syntheticData.setVariance(random.nextDouble() * 10);
        syntheticData.setWeight(random.nextDouble() * 300);

        return syntheticData;
    }

    private String generateRandomIp() {
        return random.nextInt(255) + "." + random.nextInt(255) + "." +
                random.nextInt(255) + "." + random.nextInt(255);
    }

    private void sendToKafka(TrafficData trafficData, String trafficType) {
        String trafficDataStr = trafficData.toString();
        logger.debug("Sending traffic data to Kafka");
        if (trafficType.equals("TCP") || trafficType.equals("UDP")) {
            kafkaTemplate.send(EDGE_TRAFFIC_TOPIC, trafficDataStr);
            logger.debug("Sent edge traffic data to Kafka topic '{}'", EDGE_TRAFFIC_TOPIC);
        } else {
            kafkaTemplate.send(MALICIOUS_TRAFFIC_TOPIC, trafficDataStr);
            logger.debug("Sent malicious traffic data to Kafka topic '{}'", MALICIOUS_TRAFFIC_TOPIC);
        }
    }

    private TrafficData parsePacket(Packet packet, String trafficType) {
        if (packet.contains(EthernetPacket.class)) {
            return parseEthernetPacket(packet.get(EthernetPacket.class), trafficType);
        } else if (packet.contains(IpV4Packet.class)) {
            return parseIpPacket(packet.get(IpV4Packet.class), trafficType);
        } else if (packet.contains(IpV6Packet.class)) {
            return parseIpPacket(packet.get(IpV6Packet.class), trafficType);
        } else if (packet.contains(ArpPacket.class)) {
            return parseArpPacket(packet.get(ArpPacket.class), trafficType);
        } else {
            logger.warn("Unsupported packet type: {}", packet.getClass().getSimpleName());
            return null;
        }
    }

    private TrafficData parseEthernetPacket(EthernetPacket ethernetPacket, String trafficType) {
        logger.debug("Parsing Ethernet packet: {}", ethernetPacket);
        EtherType etherType = ethernetPacket.getHeader().getType();
        logger.debug("EtherType: {}", etherType);

        if (etherType == null) {
            logger.warn("EtherType is null, packet might be malformed: {}", ethernetPacket);
            return null;
        }

        if (etherType.equals(EtherType.IPV4)) {
            IpV4Packet ipPacket = ethernetPacket.get(IpV4Packet.class);
            if (ipPacket == null) {
                logger.error("Failed to parse IPv4 packet from Ethernet packet: {}", ethernetPacket);
                return null;
            }
            return parseIpPacket(ipPacket, trafficType);
        } else if (etherType.equals(EtherType.IPV6)) {
            IpV6Packet ipPacket = ethernetPacket.get(IpV6Packet.class);
            if (ipPacket == null) {
                logger.error("Failed to parse IPv6 packet from Ethernet packet: {}", ethernetPacket);
                return null;
            }
            return parseIpPacket(ipPacket, trafficType);
        } else if (etherType.equals(EtherType.ARP)) {
            ArpPacket arpPacket = ethernetPacket.get(ArpPacket.class);
            if (arpPacket == null) {
                logger.error("Failed to parse ARP packet from Ethernet packet: {}", ethernetPacket);
                return null;
            }
            return parseArpPacket(arpPacket, trafficType);
        } else {
            logger.warn("Unsupported EtherType: {}", etherType);
            return null;
        }
    }

    private TrafficData parseIpPacket(IpV4Packet ipPacket, String trafficType) {
        if (ipPacket == null) {
            logger.error("IpV4Packet is null, cannot parse IP packet.");
            return null;
        }
        logger.debug("Parsing IP packet: {}", ipPacket);
        String srcIp = ipPacket.getHeader().getSrcAddr().getHostAddress();
        String dstIp = ipPacket.getHeader().getDstAddr().getHostAddress();
        int srcPort = -1;
        int dstPort = -1;
        String protocol = "Unknown";

        if (ipPacket.contains(TcpPacket.class)) {
            TcpPacket tcpPacket = ipPacket.get(TcpPacket.class);
            srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
            dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();
            protocol = "TCP";
            logger.debug("Parsed TCP packet: {}", tcpPacket);
        } else if (ipPacket.contains(UdpPacket.class)) {
            UdpPacket udpPacket = ipPacket.get(UdpPacket.class);
            srcPort = udpPacket.getHeader().getSrcPort().valueAsInt();
            dstPort = udpPacket.getHeader().getDstPort().valueAsInt();
            protocol = "UDP";
            logger.debug("Parsed UDP packet: {}", udpPacket);
        } else if (ipPacket.contains(UnknownPacket.class)) {
            UnknownPacket unknownPacket = ipPacket.get(UnknownPacket.class);
            logger.debug("Parsed Unknown packet: {}", unknownPacket);
        }

        logger.debug("Parsed IP packet: srcIp={}, dstIp={}, srcPort={}, dstPort={}, protocol={}",
                srcIp, dstIp, srcPort, dstPort, protocol);
        return new TrafficData(System.currentTimeMillis(), srcIp, dstIp, srcPort, dstPort, protocol, trafficType);
    }

    private TrafficData parseIpPacket(IpV6Packet ipPacket, String trafficType) {
        if (ipPacket == null) {
            logger.error("IpV6Packet is null, cannot parse IP packet.");
            return null;
        }
        logger.debug("Parsing IP packet: {}", ipPacket);
        String srcIp = ipPacket.getHeader().getSrcAddr().getHostAddress();
        String dstIp = ipPacket.getHeader().getDstAddr().getHostAddress();
        int srcPort = -1;
        int dstPort = -1;
        String protocol = "Unknown";

        if (ipPacket.contains(TcpPacket.class)) {
            TcpPacket tcpPacket = ipPacket.get(TcpPacket.class);
            srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
            dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();
            protocol = "TCP";
            logger.debug("Parsed TCP packet: {}", tcpPacket);
        } else if (ipPacket.contains(UdpPacket.class)) {
            UdpPacket udpPacket = ipPacket.get(UdpPacket.class);
            srcPort = udpPacket.getHeader().getSrcPort().valueAsInt();
            dstPort = udpPacket.getHeader().getDstPort().valueAsInt();
            protocol = "UDP";
            logger.debug("Parsed UDP packet: {}", udpPacket);
        } else if (ipPacket.contains(UnknownPacket.class)) {
            UnknownPacket unknownPacket = ipPacket.get(UnknownPacket.class);
            logger.debug("Parsed Unknown packet: {}", unknownPacket);
        }

        logger.debug("Parsed IP packet: srcIp={}, dstIp={}, srcPort={}, dstPort={}, protocol={}",
                srcIp, dstIp, srcPort, dstPort, protocol);
        return new TrafficData(System.currentTimeMillis(), srcIp, dstIp, srcPort, dstPort, protocol, trafficType);
    }

    private TrafficData parseArpPacket(ArpPacket arpPacket, String trafficType) {
        if (arpPacket == null) {
            logger.error("ArpPacket is null, cannot parse ARP packet.");
            return null;
        }
        logger.debug("Parsing ARP packet: {}", arpPacket);
        String srcIp = arpPacket.getHeader().getSrcProtocolAddr().getHostAddress();
        String dstIp = arpPacket.getHeader().getDstProtocolAddr().getHostAddress();
        String protocol = "ARP";

        logger.debug("Parsed ARP packet: srcIp={}, dstIp={}, protocol={}", srcIp, dstIp, protocol);
        return new TrafficData(System.currentTimeMillis(), srcIp, dstIp, -1, -1, protocol, trafficType);
    }
}
