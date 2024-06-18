package com.williamm56i.armin.persistence.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ReportRecord {

    BigDecimal reportNo;

    String reportName;

    String reportParams;

    byte[] report;

    BigDecimal jobId;

    String createId;

    Date createDate;

}
