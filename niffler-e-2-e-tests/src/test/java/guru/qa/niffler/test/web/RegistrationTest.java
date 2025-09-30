package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.NewUser;
import guru.qa.niffler.jupiter.extension.CreateSpendingExtension;
import guru.qa.niffler.jupiter.extension.SpendingResolverExtension;
import guru.qa.niffler.model.NewUserModel;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegistrationPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Target;
@ExtendWith({CreateSpendingExtension.class, SpendingResolverExtension.class})
public class RegistrationTest {
    // Позитивные тесты
    private static final Config CFG = Config.getInstance();
    private RegistrationPage registrationTest = new RegistrationPage();
    private LoginPage loginPage = new LoginPage();

    @NewUser
    @Test
    public void checkCorrectRegistrationAndSignInSignIn(NewUserModel newUserModel){
        Selenide.open(CFG.frontUrl(),LoginPage.class).
        goToRegistration();
        registrationTest.registredNewUser(newUserModel.getName(),newUserModel.getPassword(),newUserModel.getSubmitPassword()).
        login(newUserModel.getName(),newUserModel.getPassword())
                .checkThatPageLoaded();
    }

    @Test
    public void checkNavigateToLoginPageAfterClickToLinkInTheRegistrationPage(){
        Selenide.open(CFG.frontUrl(), LoginPage.class).
                goToRegistration().
                backToLoginPageFromRegistrationPage().
                checkThatPageLoad();
    }

    //Негативные тесты
    @Test
    public void checkMessageThenLogoPassIsShort(){
        Selenide.open(CFG.frontUrl(),LoginPage.class).
                goToRegistration();
        registrationTest.checkErrorLoginAndPasswordIfYouInpetShortValueMessage();

        Assertions.assertTrue(registrationTest.getErrorMessageForUsernameFld().isDisplayed());
        Assertions.assertTrue(registrationTest.getErrorMessageForPasswordFld().isDisplayed());
    }
    @Test
    public void checkMessageAfterIncorrectLogin(){
        String expectedMessage = "Неверные учетные данные пользователя";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .incorrectLogin("12","22");
        Assertions.assertEquals(expectedMessage,loginPage.getTextFromMessage(loginPage.getMessageAfterIncorrectAuth()));
    }
    @Test
    public void getTextAfterIncorrectPassAndConfirmPass(){
        String expectedMessage = "Passwords should be equal";
        Selenide.open(CFG.frontUrl(),LoginPage.class)
                .goToRegistration()
                .registredNewUser("user","2222","3333");
        Assertions.assertEquals(expectedMessage,registrationTest.getTextFromElement(registrationTest.getMessagePasswordsShouldBeEqual()));

    }


}
