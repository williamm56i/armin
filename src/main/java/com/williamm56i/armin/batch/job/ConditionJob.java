package com.williamm56i.armin.batch.job;

import com.williamm56i.armin.batch.listener.BaseJobListener;
import com.williamm56i.armin.batch.step.ExampleStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 依Step結束狀態決定走向
 */
@Configuration
@Slf4j
public class ConditionJob {

    @Autowired
    ExampleStep exampleStep;
    @Autowired
    JobRepository jobRepository;
    @Autowired
    BaseJobListener baseJobListener;

    @Bean("condition-job")
    public Job conditionJob() {
        return new JobBuilder("ConditionJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(baseJobListener)
                .start(exampleStep.exampleAStep()).on("COMPLETED")
                    .to(exampleStep.exampleBStep()).next(exampleStep.exampleCStep())
                .from(exampleStep.exampleAStep()).on("FAILED")
                    .to(exampleStep.exampleDStep())
                .end()
                .build();
    }
}
