package com.mystore.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotUtil {

    private static final String SCREENSHOT_DIR = System.getProperty("user.dir") + "/Screenshots/";

    // Updated method with isFailed flag
    public static String captureScreenshot(WebDriver driver, String testName, boolean isFailed) {
        try {
            File dir = new File(SCREENSHOT_DIR);
            if (!dir.exists()) dir.mkdirs();
               // 🔴 Step 1: Delete ALL existing screenshots
            File[] existingFiles = dir.listFiles((d, name) -> name.endsWith(".png"));
            if (existingFiles != null) {
                for (File file : existingFiles) {
                    file.delete();
                }
            }

            // Build prefix based on failure or not
            String prefix = isFailed ? testName + "_FAILED_" : testName + "_";

            // Delete old screenshots for this test + failure status
            File[] oldFiles = dir.listFiles((d, name) -> name.startsWith(prefix));
            if (oldFiles != null) {
                for (File f : oldFiles) f.delete();
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String suffix = isFailed ? "_FAILED_" : "_";
            String screenshotPath = SCREENSHOT_DIR + testName + suffix + timestamp + ".png";

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), Path.of(screenshotPath), StandardCopyOption.REPLACE_EXISTING);

            return screenshotPath;

        } catch (IOException e) {
            System.err.println("❌ Screenshot capture failed: " + e.getMessage());
            return null;
        }
    }
}

