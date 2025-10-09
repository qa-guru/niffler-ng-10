package guru.qa.niffler.test.web;


import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.annotation.UserType.Type.*;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.*;

@ExtendWith({UsersQueueExtension.class, BrowserExtension.class})
public class QueueTest {
    private static final Config CFG = Config.getInstance();

    @Test
    public void incomeRequestTest(@UserType(userType = WITH_INCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .checkThatPageLoaded()
                .goToFriendsPage()
                .checkThatIncomeInviteExist(user.income());
    }

    @Test
    public void friendRequestTest(@UserType(userType = WITH_FRIEND) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .checkThatPageLoaded()
                .goToFriendsPage()
                .checkThatFriendExist(user.friend());
    }

    @Test
    public void emptyUserTest(@UserType(userType = EMPTY) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .checkThatPageLoaded()
                .goToFriendsPage()
                .checkThatFriendsListIsEmpty();
    }

    @Test
    public void outcomeRequestTest(@UserType(userType = WITH_OUTCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .goToAllPeoplePage()
                .checkOutcomeRequest(user.outcome());
    }
}
