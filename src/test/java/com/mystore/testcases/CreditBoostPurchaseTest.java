package com.mystore.testcases;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mystore.base.BaseClass;
import com.mystore.page.*;
import com.mystore.utility.ExcelUtility;
import com.mystore.utility.OrderContext;

public class CreditBoostPurchaseTest extends BaseClass {

    HomePage homepage;
    LoginAble login;
    MyAccount myAccount;
    CreditBoostPage creditPage;
    ShoppingCartPage cartPage;

    @BeforeMethod
    public void setup() throws Throwable {

        launchApp();

        homepage = new HomePage(getDriver());
        login = homepage.clickAndCheckLogin();

        if (login == null) {
            Assert.fail("❌ Login page not opened.");
        }

        // Login
        login.enterEmail(prop.getProperty("username"));
        login.enterPassword(prop.getProperty("password"));
        login.clickSignIn();

        myAccount = new MyAccount(getDriver());
        Assert.assertTrue(myAccount.isUserLoggedIn(), "❌ Login failed");

        // Initialize pages
        creditPage = new CreditBoostPage(getDriver());
        cartPage = new ShoppingCartPage(getDriver());

        // Clear cart
        cartPage.navigateToCart();
        cartPage.clearCartIfNotEmpty();

        // Navigate to Credit Boost page
        creditPage.navigateToCreditBoostPage();
    }

    @Test(description = "Purchase Bronze Credit Boost with clean cart")
    public void purchaseCreditBoost() throws InterruptedException {

        // Step 1
        creditPage.selectBronzeAndEnsureCheckout();

        // Step 2
        creditPage.selectBillingAddress();

        // Step 3
        creditPage.applyCoupon("Gajanan100");

        // Step 4
        creditPage.selectSavedCardAndEnterCVV("737");

        // Step 5
        creditPage.clickPlaceOrder();

        // Step 6
        String orderId = creditPage.waitForOrderSuccessAndGetOrderId();

        System.out.println("✅ Purchase completed. Order ID: " + orderId);

        Assert.assertTrue(orderId != null && orderId.length() > 5,
                "❌ Order ID not captured");

        // ✅ Save globally (for email)
        OrderContext.setOrderDetails(orderId, "Credit");

        // ✅ Save in Excel (new method)
        ExcelUtility.appendOrderRecord(orderId, "Credit");
    }
}