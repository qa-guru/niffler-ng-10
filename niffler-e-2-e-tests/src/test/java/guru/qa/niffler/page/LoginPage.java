package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
  private final SelenideElement usernameInput = $("#username");
  private final SelenideElement passwordInput = $("#password");
  private final SelenideElement submitBtn = $("#login-button");

  public MainPage login(String username, String password) {
    usernameInput.val(username);
    passwordInput.val(password);
    submitBtn.click();
    return new MainPage();
  }
}
