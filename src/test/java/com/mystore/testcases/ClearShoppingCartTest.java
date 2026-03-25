package com.mystore.testcases;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mystore.base.BaseClass;
import com.mystore.page.HomePage;
import com.mystore.page.LoginAble;
import com.mystore.page.MyAccount;
import com.mystore.page.ShoppingCartPage;

public class ClearShoppingCartTest extends BaseClass {

    HomePage homepage;
    LoginAble login;
    MyAccount myAccount;
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

        cartPage = new ShoppingCartPage(getDriver());
    }

    @Test(description = "Clear shopping cart if items exist")
    public void clearCartTest() {

        cartPage.navigateToCart();
        cartPage.clearCartIfNotEmpty();

        Assert.assertTrue(cartPage.isCartEmpty(),
                "❌ Cart is not empty after clearing!");

        System.out.println("✅ Cart validation successful.");
    }
}
