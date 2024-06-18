package com.williamm56i.armin.batch.tasklet;

import com.williamm56i.armin.service.report.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@Slf4j
public class ReportTasklet implements Tasklet {

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("Report Tasklet");
        Map<String, Object> jobParamsMap = chunkContext.getStepContext().getJobParameters();
        Long jobId = chunkContext.getStepContext().getJobInstanceId();
        String reportName = (String) jobParamsMap.get("reportName");
        Long reportNo = (Long) jobParamsMap.get("reportNo");
        Report report = (Report) applicationContext.getBean(reportName);
        report.generate(BigDecimal.valueOf(reportNo), jobId);
        return RepeatStatus.FINISHED;
    }
}
