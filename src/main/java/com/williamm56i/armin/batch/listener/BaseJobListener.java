package com.williamm56i.armin.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BaseJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("before {}...", jobExecution.getJobInstance().getJobName());
    }

    public void afterJob(JobExecution jobExecution) {
        log.info("after {}...", jobExecution.getJobInstance().getJobName());
        if (jobExecution.getStatus().isUnsuccessful()) {
            log.error("Batch failed, jobId: {}", jobExecution.getJobId());
        }
    }
}
