package com.williamm56i.armin.service.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.williamm56i.armin.persistence.dao.ReportRecordDao;
import com.williamm56i.armin.persistence.dao.SysUserDao;
import com.williamm56i.armin.persistence.vo.ReportRecord;
import com.williamm56i.armin.persistence.vo.SysCode;
import com.williamm56i.armin.persistence.vo.SysUser;
import com.williamm56i.armin.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service("ExampleReport")
@Slf4j
public class ExampleReport extends Report{

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SysUserDao sysUserDao;
    @Autowired
    ReportRecordDao reportRecordDao;

    @Override
    public void generate(BigDecimal reportNo, Long jobId) throws IOException {
        log.info("ExampleReport is generating... {}", jobId);
        String outputFilePath = "./ExampleReport_" + DateUtils.makeDateToString(new Date(), DateUtils.YYYYMMDDHHMISS) + ".xlsx";

        /* 資料準備 */
        ReportRecord record = reportRecordDao.selectByPrimaryKey(reportNo);
        String json = record.getReportParams();

        String title = getTitle();
        Map<String, String> headerMap = getHeader(json);
        List<Map<Integer, String>> extraColumnMapList = getExtraColumn();
        Map<String, String> columnMap = getColumn();
        Map<String, Integer> widthMap = getWidth();
        List<Map<String, Object>> content = getContent(json);
        List<String> footerList = getFooter();

        /* 產生報表 */
        log.info("generate excel..");
        byte[] bytes = generateExcel(outputFilePath, title, headerMap, extraColumnMapList, columnMap, widthMap, content, footerList);
        log.info("generate excel completed!");

        /* 寫入資料庫 */
        record.setReportNo(reportNo);
        record.setReport(bytes);
        record.setJobId(new BigDecimal(jobId));
        reportRecordDao.updateReport(record);
        log.info("update report record");
//        deleteFile(outputFilePath);

        log.info("ExampleReport Completed!");
    }

    @Override
    protected String getTitle() {
        return "範例報表";
    }

    @Override
    protected Map<String, String> getHeader(String json) {
        Map<String, String> headerMap = new LinkedHashMap<>();
        try {
            SysUser vo = objectMapper.readValue(json, SysUser.class);
            String reportDate = DateUtils.makeDateToString(new Date(), DateUtils.YYYYMMDD_SLASH);
            headerMap.put("產出日期", reportDate);
        } catch (Exception e) {
            log.error("JSON parser fail");
        }
        return headerMap;
    }

    @Override
    protected List<Map<Integer, String>> getExtraColumn() {
        return null;
    }

    @Override
    protected Map<String, String> getColumn() {
        Map<String, String> columnMap = new LinkedHashMap<>();
        columnMap.put("account", "帳號");
        columnMap.put("userName", "姓名");
        columnMap.put("email", "信箱");
        columnMap.put("createId", "建立人員");
        columnMap.put("createDate", "建立日期");
        columnMap.put("updateId", "異動人員");
        columnMap.put("updateDate", "異動日期");
        return columnMap;
    }

    @Override
    protected Map<String, Integer> getWidth() {
        Map<String, Integer> widthMap = new HashMap<>();
        widthMap.put("account", 15);
        widthMap.put("userName", 15);
        widthMap.put("email", 30);
        widthMap.put("createId", 15);
        widthMap.put("createDate", 20);
        widthMap.put("updateId", 15);
        widthMap.put("updateDate", 15);
        return widthMap;
    }

    @Override
    protected List<Map<String, Object>> getContent(String json) throws JsonProcessingException {
        SysUser vo = objectMapper.readValue(json, SysUser.class);
        List<SysUser> sysUserList = sysUserDao.selectByConditions(vo.getAccount(), vo.getUserName(), vo.getEmail()); // from json
        List<Map<String, Object>> list = new ArrayList<>();
        sysUserList.forEach( sysUser -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("account", sysUser.getAccount());
            map.put("userName", sysUser.getUserName());
            map.put("email", sysUser.getEmail());
            map.put("createId", sysUser.getCreateId());
            map.put("createDate", sysUser.getCreateDate());
            map.put("updateId", sysUser.getUpdateId());
            map.put("updateDate", sysUser.getUpdateDate());
            list.add(map);
        });
        return list;
    }

    @Override
    protected List<String> getFooter() {
        List<String> footerList = new ArrayList<>();
        footerList.add("範例表尾");
        return footerList;
    }
}
