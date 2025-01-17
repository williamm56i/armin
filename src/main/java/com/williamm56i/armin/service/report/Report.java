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

    /**
     * 定義標題，全工作表共用
     *
     * @return 標題
     */
    protected abstract String getTitle();

    /**
     * 定義工作表名稱，至少要有一個
     *
     * @return list<工作表名稱>
     */
    protected abstract List<String> getSheetList();

    /**
     * 定義表頭資訊，全工作表共用
     *
     * @param json 表頭參數
     * @return Map<表頭label, 表頭value>
     */
    protected abstract Map<String, String> getHeader(String json);

    /**
     * 定義額外跨欄欄位(分工作表)
     *
     * @param sheetList 工作表清單
     * @return Map<工作表名稱, List < Map < 欄位座標, 欄位名稱>>>
     */
    protected abstract Map<String, List<Map<Integer, String>>> getExtraColumns(List<String> sheetList);

    /**
     * 定義欄位(分工作表)
     *
     * @param sheetList 工作表清單
     * @return Map<工作表名稱, Map < 欄位key, 欄位名稱>>
     */
    protected abstract Map<String, Map<String, String>> getColumns(List<String> sheetList);

    /**
     * 定義欄位寬度(分工作表)
     *
     * @param sheetList 工作表清單
     * @return Map<工作表名稱, Map < 欄位key, 欄位寬度>>
     */
    protected abstract Map<String, Map<String, Integer>> getWidths(List<String> sheetList);

    /**
     * 定義儲存格內容(分工作表)
     *
     * @param sheetList 工作表清單
     * @param json      內容參數
     * @return Map<工作表名稱, Map < 欄位key, 儲存格資料>>
     * @throws JsonProcessingException e
     */
    protected abstract Map<String, List<Map<String, Object>>> getContents(List<String> sheetList, String json) throws JsonProcessingException;

    /**
     * 定義表尾(分工作表)
     *
     * @param sheetList 工作表清單
     * @return Map<工作表名稱, List < 表尾說明>>
     */
    protected abstract Map<String, List<String>> getFooters(List<String> sheetList);

    protected byte[] generateExcel(String outputFilePath, String title,
                                   Map<String, String> headerMap,
                                   List<String> sheetList,
                                   Map<String, List<Map<Integer, String>>> extraColumnMaps,
                                   Map<String, Map<String, String>> columnMaps,
                                   Map<String, Map<String, Integer>> widthMaps,
                                   Map<String, List<Map<String, Object>>> contents,
                                   Map<String, List<String>> footerMaps) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ReportGenerator reportGenerator = new ReportGenerator();
        File file = new File(outputFilePath);
        int sheetAt = 0;
        int nextRow = 0;
        try (Workbook workbook = new XSSFWorkbook()) {
            for (String sheetName: sheetList) {
                List<Map<Integer, String>> extraColumnMapList = extraColumnMaps.get(sheetName);
                Map<String, String> columnMap = columnMaps.get(sheetName);
                Map<String, Integer> widthMap = widthMaps.get(sheetName);
                List<Map<String, Object>> content = contents.get(sheetName);
                List<String> footerList = footerMaps.get(sheetName);
                workbook.createSheet(sheetName);

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
                    for (Map<Integer, String> extraColumnMap : extraColumnMapList) {
                        CellStyle extraColumnStyle = reportGenerator.createCellStyle(workbook, 10, true, true, HorizontalAlignment.CENTER, "微軟正黑體");
                        nextRow = reportGenerator.addExtraColumn(workbook, extraColumnStyle, sheetAt, nextRow, titleColumSpan, extraColumnMap);
                    }
                }
                /* 欄位內容 */
                CellStyle columnStyle = reportGenerator.createCellStyle(workbook, 10, true, true, HorizontalAlignment.CENTER, "微軟正黑體");
                nextRow = reportGenerator.addContent(workbook, columnStyle, columnMap, widthMap, content, sheetAt, nextRow);
                /* 表尾 */
                if (footerList != null) {
                    CellStyle footerStyle = reportGenerator.createCellStyle(workbook, 8, false, false, HorizontalAlignment.LEFT, "微軟正黑體");
                    nextRow = reportGenerator.addFooter(workbook, footerStyle, footerList, sheetAt, titleColumSpan, nextRow);
                }

                nextRow = 0;
                sheetAt++;
            }
            saveFile(workbook, file);
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            outputStream.flush();
            outputStream.close();
        }
        return outputStream.toByteArray();
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
