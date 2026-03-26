package com.mystore.testcases;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.mystore.base.BaseClass;
import com.mystore.page.*;
import com.mystore.utility.ExcelUtility;
import com.mystore.utility.OrderContext;

public class SubscriptionPlanPurchaseTest extends BaseClass {

    HomePage homepage;
    LoginAble login;
    MyAccount myAccount;
    SubscriptionPlanPage subPage;
    ShoppingCartPage cartPage;

    @BeforeMethod
    public void setup() throws Throwable {
        launchApp();
        homepage = new HomePage(getDriver());
        login = homepage.clickAndCheckLogin();

        Assert.assertNotNull(login, "❌ Login page not opened");

        login.enterEmail(prop.getProperty("username"));
        login.enterPassword(prop.getProperty("password"));
        login.clickSignIn();

        myAccount = new MyAccount(getDriver());
        Assert.assertTrue(myAccount.isUserLoggedIn(), "❌ Login failed");

        subPage = new SubscriptionPlanPage(getDriver());
        cartPage = new ShoppingCartPage(getDriver());

        // Clean start
        cartPage.navigateToCart();
        cartPage.clearCartIfNotEmpty();

        // Step: Redirect to subscription plans
        subPage.navigateToSubscriptionPage();
    }

    @Test(description = "Subscription : Purchase Subscription Plan with saved card.")
    public void purchaseSubscriptionPlan() throws Exception {

        // Step 1: Select Plan
        subPage.selectBasicPlan();

        // Step 2: Checkout from Cart
        subPage.clickProceedToCheckout();

        // Step 3: Checkout Page Actions
subPage.selectSavedCard();        
        // Note: If you need to enter actual card details here, 
        // call your enterCardDetails method before accepting terms.
        Thread.sleep(2000);

        // Step 4: Terms and Subscribe
        subPage.acceptTermsAndSubscribe();

        // Step 5: Success & Order ID Extraction
        String orderId = subPage.waitForOrderSuccessAndGetOrderId();

        Assert.assertTrue(orderId != null && !orderId.isEmpty(), "❌ Failed to capture Subscription Order ID");

        // ✅ Save globally for Email Utility
        OrderContext.setOrderDetails(orderId, "Subscription");

        // ✅ Save to Excel Record
        ExcelUtility.appendOrderRecord(orderId, "Subscription");

        System.out.println("✅ Subscription purchase flow completed for Order: " + orderId);
    }
}