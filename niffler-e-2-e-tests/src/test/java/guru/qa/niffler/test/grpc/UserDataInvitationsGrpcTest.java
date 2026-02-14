package guru.qa.niffler.test.grpc;

import guru.qa.niffler.grpc.AcceptInvitationRequest;
import guru.qa.niffler.grpc.AllUsersRequest;
import guru.qa.niffler.grpc.DeclineInvitationRequest;
import guru.qa.niffler.grpc.FriendsRequest;
import guru.qa.niffler.grpc.FriendshipStatus;
import guru.qa.niffler.grpc.SendInvitationRequest;
import guru.qa.niffler.grpc.UserResponse;
import guru.qa.niffler.grpc.UsersResponse;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDataInvitationsGrpcTest extends BaseGrpcTest {

  @Test
  @User(
      incomeInvitations = 1
  )
  void acceptInvitationTest(UserJson user) throws Exception {
    final String currentUsername = user.username();
    final UserJson incomeInvitationUser = user.testData().incomeInvitations().getFirst();

    final UserResponse friend = userdataStub.acceptInvitation(AcceptInvitationRequest.newBuilder()
        .setUsername(currentUsername)
        .setFriendToBeAdded(incomeInvitationUser.username())
        .build());
    step("Check that response not null", () ->
        assertNotNull(friend)
    );

    step("Check friend in response", () -> {
      assertEquals(incomeInvitationUser.username(), friend.getUser().getUsername());
      assertSame(FriendshipStatus.FRIEND, friend.getUser().getFriendshipStatus());
    });

    step("Check that friends present in /friends request for both users", () -> {
          var currentUserResponse = userdataStub.friends(FriendsRequest.newBuilder()
              .setUsername(currentUsername)
              .setPageInfo(DEFAULT_PAGE_INFO)
              .build());
          var targetUserResponse = userdataStub.friends(FriendsRequest.newBuilder()
              .setUsername(incomeInvitationUser.username())
              .setPageInfo(DEFAULT_PAGE_INFO)
              .build());
          assertAll(
              () -> assertEquals(
                  1,
                  currentUserResponse.getUsersList().size(),
                  "Current user should have friend after accepting"
              ),
              () -> assertEquals(
                  FriendshipStatus.FRIEND,
                  currentUserResponse.getUsersList().getFirst().getFriendshipStatus(),
                  "Current user should have friend after accepting"
              ),
              () -> assertEquals(
                  1,
                  targetUserResponse.getUsersList().size(),
                  "Target user should have friend after accepting"
              ),
              () -> assertEquals(
                  FriendshipStatus.FRIEND,
                  targetUserResponse.getUsersList().getFirst().getFriendshipStatus(),
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

    final UserResponse declinedFriend = userdataStub.declineInvitation(DeclineInvitationRequest.newBuilder()
        .setUsername(currentUsername)
        .setInvitationToBeDeclined(incomeInvitationUser.username())
        .build());
    step("Check that response not null", () ->
        assertNotNull(declinedFriend)
    );

    step("Check declined friend in response", () -> {
      assertEquals(incomeInvitationUser.username(), declinedFriend.getUser().getUsername());
      assertSame(FriendshipStatus.FRIENDSHIP_STATUS_UNSPECIFIED, declinedFriend.getUser().getFriendshipStatus());
    });

    step("Check that friends request & income invitation removed for both users", () ->
        assertAll(
            () -> assertTrue(
                userdataStub.friends(FriendsRequest.newBuilder()
                        .setUsername(currentUsername)
                        .setPageInfo(DEFAULT_PAGE_INFO)
                        .build())
                    .getUsersList()
                    .isEmpty(),
                "Current user should not have income invitations after declining"),
            () -> assertEquals(
                FriendshipStatus.FRIENDSHIP_STATUS_UNSPECIFIED,
                userdataStub.allUsers(AllUsersRequest.newBuilder()
                        .setUsername(incomeInvitationUser.username())
                        .setPageInfo(DEFAULT_PAGE_INFO)
                        .build())
                    .getUsersList()
                    .getFirst()
                    .getFriendshipStatus(),
                "Inviter should not have outcome invitations after declining"
            ),
            () -> assertTrue(
                userdataStub.friends(FriendsRequest.newBuilder()
                        .setUsername(incomeInvitationUser.username())
                        .setPageInfo(DEFAULT_PAGE_INFO)
                        .build())
                    .getUsersList()
                    .isEmpty(),
                "Inviter should not have friends after declining"
            )
        )
    );
  }

  @Test
  @User(others = 1)
  void sendInvitationTest(UserJson user) throws Exception {
    final String currentUser = user.username();
    final String friendWillBeRequested = user.testData().othersUsernames()[0];

    final UserResponse targetUserResponse = userdataStub.sendInvitation(SendInvitationRequest.newBuilder()
        .setUsername(currentUser)
        .setFriendToBeRequested(friendWillBeRequested)
        .build());
    step("Check that response not null", () ->
        assertNotNull(targetUserResponse)
    );

    step("Check invitation in response", () -> {
      assertEquals(friendWillBeRequested, targetUserResponse.getUser().getUsername());
      assertEquals(FriendshipStatus.INVITE_SENT, targetUserResponse.getUser().getFriendshipStatus());
    });

    final UsersResponse currentUserPeoplesList = userdataStub.allUsers(AllUsersRequest.newBuilder()
        .setUsername(currentUser)
        .setPageInfo(DEFAULT_PAGE_INFO)
        .build());

    final UsersResponse targetUserFriendsList = userdataStub.friends(FriendsRequest.newBuilder()
        .setUsername(friendWillBeRequested)
        .setPageInfo(DEFAULT_PAGE_INFO)
        .build());

    step("Check that friends request & income invitation present for both users", () ->
        assertAll(
            () -> assertEquals(
                FriendshipStatus.INVITE_SENT,
                currentUserPeoplesList
                    .getUsersList()
                    .getFirst()
                    .getFriendshipStatus(),
                "Current user should have outcome invitation after adding"),
            () -> assertEquals(
                1,
                targetUserFriendsList
                    .getUsersList()
                    .size(),
                "Target friend should have 1 income invitation"
            ),
            () -> assertEquals(
                FriendshipStatus.INVITE_RECEIVED,
                targetUserFriendsList
                    .getUsersList()
                    .getFirst()
                    .getFriendshipStatus(),
                "Target user should have income invitation after adding"
            )
        )
    );
  }
}
