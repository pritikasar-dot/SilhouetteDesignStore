package com.mystore.page;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ShoppingCartPage {

    WebDriver driver;
    WebDriverWait wait;

    // Locators
    private By clearCartBtn = By.xpath("//span[normalize-space()='Clear cart']");
    private By emptyCartMsg1 = By.cssSelector("div.cart-empty p:nth-child(1)");
    private By emptyCartMsg2 = By.xpath("//p[normalize-space()='You have no items in your shopping cart.']");

    public ShoppingCartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void navigateToCart() {
        driver.get("https://staging2.silhouettedesignstore.com/checkout/cart/");
    }

    public boolean isCartEmpty() {
        return driver.findElements(emptyCartMsg1).size() > 0 ||
               driver.findElements(emptyCartMsg2).size() > 0;
    }

    public void clearCartIfNotEmpty() {

        try {
            if (!isCartEmpty()) {

                System.out.println("🛒 Cart has items. Clearing cart...");

                wait.until(ExpectedConditions.elementToBeClickable(clearCartBtn)).click();

                // Wait until cart becomes empty
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(emptyCartMsg1),
                        ExpectedConditions.visibilityOfElementLocated(emptyCartMsg2)
                ));

                System.out.println("✅ Cart cleared successfully.");
            } else {
                System.out.println("ℹ️ Cart already empty.");
            }

        } catch (Exception e) {
            System.out.println("⚠️ Failed to clear cart: " + e.getMessage());
            throw e;
        }
    }
}