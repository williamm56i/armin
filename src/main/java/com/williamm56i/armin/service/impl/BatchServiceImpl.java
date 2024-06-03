package com.williamm56i.armin.service.impl;

import com.williamm56i.armin.persistence.dao.BatchJobTriggerConfigDao;
import com.williamm56i.armin.service.BatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class BatchServiceImpl implements BatchService {

    @Autowired
    @Qualifier("AsyncJobLauncher")
    JobLauncher jobLauncher;
    @Autowired
    JobRepository jobRepository;
    @Autowired
    JobRegistry jobRegistry;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    BatchJobTriggerConfigDao batchJobTriggerConfigDao;

    @Override
    public long executeJob(String beanName) throws Exception {
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addDate("sysdate", new Date());
        JobParameters params = builder.toJobParameters();
        Job job = (Job) applicationContext.getBean(beanName);
        JobExecution jobExecution = jobLauncher.run(job, params);
        return jobExecution.getJobId();
    }

    @Override
    public String reloadJobFlow(String beanName) {
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
        BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanName);
        beanDefinitionRegistry.removeBeanDefinition(beanName);
        log.info("remove {} bean definition completed!", beanName);
        String jobName = batchJobTriggerConfigDao.selectJobNameByBean(beanName);
        if (jobRepository.getJobNames().contains(jobName)) {
            jobRegistry.unregister(jobName);
        }
        beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
        log.info("{} is reloaded", beanName);
        return beanName + " flow is reloaded!";
    }
}
