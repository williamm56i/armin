package com.williamm56i.armin.batch.job;

import com.williamm56i.armin.persistence.dao.BatchJobFlowControlDao;
import com.williamm56i.armin.persistence.vo.BatchJobFlowControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;

@Slf4j
public class BaseJob {

    @Autowired
    BatchJobFlowControlDao batchJobFlowControlDao;
    @Autowired
    ApplicationContext applicationContext;

    public Flow setFlow(String jobName, String flowName) {
        FlowBuilder<Flow> flow = new FlowBuilder<>(flowName);
        List<BatchJobFlowControl> stepList = batchJobFlowControlDao.selectByJobName(jobName);
        boolean isFirst = true;
        for (BatchJobFlowControl step: stepList) {
            Step stepObj  = (Step) applicationContext.getBean(step.getStepName());
            if (isFirst) {
                flow.start(stepObj);
                isFirst = false;
            } else {
                flow.next(stepObj);
            }
        }
        return flow.build();
    }
}
