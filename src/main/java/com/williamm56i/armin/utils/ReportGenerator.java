package com.williamm56i.armin.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportGenerator {

    public int addTitle(Workbook workbook, CellStyle titleStyle, String title, int sheetAt, int titleHeight, int titleColumnSpan, int nextRow) {
        Sheet sheet = workbook.getSheetAt(sheetAt);
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0,0,0, titleColumnSpan);
        sheet.addMergedRegion(cellRangeAddress);
        Row titleRow = sheet.createRow(nextRow);
        titleRow.setHeight((short)titleHeight);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(titleStyle);
        titleCell.setCellValue(title);
        nextRow++;
        return nextRow;
    }

    public int addHeader(Workbook workbook, CellStyle headerStyle, Map<String, String> headerMap, int sheetAt, int titleColumn, int nextRow) {
        Sheet sheet = workbook.getSheetAt(sheetAt);
        nextRow++;
        for (String key: headerMap.keySet()) {
            CellRangeAddress cellRangeAddress = new CellRangeAddress(nextRow, nextRow, 0, titleColumn);
            sheet.addMergedRegion(cellRangeAddress);
            Row headerRow = sheet.createRow(nextRow);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellStyle(headerStyle);
            headerCell.setCellValue(key + "：" + headerMap.get(key));
            nextRow++;
        }
        return nextRow;
    }

    public int addExtraColumn(Workbook workbook, CellStyle columnStyle, int sheetAt, int nextRow, int titleColumnSpan, Map<Integer, String> extraColumnMap) {
        Sheet sheet = workbook.getSheetAt(sheetAt);
        nextRow++;

        List<Integer> keyList = extraColumnMap.keySet().stream().sorted(Comparator.reverseOrder()).toList();

        Row titleRow = sheet.createRow(nextRow);
        for (int i = 0; i <= titleColumnSpan; i++) {
            Cell titleCell = titleRow.createCell(i);
            titleCell.setCellStyle(columnStyle);
        }

        boolean isFirst = true;
        int endSpanCol = 0;
        for (Integer key: keyList) {
            if (isFirst) {
                CellRangeAddress cellRangeAddress = new CellRangeAddress(nextRow, nextRow, key, titleColumnSpan);
                sheet.addMergedRegion(cellRangeAddress);
                endSpanCol = key - 1;
                isFirst = false;
            } else {
                CellRangeAddress cellRangeAddress = new CellRangeAddress(nextRow, nextRow, key, endSpanCol);
                sheet.addMergedRegion(cellRangeAddress);
                endSpanCol = key - 1;
            }
        }
        return nextRow;
    }

    public int addContent(Workbook workbook, CellStyle columnStyle, Map<String, String> columnMap, Map<String, Integer> widthMap, List<Map<String, Object>> content, int sheetAt, int nextRow) {
        Sheet sheet = workbook.getSheetAt(sheetAt);
        nextRow++;

        Row columnRow = sheet.createRow(nextRow);
        int col = 0;
        for (String columnKey: columnMap.keySet()) {
            sheet.setColumnWidth(col, widthMap.get(columnKey) * 256);
            Cell columnCell = columnRow.createCell(col++);
            columnCell.setCellStyle(columnStyle);
            columnCell.setCellValue(columnMap.get(columnKey));
        }
        nextRow++;

        for (Map<String, Object> map: content) {
            Row contentRow = sheet.createRow(nextRow);
            col = 0;
            for (String columnKey: columnMap.keySet()) {
                Object data = map.get(columnKey);
                if (data instanceof  String) {
                    Cell dataCell = contentRow.createCell(col++);
                    CellStyle dataStyle = createCellStyle(workbook, 10, true, false, HorizontalAlignment.LEFT, "微軟正黑體");
                    dataCell.setCellStyle(dataStyle);
                    dataCell.setCellValue((String) data);
                } else if (data instanceof Integer) {
                    Cell dataCell = contentRow.createCell(col++, CellType.NUMERIC);
                    CellStyle dataStyle = createCellStyle(workbook, 10, true, false, HorizontalAlignment.RIGHT, "微軟正黑體");
                    dataStyle.setAlignment(HorizontalAlignment.RIGHT);
                    dataCell.setCellStyle(dataStyle);
                    dataCell.setCellValue((Integer) data);
                } else if (data instanceof Double) {
                    Cell dataCell = contentRow.createCell(col++, CellType.NUMERIC);
                    CellStyle dataStyle = createCellStyle(workbook, 10, true, false, HorizontalAlignment.RIGHT, "微軟正黑體");
                    dataStyle.setAlignment(HorizontalAlignment.RIGHT);
                    dataCell.setCellStyle(dataStyle);
                    dataCell.setCellValue((Double) data);
                } else if (data instanceof BigDecimal) {
                    Cell dataCell = contentRow.createCell(col++, CellType.NUMERIC);
                    CellStyle dataStyle = createCellStyle(workbook, 10, true, false, HorizontalAlignment.RIGHT, "微軟正黑體");
                    dataStyle.setAlignment(HorizontalAlignment.RIGHT);
                    dataCell.setCellStyle(dataStyle);
                    dataCell.setCellValue(((BigDecimal) data).doubleValue());
                } else if (data instanceof Date) {
                    Cell dataCell = contentRow.createCell(col++);
                    CellStyle dataStyle = createCellStyle(workbook, 10, true, false, HorizontalAlignment.LEFT, "微軟正黑體");
                    dataCell.setCellStyle(dataStyle);
                    dataCell.setCellValue(DateUtils.makeDateToString((Date) data, DateUtils.YYYYMMDD_SLASH_TIME));
                } else  {
                    Cell dataCell = contentRow.createCell(col++);
                    CellStyle dataStyle = createCellStyle(workbook, 10, true, false, HorizontalAlignment.RIGHT, "微軟正黑體");
                    dataCell.setCellStyle(dataStyle);
                }
            }
            nextRow++;
        }

        return nextRow;
    }

    public int addFooter(Workbook workbook, CellStyle footerStyle, List<String> footerList, int sheetAt, int titleColumnSpan, int nextRow) {
        Sheet sheet = workbook.getSheetAt(sheetAt);
        nextRow++;
        for (String footer: footerList) {
            CellRangeAddress cellRangeAddress = new CellRangeAddress(nextRow, nextRow, 0, titleColumnSpan);
            sheet.addMergedRegion(cellRangeAddress);
            Row headerRow = sheet.createRow(nextRow);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellStyle(footerStyle);
            headerCell.setCellValue(footer);
            nextRow++;
        }
        return nextRow;
    }

    public CellStyle createCellStyle(Workbook workbook, int fontSize, boolean hasBorder, boolean isBold, HorizontalAlignment alignment, String fontName) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        /* 框線 */
        if (hasBorder) {
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
        }
        /* 字體字型 */
        font.setFontHeightInPoints((short) fontSize);
        font.setFontName(fontName);
        font.setBold(isBold);
        cellStyle.setFont(font);
        /* 文字對齊 */
        cellStyle.setAlignment(alignment);

        return cellStyle;
    }

    public void setRegionBorder(CellRangeAddress cellRangeAddress, Sheet sheet) {
        RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
    }
}
