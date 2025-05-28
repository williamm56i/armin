package com.williamm56i.armin.batch.job;

import com.williamm56i.armin.batch.step.RpwStep;
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
public class RpwJob {

    @Autowired
    JobRepository jobRepository;
    @Autowired
    RpwStep rpwStep;

    @Bean("rpw-job")
    public Job rpwJob() {
        return new JobBuilder("RpwJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(rpwStep.rpwStep())
                .build();
    }
}
