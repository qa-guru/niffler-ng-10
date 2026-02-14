package guru.qa.niffler.test.soap;

import guru.qa.jaxb.userdata.FriendsRequest;
import guru.qa.jaxb.userdata.FriendshipStatus;
import guru.qa.jaxb.userdata.RemoveFriendRequest;
import guru.qa.jaxb.userdata.UsersResponse;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDataFriendsSoapTest extends BaseSoapTest {

  @Test
  @User(
      friends = 1,
      incomeInvitations = 1
  )
  void getAllFriendsListTest(UserJson user) throws Exception {
    UserJson testFriend = user.testData().friends().getFirst();
    UserJson testInvitation = user.testData().incomeInvitations().getFirst();

    FriendsRequest fr = friendsRequest(user.username(), null);
    final UsersResponse usersResponse = wsClient.friendsRequest(fr);

    step("Check that response not null", () ->
        assertNotNull(usersResponse)
    );
    step("Check response list size", () ->
        assertEquals(2, usersResponse.getUser().size())
    );

    final var foundedInvitation = usersResponse.getUser().getFirst();
    final var foundedFriend = usersResponse.getUser().getLast();

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
  void getFiltersFriendsListTest(UserJson user) throws Exception {
    UserJson testFriend = user.testData().friends().getFirst();
    FriendsRequest fr = friendsRequest(user.username(), testFriend.username());
    final UsersResponse usersResponse = wsClient.friendsRequest(fr);

    step("Check that response not null", () ->
        assertNotNull(usersResponse)
    );
    step("Check response list size", () ->
        assertEquals(1, usersResponse.getUser().size())
    );

    final var foundedFriend = usersResponse.getUser().getFirst();

    step("Check friend in response", () -> {
      assertSame(FriendshipStatus.FRIEND, foundedFriend.getFriendshipStatus());
      assertEquals(testFriend.id(), UUID.fromString(foundedFriend.getId()));
      assertEquals(testFriend.username(), foundedFriend.getUsername());
    });
  }

  @Test
  @User(
      friends = 1
  )
  void removeFriendTest(UserJson user) throws Exception {
    final String currentUsername = user.username();
    final String friendUsername = user.testData().friends().getFirst().username();

    RemoveFriendRequest rfr = removeFriendRequest(currentUsername, friendUsername);

    wsClient.removeFriendRequest(rfr);

    step("Check that no friends present in GET /friends request for both users", () ->
        Assertions.assertAll(
            () -> assertTrue(
                wsClient.friendsRequest(friendsRequest(currentUsername, null))
                    .getUser()
                    .isEmpty(),
                "Current user should not have friend after removing"
            ),
            () -> assertTrue(
                wsClient.friendsRequest(friendsRequest(friendUsername, null))
                    .getUser()
                    .isEmpty(),
                "Target friend should not have friend after removing"
            )
        )
    );
  }

  private @Nonnull FriendsRequest friendsRequest(@Nonnull String username, @Nullable String searchQuery) {
    FriendsRequest fr = new FriendsRequest();
    fr.setUsername(username);
    fr.setSearchQuery(searchQuery);
    return fr;
  }

  private @Nonnull RemoveFriendRequest removeFriendRequest(@Nonnull String username, @Nonnull String friendUsername) {
    RemoveFriendRequest rfr = new RemoveFriendRequest();
    rfr.setUsername(username);
    rfr.setFriendToBeRemoved(friendUsername);
    return rfr;
  }
}
