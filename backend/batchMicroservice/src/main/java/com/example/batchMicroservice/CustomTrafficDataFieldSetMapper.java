package com.example.batchMicroservice;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.Resource;
import org.springframework.validation.BindException;

import java.util.function.Supplier;

public class CustomTrafficDataFieldSetMapper implements FieldSetMapper<TrafficData> {

    private final String attackType;

    public CustomTrafficDataFieldSetMapper(String attackType) {
        this.attackType = attackType;
    }

    @Override
    public TrafficData mapFieldSet(FieldSet fieldSet) throws BindException {
        TrafficData data = new TrafficData();

        data.setHeaderLength(fieldSet.readDouble("Header_Length"));
        data.setProtocolType(fieldSet.readString("Protocol Type"));
        data.setDuration(fieldSet.readDouble("Duration"));
        data.setRate(fieldSet.readDouble("Rate"));
        data.setSrate(fieldSet.readDouble("Srate"));
        data.setDrate(fieldSet.readDouble("Drate"));
        data.setFinFlagNumber(fieldSet.readDouble("fin_flag_number"));
        data.setSynFlagNumber(fieldSet.readDouble("syn_flag_number"));
        data.setRstFlagNumber(fieldSet.readDouble("rst_flag_number"));
        data.setPshFlagNumber(fieldSet.readDouble("psh_flag_number"));
        data.setAckFlagNumber(fieldSet.readDouble("ack_flag_number"));
        data.setEceFlagNumber(fieldSet.readDouble("ece_flag_number"));
        data.setCwrFlagNumber(fieldSet.readDouble("cwr_flag_number"));
        data.setAckCount(fieldSet.readDouble("ack_count"));
        data.setSynCount(fieldSet.readDouble("syn_count"));
        data.setFinCount(fieldSet.readDouble("fin_count"));
        data.setRstCount(fieldSet.readDouble("rst_count"));
        data.setHttp(fieldSet.readDouble("HTTP"));
        data.setHttps(fieldSet.readDouble("HTTPS"));
        data.setDns(fieldSet.readDouble("DNS"));
        data.setTelnet(fieldSet.readDouble("Telnet"));
        data.setSmtp(fieldSet.readDouble("SMTP"));
        data.setSsh(fieldSet.readDouble("SSH"));
        data.setIrc(fieldSet.readDouble("IRC"));
        data.setTcp(fieldSet.readDouble("TCP"));
        data.setUdp(fieldSet.readDouble("UDP"));
        data.setDhcp(fieldSet.readDouble("DHCP"));
        data.setArp(fieldSet.readDouble("ARP"));
        data.setIcmp(fieldSet.readDouble("ICMP"));
        data.setIgmp(fieldSet.readDouble("IGMP"));
        data.setIpv(fieldSet.readDouble("IPv"));
        data.setLlc(fieldSet.readDouble("LLC"));
        data.setTotSum(fieldSet.readDouble("Tot sum"));
        data.setMin(fieldSet.readDouble("Min"));
        data.setMax(fieldSet.readDouble("Max"));
        data.setAvg(fieldSet.readDouble("AVG"));
        data.setStdDev(fieldSet.readDouble("Std"));
        data.setTotSize(fieldSet.readDouble("Tot size"));
        data.setIat(fieldSet.readDouble("IAT"));
        data.setNumber(fieldSet.readDouble("Number"));
        data.setMagnitude(fieldSet.readDouble("Magnitue"));
        data.setRadius(fieldSet.readDouble("Radius"));
        data.setCovariance(fieldSet.readDouble("Covariance"));
        data.setVariance(fieldSet.readDouble("Variance"));
        data.setWeight(fieldSet.readDouble("Weight"));

        // Set the attack type derived from the file name
        data.setAttackType(attackType); // Set attack type

        return data;
    }
}
