package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.GatewayV2ApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RestTest
public class FriendsV2RestTest {

  @RegisterExtension
  private static final ApiLoginExtension apiLogin = ApiLoginExtension.rest();

  private final GatewayV2ApiClient gatewayApiClient = new GatewayV2ApiClient();

  private static final Comparator<UserJson> BY_USERNAME_ASC =
      Comparator.comparing(UserJson::username, String.CASE_INSENSITIVE_ORDER);

  private static List<UserJson> orderedByStatusThen(
      List<UserJson> invitations, List<UserJson> friends, Comparator<UserJson> inGroupComparator
  ) {
    final List<UserJson> res = new ArrayList<>();
    invitations.stream().sorted(inGroupComparator).forEach(res::add);
    friends.stream().sorted(inGroupComparator).forEach(res::add);
    return res;
  }

  @ApiLogin
  @User(friends = 3, incomeInvitations = 3)
  @Test
  void shouldReturnInvitationsFirstThenFriends_sortedByUsernameAscByDefault(UserJson user, @Token String token) {
    final List<UserJson> invitations = user.testData().incomeInvitations();
    final List<UserJson> friends = user.testData().friends();

    final Page<UserJson> page = gatewayApiClient.allFriends(token, 0, 50, null, null);

    step("Response contains all entries with size = friends + invitations", () ->
        assertEquals(invitations.size() + friends.size(), page.getContent().size())
    );

    final List<UserJson> expected = orderedByStatusThen(invitations, friends, BY_USERNAME_ASC);
    final List<UUID> expectedIds = expected.stream().map(UserJson::id).toList();
    final List<UUID> actualIds = page.getContent().stream().map(UserJson::id).toList();

    step("Entries are ordered by status (invitations first) and then by username ascending within each status", () ->
        assertIterableEquals(expectedIds, actualIds)
    );
  }

  @ApiLogin
  @User(friends = 5, incomeInvitations = 3)
  @Test
  void paginationShouldRespectBaseOrderingAcrossPages(UserJson user, @Token String token) {
    final List<UserJson> invitations = user.testData().incomeInvitations();
    final List<UserJson> friends = user.testData().friends();

    final List<UserJson> expected = orderedByStatusThen(invitations, friends, BY_USERNAME_ASC);

    final Page<UserJson> p0 = gatewayApiClient.allFriends(token, 0, 2, null, null);
    final Page<UserJson> p1 = gatewayApiClient.allFriends(token, 1, 2, null, null);
    final Page<UserJson> p2 = gatewayApiClient.allFriends(token, 2, 2, null, null);
    final Page<UserJson> p3 = gatewayApiClient.allFriends(token, 3, 2, null, null);

    final List<UUID> actualIds = new ArrayList<>();
    p0.getContent().forEach(u -> actualIds.add(u.id()));
    p1.getContent().forEach(u -> actualIds.add(u.id()));
    p2.getContent().forEach(u -> actualIds.add(u.id()));
    p3.getContent().forEach(u -> actualIds.add(u.id()));

    step("Concatenated pages preserve base ordering (status first, then username asc)", () ->
        assertIterableEquals(expected.stream().map(UserJson::id).toList(), actualIds)
    );
  }

  @ApiLogin
  @User(friends = 2, incomeInvitations = 1)
  @Test
  void searchQueryShouldFindFriendByExactUsername(UserJson user, @Token String token) {
    final UserJson anyFriend = user.testData().friends().getFirst();

    final Page<UserJson> page = gatewayApiClient.allFriends(token, 0, 10, null, anyFriend.username());

    step("Exactly one entry is returned for exact username", () -> assertEquals(1, page.getContent().size()));

    final UserJson found = page.getContent().getFirst();

    step("Found entry has FRIEND status and matches expected id/username", () ->
        assertAll(
            () -> assertEquals(FriendshipStatus.FRIEND, found.friendshipStatus()),
            () -> assertEquals(anyFriend.id(), found.id()),
            () -> assertEquals(anyFriend.username(), found.username())
        )
    );
  }

