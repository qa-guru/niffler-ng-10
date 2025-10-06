package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.NewUser;
import guru.qa.niffler.jupiter.extension.UserGenerateExtension;
import guru.qa.niffler.model.NewUserModel;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegistrationPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UserGenerateExtension.class)
public class RegistrationTest {
    // Позитивные тесты
    private static final Config CFG = Config.getInstance();
    private final RegistrationPage registrationTest = new RegistrationPage();

    @NewUser
    @Test
    public void checkCorrectRegistrationAndSignInSignIn(NewUserModel newUserModel) {
        Selenide.open(CFG.frontUrl(), LoginPage.class).
                goToRegistration();
        registrationTest.registredNewUser(newUserModel.getName(), newUserModel.getPassword(), newUserModel.getSubmitPassword()).
                login(newUserModel.getName(), newUserModel.getPassword())
                .checkThatPageLoaded();
    }

    @Test
    public void checkNavigateToLoginPageAfterClickToLinkInTheRegistrationPage() {
        Selenide.open(CFG.frontUrl(), LoginPage.class).
                goToRegistration().
                backToLoginPageFromRegistrationPage().
                checkThatPageLoad();
    }

    //Негативные тесты
    @Test
    public void checkMessageThenLogoPassIsShort() {
        Selenide.open(CFG.frontUrl(), LoginPage.class).
                goToRegistration();
        registrationTest.
                inputShortLogopass("mes","1","1","Allowed password length should be from 3 to 12 characters");

    }

    @Test
    public void checkMessageAfterIncorrectLogin() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .incorrectLogin("12", "22")
                .checkThatErrorMessageEqual("Неверные учетные данные пользователя");
    }

    @Test
    public void getTextAfterIncorrectPassAndConfirmPass() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .goToRegistration()
                .incorrectRegistredNewUser("user", "2222", "3333")
                .checkMessagePasswordsShouldBeequals("Passwords should be equal");
    }
}
