package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.AuthApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import retrofit2.Response;

import java.io.IOException;

@ExtendWith(BrowserExtension.class)
public class RegisterTest {

    private static final Config CFG = Config.getInstance();
    private final AuthApiClient authApiClient = new AuthApiClient();

    @Test
    void shouldRegisterNewUser() {
        String username = RandomDataUtils.randomUsername();
        String password = RandomDataUtils.randomUsername();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .goToRegisterPage()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .signIn()
                .checkThatPageLoaded()
                .login(username, password);

        MainPage mainPage = new MainPage();

        mainPage.checkThatPageLoaded()
                .checkStatisticsExist();
    }

    @Test
    void shouldNotRegisterExistingUser() throws IOException {
        String username = RandomDataUtils.randomUsername();
        String password = RandomDataUtils.randomUsername();
        final Response<Void> response = authApiClient.register(username, password);
        Assertions.assertEquals(201, response.code());
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .goToRegisterPage()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submitRegistration()
                .checkError("already exist");
    }

    @Test
    void shouldShowErrorIfPasswordAndSubmittedPasswordNotEqual() {
        String username = RandomDataUtils.randomUsername();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .goToRegisterPage()
                .setUsername(username)
                .setPassword("test01")
                .setPasswordSubmit("test02")
                .submitRegistration()
                .checkError("Passwords should be equal");
    }

    @Test
    void userShouldStayOnLoginPageWithBadCredentials() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("test9494944", "1234454667")
                .checkErrorUrl();
    }
}

