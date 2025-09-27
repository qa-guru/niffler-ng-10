package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
@Getter
public class LoginPage {
  private final SelenideElement usernameInput = $("#username");
  private final SelenideElement passwordInput = $("#password");
  private final SelenideElement submitBtn = $("#login-button");
  private final SelenideElement createNewAccountBtn = $("#register-button");
  private final SelenideElement messageAfterIncorrectAuth = $(By.xpath("//p[@class=\"form__error\"]"));


  public MainPage login(String username, String password) {
    usernameInput.val(username);
    passwordInput.val(password);
    submitBtn.click();
    return new MainPage();
  }
  public LoginPage incorrectLogin(String shortUsername, String shortPassword) {
    usernameInput.val(shortUsername);
    passwordInput.val(shortPassword);
    submitBtn.click();
    return new LoginPage();
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
  public String getTextFromMessage(SelenideElement element){
    return element.getText();
  }



}
