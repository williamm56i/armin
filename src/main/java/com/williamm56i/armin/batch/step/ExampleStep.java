package com.williamm56i.armin.batch.step;

import com.williamm56i.armin.batch.listener.BaseStepListener;
import com.williamm56i.armin.batch.tasklet.Example1Tasklet;
import com.williamm56i.armin.batch.tasklet.Example2Tasklet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class ExampleStep {

    @Autowired
    JobRepository jobRepository;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    BaseStepListener baseStepListener;
    @Autowired
    Example1Tasklet example1Tasklet;
    @Autowired
    Example2Tasklet example2Tasklet;

    @Bean("example1-step")
    public Step example1Step() {
        return new StepBuilder("範例步驟1", jobRepository)
                .allowStartIfComplete(true)
                .listener(baseStepListener)
                .tasklet(example1Tasklet, transactionManager)
                .build();
    }

    @Bean("example2-step")
    public Step example2Step() {
        return new StepBuilder("範例步驟2", jobRepository)
                .allowStartIfComplete(true)
                .listener(baseStepListener)
                .tasklet(example2Tasklet, transactionManager)
                .build();
    }

    @Bean("exampleA-step")
    public Step exampleAStep() {
        return new StepBuilder("範例步驟A", jobRepository)
                .allowStartIfComplete(true)
                .listener(baseStepListener)
                .tasklet((contribution, chunkContext) -> {
                    log.info("範例步驟A");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean("exampleB-step")
    public Step exampleBStep() {
        return new StepBuilder("範例步驟B", jobRepository)
                .allowStartIfComplete(true)
                .listener(baseStepListener)
                .tasklet((contribution, chunkContext) -> {
                    log.info("範例步驟B");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean("exampleC-step")
    public Step exampleCStep() {
        return new StepBuilder("範例步驟C", jobRepository)
                .allowStartIfComplete(true)
                .listener(baseStepListener)
                .tasklet((contribution, chunkContext) -> {
                    log.info("範例步驟C");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean("exampleD-step")
    public Step exampleDStep() {
        return new StepBuilder("範例步驟D", jobRepository)
                .allowStartIfComplete(true)
                .listener(baseStepListener)
                .tasklet((contribution, chunkContext) -> {
                    log.info("範例步驟D");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean("exampleE-step")
    public Step exampleEStep() {
        return new StepBuilder("範例步驟E", jobRepository)
                .allowStartIfComplete(true)
                .listener(baseStepListener)
                .tasklet((contribution, chunkContext) -> {
                    log.info("範例步驟E");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
