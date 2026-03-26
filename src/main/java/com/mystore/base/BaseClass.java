package com.mystore.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;

import com.mystore.actiondriver.Action;

import io.github.bonigarcia.wdm.WebDriverManager;
import listeners.ExtentTestListener;

/**
 * BaseClass: Handles Thread-Safe WebDriver initialization and 
 * Staging Basic Authentication.
 */
public class BaseClass {

    public static Properties prop;
    public static ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        DOMConfigurator.configure("log4j.xml");
        loadConfig(); // Load config early
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public void loadConfig() {
        try {
            if (prop == null) {
                prop = new Properties();
                FileInputStream ip = new FileInputStream(
                        System.getProperty("user.dir") + "/Configuration/Config.properties");
                prop.load(ip);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches browser and handles Staging Authentication via URL Injection
     */
    public void launchApp() {
        String browserName = prop.getProperty("browser", "chrome");

       switch (browserName.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver.set(new ChromeDriver());
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver.set(new FirefoxDriver());
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                driver.set(new EdgeDriver());
                break;

            default:
                throw new RuntimeException("❌ Unsupported browser: " + browserName);
        }

        getDriver().manage().window().maximize();
        Action.implicitWait(getDriver(), 10);
        Action.pageLoadTimeOut(getDriver(), 40);

        // --- BASIC AUTH HANDLING ---
        String rawUrl = prop.getProperty("url"); // e.g., https://staging2.silhouettedesignstore.com
        String authUser = prop.getProperty("staging_user", "bharat"); 
        String authPass = prop.getProperty("staging_pass", "9Fb14HMVct");

        // Injecting credentials: https://user:pass@domain.com
        String authenticatedUrl = rawUrl.replace("https://", "https://" + authUser + ":" + authPass + "@");
        
        getDriver().get(authenticatedUrl);
                // --- HANDLE COOKIE CONSENT ---
        handleCookieConsent();

        
    }
    public void logInfo(String message) {
    try {
        ExtentTestListener.getTest().info(message);
    } catch (Exception ignored) {}
}
  /**
     * Handles cookie consent globally for all tests
     */
    private void handleCookieConsent() {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));

            By cookieBtn = By.xpath("//button[normalize-space()='I agree']");

            if (!getDriver().findElements(cookieBtn).isEmpty()) {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(cookieBtn));
                element.click();
                System.out.println("✅ Cookie consent accepted.");
            } else {
                System.out.println("ℹ️ No cookie popup found.");
            }

        } catch (Exception e) {
            System.out.println("⚠️ Cookie handling skipped: " + e.getMessage());
        }
    }
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            driver.remove(); // CRITICAL: Prevents memory leaks in ThreadLocal
        }
    }
}