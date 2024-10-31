package com.example.batchMicroservice;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.SimplePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TrafficDataTrainingRepository trainingRepository;
    private final TrafficDataTestRepository testRepository;
    private final MeterRegistry meterRegistry;

    @Value("${data.training-dir}")
    private String trainingDir;

    @Value("${data.testing-dir}")
    private String testingDir;

    public BatchConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager,
            TrafficDataTrainingRepository trainingRepository, TrafficDataTestRepository testRepository,
            MeterRegistry meterRegistry) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.trainingRepository = trainingRepository;
        this.testRepository = testRepository;
        this.meterRegistry = meterRegistry;
    }

    // Thread-safe ItemStreamReader wrapper
    @Bean
    @Primary
    public SynchronizedItemStreamReader<TrafficData> synchronizedMultiResourceItemReader() {
        SynchronizedItemStreamReader<TrafficData> reader = new SynchronizedItemStreamReader<>();
        reader.setDelegate(multiResourceItemReader());
        return reader;
    }

    @Bean
    public TrackingMultiResourceItemReader multiResourceItemReader() {
        TrackingMultiResourceItemReader reader = new TrackingMultiResourceItemReader(meterRegistry);
        reader.setResources(loadResources(trainingDir, testingDir));
        return reader;
    }

    private Resource[] loadResources(String... directories) {
        return Arrays.stream(directories)
                .flatMap(dir -> Arrays.stream(new File(dir).listFiles(file -> file.getName().endsWith(".csv"))))
                .map(FileSystemResource::new)
                .toArray(Resource[]::new);
    }

    @Bean
    public ItemProcessor<TrafficData, TrafficData> processor() {
        return item -> {
            logger.info("Processing item: " + item);
            return item;
        };
    }

    @Bean
    public ItemWriter<TrafficData> conditionalWriter() {
        return items -> {
            TrackingMultiResourceItemReader trackingReader = multiResourceItemReader();
            Resource currentResource = trackingReader.getCurrentResource();
            String sourcePath = currentResource != null ? currentResource.getFile().getParent() : "unknown";
            String attackType = trackingReader.getCurrentAttackType();

            List<TrafficDataTraining> trainingDataList = new ArrayList<>();
            List<TrafficDataTest> testDataList = new ArrayList<>();

            for (TrafficData item : items) {
                if (sourcePath.contains("train")) {
                    trainingDataList.add(new TrafficDataTraining(item, attackType));
                } else if (sourcePath.contains("test")) {
                    testDataList.add(new TrafficDataTest(item, attackType));
                }
            }

            if (!trainingDataList.isEmpty()) {
                logger.info("Writing training data batch: " + trainingDataList.size());
                trainingRepository.saveAll(trainingDataList);
            }

            if (!testDataList.isEmpty()) {
                logger.info("Writing test data batch: " + testDataList.size());
                testRepository.saveAll(testDataList);
            }
        };
    }

    @Bean
    public Job job(Step partitionedStep) {
        return new JobBuilder("job", jobRepository)
                .start(partitionedStep)
                .listener(new JobExecutionListener() {
                    private Timer.Sample jobTimer;

                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        logger.info("Job started with ID: " + jobExecution.getJobId());
                        jobTimer = Timer.start(meterRegistry);
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        jobTimer.stop(meterRegistry.timer("batch.job.duration", "job",
                                jobExecution.getJobInstance().getJobName()));
                        meterRegistry.counter("batch.job.status", "status", jobExecution.getStatus().toString())
                                .increment();
                        logger.info("Job completed with status: " + jobExecution.getStatus());
                    }
                })
                .build();
    }

    @Bean
    public Step partitionedStep(@Qualifier("synchronizedMultiResourceItemReader") ItemReader<TrafficData> reader,
            ItemProcessor<TrafficData, TrafficData> processor,
            @Qualifier("conditionalWriter") ItemWriter<TrafficData> writer) {

        return new StepBuilder("partitionedStep", jobRepository)
                .partitioner("partitionedStep", partitioner())
                .step(batchStep(reader, processor, writer))
                .partitionHandler(partitionHandler(batchStep(reader, processor, writer)))
                .build();
    }

    @Bean
    public Step batchStep(ItemReader<TrafficData> reader,
            ItemProcessor<TrafficData, TrafficData> processor,
            ItemWriter<TrafficData> writer) {

        return new StepBuilder("batchStep", jobRepository)
                .<TrafficData, TrafficData>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public TaskExecutorPartitionHandler partitionHandler(Step batchStep) {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setTaskExecutor(taskExecutor());
        handler.setStep(batchStep);
        handler.setGridSize(4);
        return handler;
    }

    @Bean
    public Partitioner partitioner() {
        return new SimplePartitioner();
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("BatchThread-");
        executor.initialize();
        return executor;
    }
}
