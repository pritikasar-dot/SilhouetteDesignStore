package com.mystore.page;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.mystore.base.BaseClass;

public class NewDesignsPage extends BaseClass {

    // Locator for all product links on the "New Designs" page
    private By productLinks = By.xpath("//a[@aria-label='Product Page Link' and contains(@href, 'silhouettedesignstore.com')]");

    public NewDesignsPage() {
        PageFactory.initElements(getDriver(), this);
    }

    /**
     * Fetches all product URLs from the new designs page.
     * @return List of product URLs as Strings
     */
    public List<String> getAllProductLinks() {
        List<WebElement> products = getDriver().findElements(productLinks);
        List<String> links = new ArrayList<>();

        for (WebElement product : products) {
            String href = product.getAttribute("href");
            if (href != null && !href.trim().isEmpty()) {
                links.add(href);
            }
        }
        return links;
    }
}