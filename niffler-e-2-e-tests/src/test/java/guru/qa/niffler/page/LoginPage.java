package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
  private final SelenideElement usernameInput = $("#username");
  private final SelenideElement passwordInput = $("#password");
  private final SelenideElement logInButton = $("#login-button");
  private final SelenideElement logInWithPasskeyButton = $("#login-with-passkey-button");
  private final SelenideElement registerButton = $("#register-button");
  private final SelenideElement errorMessage = $(".form__error");

  public MainPage login(String username, String password) {
    usernameInput.val(username);
    passwordInput.val(password);
    logInButton.click();
    return new MainPage();
  }

  @Step("Go To Registration Page From Login Page")
  public RegisterPage goToRegistrationPage() {
    registerButton.click();
    return new RegisterPage();
  }

  public LoginPage checkLoginPageElements() {
    usernameInput.shouldBe(visible);
    passwordInput.shouldBe(visible);
    logInButton.shouldBe(visible);
    logInWithPasskeyButton.shouldBe(visible);
    registerButton.shouldBe(visible);
    return this;
  }
}
