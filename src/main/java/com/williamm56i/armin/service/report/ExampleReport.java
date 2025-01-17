package com.williamm56i.armin.service.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.williamm56i.armin.persistence.dao.ReportRecordDao;
import com.williamm56i.armin.persistence.dao.SysUserDao;
import com.williamm56i.armin.persistence.vo.ReportRecord;
import com.williamm56i.armin.persistence.vo.SysUser;
import com.williamm56i.armin.service.SysUserService;
import com.williamm56i.armin.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service("ExampleReport")
@Slf4j
public class ExampleReport extends Report {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SysUserService sysUserService;
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
        List<String> sheetList = getSheetList();
        Map<String, String> headerMap = getHeader(json);
        Map<String, List<Map<Integer, String>>> extraColumnMaps = getExtraColumns(sheetList);
        Map<String, Map<String, String>> columnMaps = getColumns(sheetList);
        Map<String, Map<String, Integer>> widthMaps = getWidths(sheetList);
        Map<String, List<Map<String, Object>>> contents = getContents(sheetList, json);
        Map<String, List<String>> footerMaps = getFooters(sheetList);

        /* 產生報表 */
        log.info("generate excel..");
        byte[] bytes = generateExcel(outputFilePath, title, headerMap, sheetList, extraColumnMaps, columnMaps, widthMaps, contents, footerMaps);
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
    protected List<String> getSheetList() {
        return List.of("工作表1");
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
    protected Map<String, List<Map<Integer, String>>> getExtraColumns(List<String> sheetList) {
        return new LinkedHashMap<>();
    }

    @Override
    protected Map<String, Map<String, String>> getColumns(List<String> sheetList) {
        Map<String, Map<String, String>> columnMaps = new LinkedHashMap<>();
        Map<String, String> columnMap = new LinkedHashMap<>();
        columnMap.put("account", "帳號");
        columnMap.put("userName", "姓名");
        columnMap.put("email", "信箱");
        columnMap.put("createId", "建立人員");
        columnMap.put("createDate", "建立日期");
        columnMap.put("updateId", "異動人員");
        columnMap.put("updateDate", "異動日期");
        columnMaps.put(sheetList.get(0), columnMap);
        return columnMaps;
    }

    @Override
    protected Map<String, Map<String, Integer>> getWidths(List<String> sheetList) {
        Map<String, Map<String, Integer>> widthMaps = new LinkedHashMap<>();
        Map<String, Integer> widthMap = new HashMap<>();
        widthMap.put("account", 15);
        widthMap.put("userName", 15);
        widthMap.put("email", 30);
        widthMap.put("createId", 15);
        widthMap.put("createDate", 20);
        widthMap.put("updateId", 15);
        widthMap.put("updateDate", 15);
        widthMaps.put(sheetList.get(0), widthMap);
        return widthMaps;
    }

    @Override
    protected Map<String, List<Map<String, Object>>> getContents(List<String> sheetList, String json) throws JsonProcessingException {
        Map<String, List<Map<String, Object>>> contents = new LinkedHashMap<>();
        contents.put(sheetList.get(0), sysUserService.getSysUser(json));
        return contents;
    }

    @Override
    protected Map<String, List<String>> getFooters(List<String> sheetList) {
        Map<String, List<String>> footerMaps = new LinkedHashMap<>();
        List<String> footerList = new ArrayList<>();
        footerList.add("範例表尾");
        footerMaps.put(sheetList.get(0), footerList);
        return footerMaps;
    }
}
