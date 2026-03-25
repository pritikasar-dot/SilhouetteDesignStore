package com.mystore.testcases;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mystore.base.BaseClass;
import com.mystore.page.*;
import com.mystore.utility.ScreenshotUtil;

public class SubscriptionPlanPurchaseTest extends BaseClass {

    HomePage homepage;
    LoginAble login;
    MyAccount myAccount;
    SubscriptionPlanPage subPage;
    ShoppingCartPage cartPage;

    WebDriverWait wait;

    @BeforeMethod
    public void setup() throws Throwable {

        launchApp();

        // ✅ Initialize wait properly (Fix for explicitwait error)
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(25));

        homepage = new HomePage(getDriver());
        login = homepage.clickAndCheckLogin();

        Assert.assertNotNull(login, "❌ Login page not opened");

        // Login
        login.enterEmail(prop.getProperty("username"));
        login.enterPassword(prop.getProperty("password"));
        login.clickSignIn();

        myAccount = new MyAccount(getDriver());
        Assert.assertTrue(myAccount.isUserLoggedIn(), "❌ Login failed");

        // Initialize pages
        subPage = new SubscriptionPlanPage(getDriver());
        cartPage = new ShoppingCartPage(getDriver());

        // ✅ Clear cart before test
        cartPage.navigateToCart();
        cartPage.clearCartIfNotEmpty();

        // Navigate to subscription page
        subPage.navigateToSubscriptionPage();
    }

    @Test(description = "Purchase Subscription Plan - Basic")
    public void purchaseSubscriptionPlan() throws Exception {

        // Step 1: Select plan → goes to cart
        subPage.selectBasicPlan();
        wait.until(ExpectedConditions.urlContains("cart"));

        // Step 2: Checkout
        subPage.clickProceedToCheckout();
        wait.until(ExpectedConditions.urlContains("checkout"));

        // Step 3: Enter payment details
        subPage.enterCardDetails(
                "4111111145551142",
                "03/30",
                "737"
        );

        // Step 4: Address
        subPage.fillAddressDetails();

        // Step 5: Accept terms & subscribe
        subPage.acceptTermsAndSubscribe();

        // Step 6: Verify success
        subPage.waitForSuccessPage();

        // Step 7: Screenshot
        String path = ScreenshotUtil.captureScreenshot(getDriver(), "SubscriptionSuccess", false);
        System.out.println("📸 Screenshot saved: " + path);

        System.out.println("✅ Subscription purchase completed successfully.");
    }
}