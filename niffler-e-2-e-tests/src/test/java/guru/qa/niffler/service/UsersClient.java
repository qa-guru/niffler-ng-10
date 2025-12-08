package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import guru.qa.niffler.service.impl.UsersDbClient;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface UsersClient {
  @Nonnull
  static UsersClient getInstance() {
    return "api".equals(System.getProperty("client.impl"))
        ? new UsersApiClient()
        : new UsersDbClient();
  }

  @Nonnull
  UserJson createUser(String username, String password);

  @Nonnull
  List<UserJson> addIncomeInvitation(UserJson targetUser, int count);

  @Nonnull
  List<UserJson> addOutcomeInvitation(UserJson targetUser, int count);

  @Nonnull
  List<UserJson> addFriend(UserJson targetUser, int count);
}
