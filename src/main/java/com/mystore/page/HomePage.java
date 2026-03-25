/**
 * 
 */
package com.mystore.page;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mystore.base.BaseClass;

/**
 * * HomePage
 * -----------
 * Represents the home page after navigation.
 * Handles search, menu, banners, and logo navigation.
 */
public class HomePage extends BaseClass{
	
	   private WebDriver driver;
	    private WebDriverWait wait;

	    // ===== HomePage Elements =====
	    @FindBy(id = "search")
	    private WebElement searchBox;

	    @FindBy(xpath = "//label[@for='search']/span")
	    private WebElement searchIcon;

	    @FindBy(css = "div.kuQuickNoResultsMessage")
	    private WebElement noResultsMessage;

	    private By productNames = By.cssSelector("div.klevuQuickProductName");
	    @FindBy(css = "ul.flex li a.level1-link.has-sub-cat")
	    private List<WebElement> mainMenuItems;

	    @FindBy(xpath = "//img[@alt=' the design store logo']")
	    private WebElement logo;

	    // ===== IndexPage Elements =====
	    @FindBy(css = "a.user-link-dropdown.lm")
	    private WebElement signInRegisterHover;

	    @FindBy(css = "a.signin_link")
	    private WebElement signInButton;

	    @FindBy(css = "a[title='Register']")
	    private WebElement registerButton;

	    private By loginPopupLocator = By.cssSelector("form.popup-login-form, div#login-popup, .login-popup-class");
	    private String loginPageUrlFragment = "/customer/account/login";

	    private By registerPopupLocator = By.cssSelector("form.popup-register-form, div#register-popup, .register-popup-class");
	    private String registerPageUrlFragment = "/customer/account/create";

	    public HomePage(WebDriver driver) {
	        this.driver = getDriver(); // BaseClass method
	        this.wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
	        PageFactory.initElements(getDriver(), this);
	    }

	    // ===== HomePage Methods =====
	    public void searchTerm(String searchText) {
	        searchBox.clear();
	        searchBox.sendKeys(searchText);
	        searchBox.sendKeys(Keys.ENTER);
	    }

	    public List<WebElement> getProductNames() {
	        return driver.findElements(productNames);
	    }

	    public boolean doTopProductsContainSearchTerm(String searchTerm, int topN) {
	        List<WebElement> products = getProductNames();
	        int limit = Math.min(topN, products.size());

	        for (int i = 0; i < limit; i++) {
	            String productName = products.get(i).getText().toLowerCase();
	            if (!productName.contains(searchTerm.toLowerCase())) {
	                System.out.println("❌ Product failed: " + productName);
	                return false;
	            } else {
	                System.out.println("✅ Product matched: " + productName);
	            }
	        }
	        return true;
	    }

	    public boolean isNoResultsMessageDisplayed() throws TimeoutException {
	        wait.until(ExpectedConditions.visibilityOf(noResultsMessage));
	        return noResultsMessage.isDisplayed();
	    }

	    public String getNoResultsText() {
	        return noResultsMessage.getText();
	    }

	    public List<String> getMenuItemNames() {
	        List<String> names = new ArrayList<>();
	        for (WebElement item : mainMenuItems) {
	            names.add(item.getText().trim());
	        }
	        return names;
	    }

	    public void verifyAllMenuItemsClickable() {
	        for (int i = 0; i < mainMenuItems.size(); i++) {
	            try {
	                WebElement menuItem = mainMenuItems.get(i);
	                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", menuItem);
	                wait.until(ExpectedConditions.elementToBeClickable(menuItem));
	                System.out.println("✅ Menu clickable: " + menuItem.getText());
	            } catch (Exception e) {
	                System.out.println("❌ Menu not clickable at index " + i + " - " + e.getMessage());
	            }
	        }
	    }

	    public void clickLogo() {
	        wait.until(ExpectedConditions.elementToBeClickable(logo)).click();
	    }

