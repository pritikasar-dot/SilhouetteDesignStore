package com.mystore.testcases;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.testng.annotations.Test;

import com.mystore.base.BaseClass;
import com.mystore.page.NewDesignsPage;
import com.mystore.utility.ExcelUtility;

public class FetchProductLinksTest extends BaseClass {

    private static final String EXCEL_PATH = System.getProperty("user.dir") + "/src/test/resources/TestData/TestData.xlsx";
    private static final String SHEET_NAME = "ProductList";

    @Test(description = "Product listing page (New): Fetch product links and append in the excelsheet")
    public void fetchAndWriteProductLinks() throws Exception {
        // 1️⃣ Launch site (URL from Config.properties)
        launchApp();

        // 2️⃣ Navigate to New Designs Page
        getDriver().get("https://staging2.silhouettedesignstore.com/new.html");
        NewDesignsPage newDesigns = new NewDesignsPage();

        // 3️⃣ Fetch product links
        List<String> links = newDesigns.getAllProductLinks();

        // Print them in console
        System.out.println("🟩 Found " + links.size() + " product links:");
        for (String link : links) {
            System.out.println(link);
        }

        // 4️⃣ Convert to format suitable for ExcelUtility
        List<String[]> dataToWrite = new ArrayList<>();
        dataToWrite.add(new String[] {"Product Links"}); // Header
        for (String link : links) {
            dataToWrite.add(new String[] {link});
        }

        // 5️⃣ Clear old data from sheet
        clearSheet(EXCEL_PATH, SHEET_NAME);

        // 6️⃣ Write new product links
        ExcelUtility.writeProductData(EXCEL_PATH, SHEET_NAME, dataToWrite);
    }

    /**
     * Clears existing data from a sheet (replaces with blank)
     */
    private void clearSheet(String excelPath, String sheetName) {
        try {
            File file = new File(excelPath);
            XSSFWorkbook workbook;
            XSSFSheet sheet;

            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
                fis.close();
            } else {
                workbook = new XSSFWorkbook();
            }

            sheet = workbook.getSheet(sheetName);
            if (sheet != null) {
                int lastRow = sheet.getLastRowNum();
                for (int i = lastRow; i >= 0; i--) {
                    Row row = sheet.getRow(i);
                    if (row != null) sheet.removeRow(row);
                }
            } else {
                sheet = workbook.createSheet(sheetName);
            }

            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            workbook.close();
            System.out.println("🧹 Cleared old data from sheet: " + sheetName);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to clear Excel sheet: " + e.getMessage(), e);
        }
    }
}