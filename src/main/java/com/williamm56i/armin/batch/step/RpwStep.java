package com.williamm56i.armin.batch.step;

import com.williamm56i.armin.batch.rpw.SysUserProcessor;
import com.williamm56i.armin.batch.rpw.SysUserReader;
import com.williamm56i.armin.batch.rpw.SysUserWriter;
import com.williamm56i.armin.persistence.vo.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class RpwStep {

    @Autowired
    JobRepository jobRepository;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    SysUserReader sysUserReader;
    @Autowired
    SysUserProcessor sysUserProcessor;
    @Autowired
    SysUserWriter sysUserWriter;

    @Bean("rpw-step")
    public Step rpwStep() {
        return new StepBuilder("RPW步驟", jobRepository)
                .allowStartIfComplete(true).<SysUser, SysUser>chunk(100, transactionManager)
                .reader(sysUserReader.read())
                .processor(sysUserProcessor)
                .writer(sysUserWriter)
                .faultTolerant()
                .retryLimit(5)
                .retry(Exception.class)
                .skipLimit(5)
                .skip(Exception.class)
                .build();
    }
}
