/**
 * 
 */
package com.mystore.page;

/**
 * 
 */
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mystore.base.BaseClass;

public class DesignsPage extends BaseClass {

    private WebDriverWait wait;
    private Actions actions;
    private JavascriptExecutor js;

    @FindBy(css = ".product-item") 
    List<WebElement> productItems;

    @FindBy(css = ".alert-success, .message-success") 
    WebElement successMessage;

    @FindBy(css = ".minicart-wrapper .counter-number") 
    WebElement cartCounter;

    @FindBy(css = ".loading-mask") 
    List<WebElement> loadingMasks;

    public DesignsPage() {
        PageFactory.initElements(getDriver(), this);
        wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
        actions = new Actions(getDriver());
        js = (JavascriptExecutor) getDriver();
    }

    /** Opens the designs page and waits for full load */
    public void openDesignsPage() {
        getDriver().get("https://staging2.silhouettedesignstore.com/designs.html");
        waitUntilPageReady();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".product-item")));
    }

    /** Adds first N products dynamically, with scroll + retry handling */
    public void addFirstNProductsToCart(int n) {
        int addedCount = 0;
        int scrollStep = 800;

        while (addedCount < n) {
            List<WebElement> allProducts = getDriver().findElements(By.cssSelector(".product-item"));

            for (int i = addedCount; i < allProducts.size() && addedCount < n; i++) {
                WebElement product = allProducts.get(i);

                try {
                    scrollIntoView(product);
                    hoverOver(product);
                    WebElement cartIcon = product.findElement(By.cssSelector(".action.tocart, .tocart"));
                    safeClick(cartIcon);
                    waitForLoadingMaskToDisappear();
                    waitForSuccessOrCartUpdate(addedCount + 1);
                    addedCount++;

                } catch (Exception e) {
                    System.out.println("⚠️ Retry adding product " + (addedCount + 1) + " due to: " + e.getMessage());
                    retryAddProduct(product, addedCount + 1);
                }
            }

            // If still less than N, scroll to load more
            if (addedCount < n) {
                scrollPage(scrollStep);
                waitForLoadingMaskToDisappear();
            }
        }

        System.out.println("✅ Successfully added " + addedCount + " products to cart.");
    }

    /** Retry logic for failed product add */
    private void retryAddProduct(WebElement product, int index) {
        try {
            scrollIntoView(product);
            hoverOver(product);
            WebElement cartIcon = product.findElement(By.cssSelector(".action.tocart, .tocart"));
            safeClick(cartIcon);
            waitForLoadingMaskToDisappear();
            waitForSuccessOrCartUpdate(index);
        } catch (Exception ex) {
            System.out.println("❌ Retry failed for product #" + index + ": " + ex.getMessage());
        }
    }

    /** Hover helper */
    private void hoverOver(WebElement element) {
        actions.moveToElement(element).perform();
    }

    /** Safe click with JavaScript fallback */
    private void safeClick(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", element);
        }
    }

    /** Waits for loading mask to disappear */
    private void waitForLoadingMaskToDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loading-mask")));
        } catch (Exception ignored) {}
    }

    /** Wait for either success message or cart count increment */
    private void waitForSuccessOrCartUpdate(int expectedCount) {
        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOf(successMessage),
                ExpectedConditions.textToBePresentInElement(cartCounter, String.valueOf(expectedCount))
            ));
        } catch (TimeoutException e) {
            System.out.println("⚠️ No success message seen for product " + expectedCount);
        }
    }

    /** Scroll page incrementally */
    private void scrollPage(int pixels) {
        js.executeScript("window.scrollBy(0," + pixels + ");");
        waitUntilPageReady();
    }

    /** Scroll element into view */
    private void scrollIntoView(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    /** Wait for document.readyState === complete */
    private void waitUntilPageReady() {
        wait.until(webDriver ->
            js.executeScript("return document.readyState").toString().equals("complete"));
    }

    /** Get current cart count */
    public int getCartCount() {
        try {
            return Integer.parseInt(cartCounter.getText().trim());
        } catch (Exception e) {
            return 0;
        }
    }
}