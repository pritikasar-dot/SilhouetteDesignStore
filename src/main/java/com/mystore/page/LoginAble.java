package com.mystore.page;

/**
 * Common contract for both LoginPopUp and LoginPage.
 */
public interface LoginAble {
	void enterEmail(String email);
    void enterPassword(String password);
    void clickSignIn();

    boolean isSignInButtonDisplayed();
    boolean isUserLoggedIn();

    boolean isErrorDisplayed();
    String getErrorMessageText();
    boolean waitForErrorDisplayed(int timeoutSeconds);
}