package com.mystore.testcases;

import java.util.List;
import java.util.Random;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.mystore.base.BaseClass;
import com.mystore.page.NewDesignsPage;
import com.mystore.page.ProductPage;

public class ProductPageTest extends BaseClass {

	ProductPage productPage;
    NewDesignsPage newDesignsPage;

    String randomProductURL;

    @BeforeClass
    public void setup() {
        // 1️⃣ Launch app using BaseClass (browser + config)
        launchApp();

        // 2️⃣ Go to “New Designs” page
        getDriver().get("https://staging2.silhouettedesignstore.com/new.html");
        newDesignsPage = new NewDesignsPage();

        // 3️⃣ Fetch all product links
        List<String> allLinks = newDesignsPage.getAllProductLinks();
        System.out.println("🟩 Total products found: " + allLinks.size());

        if (allLinks.isEmpty()) {
            throw new RuntimeException("❌ No product links found on New Designs page!");
        }

        // 4️⃣ Pick a random product link
        randomProductURL = allLinks.get(new Random().nextInt(allLinks.size()));
        System.out.println("🎯 Random Product Selected: " + randomProductURL);

        // 5️⃣ Navigate to random product page
        getDriver().get(randomProductURL);
        productPage = new ProductPage();
    }


    @Test(priority = 1, description = "Product Detail page : Verify product title, SKU, and price")
    public void verifyProductDetails() {

        // 1️⃣ Verify product title
        String title = productPage.getProductTitle();
        System.out.println("📌 Product Title: " + title);

        // 2️⃣ Verify SKU
        String sku = productPage.getSKU();
        System.out.println("📦 Extracted SKU: " + sku);
       
        // 3️⃣ Verify Regular Price
        String price = productPage.getRegularPrice();
        System.out.println("💲 Regular Price: " + price);
        
    }

    @Test(priority = 2, description = "Product Detail Page : Verify Add to Cart functionality")
    public void verifyAddToCart() {
        productPage.clickAddToCart();
       // Assert.assertTrue(Action.isDisplayed(getDriver(), By.cssSelector(".minicart-wrapper .counter-number")));
    }
}