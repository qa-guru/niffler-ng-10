package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement submitRegistrationButton = $("#register-button");
    private final SelenideElement signInButton = $(".form_sign-in");
    private final SelenideElement error = $(".form__error");


    public RegisterPage setUsername(String name) {
        usernameInput.val(name);
        return this;
    }

    public RegisterPage setPassword(String pwd) {
        passwordInput.val(pwd);
        return this;
    }

    public RegisterPage setPasswordSubmit(String pwd) {
        passwordSubmitInput.val(pwd);
        return this;
    }

    public RegisterPage submitRegistration() {
        submitRegistrationButton.click();
        return this;
    }

    public LoginPage signIn(){
        signInButton.click();
        return new LoginPage();
    }

    public RegisterPage checkError(String err) {
        error.shouldHave(matchText(err));
        return this;
    }
}
