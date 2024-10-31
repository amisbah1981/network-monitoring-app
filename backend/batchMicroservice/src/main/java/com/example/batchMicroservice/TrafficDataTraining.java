package com.example.batchMicroservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "traffic_data_training")
public class TrafficDataTraining extends TrafficData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Getters and setters for ID
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TrafficDataTraining(TrafficData data, String attackType) {
        // Copy all necessary fields from TrafficData
        this.setHeaderLength(data.getHeaderLength());
        this.setProtocolType(data.getProtocolType());
        this.setDuration(data.getDuration());
        this.setRate(data.getRate());
        this.setSrate(data.getSrate());
        this.setDrate(data.getDrate());
        this.setFinFlagNumber(data.getFinFlagNumber());
        this.setSynFlagNumber(data.getSynFlagNumber());
        this.setRstFlagNumber(data.getRstFlagNumber());
        this.setPshFlagNumber(data.getPshFlagNumber());
        this.setAckFlagNumber(data.getAckFlagNumber());
        this.setEceFlagNumber(data.getEceFlagNumber());
        this.setCwrFlagNumber(data.getCwrFlagNumber());
        this.setAckCount(data.getAckCount());
        this.setSynCount(data.getSynCount());
        this.setFinCount(data.getFinCount());
        this.setRstCount(data.getRstCount());
        this.setHttp(data.getHttp());
        this.setHttps(data.getHttps());
        this.setDns(data.getDns());
        this.setTelnet(data.getTelnet());
        this.setSmtp(data.getSmtp());
        this.setSsh(data.getSsh());
        this.setIrc(data.getIrc());
        this.setTcp(data.getTcp());
        this.setUdp(data.getUdp());
        this.setDhcp(data.getDhcp());
        this.setArp(data.getArp());
        this.setIcmp(data.getIcmp());
        this.setIgmp(data.getIgmp());
        this.setIpv(data.getIpv());
        this.setLlc(data.getLlc());
        this.setTotSum(data.getTotSum());
        this.setMin(data.getMin());
        this.setMax(data.getMax());
        this.setAvg(data.getAvg());
        this.setStdDev(data.getStdDev());
        this.setTotSize(data.getTotSize());
        this.setIat(data.getIat());
        this.setNumber(data.getNumber());
        this.setMagnitude(data.getMagnitude());
        this.setRadius(data.getRadius());
        this.setCovariance(data.getCovariance());
        this.setVariance(data.getVariance());
        this.setWeight(data.getWeight());
        this.setAttackType(attackType);
        // Add any additional fields specific to TrafficDataTraining if needed
    }
}
