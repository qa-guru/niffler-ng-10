package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.StaticUser;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.PeoplePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.annotation.UserType.Type.*;

@ExtendWith(BrowserExtension.class)
public class FriendsTest {

    private static final Config CFG = Config.getInstance();
    LoginPage loginPage = new LoginPage();
    MainPage mainPage = new MainPage();
    FriendsPage friendsPage = new FriendsPage();
    PeoplePage peoplePage = new PeoplePage();

    @BeforeEach
    void setUp() {
        loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    @DisplayName("Должен отображаться список друзей")
    void friendShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        loginPage.login(user.username(), user.password());
        mainPage.goToFriendsPage();
        friendsPage.checkExistingFriends(user.friend());

    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    @DisplayName("Таблица друзей должна быть пустой")
    void friendsTableShouldBeEmptyForNewUser(@UserType(EMPTY) StaticUser user) {
        loginPage.login(user.username(), user.password());
        mainPage.goToFriendsPage();
        friendsPage.checkNoExistingFriends();
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    @DisplayName("Должен отображаться входящий запрос на добавление в друзья")
    void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        loginPage.login(user.username(), user.password());
        mainPage.goToFriendsPage();
        friendsPage.checkExistingInvitations(user.income());
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    @DisplayName("Статус добавления в друзья должен быть в статусе Waiting...")
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        loginPage.login(user.username(), user.password());
        mainPage.goToFriendsPage();
        friendsPage.goToPeoplePage();
        peoplePage.checkWaitingOfUserInvitations(user.outcome());
    }
}
