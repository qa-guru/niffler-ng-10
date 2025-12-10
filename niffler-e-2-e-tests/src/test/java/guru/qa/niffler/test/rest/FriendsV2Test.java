package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.page.RestResponsePage;
import guru.qa.niffler.service.impl.GatewayApiClient;
import guru.qa.niffler.service.impl.GatewayV2ApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTest
public class FriendsV2Test {

  @RegisterExtension
  private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

  private final GatewayV2ApiClient gatewayApiClient = new GatewayV2ApiClient();

  @User(
      incomeInvitations = 1,
      friends = 2
  )
  @ApiLogin
  @Test
  void allFriendsAndIncomeInvitationsShouldBeReturned(@Token String token) {
    final RestResponsePage<UserJson> result = gatewayApiClient.allFriends(token, 0, 10, List.of("username,asc"), null);
    assertEquals(3, result.getContent().size());
  }
}