	    // ===== IndexPage Methods =====
	    public boolean verifyLogo() {
	        boolean isDisplayed = logo.isDisplayed();
	        System.out.println(isDisplayed ? "✅ Logo is displayed" : "❌ Logo is NOT displayed");
	        return isDisplayed;
	    }

	    public String verifyTitle() {
	        return driver.getTitle();
	    }

	    public LoginAble clickAndCheckLogin() {
	        try {
	            Actions actions = new Actions(driver);
	            actions.moveToElement(signInRegisterHover).pause(Duration.ofMillis(1000)).perform();

	            try {
	                signInButton.click();
	            } catch (Exception e) {
	                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signInButton);
	            }

	            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));

	            try {
	                WebElement popup = shortWait.until(ExpectedConditions.visibilityOfElementLocated(loginPopupLocator));
	                if (popup.isDisplayed()) {
	                    log("✅ Login popup detected.");
	                    return new LoginPopUp();
	                }
	            } catch (Exception ignored) {}

	            if (shortWait.until(ExpectedConditions.urlContains(loginPageUrlFragment))) {
	                log("✅ Redirected to login page.");
	                return new LoginPage();
	            }

	            log("❌ Unknown state after login attempt.");
	            throw new IllegalStateException("Neither popup nor login page detected.");

	        } catch (Exception e) {
	            log("❌ Error during login attempt: " + e.getMessage());
	            throw new RuntimeException("Login flow failed", e);
	        }
	    }

	    public Registration clickAndCheckRegisterPopup() {
	        try {
	            Actions actions = new Actions(driver);
	            actions.moveToElement(signInRegisterHover).pause(Duration.ofMillis(500)).perform();

	            try {
	                registerButton.click();
	            } catch (Exception e) {
	                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", registerButton);
	            }

	            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));
	            shortWait.until(ExpectedConditions.or(
	                    ExpectedConditions.visibilityOfElementLocated(registerPopupLocator),
	                    ExpectedConditions.urlContains(registerPageUrlFragment)
	            ));

	            if (isElementVisible(registerPopupLocator)) {
	                log("✅ Register popup detected.");
	                return new AccountCreationPopup();
	            } else if (driver.getCurrentUrl().contains(registerPageUrlFragment)) {
	                log("✅ Redirected to registration page.");
	                return new AccountCreationPage(driver);
	            }

	            log("❌ Unknown state after registration attempt.");
	            throw new IllegalStateException("Neither popup nor registration page detected.");

	        } catch (Exception e) {
	            log("❌ Error during registration attempt: " + e.getMessage());
	            throw new RuntimeException("Register flow failed", e);
	        }
	    }

	    public List<String> getBrokenLinks() {
	        List<String> brokenLinks = new ArrayList<>();
	        List<WebElement> links = driver.findElements(By.tagName("a"));
	        System.out.println("🔗 Total links found: " + links.size());

	        for (WebElement link : links) {
	            String url = link.getAttribute("href");
	            if (url == null || url.isEmpty()) continue;

	            try {
	                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	                connection.setRequestMethod("HEAD");
	                connection.connect();
	                int responseCode = connection.getResponseCode();

	                if (responseCode >= 400) {
	                    brokenLinks.add(url + " → " + responseCode);
	                    System.out.println("❌ Broken link: " + url + " → " + responseCode);
	                } else {
	                    System.out.println("✅ Valid link: " + url);
	                }
	            } catch (Exception e) {
	                brokenLinks.add(url + " → Exception: " + e.getMessage());
	                System.out.println("⚠️ Error checking link: " + url + " → " + e.getMessage());
	            }
	        }
	        return brokenLinks;
	    }

	    private boolean isElementVisible(By locator) {
	        try {
	            return driver.findElement(locator).isDisplayed();
	        } catch (Exception e) {
	            return false;
	        }
	    }

	    private void log(String msg) {
	        System.out.println(msg);
	    }
	}