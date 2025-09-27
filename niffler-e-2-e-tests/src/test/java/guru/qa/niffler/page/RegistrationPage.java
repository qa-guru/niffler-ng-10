package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;


@Getter
public class RegistrationPage {
    //Элементы регистрации
    private final SelenideElement usernameField = $("#username");
    private final SelenideElement passwordField = $("#password");
    private final SelenideElement submitPasswordField = $("#passwordSubmit");
    private final SelenideElement singUpButton = $("#register-button");
    private final SelenideElement backToLoginPage = $(By.xpath("//a[@class=\"form__link\"]"));
    //Сообщения об ошибках
    private final SelenideElement errorMessageForUsernameFld = $(By.xpath("(//span[@class=\"form__error\"])[1]"));
    private final SelenideElement errorMessageForPasswordFld = $(By.xpath("(//span[@class=\"form__error\"])[2]"));
    private final SelenideElement messagePasswordsShouldBeEqual = $(By.xpath("//span[contains(text(),'Passwords should be equal')]"));
    //Сообщение о корректной авторизации
    private final SelenideElement messageAfterCorrectRegistration = $(By.xpath("//p[@class=\"form__paragraph form__paragraph_success\"]"));
    private final SelenideElement signInBtn = $(By.xpath("//a[@class=\"form_sign-in\"]"));

    public  LoginPage registredNewUser(String userName, String password, String submitPassword){
        usernameField.shouldBe(clickable).val(userName);
        passwordField.shouldBe(clickable).val(password);
        submitPasswordField.shouldBe(clickable).val(submitPassword);
        singUpButton.shouldBe(clickable).click();
        if (messageAfterCorrectRegistration.isDisplayed()){
            signInBtn.shouldBe(clickable).click();
        }
        return new LoginPage();
    }

    public RegistrationPage checkErrorLoginAndPasswordIfYouInpetShortValueMessage() {
        usernameField.shouldBe(clickable).val("1");
        passwordField.shouldBe(clickable).val("1");
        submitPasswordField.shouldBe(clickable).val("1");
        singUpButton.shouldBe(clickable).click();
        return this;
    }

    public LoginPage backToLoginPageFromRegistrationPage(){
        backToLoginPage.shouldBe(clickable).click();
        return new LoginPage();
    }

    public String getTextFromElement(SelenideElement element){
        return element.getText();
    }


}
