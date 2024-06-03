package com.williamm56i.armin.batch.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class Example1Tasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("example 1 tasklet");
        contribution.getStepExecution().getJobExecution().getExecutionContext().putString("param", "test");
        return RepeatStatus.FINISHED;
    }
}
