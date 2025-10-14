package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {
    public final SelenideElement
            usernameInput = $("#username"),
            passwordInput = $("#password"),
            passwordSubmitInput = $("#passwordSubmit"),
            sighUpButton = $("#register-button"),
            signInButton = $(".form_sign-in"), //Кнопка при успешной регистрации
            errorMessageUserAlreadyExist = $(".form__error"), //Username `User_1` already exists
            successMessage = $(".form__paragraph_success");

    String successRegisterText = "Congratulations! You've registered!";


    @Step("Create a new user ")
    public RegisterPage fillAndSubmitRegistration(String userName,String password, String confirmPassword  ) {
        usernameInput.setValue(userName);
        passwordInput.setValue(password);
        passwordSubmitInput.setValue(confirmPassword);
        sighUpButton.click();
        return this;
    }

    @Step("Set user name '{userName}' ")
    public RegisterPage setUsername(String userName) {
        usernameInput.setValue(userName);
        return this;
    }

    @Step("Set password '{password}'")
    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Set password '{password}'")
    public RegisterPage setPasswordSubmit(String password) {
        passwordSubmitInput.setValue(password);
        return this;
    }

    @Step("Click sign up button ")
    public RegisterPage submitRegistration() {
        sighUpButton.click();
        return this;
    }

    @Step("Click sign in button after registration to Login page ")
    public LoginPage sighInButtonToLoginPage() {
        signInButton.click();
        return new LoginPage();
    }

    @Step("Check Error message")
    public RegisterPage checkSuccessMessage() {
        successMessage.shouldHave(text(successRegisterText));
        return this;
    }

    @Step("Check Error message")
    public RegisterPage checkErrorMessage() {
        errorMessageUserAlreadyExist.shouldBe(visible);
        return this;
    }

    @Step("Check Error message")
    public RegisterPage checkErrorMessageWithText(String text) {
        errorMessageUserAlreadyExist.shouldHave(text(text));
        return this;
    }

}
