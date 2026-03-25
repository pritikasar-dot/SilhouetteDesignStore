package listeners;

import org.testng.*;
import com.aventstack.extentreports.*;
import com.mystore.utility.*;

import org.openqa.selenium.WebDriver;
import com.mystore.base.BaseClass;

public class ExtentTestListener extends BaseClass implements ITestListener {

    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();

        ExtentTest extentTest = extent.createTest(methodName, description);
        test.set(extentTest);

        test.get().info("🟡 Test Started: " + methodName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().pass("✅ Test Passed");
        // No screenshot on success
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().fail(result.getThrowable());

        String screenshotPath = captureScreenshot(result, true);
        if (screenshotPath != null) {
            test.get().addScreenCaptureFromPath(screenshotPath, "Screenshot on Failure");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().skip("⚠️ Test Skipped: " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    private String captureScreenshot(ITestResult result, boolean isFailed) {
        try {
            WebDriver driver = getDriver();
            if (driver == null) return null;

            return ScreenshotUtil.captureScreenshot(driver, result.getMethod().getMethodName(), isFailed);

        } catch (Exception e) {
            System.err.println("❌ Screenshot capture failed: " + e.getMessage());
            return null;
        }
    }

    public static ExtentTest getTest() {
        return test.get();
    }
}
