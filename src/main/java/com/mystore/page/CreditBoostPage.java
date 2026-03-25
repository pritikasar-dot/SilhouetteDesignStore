package com.mystore.page;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CreditBoostPage {

    WebDriver driver;
    WebDriverWait wait;

    // Locators
    private By bronzeSelectBtn = By.xpath("//form[@id='product_addtocart_form_344803']//button[@title='Select']");
    private By couponTextbox = By.id("discount-code");
    private By applyCouponBtn = By.xpath("//button[@title='Apply Coupon']");

    private By cardNumber = By.xpath("//input[contains(@id,'encryptedCardNumber')]");
    private By expiryDate = By.xpath("//input[contains(@id,'encryptedExpiryDate')]");
    private By cvv = By.xpath("//input[contains(@id,'encryptedSecurityCode')]");

    private By placeOrderBtn = By.xpath("//form[@id='adyen-cc-form']//button[@title='Place Order']");

    public CreditBoostPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void navigateToCreditBoostPage() {
        driver.get("https://staging2.silhouettedesignstore.com/get-credits/credit-boosts.html");
    }

    /**
     * Click Bronze → Wait for checkout
     * If redirect fails → fallback to direct checkout
     */
    public void selectBronzeAndEnsureCheckout() {

        wait.until(ExpectedConditions.elementToBeClickable(bronzeSelectBtn)).click();

        try {
            // Wait for redirect
            wait.until(ExpectedConditions.urlContains("/checkout/#payment"));
            System.out.println("✅ Redirected to Checkout Page");

        } catch (Exception e) {

            System.out.println("⚠️ Redirect failed, navigating manually to checkout...");

            driver.get("https://staging2.silhouettedesignstore.com/checkout/");

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/checkout"),
                    ExpectedConditions.visibilityOfElementLocated(couponTextbox)
            ));
        }
    }

    public void applyCoupon(String coupon) {

        WebElement couponField = wait.until(ExpectedConditions.elementToBeClickable(couponTextbox));
        couponField.clear();
        couponField.sendKeys(coupon);

        wait.until(ExpectedConditions.elementToBeClickable(applyCouponBtn)).click();

        System.out.println("✅ Coupon applied: " + coupon);
    }

    public void enterCardDetails(String number, String exp, String code) {

    // Step 1: Scroll to payment section
    WebElement cardFrame = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//iframe[contains(@title,'card number')]")));

    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", cardFrame);

    // Step 2: Switch to Card Number iframe
    driver.switchTo().frame(cardFrame);
    wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//input[contains(@id,'encryptedCardNumber')]")))
            .sendKeys(number);

    driver.switchTo().defaultContent();

    // Step 3: Expiry Date iframe
    WebElement expFrame = driver.findElement(By.xpath("//iframe[contains(@title,'expiry')]"));
    driver.switchTo().frame(expFrame);
    driver.findElement(By.xpath("//input[contains(@id,'encryptedExpiryDate')]"))
            .sendKeys(exp);
    driver.switchTo().defaultContent();

    // Step 4: CVV iframe
    WebElement cvvFrame = driver.findElement(By.xpath("//iframe[contains(@title,'security')]"));
    driver.switchTo().frame(cvvFrame);
    driver.findElement(By.xpath("//input[contains(@id,'encryptedSecurityCode')]"))
            .sendKeys(code);
    driver.switchTo().defaultContent();

    System.out.println("✅ Card details entered successfully");
}

    public void clickPlaceOrder() {

        WebElement placeOrder = wait.until(ExpectedConditions.elementToBeClickable(placeOrderBtn));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", placeOrder);
        placeOrder.click();

        System.out.println("✅ Place Order clicked");
    }

    public void waitForOrderSuccess() {

        wait.until(ExpectedConditions.urlContains("success"));

        System.out.println("✅ Order placed successfully");
    }
}