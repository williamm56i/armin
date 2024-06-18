package com.williamm56i.armin.batch.step;

import com.williamm56i.armin.batch.listener.BaseStepListener;
import com.williamm56i.armin.batch.tasklet.ReportTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ReportStep {

    @Autowired
    JobRepository jobRepository;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    BaseStepListener baseStepListener;
    @Autowired
    ReportTasklet reportTasklet;

    @Bean("report-step")
    public Step reportStep() {
        return new StepBuilder("報表步驟", jobRepository)
                .allowStartIfComplete(true)
                .listener(baseStepListener)
                .tasklet(reportTasklet, transactionManager)
                .build();
    }
}
