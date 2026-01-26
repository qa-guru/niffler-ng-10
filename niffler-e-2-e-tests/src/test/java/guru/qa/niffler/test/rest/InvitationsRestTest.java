package guru.qa.niffler.test.rest;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.jupiter.extension.UserExtension;
import guru.qa.niffler.model.FriendJson;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import guru.qa.niffler.service.impl.GatewayApiClient;
import guru.qa.niffler.service.impl.GatewayV2ApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RestTest
public class InvitationsRestTest {

  @RegisterExtension
  private static final ApiLoginExtension apiLogin = ApiLoginExtension.rest();

  private final GatewayApiClient gatewayApiClient = new GatewayApiClient();
  private final GatewayV2ApiClient gatewayApiV2Client = new GatewayV2ApiClient();
  private final AuthApiClient authApiClient = new AuthApiClient();

  @Test
  @User(incomeInvitations = 1)
  @ApiLogin
  void acceptInvitation_createsFriendshipForBothUsers(UserJson user,
                                                      @Token String bearerToken) {
    final String incomeInvitation = user.testData().incomeInvitations().getFirst().username();

    final UserJson friend = gatewayApiClient.acceptInvitation(bearerToken, new FriendJson(
        incomeInvitation
    ));
    step("Check that response not null", () ->
        assertNotNull(friend)
    );

    step("Check friend in response", () -> {
      assertEquals(user.testData().incomeInvitations().getFirst().username(), friend.username());
      assertEquals(FriendshipStatus.FRIEND, friend.friendshipStatus());
    });

    ThreadSafeCookieStore.INSTANCE.removeAll();
    final String friendToken = "Bearer " + authApiClient.login(friend.username(), UserExtension.DEFAULT_PASSWORD);

    step("Check that friends present in GET /friends request for both users", () ->
        Assertions.assertAll(
            () -> assertEquals(
                1,
                gatewayApiV2Client.allFriends(bearerToken, 0, 10, null, null).getContent().size(),
                "Current user should have friend after accepting"
            ),
            () -> assertEquals(
                1,
                gatewayApiV2Client.allFriends(friendToken, 0, 10, null, null).getContent().size(),
                "Target friend should have friend after accepting"
            )
        )
    );
  }

  @Test
  @User(incomeInvitations = 1)
  @ApiLogin
  void declineInvitation_removesInvitationsForBothUsers(UserJson user,
                                                        @Token String bearerToken) throws Exception {
    final String incomeInvitation = user.testData().incomeInvitations().getFirst().username();

    final UserJson declinedFriend = gatewayApiClient.declineInvitation(bearerToken, new FriendJson(
        incomeInvitation
    ));
    step("Check that response not null", () ->
        assertNotNull(declinedFriend)
    );

    step("Check declined friend in response", () -> {
      assertEquals(user.testData().incomeInvitations().getFirst().username(), declinedFriend.username());
      assertNull(declinedFriend.friendshipStatus());
    });

    ThreadSafeCookieStore.INSTANCE.removeAll();
    final String declinedFriendToken = "Bearer " + authApiClient.login(declinedFriend.username(), UserExtension.DEFAULT_PASSWORD);

    step("Check that outcome & income invitations removed for both users", () ->
        Assertions.assertAll(
            () -> assertTrue(
                gatewayApiV2Client.allFriends(bearerToken, 0, 10, null, null).isEmpty(),
                "Current user should not have income invitations & friends after declining"),
            () -> assertNull(
                gatewayApiV2Client.allUsers(declinedFriendToken, 0, 10, null, null).getContent().getFirst().friendshipStatus(),
                "Inviter should not have outcome invitations after declining"),
            () -> assertTrue(
                gatewayApiV2Client.allFriends(declinedFriendToken, 0, 10, null, null).isEmpty(),
                "Inviter should not have friends after declining"
            )
        )
    );
  }

  @Test
  @User(others = 1)
  @ApiLogin
  void sendInvitation_createsOutcomeForCurrent_andIncomeForTarget(UserJson user,
                                                                  @Token String bearerToken) throws Exception {
    final String friendWillBeAdded = user.testData().othersUsernames()[0];

    final UserJson outcomeInvitation = gatewayApiClient.sendInvitation(bearerToken, new FriendJson(
        friendWillBeAdded
    ));
    step("Check that response not null", () ->
        assertNotNull(outcomeInvitation)
    );

    step("Check invitation in response", () -> {
      assertEquals(friendWillBeAdded, outcomeInvitation.username());
      assertEquals(FriendshipStatus.INVITE_SENT, outcomeInvitation.friendshipStatus());
    });


    ThreadSafeCookieStore.INSTANCE.removeAll();
    final String friendToken = "Bearer " + authApiClient.login(friendWillBeAdded, UserExtension.DEFAULT_PASSWORD);

    step("Check that friends request & income invitation present for both users", () ->
        Assertions.assertAll(
            () -> assertEquals(
                FriendshipStatus.INVITE_SENT,
                gatewayApiV2Client.allUsers(bearerToken, 0, 10, null, null)
                    .getContent()
                    .getFirst()
                    .friendshipStatus(),
                "Current user should have outcome invitation after adding"),
            () -> assertEquals(
                FriendshipStatus.INVITE_RECEIVED,
                gatewayApiV2Client.allFriends(friendToken, 0, 10, null, null)
                    .getContent()
                    .getFirst()
                    .friendshipStatus(),
                "Target friend should have 1 income invitation"
            )
        )
    );
  }
}
