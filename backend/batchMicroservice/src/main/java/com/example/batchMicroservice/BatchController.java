package com.example.batchMicroservice;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@RestController
public class BatchController {

    private static final Logger logger = LoggerFactory.getLogger(BatchController.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @GetMapping("/loadData")
    public String loadData() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("timestamp", new Date())
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);
            logger.info("Batch job started successfully");
            return "Batch job started successfully";
        } catch (Exception e) {
            logger.error("Failed to start batch job", e);
            return "Failed to start batch job: " + e.getMessage();
        }
    }
}
