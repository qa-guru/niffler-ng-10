package guru.qa.niffler.test.web;


import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.annotation.UserType.*;
import static guru.qa.niffler.jupiter.annotation.UserType.Type.*;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(UsersQueueExtension.class)
public class QueueTest {
    private static final Config CFG = Config.getInstance();

    @Test
    public void incomeRequestTest(@UserType(empty = WITH_INCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .checkThatPageLoaded()
                .goToFriendsPage()
                .shouldPageHaveBtn();
    }

    @Test
    public void friendRequestTest(@UserType(empty = WITH_FRIEND) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .checkThatPageLoaded()
                .goToFriendsPage()
                .checkPageHaveUnFriendBtn();
    }

    ;

    @Test
    public void emptyUserTest(@UserType(empty = EMPTY) StaticUser user) {
        boolean result = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .checkThatPageLoaded()
                .goToFriendsPage()
                .checkFriendsIfUserIsEmpty();
        assertTrue(result);
    }

    @Test
    public void outcomeRequestTest(@UserType(empty = WITH_OUTCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .goToAllPeoplePage()
                .checkPageHaveWaitingBtn();
    }
}
