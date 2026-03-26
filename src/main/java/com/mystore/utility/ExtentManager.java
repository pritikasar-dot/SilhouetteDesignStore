package com.mystore.utility;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports getInstance() {

        if (extent == null) {

            String path = System.getProperty("user.dir") + "/test-output/ExtentReport.html";

            ExtentSparkReporter spark = new ExtentSparkReporter(path);

            spark.config().setTheme(Theme.DARK);
            spark.config().setReportName("🚀 Automation Execution Dashboard");
            spark.config().setDocumentTitle("Silhouette Test Report");

            // 🔥 CUSTOM UI
            spark.config().setCss(getCustomCSS());
            spark.config().setJs(getCustomJS());

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo("Project", "Silhouette Design Store");
            extent.setSystemInfo("Tester", "Priti Kasar");
            extent.setSystemInfo("Environment", "Staging2");
            extent.setSystemInfo("Framework", "Selenium + TestNG");

        }

        return extent;
    }

    // 🎨 CUSTOM CSS
    private static String getCustomCSS() {
        return """
        body { background-color: #0f172a !important; }

        .navbar {
            background: linear-gradient(90deg,#4facfe,#00f2fe) !important;
        }

        .brand-logo {
            font-size: 20px !important;
            font-weight: bold;
        }

        .test.pass {
            border-left: 5px solid #00ffae !important;
        }

        .test.fail {
            border-left: 5px solid #ff4d4d !important;
        }

        .test.skip {
            border-left: 5px solid #ffc107 !important;
        }

        .node {
            border-radius: 10px !important;
        }

        .card-panel {
            border-radius: 12px !important;
            box-shadow: 0px 4px 20px rgba(0,0,0,0.5) !important;
        }
        """;
    }

    // ⚡ CUSTOM JS
    private static String getCustomJS() {
        return """
        document.addEventListener("DOMContentLoaded", function() {

            // Auto open failed tests
            document.querySelectorAll('.test.fail').forEach(el => el.click());

            // Hover animation
            document.querySelectorAll('.test').forEach(el => {
                el.style.transition = "0.3s";
                el.addEventListener("mouseenter", () => el.style.transform = "scale(1.02)");
                el.addEventListener("mouseleave", () => el.style.transform = "scale(1)");
            });

        });
        """;
    }
}