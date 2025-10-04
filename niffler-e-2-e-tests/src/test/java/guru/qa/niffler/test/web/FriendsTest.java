package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({BrowserExtension.class, UsersQueueExtension.class})
public class FriendsTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void shouldNotSeeFriendForEmptyUser(@UserType(UserType.Type.EMPTY) UsersQueueExtension.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password());
        new MainPage().goToFriendsPage()
                .checkEmptyStateOfFriends();
    }

    @Test
    void shouldSeeFriendForUserWithFriend(@UserType(UserType.Type.WITH_FRIEND) UsersQueueExtension.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password());
        new MainPage().goToFriendsPage()
                .checkThatFriendIsVisible(user.friend());
    }

    @Test
    void shouldSeeOutcomeRequest(@UserType(UserType.Type.WITH_OUTCOME_REQUEST) UsersQueueExtension.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password());
        new MainPage().goToFriendsPage()
                .clickTabAllPeople()
                .find(user.outcome())
                .checkStatusWaiting(user.outcome());
    }

    @Test
    void shouldSeeIncomeRequest(@UserType(UserType.Type.WITH_INCOME_REQUEST) UsersQueueExtension.StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password());
        new MainPage().goToFriendsPage()
                .find(user.income())
                .checkFriendsRequest(user.income());
    }
}
