package com.williamm56i.armin.batch.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Slf4j
public class Example2Tasklet implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("example 2 tasklet");
        Map<String, Object> jobParamsMap = chunkContext.getStepContext().getJobParameters();
        log.info("sysdate: {},", jobParamsMap.get("sysdate"));
        log.info("param: {}", contribution.getStepExecution().getJobExecution().getExecutionContext().getString("param"));
        return RepeatStatus.FINISHED;
    }
}
