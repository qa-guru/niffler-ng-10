package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.getSelectedText;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class LoginPage {
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement submitBtn = $("#login-button");
    private final SelenideElement createAccount = $("#register-button");
    private final SelenideElement headerElement = $(".header");

    public LoginPage login(String username, String password) {
        usernameInput.val(username);
        passwordInput.val(password);
        submitBtn.click();
        return this;
    }

    public RegisterPage goToRegisterPage() {
        createAccount.click();
        headerElement.shouldHave(matchText("Sign up"));
        return new RegisterPage();
    }

    public LoginPage checkThatPageLoaded() {
        submitBtn.should(visible);
        return this;
    }

    public LoginPage checkErrorUrl() {
        String url = url();
        assertThat(url, containsString("/login?error"));
        return this;
    }
}
