package com.williamm56i.armin.persistence.dao;

import com.williamm56i.armin.persistence.vo.ReportRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface ReportRecordDao {

    ReportRecord selectByPrimaryKey(@Param("reportNo")BigDecimal reportNo);
}
