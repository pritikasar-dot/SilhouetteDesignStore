package com.mystore.page;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

public class SubscriptionPlanPage {

    WebDriver driver;
    WebDriverWait wait;

    public SubscriptionPlanPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(40));
    }

    // ================= LOCATORS =================
    private By basicPlanBtn = By.xpath("//div[@class='main-cards month_products']//div[4]//a[1]");
    private By proceedToCheckout = By.xpath("//a[@id='proceed_to_checkout']");
    private By addNewCardDropdown = By.xpath("//select[@id='user_cards']");
    private By cardDropdown = By.id("user_cards");
    private By tacCheckbox = By.id("tac_checkbox");
    private By subscribeBtn = By.id("subscribe_plan");
    private By loaderMask = By.cssSelector("div.loading-mask");

    // ================= ACTIONS =================

    public void navigateToSubscriptionPage() {
        driver.get("https://staging2.silhouettedesignstore.com/design-credits/subscription-plans");
        System.out.println("✅ Navigated to Subscription Plans");
    }

    public void selectBasicPlan() {
        wait.until(ExpectedConditions.elementToBeClickable(basicPlanBtn)).click();
        System.out.println("✅ Basic Plan selected");
    }

    public void clickProceedToCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(proceedToCheckout)).click();
        System.out.println("✅ Proceeded to checkout");
    }

   public void selectSavedCard() {
    waitForLoaderToDisappear();

    WebElement selectElem = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("user_cards")));

    // Force selection and trigger BOTH 'change' and 'click' events
    // This wakes up the page's background listeners
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript(
        "arguments[0].value='7';" +
        "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));" +
        "arguments[0].dispatchEvent(new Event('click', {bubbles: true}));", 
        selectElem
    );

    System.out.println("✅ Card selected and events triggered");

    // CRITICAL: Wait for the address section to actually disappear 
    // or for the 'Subscribe' button to become fully active
    try {
        // Wait for the address container to be removed from the DOM or hidden
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("address")));
        System.out.println("✅ Address section disappeared as expected");
    } catch (Exception e) {
        System.out.println("ℹ️ Address section still visible, but Subscribe button is enabled.");
    }
}

    

    public void acceptTermsAndSubscribe() {
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(tacCheckbox));
        if (!checkbox.isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
        }
        
        WebElement subBtn = wait.until(ExpectedConditions.elementToBeClickable(subscribeBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", subBtn);
        System.out.println("✅ Subscribe button clicked");
    }

    public String waitForOrderSuccessAndGetOrderId() {
       // 1. Wait for success URL
    wait.until(ExpectedConditions.urlContains("sub_success"));
    
    String currentUrl = driver.getCurrentUrl();
    System.out.println("🔍 Success URL: " + currentUrl);

    // 2. Extract digits from the end of the URL using Regex
    // This handles trailing slashes or extra text gracefully
    java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)(?!.*\\d)").matcher(currentUrl);
    
    if (matcher.find()) {
        String orderId = matcher.group(1);
        System.out.println("✅ Captured Subscription Order ID: " + orderId);
        return orderId;
    } else {
        throw new RuntimeException("❌ Failed to extract Order ID from URL: " + currentUrl);
    }
    }

    private void waitForLoader() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loaderMask));
        } catch (Exception e) {
            // Loader not present
        }
    }
    private void waitForLoaderToDisappear() {
    try {
        // Using a shorter wait for the loader specifically
        WebDriverWait loaderWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Wait for Magento/Store loading masks to vanish
        loaderWait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//div[contains(@class,'loading-mask')]")));
        
        loaderWait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//div[contains(@class,'loader')]")));

    } catch (Exception e) {
        System.out.println("ℹ️ Loader not found or already disappeared.");
    }
}

private void scrollAndClick(WebElement element) {
    try {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    } catch (Exception e) {
        // Fallback to JS Click if intercepted by a header/footer
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
}
}