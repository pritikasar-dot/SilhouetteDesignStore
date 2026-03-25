package com.mystore.page;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mystore.base.BaseClass;
import com.mystore.actiondriver.Action;

public class ProductPage extends BaseClass {

    // ================= WebElements =================
    @FindBy(css = "h1.page-title")
    WebElement productName;

    @FindBy(xpath = "//div[@itemprop='sku']")
    WebElement sku;

    @FindBy(xpath = "//span[@data-price-type='finalPrice']//span[@class='price']")
    WebElement price;

    @FindBy(id = "product-addtocart-button")
    WebElement addToCartBtn;

    // ================= Constructor =================
    public ProductPage() {
        PageFactory.initElements(getDriver(), this);
    }

    // ================= Page Actions =================

    /** Get product title */
    public String getProductTitle() {
        return Action.getText(productName).trim();
    }

    /** Get SKU dynamically */
    public String getSKU() {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
            WebElement skuElem = wait.until(ExpectedConditions.visibilityOf(sku));
            String text = skuElem.getText().trim();

            // Extract numeric SKU only
            text = text.replaceAll("[^0-9]", "");

            if (text.isEmpty()) {
                text = "NotFound";
            }
            return text;
        } catch (Exception e) {
            return "NotFound";
        }
    }

    /** Get Regular Price dynamically */
    public String getRegularPrice() {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
            WebElement priceElem = wait.until(ExpectedConditions.visibilityOf(price));
            return Action.getText(priceElem).replaceAll("[^\\d.$]", "").trim(); // Keep $ and digits
        } catch (Exception e) {
            return "N/A";
        }
    }

    /** Click Add to Cart button */
    public void clickAddToCart() {
        Action.click(getDriver(), addToCartBtn);
    }
}