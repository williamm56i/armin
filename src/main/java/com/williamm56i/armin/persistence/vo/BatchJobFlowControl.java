package com.williamm56i.armin.persistence.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BatchJobFlowControl {

    String jobName;

    String stepName;

    BigDecimal stepOrder;

    String isExecutable;
}
