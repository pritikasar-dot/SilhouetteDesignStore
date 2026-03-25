package com.mystore.page;

import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

public class SubscriptionPlanPage {

    WebDriver driver;
    WebDriverWait wait;

    public SubscriptionPlanPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    // ================= LOCATORS =================

    private By loaderMask = By.cssSelector("div.loading-mask");

    private By basicPlanBtn = By.xpath("//div[contains(@class,'month_products')]//div[4]//a[contains(@title,'Subscribe')]");

    private By proceedToCheckout = By.id("proceed_to_checkout");

    private By nameOnCard = By.xpath("//input[@placeholder='J. Smith']");
    private By firstName = By.id("first_name");
    private By lastName = By.id("last_name");
    private By address1 = By.id("address");
    private By address2 = By.id("address_two");
    private By stateDropdown = By.id("state");
    private By city = By.id("city");
    private By postalCode = By.id("postal_code");
    private By phone = By.id("phone");
    private By updateAddressBtn = By.id("update_address");

    private By tacCheckbox = By.id("tac_checkbox");
    private By subscribeBtn = By.id("subscribe_plan");

    // ================= COMMON UTILS =================

    private void waitForLoaderToDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderMask));
        } catch (Exception e) {
            System.out.println("ℹ️ Loader not present or already gone.");
        }
    }

    private void scrollTo(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    private void safeClick(By locator) {
        waitForLoaderToDisappear();

        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollTo(element);

        waitForLoaderToDisappear();

        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private void type(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        scrollTo(element);
        element.clear();
        element.sendKeys(value);
    }

    // ================= NAVIGATION =================

    public void navigateToSubscriptionPage() {
        driver.get("https://staging2.silhouettedesignstore.com/design-credits/subscription-plans");

        waitForLoaderToDisappear();
        wait.until(ExpectedConditions.visibilityOfElementLocated(basicPlanBtn));

        System.out.println("✅ Subscription page loaded");
    }

    // ================= PLAN =================

    public void selectBasicPlan() {
        safeClick(basicPlanBtn);

        wait.until(ExpectedConditions.urlContains("cart"));

        System.out.println("✅ Basic plan selected → Cart page");
    }

    // ================= CHECKOUT =================

    public void clickProceedToCheckout() {
        safeClick(proceedToCheckout);
        System.out.println("✅ Proceeded to checkout");
    }

    // ================= PAYMENT (IFRAME HANDLING) =================

    private void switchToFrameAndType(By frame, By input, String value) {

        WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(frame));
        scrollTo(iframe);

        driver.switchTo().frame(iframe);
        wait.until(ExpectedConditions.visibilityOfElementLocated(input)).sendKeys(value);
        driver.switchTo().defaultContent();
    }

    public void enterCardDetails(String number, String exp, String cvv) {

        switchToFrameAndType(
                By.xpath("//iframe[contains(@title,'card number')]"),
                By.xpath("//input[contains(@id,'encryptedCardNumber')]"),
                number
        );

        switchToFrameAndType(
                By.xpath("//iframe[contains(@title,'expiry')]"),
                By.xpath("//input[contains(@id,'encryptedExpiryDate')]"),
                exp
        );

        switchToFrameAndType(
                By.xpath("//iframe[contains(@title,'security')]"),
                By.xpath("//input[contains(@id,'encryptedSecurityCode')]"),
                cvv
        );

        System.out.println("✅ Card details entered");
    }

    // ================= ADDRESS =================

    public void fillAddressDetails() {

        type(nameOnCard, "Subscription");
        type(firstName, "test");
        type(lastName, "subscription");
        type(address1, "Electra");
        type(address2, "6400 Lookout Rd");

        new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(stateDropdown)))
                .selectByVisibleText("Colorado");

        type(city, "Boulder");
        type(postalCode, "80301");
        type(phone, "2135467890");

        safeClick(updateAddressBtn);

        waitForLoaderToDisappear();

        // ✅ IMPORTANT FIX → scroll to top after update
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");

        System.out.println("✅ Address updated");
    }

    // ================= FINAL =================

    public void acceptTermsAndSubscribe() {

        waitForLoaderToDisappear();

        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(tacCheckbox));
        scrollTo(checkbox);

        if (!checkbox.isSelected()) {
            wait.until(ExpectedConditions.elementToBeClickable(checkbox)).click();
        }

        waitForLoaderToDisappear();

        // avoid sticky header blocking
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,-200)");

        safeClick(subscribeBtn);

        System.out.println("✅ Subscribe clicked");
    }

    public void waitForSuccessPage() {

        wait.until(ExpectedConditions.urlContains("success"));

        try {
            Thread.sleep(3000); // stabilize UI
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("✅ Subscription successful");
    }
}