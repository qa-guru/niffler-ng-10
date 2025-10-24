package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.util.DataUtil.getRandomPassword;
import static guru.qa.niffler.util.DataUtil.getRandomUserName;

public class RegisterTest {
    private static final Config CFG = Config.getInstance();
    private LoginPage loginPage;
    private RegisterPage registerPage;


    @BeforeEach
    void setUp() {
        loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        registerPage = new RegisterPage();
    }


    @Test
    void shouldRegisterNewUser() {
        String userName = getRandomUserName();
        String userPassword = getRandomPassword();

        loginPage.goToRegistrationPage();
        registerPage.fillAndSubmitRegistration(userName, userPassword, userPassword)
                .checkSuccessMessage()
                .sighInButtonToLoginPage();
        loginPage.checkLoginPageLoaded();
    }

    @Test
    void shouldNotRegisterWithExistingUserName() {
        String userName = getRandomUserName();
        String userPassword = getRandomPassword();
        String userPasswordNew = getRandomPassword();

        loginPage.goToRegistrationPage();
        registerPage.fillAndSubmitRegistration(userName, userPassword, userPassword)
                .checkSuccessMessage()
                .sighInButtonToLoginPage();
        loginPage.checkLoginPageLoaded();
        loginPage.goToRegistrationPage();
        registerPage.fillAndSubmitRegistration(userName, userPasswordNew, userPasswordNew)
                .checkErrorMessageWithText("Username `" + userName + "` already exists");
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String userName = getRandomUserName();
        String userPassword = getRandomPassword();
        String userPasswordNew = userPassword.concat("123");

        loginPage.goToRegistrationPage();
        registerPage.fillAndSubmitRegistration(userName, userPassword, userPasswordNew)
                .checkErrorMessageWithText("Passwords should be equal");
    }

    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        String userName = getRandomUserName();
        String userPassword = getRandomPassword();

        loginPage.goToRegistrationPage();
        registerPage.fillAndSubmitRegistration(userName, userPassword, userPassword)
                .sighInButtonToLoginPage();
        loginPage.login(userName, userPassword)
                .checkThatPageLoaded();
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        String userName = getRandomUserName();
        String userPassword = getRandomPassword();
        String userPasswordNew = userPassword.concat("123");

        loginPage.goToRegistrationPage();
        registerPage.fillAndSubmitRegistration(userName, userPassword, userPassword)
                .sighInButtonToLoginPage();
        loginPage.login(userName, userPasswordNew);
        loginPage.checkLoginPageLoaded();
        registerPage.checkErrorMessageWithText("Неверные учетные данные пользователя");

    }
}
