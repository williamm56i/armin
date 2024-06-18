package com.williamm56i.armin.service;

public interface BatchService {

    /**
     * 依Job BeanName執行批次作業
     *
     * @param beanName job beanName
     * @return long
     */
    long executeJob(String beanName) throws Exception;

    /**
     * 依Job BeanName執行批次作業
     *
     * @param beanName report-job
     * @param reportName reportName
     * @param reportNo reportNo
     * @return long
     */
    long executeJob(String beanName, String reportName, Long reportNo) throws Exception;

    /**
     * 依Job BeanName於Runtime更新批次執行流程
     *
     * @param beanName job beanName
     * @return String
     */
    String reloadJobFlow(String beanName);
}
