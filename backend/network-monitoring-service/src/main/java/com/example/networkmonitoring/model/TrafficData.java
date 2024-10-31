package com.example.networkmonitoring.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TrafficData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long timestamp;
    private String srcIp;
    private String dstIp;
    private int srcPort;
    private int dstPort;
    private String protocol;
    private String trafficType;

    private double headerLength;
    private double protocolType;
    private double duration;
    private double rate;
    private double srate;
    private double drate;
    private double finFlagNumber;
    private double synFlagNumber;
    private double rstFlagNumber;
    private double pshFlagNumber;
    private double ackFlagNumber;
    private double eceFlagNumber;
    private double cwrFlagNumber;
    private double ackCount;
    private double synCount;
    private double finCount;
    private double rstCount;
    private double http;
    private double https;
    private double dns;
    private double telnet;
    private double smtp;
    private double ssh;
    private double irc;
    private double tcp;
    private double udp;
    private double dhcp;
    private double arp;
    private double icmp;
    private double igmp;
    private double ipv;
    private double llc;
    private double totSum;
    private double min;
    private double max;
    private double avg;
    private double std;
    private double totSize;
    private double iat;
    private double number;
    private double magnitude;
    private double radius;
    private double covariance;
    private double variance;
    private double weight;

    public void setHeaderLength(double headerLength) {
        this.headerLength = headerLength;
    }

    public void setProtocolType(double protocolType) {
        this.protocolType = protocolType;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setSrate(double srate) {
        this.srate = srate;
    }

    public void setDrate(double drate) {
        this.drate = drate;
    }

    public void setFinFlagNumber(double finFlagNumber) {
        this.finFlagNumber = finFlagNumber;
    }

    public void setSynFlagNumber(double synFlagNumber) {
        this.synFlagNumber = synFlagNumber;
    }

    public void setRstFlagNumber(double rstFlagNumber) {
        this.rstFlagNumber = rstFlagNumber;
    }

    public void setPshFlagNumber(double pshFlagNumber) {
        this.pshFlagNumber = pshFlagNumber;
    }

    public void setAckFlagNumber(double ackFlagNumber) {
        this.ackFlagNumber = ackFlagNumber;
    }

    public void setEceFlagNumber(double eceFlagNumber) {
        this.eceFlagNumber = eceFlagNumber;
    }

    public void setCwrFlagNumber(double cwrFlagNumber) {
        this.cwrFlagNumber = cwrFlagNumber;
    }

    public void setAckCount(double ackCount) {
        this.ackCount = ackCount;
    }

    public void setSynCount(double synCount) {
        this.synCount = synCount;
    }

    public void setFinCount(double finCount) {
        this.finCount = finCount;
    }

    public void setRstCount(double rstCount) {
        this.rstCount = rstCount;
    }

    public void setHttp(double http) {
        this.http = http;
    }

    public void setHttps(double https) {
        this.https = https;
    }

    public void setDns(double dns) {
        this.dns = dns;
    }

    public void setTelnet(double telnet) {
        this.telnet = telnet;
    }

    public void setSmtp(double smtp) {
        this.smtp = smtp;
    }

    public void setSsh(double ssh) {
        this.ssh = ssh;
    }

    public void setIrc(double irc) {
        this.irc = irc;
    }

    public void setTcp(double tcp) {
        this.tcp = tcp;
    }

    public void setUdp(double udp) {
        this.udp = udp;
    }

    public void setDhcp(double dhcp) {
        this.dhcp = dhcp;
    }

    public void setArp(double arp) {
        this.arp = arp;
    }

    public void setIcmp(double icmp) {
        this.icmp = icmp;
    }

    public void setIgmp(double igmp) {
        this.igmp = igmp;
    }

    public void setIpv(double ipv) {
        this.ipv = ipv;
    }

    public void setLlc(double llc) {
        this.llc = llc;
    }

    public void setTotSum(double totSum) {
        this.totSum = totSum;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public void setStd(double std) {
        this.std = std;
    }

    public void setTotSize(double totSize) {
        this.totSize = totSize;
    }

    public void setIat(double iat) {
        this.iat = iat;
    }

    public void setNumber(double number) {
        this.number = number;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setCovariance(double covariance) {
        this.covariance = covariance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeaderLength() {
        return headerLength;
    }

    public double getProtocolType() {
        return protocolType;
    }

    public double getDuration() {
        return duration;
    }

    public double getRate() {
        return rate;
    }

    public double getSrate() {
        return srate;
    }

    public double getDrate() {
        return drate;
    }

    public double getFinFlagNumber() {
        return finFlagNumber;
    }

    public double getSynFlagNumber() {
        return synFlagNumber;
    }

    public double getRstFlagNumber() {
        return rstFlagNumber;
    }

    public double getPshFlagNumber() {
        return pshFlagNumber;
    }

    public double getAckFlagNumber() {
        return ackFlagNumber;
    }

    public double getEceFlagNumber() {
        return eceFlagNumber;
    }

    public double getCwrFlagNumber() {
        return cwrFlagNumber;
    }

    public double getAckCount() {
        return ackCount;
    }

    public double getSynCount() {
        return synCount;
    }

    public double getFinCount() {
        return finCount;
    }

    public double getRstCount() {
        return rstCount;
    }

    public double getHttp() {
        return http;
    }

    public double getHttps() {
        return https;
    }

    public double getDns() {
        return dns;
    }

    public double getTelnet() {
        return telnet;
    }

    public double getSmtp() {
        return smtp;
    }

    public double getSsh() {
        return ssh;
    }

    public double getIrc() {
        return irc;
    }

    public double getTcp() {
        return tcp;
    }

    public double getUdp() {
        return udp;
    }

    public double getDhcp() {
        return dhcp;
    }

    public double getArp() {
        return arp;
    }

    public double getIcmp() {
        return icmp;
    }

    public double getIgmp() {
        return igmp;
    }

    public double getIpv() {
        return ipv;
    }

    public double getLlc() {
        return llc;
    }

    public double getTotSum() {
        return totSum;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getAvg() {
        return avg;
    }

    public double getStd() {
        return std;
    }

    public double getTotSize() {
        return totSize;
    }

    public double getIat() {
        return iat;
    }

    public double getNumber() {
        return number;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public double getRadius() {
        return radius;
    }

    public double getCovariance() {
        return covariance;
    }

    public double getVariance() {
        return variance;
    }

    public double getWeight() {
        return weight;
    }

    public TrafficData() {
    }

    public TrafficData(long timestamp, String srcIp, String dstIp, int srcPort, int dstPort, String protocol,
            String trafficType) {
        this.timestamp = timestamp;
        this.srcIp = srcIp;
        this.dstIp = dstIp;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.protocol = protocol;
        this.trafficType = trafficType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    public int getDstPort() {
        return dstPort;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getTrafficType() {
        return trafficType;
    }

    public void setTrafficType(String trafficType) {
        this.trafficType = trafficType;
    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert TrafficData to JSON", e);
        }
    }
}
