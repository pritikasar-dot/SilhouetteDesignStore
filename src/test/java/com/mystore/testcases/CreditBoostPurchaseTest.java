package com.mystore.testcases;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mystore.base.BaseClass;
import com.mystore.page.*;

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

        // ✅ Clear cart before purchase
        cartPage.navigateToCart();
        cartPage.clearCartIfNotEmpty();

        // Navigate to credit boost page
        creditPage.navigateToCreditBoostPage();
    }

    @Test(description = "Purchase Bronze Credit Boost with clean cart")
    public void purchaseCreditBoost() {

        // Step 1: Select product & ensure checkout
        creditPage.selectBronzeAndEnsureCheckout();

        // Step 2: Apply coupon
        creditPage.applyCoupon("gajanan100");

        // Step 3: Enter payment details
        creditPage.enterCardDetails(
                "4111111145551142",
                "03/30",
                "737"
        );

        // Step 4: Place order
        creditPage.clickPlaceOrder();

        // Step 5: Validate success
        creditPage.waitForOrderSuccess();

        System.out.println("✅ Purchase completed successfully.");
    }
}