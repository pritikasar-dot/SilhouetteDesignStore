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

    public CreditBoostPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(40));
    }

    // Locators
    private By bronzeSelectBtn = By.xpath("//form[@id='product_addtocart_form_344803']//button[@title='Select']");
    
    private By billingAddress = By.xpath("//div[@class='billing-address-item-details']");
    
    private By couponTextbox = By.id("discount-code");
    private By applyCouponBtn = By.xpath("//button[@title='Apply Coupon']");
    
    private By savedCardRadio = By.xpath("//input[@id='adyen_cc_vault_37023']");
    private By cvvField = By.xpath("//input[contains(@id,'encryptedSecurityCode')]");
    
    private By placeOrderBtn = By.xpath("//div[contains(@class,'payment-method _active')]//button[contains(@title,'Place Order')]");

    // -----------------------------
    public void navigateToCreditBoostPage() {
        driver.get("https://staging2.silhouettedesignstore.com/get-credits/credit-boosts.html");
    }

    public void selectBronzeAndEnsureCheckout() {

        wait.until(ExpectedConditions.elementToBeClickable(bronzeSelectBtn)).click();

        try {
            wait.until(ExpectedConditions.urlContains("/checkout/#payment"));
        } catch (Exception e) {
            driver.get("https://staging2.silhouettedesignstore.com/checkout/#payment");
        }

        waitForPageLoad();
        System.out.println("✅ Checkout page loaded");
    }

    // -----------------------------
    // Page Load Wait
    // -----------------------------
    public void waitForPageLoad() {
        wait.until(driver ->
                ((JavascriptExecutor) driver)
                        .executeScript("return document.readyState")
                        .equals("complete"));
    }

    // -----------------------------
    public void selectBillingAddress() {
        WebElement address = wait.until(ExpectedConditions.visibilityOfElementLocated(billingAddress));
        scrollAndClick(address);
        System.out.println("✅ Billing address selected");
    }

    public void applyCoupon(String coupon) {

        WebElement couponField = wait.until(ExpectedConditions.elementToBeClickable(couponTextbox));
        couponField.clear();
        couponField.sendKeys(coupon);

        scrollAndClick(wait.until(ExpectedConditions.elementToBeClickable(applyCouponBtn)));

        System.out.println("✅ Coupon applied: " + coupon);
    }

    public void selectSavedCardAndEnterCVV(String cvv) {


    // Step 1: Select saved card
    scrollAndClick(wait.until(ExpectedConditions.elementToBeClickable(savedCardRadio)));

    // Step 2: Wait for CVV iframe to appear
    WebElement cvvFrame = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//iframe[contains(@title,'security code')]")));

    // Scroll to iframe
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", cvvFrame);

    // Step 3: Switch to iframe
    driver.switchTo().frame(cvvFrame);

    // Step 4: Enter CVV
    WebElement cvvInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//input[contains(@id,'encryptedSecurityCode')]")));

    cvvInput.clear();
    cvvInput.sendKeys(cvv);

    // Step 5: Switch back
    driver.switchTo().defaultContent();

    System.out.println("✅ CVV entered inside iframe");
}
public void waitForLoaderToDisappear() throws InterruptedException {
    try {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        shortWait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//div[contains(@class,'loading-mask')]")));

        shortWait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//div[contains(@class,'loader')]")));

    } catch (Exception e) {
        System.out.println("⚠️ Loader not found or already gone");
            Thread.sleep(2000);

    }
}

   public void clickPlaceOrder() throws InterruptedException {

    waitForLoaderToDisappear();
    

    WebElement placeOrder = wait.until(ExpectedConditions.presenceOfElementLocated(placeOrderBtn));

    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", placeOrder);
        wait.until(ExpectedConditions.visibilityOf(placeOrder));


     try {
        placeOrder.click();
    } catch (Exception e) {
        System.out.println("⚠️ Normal click failed, using JS click");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", placeOrder);
    }


    System.out.println("✅ Place Order clicked");
}

    public String waitForOrderSuccessAndGetOrderId() {

       // 1. Wait for URL to reach success
    wait.until(ExpectedConditions.urlContains("success"));

    // 2. Wait for the Page Title to actually contain digits (max 10 sec)
    // This replaces Thread.sleep(3000)
    wait.until(d -> d.getTitle().matches(".*\\d+.*"));

    String title = driver.getTitle();
    System.out.println("📄 Page Title: " + title);

    // 3. Extract only the digits
    String orderId = title.replaceAll("[^0-9]", "");

    if (orderId.isEmpty()) {
        throw new RuntimeException("❌ Order ID not found in Title: " + title);
    }

    System.out.println("✅ Captured Credit Order ID: " + orderId);
    return orderId;
    }

    // -----------------------------
    private void scrollAndClick(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }
}