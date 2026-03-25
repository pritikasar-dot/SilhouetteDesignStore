/**
 * 
 */
package com.mystore.utility;

/**
 * 
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel Utility Library - Compatible with Apache POI 5.x
 * Supports reading/writing data, adding/removing sheets & columns
 */
public class NewExcelLibrary {

    private String path;
    private FileInputStream fis = null;
    private FileOutputStream fileOut = null;
    private XSSFWorkbook workbook = null;
    private XSSFSheet sheet = null;
    private XSSFRow row = null;
    private XSSFCell cell = null;

    // Default Excel file path
    public static final String DEFAULT_PATH =
            System.getProperty("user.dir") + "\\src\\test\\resources\\TestData\\TestData.xlsx";

    // Constructor for default path
    public NewExcelLibrary() {
        this(DEFAULT_PATH);
    }

    // Constructor with custom path
    public NewExcelLibrary(String path) {
        this.path = path;
        try {
            fis = new FileInputStream(path);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheetAt(0);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get row count
    public int getRowCount(String sheetName) {
        int index = workbook.getSheetIndex(sheetName);
        if (index == -1) return 0;
        sheet = workbook.getSheetAt(index);
        return sheet.getLastRowNum() + 1;
    }

    // Get cell data by column name
    public String getCellData(String sheetName, String colName, int rowNum) {
        try {
            if (rowNum <= 0) return "";

            int index = workbook.getSheetIndex(sheetName);
            if (index == -1) return "";

            sheet = workbook.getSheetAt(index);
            row = sheet.getRow(0);
            if (row == null) return "";

            int colNum = -1;
            for (int i = 0; i < row.getLastCellNum(); i++) {
                if (row.getCell(i).getStringCellValue().trim().equalsIgnoreCase(colName.trim())) {
                    colNum = i;
                    break;
                }
            }
            if (colNum == -1) return "";

            row = sheet.getRow(rowNum - 1);
            if (row == null) return "";

            cell = row.getCell(colNum);
            return formatCellValue(cell);
        } catch (Exception e) {
            e.printStackTrace();
            return "row " + rowNum + " or column " + colName + " does not exist in xls";
        }
    }

    // Get cell data by column index
    public String getCellData(String sheetName, int colNum, int rowNum) {
        try {
            if (rowNum <= 0) return "";

            int index = workbook.getSheetIndex(sheetName);
            if (index == -1) return "";

            sheet = workbook.getSheetAt(index);
            row = sheet.getRow(rowNum - 1);
            if (row == null) return "";

            cell = row.getCell(colNum);
            return formatCellValue(cell);
        } catch (Exception e) {
            e.printStackTrace();
            return "row " + rowNum + " or column " + colNum + " does not exist in xls";
        }
    }

    // Set cell data
    public boolean setCellData(String sheetName, String colName, int rowNum, String data) {
        try {
            if (rowNum <= 0) return false;

            fis = new FileInputStream(path);
            workbook = new XSSFWorkbook(fis);

            int index = workbook.getSheetIndex(sheetName);
            if (index == -1) return false;

            sheet = workbook.getSheetAt(index);
            row = sheet.getRow(0);
            if (row == null) return false;

            int colNum = -1;
            for (int i = 0; i < row.getLastCellNum(); i++) {
                if (row.getCell(i).getStringCellValue().trim().equalsIgnoreCase(colName.trim())) {
                    colNum = i;
                    break;
                }
            }
            if (colNum == -1) return false;

            row = sheet.getRow(rowNum - 1);
            if (row == null) row = sheet.createRow(rowNum - 1);

            cell = row.getCell(colNum);
            if (cell == null) cell = row.createCell(colNum);

            cell.setCellValue(data);

            fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Add sheet
    public boolean addSheet(String sheetName) {
        try {
            workbook.createSheet(sheetName);
            fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Remove sheet
    public boolean removeSheet(String sheetName) {
        int index = workbook.getSheetIndex(sheetName);
        if (index == -1) return false;
        try {
            workbook.removeSheetAt(index);
            fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Add column
    public boolean addColumn(String sheetName, String colName) {
        try {
            fis = new FileInputStream(path);
            workbook = new XSSFWorkbook(fis);

            int index = workbook.getSheetIndex(sheetName);
            if (index == -1) return false;

            sheet = workbook.getSheetAt(index);
            row = sheet.getRow(0);
            if (row == null) row = sheet.createRow(0);

            cell = row.createCell(row.getLastCellNum() == -1 ? 0 : row.getLastCellNum());
            cell.setCellValue(colName);

            XSSFCellStyle style = workbook.createCellStyle();
            cell.setCellStyle(style);

            fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Remove column
    public boolean removeColumn(String sheetName, int colNum) {
        try {
            if (!isSheetExist(sheetName)) return false;

            fis = new FileInputStream(path);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);

            for (int i = 0; i < getRowCount(sheetName); i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    cell = row.getCell(colNum);
                    if (cell != null) {
                        row.removeCell(cell);
                    }
                }
            }

            fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Check if sheet exists
    public boolean isSheetExist(String sheetName) {
        int index = workbook.getSheetIndex(sheetName);
        if (index == -1) {
            index = workbook.getSheetIndex(sheetName.toUpperCase());
            return index != -1;
        }
        return true;
    }

    // Get column count
    public int getColumnCount(String sheetName) {
        if (!isSheetExist(sheetName)) return -1;
        sheet = workbook.getSheet(sheetName);
        row = sheet.getRow(0);
        return (row == null) ? -1 : row.getLastCellNum();
    }

    // Get row number of cell with given value
    public int getCellRowNum(String sheetName, String colName, String cellValue) {
        for (int i = 2; i <= getRowCount(sheetName); i++) {
            if (getCellData(sheetName, colName, i).equalsIgnoreCase(cellValue)) {
                return i;
            }
        }
        return -1;
    }

    // Utility: format cell value to string
    private String formatCellValue(Cell cell) {
        if (cell == null) return "";

        CellType type = cell.getCellType();
        switch (type) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
            case FORMULA:
                if (DateUtil.isCellDateFormatted(cell)) {
                    double d = cell.getNumericCellValue();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(DateUtil.getJavaDate(d));
                    return cal.get(Calendar.DAY_OF_MONTH) + "/" +
                           (cal.get(Calendar.MONTH) + 1) + "/" +
                           (String.valueOf(cal.get(Calendar.YEAR)).substring(2));
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}