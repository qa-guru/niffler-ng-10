package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.apollo.api.Error;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.Friends2SubQueriesQuery;
import guru.qa.FriendsQuery;
import guru.qa.FriendsWithCategoriesQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static guru.qa.niffler.model.FriendshipStatus.INVITE_RECEIVED;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FriendsGraphQlTest extends BaseGraphQlTest {

  @Test
  @User
  @ApiLogin
  void emptyFriendsAndInvitationsListShouldReceivedForNewUser(@Token String bearerToken) {
    final ApolloCall<FriendsQuery.Data> apolloCall = apolloClient.query(new FriendsQuery())
        .addHttpHeader("Authorization", bearerToken);

    final ApolloResponse<FriendsQuery.Data> response = Rx2Apollo.single(apolloCall).blockingGet();
    final FriendsQuery.Data responseData = response.dataOrThrow();

    final List<FriendsQuery.Edge> friends = responseData.user.friends.edges;

    step("Check that friends list is empty", () ->
        assertTrue(friends.isEmpty())
    );
  }

  @CsvSource({
      "friends"
  })
  @ParameterizedTest(name = "Получена ошибка Can`t fetch over 2 {0} sub-queries")
  @User(
      friends = 2,
      incomeInvitations = 2
  )
  @ApiLogin
  void errorShouldReceivedForOver2SubQueries(String expectedMessagePart,
                                             @Token String bearerToken) {
    final ApolloCall<Friends2SubQueriesQuery.Data> apolloCall = apolloClient.query(new Friends2SubQueriesQuery())
        .addHttpHeader("Authorization", bearerToken);

    final ApolloResponse<Friends2SubQueriesQuery.Data> response = Rx2Apollo.single(apolloCall).blockingGet();

    final Error firstError = response.errors.getFirst();

    assertNull(response.data);
    assertEquals(
        "Can`t fetch over 2 " + expectedMessagePart + " sub-queries",
        firstError.getMessage()
    );
  }

  @Test
  @User(
      friends = 1,
      incomeInvitations = 1
  )
  @ApiLogin
  void friendsAndIncomeInvitationsListShouldReceived(UserJson user,
                                                     @Token String bearerToken) {
    UserJson friend = user.testData().friends().getFirst();
    UserJson invitation = user.testData().incomeInvitations().getFirst();

    final ApolloCall<FriendsQuery.Data> apolloCall = apolloClient.query(new FriendsQuery())
        .addHttpHeader("Authorization", bearerToken);

    final ApolloResponse<FriendsQuery.Data> response = Rx2Apollo.single(apolloCall).blockingGet();
    final FriendsQuery.Data responseData = response.dataOrThrow();

    final List<FriendsQuery.Edge> friends = responseData.user.friends.edges;

    step("Check friend in response", () -> {
      assertEquals(2, friends.size());
      step("Check sorting by status", () ->
          assertEquals(INVITE_RECEIVED.name(), friends.getFirst().node.friendshipStatus.rawValue)
      );

      final FriendsQuery.Node friendUser = friends.getLast().node;

      assertEquals(friend.id().toString(), friendUser.id);
      assertEquals(friend.username(), friendUser.username);
      assertEquals(FriendshipStatus.FRIEND.name(), friendUser.friendshipStatus.rawValue);
    });
    step("Check income invitation in response", () -> {
      final FriendsQuery.Node invitationUser = friends.getFirst().node;

      assertEquals(invitation.id().toString(), invitationUser.id);
      assertEquals(invitation.username(), invitationUser.username);
      assertEquals(FriendshipStatus.INVITE_RECEIVED.name(), invitationUser.friendshipStatus.rawValue);
    });
  }

  @Test
  @User(
      categories = {
          @Category(name = "Бар")
      },
      friends = 1
  )
  @ApiLogin
  void errorShouldReceivedForOtherPeopleCategories(@Token String bearerToken) {
    final ApolloCall<FriendsWithCategoriesQuery.Data> apolloCall = apolloClient.query(new FriendsWithCategoriesQuery())
        .addHttpHeader("Authorization", bearerToken);

    final ApolloResponse<FriendsWithCategoriesQuery.Data> response = Rx2Apollo.single(apolloCall).blockingGet();

    final Error firstError = response.errors.getFirst();

    assertNotNull(response.data); //because exception in @SchemaMapping, not @QueryMapping
    assertEquals(
        "Can`t query categories for another user",
        firstError.getMessage()
    );
  }
}
