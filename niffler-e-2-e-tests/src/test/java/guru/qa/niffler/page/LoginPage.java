package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
  private final SelenideElement usernameInput = $("#username");
  private final SelenideElement passwordInput = $("#password");
  private final SelenideElement submitBtn = $("#login-button");
  private final SelenideElement createNewAccountBtn = $("#register-button");



  public MainPage login(String username, String password) {
    usernameInput.val(username);
    passwordInput.val(password);
    submitBtn.click();
    return new MainPage();
  }

  public RegistrationPage goToRegistration(){

    createNewAccountBtn.shouldBe(Condition.clickable)
            .click();
    return new RegistrationPage();
  }

  public LoginPage checkThatPageLoad(){
    usernameInput.shouldBe(visible);
    passwordInput.shouldBe(visible);
    submitBtn.shouldBe(visible);
    createNewAccountBtn.shouldBe(visible);
    return this;
  }


}
