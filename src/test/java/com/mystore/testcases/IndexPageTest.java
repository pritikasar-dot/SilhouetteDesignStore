/**
 * 
 */
package com.mystore.testcases;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.mystore.base.BaseClass;
import com.mystore.page.HomePage;
import com.mystore.utility.Log;

/* IndexPageTest
 * ---------------
 * Sanity & end-to-end tests for index page features:
 * - Logo
 * - Title
 * - Broken links
 * - Navigation
 * - Menu
 * - Banner
 */
@Listeners(listeners.ExtentTestListener.class)
public class IndexPageTest extends BaseClass {

    private HomePage homepage;

    @BeforeMethod
    public void setup() {
        launchApp();
        Log.info("✅ Browser launched and application opened");
    }

    @Test(description = "HomePage : Verify the homepage logo", groups = {"sanity", "smoke"}, priority = 1)
    public void verifyLogo() throws Throwable {
        Log.startTestCase("Verify Homepage Logo");

        homepage = new HomePage(getDriver());
        boolean isLogoPresent = homepage.verifyLogo();

        Log.info("Checking if homepage logo is displayed");
        Assert.assertTrue(isLogoPresent, "❌ Logo is not present on the homepage");

        Log.info("✅ Homepage logo is present");
        Log.endTestCase("Verify Homepage Logo");
    }

    @Test(description = "HomePage : Verify the homepage title", groups = {"sanity", "smoke"}, priority = 2)
    public void verifyTitle() {
        Log.startTestCase("Verify Homepage Title");

        homepage = new HomePage(getDriver());
        String actualTitle = homepage.verifyTitle();

        Log.info("Actual page title: " + actualTitle);
        Assert.assertEquals(actualTitle,
                "Crafting Made Easy with Digital Designs & Fonts",
                "❌ Page title did not match the expected value");

        Log.info("✅ Homepage title verified successfully");
        Log.endTestCase("Verify Homepage Title");
    }

   /* @Test(description = "Verify there are no broken links on the homepage", groups = {"system"})
    public void verifyBrokenLinks() {
        Log.startTestCase("Verify Broken Links");

        homepage = new HomePage(getDriver());
        List<String> brokenLinks = homepage.getBrokenLinks();

        if (brokenLinks.isEmpty()) {
            Log.info("✅ No broken links found on the homepage");
        } else {
            Log.error("❌ Broken links found: " + String.join(", ", brokenLinks));
        }

        Assert.assertTrue(brokenLinks.isEmpty(), "Broken links detected on the homepage");
        Log.endTestCase("Verify Broken Links");
    }*/

    @Test(description = "HomePage : Verify navigation to homepage via logo", groups = {"sanity", "smoke"}, priority = 3)
    public void testNavigationToHomeViaLogo() {
        Log.startTestCase("Verify Navigation via Logo");

        homepage = new HomePage(getDriver());
        homepage.clickLogo();

        String expectedUrl = "https://staging2.silhouettedesignstore.com/";
        String actualUrl = getDriver().getCurrentUrl();

        Log.info("Navigated URL: " + actualUrl);
        Assert.assertEquals(actualUrl, expectedUrl, "❌ Navigation via logo failed");

        Log.info("✅ Navigation via homepage logo verified successfully");
        Log.endTestCase("Verify Navigation via Logo");
    }

    @AfterMethod
    public void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            Log.info("✅ Browser closed after test execution");
        }
    }
}