  @ApiLogin
  @User(friends = 1, incomeInvitations = 2)
  @Test
  void searchQueryShouldFindInvitationByExactUsername(UserJson user, @Token String token) {
    final UserJson invitation = user.testData().incomeInvitations().getFirst();

    final Page<UserJson> page = gatewayApiClient.allFriends(token, 0, 10, null, invitation.username());

    step("Exactly one entry is returned for exact username", () -> assertEquals(1, page.getContent().size()));

    final UserJson found = page.getContent().getFirst();

    step("Found entry belongs to income invitations and matches expected id/username", () ->
        assertAll(
            () -> assertEquals(invitation.id(), found.id()),
            () -> assertEquals(invitation.username(), found.username())
        )
    );
  }

  @ApiLogin
  @User(friends = 3, incomeInvitations = 3)
  @Test
  void searchQueryWithNoMatchesShouldReturnEmptyPage(UserJson user, @Token String token) {
    final String absent = "no_such_user_" + UUID.randomUUID();

    final Page<UserJson> page = gatewayApiClient.allFriends(token, 0, 10, null, absent);

    step("No results for a username that does not exist", () ->
        assertEquals(0, page.getContent().size())
    );
  }

  @ApiLogin
  @User(friends = 6, incomeInvitations = 2)
  @Test
  void paginationShouldWorkWithSearchQuery(UserJson user, @Token String token) {
    final List<UserJson> friends = user.testData().friends();
    final String query = friends.getFirst().username();

    final Page<UserJson> p0 = gatewayApiClient.allFriends(token, 0, 1, null, query);
    final Page<UserJson> p1 = gatewayApiClient.allFriends(token, 1, 1, null, query);

    step("First page has at most 1 entry", () -> assertTrue(p0.getContent().size() <= 1));
    step("Second page has at most 1 entry", () -> assertTrue(p1.getContent().size() <= 1));

    if (p0.getContent().size() == 1) {
      step("If search matches a single username, next page must be empty", () ->
          assertEquals(0, p1.getContent().size())
      );
    }
  }

  @ApiLogin
  @User(friends = 3, incomeInvitations = 0)
  @Test
  void defaultOrderingWithinSingleStatusShouldBeAlphabetical(UserJson user, @Token String token) {
    final List<UserJson> friends = user.testData().friends();

    final Page<UserJson> page = gatewayApiClient.allFriends(token, 0, 50, null, null);
    final List<UserJson> returned = page.getContent();

    step("All returned entries are friends (no invitations created)", () ->
        assertTrue(returned.stream().allMatch(u -> u.friendshipStatus() == FriendshipStatus.FRIEND))
    );

    final List<String> expectedOrder = friends.stream().sorted(BY_USERNAME_ASC).map(UserJson::username).toList();
    final List<String> actualOrder = returned.stream().map(UserJson::username).toList();

    step("Within FRIEND status, default ordering is username ascending", () ->
        assertIterableEquals(expectedOrder, actualOrder)
    );
  }

  @ApiLogin
  @User(friends = 0, incomeInvitations = 4)
  @Test
  void defaultOrderingWithinInvitationsShouldBeAlphabetical(UserJson user, @Token String token) {
    final List<UserJson> invitations = user.testData().incomeInvitations();

    final Page<UserJson> page = gatewayApiClient.allFriends(token, 0, 50, null, null);
    final List<UserJson> returned = page.getContent();

    step("All returned entries are invitations (no friends created)", () ->
        assertTrue(returned.stream().allMatch(u -> u.friendshipStatus() != FriendshipStatus.FRIEND))
    );

    final List<String> expected = invitations.stream().sorted(BY_USERNAME_ASC).map(UserJson::username).toList();
    final List<String> actual = returned.stream().map(UserJson::username).toList();

    step("Within invitations group, default ordering is username ascending", () ->
        assertIterableEquals(expected, actual)
    );
  }

  @ApiLogin
  @User(friends = 2, incomeInvitations = 3)
  @Test
  void pageBeyondLastShouldReturnEmpty(UserJson user, @Token String token) {
    final int total = user.testData().friends().size() + user.testData().incomeInvitations().size();

    final int size = 2;
    final int lastPage = (total + size - 1) / size;
    final Page<UserJson> page = gatewayApiClient.allFriends(token, lastPage, size, null, null);

    step("Requesting a page index beyond the last should return empty content", () ->
        assertEquals(0, page.getContent().size())
    );
  }
}
