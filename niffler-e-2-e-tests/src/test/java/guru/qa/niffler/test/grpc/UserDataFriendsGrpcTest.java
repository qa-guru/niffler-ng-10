package guru.qa.niffler.test.grpc;

import guru.qa.niffler.grpc.FriendsRequest;
import guru.qa.niffler.grpc.FriendshipStatus;
import guru.qa.niffler.grpc.RemoveFriendRequest;
import guru.qa.niffler.grpc.UsersResponse;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import java.util.UUID;
import java.util.stream.Stream;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDataFriendsGrpcTest extends BaseGrpcTest {

  @Test
  @User(
      friends = 1,
      incomeInvitations = 1
  )
  void getAllFriendsPageTest(UserJson user) {
    UserJson testFriend = user.testData().friends().getFirst();
    UserJson testInvitation = user.testData().incomeInvitations().getFirst();

    final UsersResponse usersResponse = userdataStub.friends(FriendsRequest.newBuilder()
        .setUsername(user.username())
        .setPageInfo(DEFAULT_PAGE_INFO)
        .build());

    step("Check that response not null", () ->
        assertNotNull(usersResponse)
    );
    step("Check response list size", () ->
        assertEquals(2, usersResponse.getUsersList().size())
    );

    final var foundedInvitation = usersResponse.getUsersList().getFirst();
    final var foundedFriend = usersResponse.getUsersList().getLast();

    step("Check income invitation in response", () -> {
      assertSame(FriendshipStatus.INVITE_RECEIVED, foundedInvitation.getFriendshipStatus());
      assertEquals(testInvitation.id(), UUID.fromString(foundedInvitation.getId()));
      assertEquals(testInvitation.username(), foundedInvitation.getUsername());
    });

    step("Check friend in response", () -> {
      assertSame(FriendshipStatus.FRIEND, foundedFriend.getFriendshipStatus());
      assertEquals(testFriend.id(), UUID.fromString(foundedFriend.getId()));
      assertEquals(testFriend.username(), foundedFriend.getUsername());
    });
  }

  @Test
  @User(
      friends = 2,
      incomeInvitations = 1
  )
  void getFilteredFriendsPageTest(UserJson user) {
    UserJson testFriend = user.testData().friends().getFirst();

    final UsersResponse usersResponse = userdataStub.friends(FriendsRequest.newBuilder()
        .setUsername(user.username())
        .setSearchQuery(testFriend.username())
        .setPageInfo(DEFAULT_PAGE_INFO)
        .build());

    step("Check that response not null", () ->
        assertNotNull(usersResponse)
    );
    step("Check response list size", () ->
        assertEquals(1, usersResponse.getUsersList().size())
    );

    final var foundedFriend = usersResponse.getUsersList().getFirst();

    step("Check friend in response", () -> {
      assertSame(FriendshipStatus.FRIEND, foundedFriend.getFriendshipStatus());
      assertEquals(testFriend.id(), UUID.fromString(foundedFriend.getId()));
      assertEquals(testFriend.username(), foundedFriend.getUsername());
    });
  }

  static Stream<Arguments> getFriendsPageInExpectedSortTest() {
    return Stream.of(
        Arguments.of(

        ),
        Arguments.of(

        )
    );
  }

  @Test
  @User(
      friends = 1
  )
  void removeFriendTest(UserJson user) throws Exception {
    final String currentUsername = user.username();
    final String friendUsername = user.testData().friends().getFirst().username();

    userdataStub.removeFriend(RemoveFriendRequest.newBuilder()
        .setUsername(currentUsername)
        .setFriendToBeRemoved(friendUsername)
        .build());

    step("Check that no friends present in GET /friends request for both users", () ->
        Assertions.assertAll(
            () -> assertTrue(
                userdataStub.friends(FriendsRequest.newBuilder()
                        .setUsername(currentUsername)
                        .setPageInfo(DEFAULT_PAGE_INFO)
                        .build())
                    .getUsersList()
                    .isEmpty(),
                "Current user should not have friend after removing"
            ),
            () -> assertTrue(
                userdataStub.friends(FriendsRequest.newBuilder()
                        .setUsername(friendUsername)
                        .setPageInfo(DEFAULT_PAGE_INFO)
                        .build())
                    .getUsersList()
                    .isEmpty(),
                "Target friend should not have friend after removing"
            )
        )
    );
  }
}
