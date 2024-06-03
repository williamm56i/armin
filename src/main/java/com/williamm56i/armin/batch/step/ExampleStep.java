package com.williamm56i.armin.batch.step;

import com.williamm56i.armin.batch.listener.BaseStepListener;
import com.williamm56i.armin.batch.tasklet.Example1Tasklet;
import com.williamm56i.armin.batch.tasklet.Example2Tasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
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
}
