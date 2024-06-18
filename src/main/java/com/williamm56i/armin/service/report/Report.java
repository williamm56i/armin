package com.williamm56i.armin.service.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.williamm56i.armin.utils.ReportGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class Report {

    public abstract void generate(BigDecimal reportNo, Long jobId) throws IOException;

    protected abstract String getTitle();

    protected abstract Map<String, String> getHeader(String json);

    protected abstract List<Map<Integer, String>> getExtraColumn();

    protected abstract Map<String, String> getColumn();

    protected abstract Map<String, Integer> getWidth();

    protected abstract List<Map<String, Object>> getContent(String json) throws JsonProcessingException;

    protected abstract List<String> getFooter();

    protected byte[] generateExcel(String outputFilePath, String title,
                                   Map<String, String> headerMap,
                                   List<Map<Integer, String>> extraColumnMapList,
                                   Map<String, String> columnMap,
                                   Map<String, Integer> widthMap,
                                   List<Map<String, Object>> content,
                                   List<String> footerList) throws IOException {
        ReportGenerator reportGenerator = new ReportGenerator();
        File file = new File(outputFilePath);
        int sheetAt = 0;
        int nextRow = 0;
        try (Workbook workbook = new XSSFWorkbook()){
            workbook.createSheet("Sheet 1");
            /* 標題 */
            int titleColumSpan = columnMap.size() - 1;
            int titleHeight = 800;
            CellStyle titleStyle = reportGenerator.createCellStyle(workbook, 18, false, true, HorizontalAlignment.CENTER, "微軟正黑體");
            nextRow = reportGenerator.addTitle(workbook, titleStyle, title, sheetAt, titleHeight, titleColumSpan, nextRow);
            /* 表頭 */
            CellStyle headerStyle = reportGenerator.createCellStyle(workbook, 10, false, false, HorizontalAlignment.LEFT, "微軟正黑體");
            nextRow = reportGenerator.addHeader(workbook, headerStyle, headerMap, sheetAt, titleColumSpan, nextRow);
            /* 額外欄位 */
            if (extraColumnMapList != null) {
                for (Map<Integer, String> extraColumnMap: extraColumnMapList) {
                    CellStyle extraColumnStyle = reportGenerator.createCellStyle(workbook, 10, true, true, HorizontalAlignment.CENTER, "微軟正黑體");
                    nextRow = reportGenerator.addExtraColumn(workbook, extraColumnStyle, sheetAt, nextRow, titleColumSpan, extraColumnMap);
                }
            }
            /* 欄位內容 */
            CellStyle columnStyle = reportGenerator.createCellStyle(workbook, 10, true, true, HorizontalAlignment.CENTER, "微軟正黑體");
            nextRow = reportGenerator.addContent(workbook, columnStyle, columnMap, widthMap, content, sheetAt, nextRow);
            /* 表尾 */
            CellStyle footerStyle = reportGenerator.createCellStyle(workbook, 8, false, false, HorizontalAlignment.LEFT, "微軟正黑體");
            nextRow = reportGenerator.addFooter(workbook, footerStyle, footerList, sheetAt, titleColumSpan, nextRow);

            saveFile(workbook, file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    protected void saveFile(Workbook workbook, File outputFile) throws IOException {
        OutputStream outputStream = Files.newOutputStream(outputFile.toPath());
        workbook.write(outputStream);
        outputStream.close();
    }

    protected void deleteFile(String outputFilePath) {
        File file = new File(outputFilePath);
        if (file.delete()) {
            log.info("file deleted!");
        }
    }
}
