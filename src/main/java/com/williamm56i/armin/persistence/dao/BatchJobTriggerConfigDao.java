package com.williamm56i.armin.persistence.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BatchJobTriggerConfigDao {

    String selectJobNameByBean(@Param("beanName") String beanName);
}
