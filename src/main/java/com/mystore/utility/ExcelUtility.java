/**
 * 
 */
/**
 * Excel Utility – unified helper for reading Excel-based test data.
 * Supports both conditional row extraction and random term fetching.
 */
package com.mystore.utility;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExcelUtility {

    private XSSFWorkbook workbook;
    private static final String FILE_PATH = System.getProperty("user.dir") + "/TestData/Orders.xlsx";

    /**
     * Loads Excel workbook from given file path.
     */
    public ExcelUtility(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            workbook = new XSSFWorkbook(fis);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to load Excel file: " + e.getMessage(), e);
        }
    }

    /**
     * Returns all cell values from the first row that matches the given column/value condition.
     * Example:
     *   getRowDataByCondition("Users", "Username", "John")
     *   → returns the entire row where Username = John
     */
    public String[] getRowDataByCondition(String sheetName, String columnName, String value) {
        XSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new RuntimeException("❌ Sheet not found: " + sheetName);
        }

        int colIndex = -1;
        XSSFRow headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new RuntimeException("❌ Sheet '" + sheetName + "' has no header row!");
        }

        // Find the column index for the specified header name
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            XSSFCell cell = headerRow.getCell(i);
            if (cell != null && cell.getStringCellValue().trim().equalsIgnoreCase(columnName)) {
                colIndex = i;
                break;
            }
        }

        if (colIndex == -1) {
            throw new RuntimeException("❌ Column not found: " + columnName);
        }

        // Find the row where the value matches
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            XSSFRow row = sheet.getRow(r);
            if (row != null && row.getCell(colIndex) != null &&
                row.getCell(colIndex).getStringCellValue().trim().equalsIgnoreCase(value)) {

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

    /**
     * Returns a random non-empty value from a given sheet’s first column.
     * Used for tests like SearchTest to pick random data each run.
     */
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
            throw new RuntimeException("⚠ No non-empty values found in sheet: " + sheetName);
        }

        return values.get(new Random().nextInt(values.size()));
    }

    /**
     * Converts different Excel cell types into String safely.
     */
    private String getCellValueAsString(XSSFCell cell) {
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
    }

    /**
     * Returns all data in a given sheet as a 2D String array
     * (useful for @DataProvider setups).
     */
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
/**
    * Writes a list of row data to the specified sheet in an Excel file.
    *
    * @param excelPath Path to Excel file
    * @param sheetName Sheet name to write to
    * @param dataToWrite List of rows, each row is a String array
    */
   public static void writeProductData(String excelPath, String sheetName, List<String[]> dataToWrite) {
       try {
           File file = new File(excelPath);
           XSSFWorkbook workbook;
           Sheet sheet;

           if (file.exists()) {
               FileInputStream fis = new FileInputStream(file);
               workbook = new XSSFWorkbook(fis);
               sheet = workbook.getSheet(sheetName);
               if (sheet == null) sheet = workbook.createSheet(sheetName);
               fis.close();
           } else {
               workbook = new XSSFWorkbook();
               sheet = workbook.createSheet(sheetName);
           }

           int lastRow = sheet.getLastRowNum() + 1;
           for (String[] rowData : dataToWrite) {
               Row row = sheet.createRow(lastRow++);
               for (int i = 0; i < rowData.length; i++) {
                   Cell cell = row.createCell(i);
                   cell.setCellValue(rowData[i]);
               }
           }

           FileOutputStream fos = new FileOutputStream(file);
           workbook.write(fos);
           fos.close();
           workbook.close();

           System.out.println("✅ Product data written to Excel successfully!");
       } catch (Exception e) {
           throw new RuntimeException("❌ Failed to write Excel file: " + e.getMessage(), e);
       }
   }
   public static void appendOrderRecord(String orderId) {
       FileInputStream fis = null;
       Workbook workbook = null;
       FileOutputStream fos = null;

       try {
           fis = new FileInputStream(FILE_PATH);
           workbook = new XSSFWorkbook(fis);
           Sheet sheet = workbook.getSheetAt(0);

           int lastRowNum = sheet.getLastRowNum();
           Row row = sheet.createRow(lastRowNum + 1);

           Cell orderCell = row.createCell(0);
           orderCell.setCellValue(orderId);

           Cell timeCell = row.createCell(1);
           timeCell.setCellValue(new Date().toString());

           fis.close(); // Close input before writing

           fos = new FileOutputStream(FILE_PATH);
           workbook.write(fos);
           System.out.println("✅ Order ID appended to Excel: " + orderId);

       } catch (IOException e) {
           System.out.println("❌ Failed to write to Excel: " + e.getMessage());
       } finally {
           try {
               if (workbook != null) workbook.close();
               if (fos != null) fos.close();
           } catch (IOException e) {
               System.out.println("⚠ Failed to close streams: " + e.getMessage());
           }
       }
   }
	}
