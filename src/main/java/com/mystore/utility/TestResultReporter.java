package com.mystore.utility;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestResult;
import org.apache.poi.xwpf.usermodel.*;

public class TestResultReporter {

    private static final String REPORT_DIR = System.getProperty("user.dir") + "/test-output/";

    // ---------------- CSV Report ----------------
    public static String generateCSVReport(ISuite suite) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filePath = REPORT_DIR + "TestSummary_" + timestamp + ".csv";

        try {
            new File(REPORT_DIR).mkdirs();
            FileWriter writer = new FileWriter(filePath);
            writer.append("TestCase,Status,ExecutionTime(ms),StartTime\n");

            for (ISuiteResult result : suite.getResults().values()) {
                result.getTestContext().getPassedTests().getAllResults().forEach(r ->
                        writeCSV(writer, r, "PASSED"));
                result.getTestContext().getFailedTests().getAllResults().forEach(r ->
                        writeCSV(writer, r, "FAILED"));
                result.getTestContext().getSkippedTests().getAllResults().forEach(r ->
                        writeCSV(writer, r, "SKIPPED"));
            }

            writer.flush();
            writer.close();
            return filePath;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void writeCSV(FileWriter writer, ITestResult result, String status) {
        try {
            writer.append(result.getMethod().getDescription()).append(",");
            writer.append(status).append(",");
            writer.append(String.valueOf(result.getEndMillis() - result.getStartMillis())).append(",");
            writer.append(new Date(result.getStartMillis()).toString()).append("\n");
        } catch (IOException ignored) {}
    }

    // ---------------- DOCX Report ----------------
    public static String generateDOCXReport(ISuite suite) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filePath = REPORT_DIR + "TestSummary_" + timestamp + ".docx";

        try {
            new File(REPORT_DIR).mkdirs();
            XWPFDocument document = new XWPFDocument();

            XWPFParagraph title = document.createParagraph();
            XWPFRun run = title.createRun();
            run.setText("Automation Test Summary - " + timestamp);
            run.setBold(true);
            run.setFontSize(14);
            document.createParagraph(); // blank line

            for (ISuiteResult suiteResult : suite.getResults().values()) {
                suiteResult.getTestContext().getPassedTests().getAllResults()
                        .forEach(r -> writeDOCX(document, r, "PASSED"));
                suiteResult.getTestContext().getFailedTests().getAllResults()
                        .forEach(r -> writeDOCX(document, r, "FAILED"));
                suiteResult.getTestContext().getSkippedTests().getAllResults()
                        .forEach(r -> writeDOCX(document, r, "SKIPPED"));
            }

            try (FileOutputStream out = new FileOutputStream(filePath)) {
                document.write(out);
            }
            document.close();
            return filePath;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void writeDOCX(XWPFDocument doc, ITestResult result, String status) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        r.setText(result.getMethod().getDescription() + " - " + status + " (" +
                (result.getEndMillis() - result.getStartMillis()) + " ms)");
        if ("FAILED".equals(status)) r.setColor("FF0000");
        else if ("PASSED".equals(status)) r.setColor("008000");
    }
}