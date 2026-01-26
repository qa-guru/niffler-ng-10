package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.PeoplePage;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@WebTest
public class FriendsTest {

  @Test
  @User(friends = 1)
  @ApiLogin
  void friendShouldBePresentInFriendsTable(UserJson user) {
    final String friendUsername = user.testData().friendsUsernames()[0];

    new MainPage().getHeader()
        .toFriendsPage()
        .checkExistingFriends(friendUsername);
  }

  @Test
  @User
  @ApiLogin
  void friendsTableShouldBeEmptyForNewUser() {
    new MainPage().getHeader()
        .toFriendsPage()
        .checkExistingFriendsCount(0);
  }

  @Test
  @User(incomeInvitations = 1)
  @ApiLogin
  void incomeInvitationBePresentInFriendsTable(UserJson user) {
    final String incomeInvitationUsername = user.testData().incomeInvitationsUsernames()[0];

    new MainPage().getHeader()
        .toFriendsPage()
        .checkExistingInvitations(incomeInvitationUsername);
  }

  @Test
  @User(outcomeInvitations = 1)
  @ApiLogin
  void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
    final String outcomeInvitationUsername = user.testData().outcomeInvitationsUsernames()[0];

    new MainPage().getHeader()
        .toAllPeoplesPage()
        .checkInvitationSentToUser(outcomeInvitationUsername);
  }

  @Test
  @User(friends = 1)
  @ApiLogin
  void shouldRemoveFriend(UserJson user) {
    final String userToRemove = user.testData().friendsUsernames()[0];

    new MainPage().getHeader()
        .toFriendsPage()
        .removeFriend(userToRemove)
        .checkExistingFriendsCount(0);
  }

  @Test
  @User(incomeInvitations = 1)
  @ApiLogin
  void shouldAcceptInvitation(UserJson user) {
    final String userToAccept = user.testData().incomeInvitationsUsernames()[0];

    new MainPage().getHeader()
        .toFriendsPage()
        .checkExistingInvitationsCount(1)
        .acceptFriendInvitationFromUser(userToAccept)
        .checkExistingInvitationsCount(0);

    Selenide.refresh();

    new FriendsPage().checkExistingFriendsCount(1)
        .checkExistingFriends(userToAccept);
  }

  @Test
  @User(incomeInvitations = 1)
  @ApiLogin
  void shouldDeclineInvitation(UserJson user) {
    final String userToDecline = user.testData().incomeInvitationsUsernames()[0];

    new MainPage().getHeader()
        .toFriendsPage()
        .checkExistingInvitationsCount(1)
        .declineFriendInvitationFromUser(userToDecline)
        .checkExistingInvitationsCount(0);

    Selenide.refresh();

    new FriendsPage().checkExistingFriendsCount(0);

    open(PeoplePage.URL, PeoplePage.class)
        .checkExistingUser(userToDecline);
  }
}
