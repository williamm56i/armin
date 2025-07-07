package com.williamm56i.armin.batch.job;

import com.williamm56i.armin.batch.listener.BaseJobListener;
import com.williamm56i.armin.batch.step.ExampleStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;


/**
 * 併行處理
 */
@Configuration
@Slf4j
public class ParallelJob {

    @Autowired
    ExampleStep exampleStep;
    @Autowired
    JobRepository jobRepository;
    @Autowired
    BaseJobListener baseJobListener;

    @Bean("parallel-job")
    public Job parallelJob() {
        return new JobBuilder("ParallelJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(baseJobListener)
                .flow(exampleStep.exampleAStep())
                .next(parallelFlow())
                .next(exampleStep.exampleDStep())
                .end()
                .build();
    }

    private Flow parallelFlow() {
        FlowBuilder<Flow> flowB = new FlowBuilder<>("bFlow");
        FlowBuilder<Flow> flowC = new FlowBuilder<>("cFlow");
        Flow b = flowB.start(exampleStep.exampleBStep()).build();
        Flow c = flowC.start(exampleStep.exampleCStep()).build();

        FlowBuilder<Flow> flow = new FlowBuilder<>("parallelFlow");
        return flow.split(new SimpleAsyncTaskExecutor()).add(b, c).build();
    }
}
