package com.mystore.utility;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExcelUtility {

    private XSSFWorkbook workbook;
    private static final String FILE_PATH =
            System.getProperty("user.dir") + "/TestData/Orders.xlsx";

    // ==============================
    // 📥 CONSTRUCTOR
    // ==============================
    public ExcelUtility(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            workbook = new XSSFWorkbook(fis);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load Excel file: " + e.getMessage(), e);
        }
    }

    // ==============================
    // 📊 GET ROW BY CONDITION
    // ==============================
    public String[] getRowDataByCondition(String sheetName, String columnName, String value) {

        XSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new RuntimeException("❌ Sheet not found: " + sheetName);
        }

        XSSFRow headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new RuntimeException("❌ Header row missing in sheet: " + sheetName);
        }

        int colIndex = -1;

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            if (headerRow.getCell(i).getStringCellValue().trim()
                    .equalsIgnoreCase(columnName)) {
                colIndex = i;
                break;
            }
        }

        if (colIndex == -1) {
            throw new RuntimeException("❌ Column not found: " + columnName);
        }

        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            XSSFRow row = sheet.getRow(r);

            if (row != null && row.getCell(colIndex) != null &&
                    row.getCell(colIndex).getStringCellValue().trim()
                            .equalsIgnoreCase(value)) {

                String[] rowData = new String[row.getLastCellNum()];

                for (int c = 0; c < row.getLastCellNum(); c++) {
                    XSSFCell cell = row.getCell(c);
                    rowData[c] = (cell == null) ? "" : getCellValueAsString(cell);
                }

                return rowData;
            }
        }

        return null;
    }

    // ==============================
    // 🎲 RANDOM VALUE FROM COLUMN
    // ==============================
    public String getRandomValueFromColumn(String sheetName) {

        XSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new RuntimeException("❌ Sheet not found: " + sheetName);
        }

        List<String> values = new ArrayList<>();

        for (int r = 0; r <= sheet.getLastRowNum(); r++) {
            XSSFRow row = sheet.getRow(r);

            if (row != null && row.getCell(0) != null) {
                String value = row.getCell(0).toString().trim();
                if (!value.isEmpty()) {
                    values.add(value);
                }
            }
        }

        if (values.isEmpty()) {
            throw new RuntimeException("⚠ No values found in sheet: " + sheetName);
        }

        return values.get(new Random().nextInt(values.size()));
    }

    // ==============================
    // 📊 GET SHEET DATA
    // ==============================
    public String[][] getSheetData(String sheetName) {

        XSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new RuntimeException("❌ Sheet not found: " + sheetName);
        }

        int rowCount = sheet.getPhysicalNumberOfRows();
        int colCount = sheet.getRow(0).getLastCellNum();

        String[][] data = new String[rowCount - 1][colCount];

        for (int i = 1; i < rowCount; i++) {
            XSSFRow row = sheet.getRow(i);

            for (int j = 0; j < colCount; j++) {
                XSSFCell cell = row.getCell(j);
                data[i - 1][j] = (cell == null) ? "" : getCellValueAsString(cell);
            }
        }

        return data;
    }

    // ==============================
    // ✍️ WRITE PRODUCT DATA
    // ==============================
    public static void writeProductData(String excelPath, String sheetName, List<String[]> data) {

        try (Workbook workbook = new XSSFWorkbook()) {

            File file = new File(excelPath);
            Sheet sheet;

            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    workbook.close();
                }
            }

            sheet = workbook.createSheet(sheetName);

            int rowIndex = 0;

            for (String[] rowData : data) {
                Row row = sheet.createRow(rowIndex++);

                for (int i = 0; i < rowData.length; i++) {
                    row.createCell(i).setCellValue(rowData[i]);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            System.out.println("✅ Product data written successfully!");

        } catch (Exception e) {
            throw new RuntimeException("❌ Excel write failed: " + e.getMessage(), e);
        }
    }

    // ==============================
    // 🧾 APPEND ORDER RECORD (FINAL)
    // ==============================
    public static void appendOrderRecord(String orderId, String orderType) {

    Workbook workbook = null;
    FileOutputStream fos = null;

    try {
        File file = new File(FILE_PATH);

        // ✅ CREATE DIRECTORY IF NOT EXISTS
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
            System.out.println("📁 Created directory: " + parentDir.getAbsolutePath());
        }

        Sheet sheet;

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(fis);
            }
            sheet = workbook.getSheet("Orders");
            if (sheet == null) {
                sheet = workbook.createSheet("Orders");
            }

        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Orders");

            // ✅ Header
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Order ID");
            header.createCell(1).setCellValue("Order Type");
            header.createCell(2).setCellValue("Date Time");
        }

        int lastRow = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(lastRow);

        row.createCell(0).setCellValue(orderId);
        row.createCell(1).setCellValue(orderType);
        row.createCell(2).setCellValue(
                LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        fos = new FileOutputStream(file);
        workbook.write(fos);

        System.out.println("✅ Order saved: " + orderId);

    } catch (Exception e) {
        throw new RuntimeException("❌ Failed to append order: " + e.getMessage(), e);

    } finally {
        try {
            if (workbook != null) workbook.close();
            if (fos != null) fos.close();
        } catch (IOException e) {
            System.out.println("⚠ Resource close failed: " + e.getMessage());
        }
    }
}

    // ==============================
    // 🔧 HELPER
    // ==============================
    private String getCellValueAsString(XSSFCell cell) {
        return new DataFormatter().formatCellValue(cell);
    }
}