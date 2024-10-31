package com.example.batchMicroservice;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

import io.micrometer.core.instrument.MeterRegistry;

public class BatchJobListener extends JobExecutionListenerSupport {

    private final MeterRegistry meterRegistry;

    public BatchJobListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        meterRegistry.counter("batch.job.start", "job", jobExecution.getJobInstance().getJobName()).increment();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        meterRegistry.counter("batch.job.end", "job", jobExecution.getJobInstance().getJobName()).increment();
    }
}
