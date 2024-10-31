package com.example.maliciousdevices.model;

public class TrafficData {

    private String deviceId;
    private String trafficType;
    private String protocol;
    private long timestamp;

    // CICIoMT2024 features
    private double headerLength;
    private int protocolType;
    private double duration;
    private double rate;
    private double srate;
    private double drate;
    private int finFlagNumber;
    private int synFlagNumber;
    private int rstFlagNumber;
    private int pshFlagNumber;
    private double std;
    private double totSize;
    private double iat;
    private int number;
    private double magnitue;
    private double radius;
    private double covariance;
    private double variance;
    private double weight;
    private String attackType;
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getTrafficType() {
        return trafficType;
    }
    public void setTrafficType(String trafficType) {
        this.trafficType = trafficType;
    }
    public String getProtocol() {
        return protocol;
    }
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public double getHeaderLength() {
        return headerLength;
    }
    public void setHeaderLength(double headerLength) {
        this.headerLength = headerLength;
    }
    public int getProtocolType() {
        return protocolType;
    }
    public void setProtocolType(int protocolType) {
        this.protocolType = protocolType;
    }
    public double getDuration() {
        return duration;
    }
    public void setDuration(double duration) {
        this.duration = duration;
    }
    public double getRate() {
        return rate;
    }
    public void setRate(double rate) {
        this.rate = rate;
    }
    public double getSrate() {
        return srate;
    }
    public void setSrate(double srate) {
        this.srate = srate;
    }
    public double getDrate() {
        return drate;
    }
    public void setDrate(double drate) {
        this.drate = drate;
    }
    public int getFinFlagNumber() {
        return finFlagNumber;
    }
    public void setFinFlagNumber(int finFlagNumber) {
        this.finFlagNumber = finFlagNumber;
    }
    public int getSynFlagNumber() {
        return synFlagNumber;
    }
    public void setSynFlagNumber(int synFlagNumber) {
        this.synFlagNumber = synFlagNumber;
    }
    public int getRstFlagNumber() {
        return rstFlagNumber;
    }
    public void setRstFlagNumber(int rstFlagNumber) {
        this.rstFlagNumber = rstFlagNumber;
    }
    public int getPshFlagNumber() {
        return pshFlagNumber;
    }
    public void setPshFlagNumber(int pshFlagNumber) {
        this.pshFlagNumber = pshFlagNumber;
    }
    public double getStd() {
        return std;
    }
    public void setStd(double std) {
        this.std = std;
    }
    public double getTotSize() {
        return totSize;
    }
    public void setTotSize(double totSize) {
        this.totSize = totSize;
    }
    public double getIat() {
        return iat;
    }
    public void setIat(double iat) {
        this.iat = iat;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public double getMagnitue() {
        return magnitue;
    }
    public void setMagnitue(double magnitue) {
        this.magnitue = magnitue;
    }
    public double getRadius() {
        return radius;
    }
    public void setRadius(double radius) {
        this.radius = radius;
    }
    public double getCovariance() {
        return covariance;
    }
    public void setCovariance(double covariance) {
        this.covariance = covariance;
    }
    public double getVariance() {
        return variance;
    }
    public void setVariance(double variance) {
        this.variance = variance;
    }
    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    public String getAttackType() {
        return attackType;
    }
    public void setAttackType(String attackType) {
        this.attackType = attackType;
    }

    //

}
