package com.williamm56i.armin.batch.decider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
public class ExampleDecider implements JobExecutionDecider {

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        // 邏輯
        log.info("decide");
        int random = new Random().nextInt(10) + 1;
        if (random % 2 == 0) {
          return new FlowExecutionStatus("COMPLETED");
        }
        return new FlowExecutionStatus("FAILED");
    }
}
