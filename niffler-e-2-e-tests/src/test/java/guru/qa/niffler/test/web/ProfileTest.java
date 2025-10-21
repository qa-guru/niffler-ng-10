package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProfileTest {
    private static final Config CFG = Config.getInstance();
    private static final String userLogin = "User_3";
    private static final String userPassword = "12345";
    private LoginPage loginPage;
    private MainPage mainPage = new MainPage();
    private ProfilePage profilePage = new ProfilePage();

    @BeforeEach
    void setUp() {
        loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        loginPage.login(userLogin, userPassword);
        mainPage.goToProfilePage();
    }

    @Category(
            username = userLogin,
            archived = false)
    @Test
    void activeCategoryShouldPresentInCategoryList(CategoryJson category) {
    profilePage.checkCategoryIsDisplayed(category.name());

    }
    @Category(
            username = userLogin,
            archived = true)
    @Test
    void archivedCategoryShouldNotBePresentedInActiveCategoryList(CategoryJson category) {
    profilePage.showActiveAndArchivedCategoriesList();
    profilePage.checkArchiveCategoryIsDisplayed(category.name());

    }

}
