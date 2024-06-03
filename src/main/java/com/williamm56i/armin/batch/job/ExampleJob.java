package com.williamm56i.armin.batch.job;

import com.williamm56i.armin.batch.listener.BaseJobListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ExampleJob extends BaseJob{

    @Autowired
    JobRepository jobRepository;
    @Autowired
    BaseJobListener baseJobListener;

    @Bean("example-job")
    public Job exampleJob() {
        return new JobBuilder("ExampleJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(baseJobListener)
                .start(setFlow("ExampleJob", "ExampleFlow"))
                .end()
                .build();
    }
}
