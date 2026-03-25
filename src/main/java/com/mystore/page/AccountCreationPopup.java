package com.mystore.page;

import java.time.Duration;
import java.util.concurrent.Callable;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import com.mystore.base.BaseClass;

public class AccountCreationPopup extends BaseClass implements Registration{
	
	@FindBy(name= "firstname")
    private WebElement enterFirstName;

    @FindBy(name = "lastname")
    private WebElement enterLastName;

    @FindBy(name = "email")
    private WebElement enterEmail;

    @FindBy(name = "password")
    private WebElement enterPassword;


    @FindBy(id = "remember_meYtMT7AEXUn")
    private WebElement rememberberME;

    @FindBy(xpath = "//button[@title='Create an account']")
    private WebElement createAccountBtn;

    @FindBy(xpath = "//div[@class='login-register-popup']//div[@class='response-msg']//div[@class='error']")
    private WebElement errorMessage;

    private By successMessage = By.xpath("//div[@class='response-msg']/div[@class='success' and contains(text(), 'Registration successful')]");
    private By requiredFieldError = By.cssSelector("div.mage-error");

    public AccountCreationPopup() {
        PageFactory.initElements(BaseClass.getDriver(), this);
    }

    private WebDriverWait getWait() {
        return new WebDriverWait(BaseClass.getDriver(), Duration.ofSeconds(10));
    }

    private WebElement waitUntilVisible(WebElement element) {
        return getWait().until(ExpectedConditions.visibilityOf(element));
    }

    @Override
    public void enterFirstName(String Fname) {
        WebElement field = waitUntilVisible(enterFirstName);
        field.clear();
        field.sendKeys(Fname);
    }

    @Override
    public void enterLastName(String Lname) {
        WebElement field = waitUntilVisible(enterLastName);
        field.clear();
        field.sendKeys(Lname);
    }

    @Override
    public void enterEmail(String email) {
        WebElement field = waitUntilVisible(enterEmail);
        field.clear();
        field.sendKeys(email);
    }

    @Override
    public void enterPassword(String password) {
        WebElement field = waitUntilVisible(enterPassword);
        field.clear();
        field.sendKeys(password);
    }

    @Override
    public void enterConfirmPassword(String confirmpassword) {
        // Implement if needed
    }

    @Override
    public void clickCreateAccount() {
        try {
            getWait().until(ExpectedConditions.elementToBeClickable(createAccountBtn));
            getWait().until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.loading-mask")));
            try {
                createAccountBtn.click();
            } catch (Exception e) {
                ((JavascriptExecutor) BaseClass.getDriver()).executeScript("arguments[0].click();", createAccountBtn);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to click Create Account button: " + e.getMessage());
        }
    }

    @Override
    public WebElement getCreateAccountButton() {
        return createAccountBtn;
    }

    @Override
    public boolean isCreateAccountButtonDisplayed() {
        try {
            return createAccountBtn.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isUserRegistered() {
        try {
            WebElement msg = getWait().until(ExpectedConditions.visibilityOf(BaseClass.getDriver().findElement(successMessage)));
            return msg.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isErrorDisplayed() {
        try {
            return errorMessage.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public String getErrorMessageText() {
        try {
            return errorMessage.getText().trim();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    @Override
    public boolean isSuccessMessageDisplayed() {
        try {
            WebElement msg = getWait().until(ExpectedConditions.visibilityOf(BaseClass.getDriver().findElement(successMessage)));
            return msg.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public String getSuccessMessageText() {
        try {
            return BaseClass.getDriver().findElement(successMessage).getText();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    @Override
    public boolean isrequiredErrorDisplayed() {
        try {
            WebElement errorMsg = getWait().until(ExpectedConditions.visibilityOf(BaseClass.getDriver().findElement(requiredFieldError)));
            return errorMsg.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public String getrequiredErrorMessageText() {
        try {
            return BaseClass.getDriver().findElement(requiredFieldError).getText();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    @Override
    public void triggerFieldValidation() {
        JavascriptExecutor js = (JavascriptExecutor) BaseClass.getDriver();
        try {
            if (enterFirstName.isDisplayed()) js.executeScript("arguments[0].focus(); arguments[0].blur();", enterFirstName);
            if (enterLastName.isDisplayed()) js.executeScript("arguments[0].focus(); arguments[0].blur();", enterLastName);
            if (enterEmail.isDisplayed()) js.executeScript("arguments[0].focus(); arguments[0].blur();", enterEmail);
            if (enterPassword.isDisplayed()) js.executeScript("arguments[0].focus(); arguments[0].blur();", enterPassword);
        } catch (Exception e) {
            System.out.println("⚠ Failed to trigger field validation: " + e.getMessage());
        }
    }

    @Override
    public boolean waitForErrorDisplayed(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(BaseClass.getDriver(), Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public WebElement getSuccessMessageElement() {
        return getWait().until(ExpectedConditions.visibilityOf(BaseClass.getDriver().findElement(successMessage)));
    }

    @Override
    public WebElement getErrorElement() {
        return getWait().until(ExpectedConditions.visibilityOf(errorMessage));
    }

    @Override
    public WebElement getRequiredErrorElement() {
        return getWait().until(ExpectedConditions.visibilityOf(BaseClass.getDriver().findElement(requiredFieldError)));
    }
}