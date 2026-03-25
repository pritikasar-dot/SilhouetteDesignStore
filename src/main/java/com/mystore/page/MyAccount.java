package com.mystore.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.mystore.base.BaseClass;

public class MyAccount extends BaseClass{
	  WebDriver driver;

	    // User info container
	    @FindBy(css = "div#user-logged-in span.user-name.bs")
	    private WebElement loggedInUserName;

	    @FindBy(css = "div#user-logged-in a.user-link-dropdown.lm")
	    private WebElement accountLink;

	    // Constructor
	    public MyAccount(WebDriver driver) {
	        this.driver = driver;
	        PageFactory.initElements(driver, this);
	    }

	    // Actions
	    public boolean isUserLoggedIn() {
	        return loggedInUserName.isDisplayed();
	    }

	    public String getLoggedInUserName() {
	        return loggedInUserName.getText();
	    }

	    public boolean isAccountLinkDisplayed() {
	        return accountLink.isDisplayed();
	    }
	}

