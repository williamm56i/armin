package com.williamm56i.armin.persistence.dao;

import com.williamm56i.armin.persistence.vo.BatchJobFlowControl;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BatchJobFlowControlDao {

    List<BatchJobFlowControl> selectByJobName(@Param("jobName")String jobName);
}
