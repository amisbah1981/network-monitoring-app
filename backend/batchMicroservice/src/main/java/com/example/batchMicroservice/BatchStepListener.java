package com.example.batchMicroservice;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import io.micrometer.core.instrument.MeterRegistry;

public class BatchStepListener extends StepExecutionListenerSupport {

    private final MeterRegistry meterRegistry;

    public BatchStepListener(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        meterRegistry.counter("batch.step.start", "step", stepExecution.getStepName()).increment();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        meterRegistry.counter("batch.step.end", "step", stepExecution.getStepName()).increment();
        meterRegistry.counter("batch.step.read.count", "step", stepExecution.getStepName())
                .increment(stepExecution.getReadCount());
        return stepExecution.getExitStatus();
    }
}
