package com.example.batchMicroservice;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.Resource;

public class TrackingMultiResourceItemReader extends MultiResourceItemReader<TrafficData> {

    private Resource currentResource;
    private MeterRegistry meterRegistry;
    private Timer.Sample timerSample;
    private int rowCount;
    private String currentAttackType;

    private String extractAttackType(String fileName) {
        return fileName.replace(".csv", "");
    }

    public TrackingMultiResourceItemReader(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        FlatFileItemReader<TrafficData> delegate = new FlatFileItemReader<>() {
            @Override
            public void setResource(Resource resource) {
                currentResource = resource; // Track the current resource
                currentAttackType = extractAttackType(resource.getFilename());
                super.setResource(resource);
                timerSample = Timer.start(meterRegistry);
                rowCount = 0;
            }
        };

        // Set the LineMapper for parsing TrafficData
        DefaultLineMapper<TrafficData> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();

        // Define the column names as per CSV structure
        tokenizer.setNames("Header_Length", "Protocol Type", "Duration", "Rate", "Srate", "Drate",
                "fin_flag_number", "syn_flag_number", "rst_flag_number", "psh_flag_number",
                "ack_flag_number", "ece_flag_number", "cwr_flag_number", "ack_count",
                "syn_count", "fin_count", "rst_count", "HTTP", "HTTPS", "DNS", "Telnet",
                "SMTP", "SSH", "IRC", "TCP", "UDP", "DHCP", "ARP", "ICMP", "IGMP",
                "IPv", "LLC", "Tot sum", "Min", "Max", "AVG", "Std", "Tot size",
                "IAT", "Number", "Magnitue", "Radius", "Covariance", "Variance", "Weight");

        lineMapper.setFieldSetMapper(new CustomTrafficDataFieldSetMapper(currentAttackType));
        lineMapper.setLineTokenizer(tokenizer);

        // FieldSetMapper
        delegate.setLineMapper(lineMapper);

        delegate.setLinesToSkip(1); // Skip the header row
        this.setDelegate(delegate);
    }

    public Resource getCurrentResource() {
        return currentResource;
    }

    public String getCurrentAttackType() {
        return currentAttackType;
    }

    @Override
    public TrafficData read() throws Exception {
        TrafficData item = super.read();
        if (item != null) {
            rowCount++;
            meterRegistry.counter("batch.file.row.count", "file", currentResource.getFilename()).increment();
        } else if (currentResource != null) {
            timerSample.stop(meterRegistry.timer("batch.file.processing.time", "file", currentResource.getFilename()));
            meterRegistry.counter("batch.file.completed", "file", currentResource.getFilename()).increment();
            currentResource = null; // Reset after file completion
        }
        return item;
    }
}
