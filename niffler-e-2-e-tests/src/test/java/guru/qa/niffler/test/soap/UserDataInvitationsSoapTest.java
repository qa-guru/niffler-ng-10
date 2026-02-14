package guru.qa.niffler.test.soap;

import guru.qa.jaxb.userdata.AcceptInvitationRequest;
import guru.qa.jaxb.userdata.AllUsersRequest;
import guru.qa.jaxb.userdata.DeclineInvitationRequest;
import guru.qa.jaxb.userdata.FriendsRequest;
import guru.qa.jaxb.userdata.FriendshipStatus;
import guru.qa.jaxb.userdata.SendInvitationRequest;
import guru.qa.jaxb.userdata.UserResponse;
import guru.qa.jaxb.userdata.UsersResponse;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDataInvitationsSoapTest extends BaseSoapTest {

  @Test
  @User(
      incomeInvitations = 1
  )
  void acceptInvitationTest(UserJson user) throws Exception {
    final String currentUsername = user.username();
    final UserJson incomeInvitationUser = user.testData().incomeInvitations().getFirst();

    AcceptInvitationRequest air = acceptInvitationRequest(currentUsername, incomeInvitationUser.username());

    final UserResponse friend = wsClient.acceptInvitationRequest(air);
    step("Check that response not null", () ->
        assertNotNull(friend)
    );

    step("Check friend in response", () -> {
      assertEquals(incomeInvitationUser.username(), friend.getUser().getUsername());
      assertSame(FriendshipStatus.FRIEND, friend.getUser().getFriendshipStatus());
    });

    step("Check that friends present in /friends request for both users", () -> {
          UsersResponse currentUserResponse = wsClient.friendsRequest(friendsRequest(currentUsername, null));
          UsersResponse targetUserResponse = wsClient.friendsRequest(friendsRequest(incomeInvitationUser.username(), null));
          Assertions.assertAll(
              () -> assertEquals(
                  1,
                  currentUserResponse.getUser().size(),
                  "Current user should have friend after accepting"
              ),
              () -> assertEquals(
                  FriendshipStatus.FRIEND,
                  currentUserResponse.getUser().getFirst().getFriendshipStatus(),
                  "Current user should have friend after accepting"
              ),
              () -> assertEquals(
                  1,
                  targetUserResponse.getUser().size(),
                  "Target user should have friend after accepting"
              ),
              () -> assertEquals(
                  FriendshipStatus.FRIEND,
                  targetUserResponse.getUser().getFirst().getFriendshipStatus(),
                  "Target user should have friend after accepting"
              )
          );
        }
    );
  }

  @Test
  @User(
      incomeInvitations = 1
  )
  void declineInvitationTest(UserJson user) throws Exception {
    final String currentUsername = user.username();
    final UserJson incomeInvitationUser = user.testData().incomeInvitations().getFirst();

    DeclineInvitationRequest dir = declineInvitationRequest(currentUsername, incomeInvitationUser.username());

    final UserResponse declinedFriend = wsClient.declineInvitationRequest(dir);
    step("Check that response not null", () ->
        assertNotNull(declinedFriend)
    );

    step("Check declined friend in response", () -> {
      assertEquals(incomeInvitationUser.username(), declinedFriend.getUser().getUsername());
      assertSame(FriendshipStatus.VOID, declinedFriend.getUser().getFriendshipStatus());
    });

    step("Check that friends request & income invitation removed for both users", () ->
        Assertions.assertAll(
            () -> assertTrue(
                wsClient.friendsRequest(friendsRequest(currentUsername, null))
                    .getUser()
                    .isEmpty(),
                "Current user should not have income invitations after declining"),
            () -> assertEquals(
                FriendshipStatus.VOID,
                wsClient.allUsersRequest(allUsersRequest(incomeInvitationUser.username(), null))
                    .getUser()
                    .getFirst()
                    .getFriendshipStatus(),
                "Inviter should not have outcome invitations after declining"
            ),
            () -> assertTrue(
                wsClient.friendsRequest(friendsRequest(incomeInvitationUser.username(), null))
                    .getUser()
                    .isEmpty(),
                "Inviter should not have friends after declining"
            )
        )
    );
  }

  @Test
  @User(
      others = 1
  )
  void sendInvitationTest(UserJson user) throws Exception {
    final String currentUser = user.username();
    final String friendWillBeAdded = user.testData().othersUsernames()[0];

    SendInvitationRequest sir = sendInvitationsRequest(currentUser, friendWillBeAdded);

    final UserResponse targetUserResponse = wsClient.sendInvitationRequest(sir);
    step("Check that response not null", () ->
        assertNotNull(targetUserResponse)
    );

    step("Check invitation in response", () -> {
      assertEquals(friendWillBeAdded, targetUserResponse.getUser().getUsername());
      assertEquals(FriendshipStatus.INVITE_SENT, targetUserResponse.getUser().getFriendshipStatus());
    });

    step("Check that friends request & income invitation present for both users", () ->
        Assertions.assertAll(
            () -> assertEquals(
                FriendshipStatus.INVITE_SENT,
                wsClient.allUsersRequest(allUsersRequest(currentUser, null))
                    .getUser()
                    .getFirst()
                    .getFriendshipStatus(),
                "Current user should have outcome invitation after adding"),
            () -> assertEquals(
                1,
                wsClient.friendsRequest(friendsRequest(friendWillBeAdded, null))
                    .getUser()
                    .size(),
                "Target friend should have 1 income invitation"
            )
        )
    );
  }

  private @Nonnull AllUsersRequest allUsersRequest(@Nonnull String username, @Nullable String searchQuery) {
    AllUsersRequest au = new AllUsersRequest();
    au.setUsername(username);
    au.setSearchQuery(searchQuery);
    return au;
  }

  private @Nonnull FriendsRequest friendsRequest(@Nonnull String username, @Nullable String searchQuery) {
    FriendsRequest fr = new FriendsRequest();
    fr.setUsername(username);
    fr.setSearchQuery(searchQuery);
    return fr;
  }

  private @Nonnull SendInvitationRequest sendInvitationsRequest(@Nonnull String username, @Nonnull String friendUsername) {
    SendInvitationRequest sir = new SendInvitationRequest();
    sir.setUsername(username);
    sir.setFriendToBeRequested(friendUsername);
    return sir;
  }


  private @Nonnull DeclineInvitationRequest declineInvitationRequest(@Nonnull String username, @Nonnull String inviterUsername) {
    DeclineInvitationRequest dir = new DeclineInvitationRequest();
    dir.setUsername(username);
    dir.setInvitationToBeDeclined(inviterUsername);
    return dir;
  }

  private @Nonnull AcceptInvitationRequest acceptInvitationRequest(@Nonnull String username, @Nonnull String inviterUsername) {
    AcceptInvitationRequest air = new AcceptInvitationRequest();
    air.setUsername(username);
    air.setFriendToBeAdded(inviterUsername);
    return air;
  }
}
