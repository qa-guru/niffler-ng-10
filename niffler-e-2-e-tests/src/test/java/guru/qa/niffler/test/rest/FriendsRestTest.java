package guru.qa.niffler.test.rest;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.jupiter.extension.UserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import guru.qa.niffler.service.impl.GatewayApiClient;
import guru.qa.niffler.service.impl.GatewayV2ApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RestTest
public class FriendsRestTest {

  @RegisterExtension
  private static final ApiLoginExtension apiLogin = ApiLoginExtension.rest();

  private final GatewayApiClient gatewayApiClient = new GatewayApiClient();
  private final GatewayV2ApiClient gatewayApiV2Client = new GatewayV2ApiClient();
  private final AuthApiClient authApiClient = new AuthApiClient();


  @Test
  @User(friends = 1)
  @ApiLogin
  void friendshipShouldBeRemovedByApi(UserJson user,
                                      @Token String bearerToken) {
    final String friendUsername = user.testData().friends().getFirst().username();
    gatewayApiClient.removeFriend(bearerToken, friendUsername);

    ThreadSafeCookieStore.INSTANCE.removeAll();
    String friendToken = "Bearer " + authApiClient.login(friendUsername, UserExtension.DEFAULT_PASSWORD);

    step("Check that no friends present in GET /friends request for both users", () ->
        Assertions.assertAll(
            () -> assertTrue(
                gatewayApiV2Client.allFriends(bearerToken, 0, 10, null, null).isEmpty(),
                "Current user should not have friend after removing"
            ),
            () -> assertTrue(
                gatewayApiV2Client.allFriends(friendToken, 0, 10, null, null).isEmpty(),
                "Target friend should not have friend after removing"
            )
        )
    );
  }
}
