package com.example.batchMicroservice;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class TrafficData {

    private Double headerLength;
    private String protocolType;
    private Double duration;
    private Double rate;
    private Double srate;
    private Double drate;
    private Double finFlagNumber;
    private Double synFlagNumber;
    private Double rstFlagNumber;
    private Double pshFlagNumber;
    private Double ackFlagNumber;
    private Double eceFlagNumber;
    private Double cwrFlagNumber;
    private Double ackCount;
    private Double synCount;
    private Double finCount;
    private Double rstCount;
    private Double http;
    private Double https;
    private Double dns;
    private Double telnet;
    private Double smtp;
    private Double ssh;
    private Double irc;
    private Double tcp;
    private Double udp;
    private Double dhcp;
    private Double arp;
    private Double icmp;
    private Double igmp;
    private Double ipv;
    private Double llc;
    private Double totSum;
    private Double min;
    private Double max;
    private Double avg;
    private Double stdDev;
    private Double totSize;
    private Double iat;
    private Double number;
    private Double magnitude;
    private Double radius;
    private Double covariance;
    private Double variance;
    private Double weight;

    // New field for attack type
    private String attackType;

    // Getters and setters for all fields
    public Double getHeaderLength() {
        return headerLength;
    }

    public String getProtocolType() {
        return protocolType;
    }

    public Double getDuration() {
        return duration;
    }

    public Double getRate() {
        return rate;
    }

    public Double getSrate() {
        return srate;
    }

    public Double getDrate() {
        return drate;
    }

    public Double getFinFlagNumber() {
        return finFlagNumber;
    }

    public Double getSynFlagNumber() {
        return synFlagNumber;
    }

    public Double getRstFlagNumber() {
        return rstFlagNumber;
    }

    public Double getPshFlagNumber() {
        return pshFlagNumber;
    }

    public Double getAckFlagNumber() {
        return ackFlagNumber;
    }

    public Double getEceFlagNumber() {
        return eceFlagNumber;
    }

    public Double getCwrFlagNumber() {
        return cwrFlagNumber;
    }

    public Double getAckCount() {
        return ackCount;
    }

    public Double getSynCount() {
        return synCount;
    }

    public Double getFinCount() {
        return finCount;
    }

    public Double getRstCount() {
        return rstCount;
    }

    public Double getHttp() {
        return http;
    }

    public Double getHttps() {
        return https;
    }

    public Double getDns() {
        return dns;
    }

    public Double getTelnet() {
        return telnet;
    }

    public Double getSmtp() {
        return smtp;
    }

    public Double getSsh() {
        return ssh;
    }

    public Double getIrc() {
        return irc;
    }

    public Double getTcp() {
        return tcp;
    }

    public Double getUdp() {
        return udp;
    }

    public Double getDhcp() {
        return dhcp;
    }

    public Double getArp() {
        return arp;
    }

    public Double getIcmp() {
        return icmp;
    }

    public Double getIgmp() {
        return igmp;
    }

    public Double getIpv() {
        return ipv;
    }

    public Double getLlc() {
        return llc;
    }

    public Double getTotSum() {
        return totSum;
    }

    public Double getMin() {
        return min;
    }

    public Double getMax() {
        return max;
    }

    public Double getAvg() {
        return avg;
    }

    public Double getStdDev() {
        return stdDev;
    }

    public Double getTotSize() {
        return totSize;
    }

    public Double getIat() {
        return iat;
    }

    public Double getNumber() {
        return number;
    }

    public Double getMagnitude() {
        return magnitude;
    }

    public Double getRadius() {
        return radius;
    }

    public Double getCovariance() {
        return covariance;
    }

    public Double getVariance() {
        return variance;
    }

    public Double getWeight() {
        return weight;
    }

    public String getAttackType() {
        return attackType;
    }

    public void setHeaderLength(Double headerLength) {
        this.headerLength = headerLength;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public void setSrate(Double srate) {
        this.srate = srate;
    }

    public void setDrate(Double drate) {
        this.drate = drate;
    }

    public void setFinFlagNumber(Double finFlagNumber) {
        this.finFlagNumber = finFlagNumber;
    }

    public void setSynFlagNumber(Double synFlagNumber) {
        this.synFlagNumber = synFlagNumber;
    }

    public void setRstFlagNumber(Double rstFlagNumber) {
        this.rstFlagNumber = rstFlagNumber;
    }

    public void setPshFlagNumber(Double pshFlagNumber) {
        this.pshFlagNumber = pshFlagNumber;
    }

    public void setAckFlagNumber(Double ackFlagNumber) {
        this.ackFlagNumber = ackFlagNumber;
    }

    public void setEceFlagNumber(Double eceFlagNumber) {
        this.eceFlagNumber = eceFlagNumber;
    }

    public void setCwrFlagNumber(Double cwrFlagNumber) {
        this.cwrFlagNumber = cwrFlagNumber;
    }

    public void setAckCount(Double ackCount) {
        this.ackCount = ackCount;
    }

    public void setSynCount(Double synCount) {
        this.synCount = synCount;
    }

    public void setFinCount(Double finCount) {
        this.finCount = finCount;
    }

    public void setRstCount(Double rstCount) {
        this.rstCount = rstCount;
    }

    public void setHttp(Double http) {
        this.http = http;
    }

    public void setHttps(Double https) {
        this.https = https;
    }

    public void setDns(Double dns) {
        this.dns = dns;
    }

    public void setTelnet(Double telnet) {
        this.telnet = telnet;
    }

    public void setSmtp(Double smtp) {
        this.smtp = smtp;
    }

    public void setSsh(Double ssh) {
        this.ssh = ssh;
    }

    public void setIrc(Double irc) {
        this.irc = irc;
    }

    public void setTcp(Double tcp) {
        this.tcp = tcp;
    }

    public void setUdp(Double udp) {
        this.udp = udp;
    }

    public void setDhcp(Double dhcp) {
        this.dhcp = dhcp;
    }

    public void setArp(Double arp) {
        this.arp = arp;
    }

    public void setIcmp(Double icmp) {
        this.icmp = icmp;
    }

    public void setIgmp(Double igmp) {
        this.igmp = igmp;
    }

    public void setIpv(Double ipv) {
        this.ipv = ipv;
    }

    public void setLlc(Double llc) {
        this.llc = llc;
    }

    public void setTotSum(Double totSum) {
        this.totSum = totSum;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }

    public void setStdDev(Double stdDev) {
        this.stdDev = stdDev;
    }

    public void setTotSize(Double totSize) {
        this.totSize = totSize;
    }

    public void setIat(Double iat) {
        this.iat = iat;
    }

    public void setNumber(Double number) {
        this.number = number;
    }

    public void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public void setCovariance(Double covariance) {
        this.covariance = covariance;
    }

    public void setVariance(Double variance) {
        this.variance = variance;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setAttackType(String attackType) {
        this.attackType = attackType;
    }
}
