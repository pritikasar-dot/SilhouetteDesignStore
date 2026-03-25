/**
 * 
 */
package com.mystore.page;

import java.time.Duration;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mystore.base.BaseClass;
import com.mystore.actiondriver.Action;

/**
 * 
 */
public  class LoginPopUp extends BaseClass implements LoginAble{
@FindBy(id="username")  private WebElement Email;

@FindBy(id="popup-password")  private WebElement Password;
 
@FindBy(xpath="//button[@title='Sign In']")  private WebElement SignInBtn;

@FindBy(xpath = "//a[text()=' Create an account']")  private WebElement createAccountLink;

@FindBy(xpath = "//a[text()='Forgot Password?']") private WebElement forgotPasswordLink;

@FindBy(id="remember_me") private WebElement rememberMeCheckbox;

@FindBy(css = "div#user-logged-in a.user-link-dropdown.lm")
private WebElement accountLink;

@FindBy(xpath = "//div[@class='login-register-popup']//div[@class='response-msg']//div[@class='error']")
private WebElement loginErrorMessage;

public LoginPopUp() {
	PageFactory.initElements(getDriver(), this);
}
// Actions


@Override
public void enterEmail(String email) {
    Email.clear();
    Email.sendKeys(email);
}
@Override
public void enterPassword(String password) {
    Password.clear();
    Password.sendKeys(password);
}
@Override
public void clickSignIn() {
    SignInBtn.click();
}

public AccountCreationPopup createAnAccountpopup() {
	Action.click(getDriver(), createAccountLink);
	return new AccountCreationPopup();
}
public ForgotPasswordPopUp ForgotPasswordopup() {
	Action.click(getDriver(), forgotPasswordLink);
	return new ForgotPasswordPopUp();
	}
public void toggleRememberMe() {
    rememberMeCheckbox.click();
}

public boolean isSignInButtonDisplayed() {
	try {
        return SignInBtn.isDisplayed();
    } catch (Exception e) {
        return false;
    }}

public boolean isAccountLinkDisplayed() {
    return accountLink.isDisplayed();
}
@Override
public boolean isUserLoggedIn() {
	// TODO Auto-generated method stub
	return false;
}
public boolean isErrorDisplayed() {
    try {
        return loginErrorMessage.isDisplayed();
    } catch (NoSuchElementException e) {
        return false;
    }
}

public String getErrorMessageText() {
    try {
        return loginErrorMessage.getText().trim();
    } catch (NoSuchElementException e) {
        return "";
    }
}

public boolean waitForErrorDisplayed(int timeoutSeconds) {
    try {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
        return wait.until(ExpectedConditions.visibilityOf(loginErrorMessage)).isDisplayed();
    } catch (Exception e) {
        return false;
    }
}}
