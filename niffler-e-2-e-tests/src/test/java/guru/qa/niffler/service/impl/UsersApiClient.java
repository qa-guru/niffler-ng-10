package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.UserExtension;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class UsersApiClient implements UsersClient {

  private static final Config CFG = Config.getInstance();

  private final AuthApi authApi = new RestClient.EmtyRestClient(CFG.authUrl()).create(AuthApi.class);
  private final UserdataApi userdataApi = new RestClient.EmtyRestClient(CFG.userdataUrl()).create(UserdataApi.class);

  @NotNull
  @Override
  @Step("Create user with username '{0}' using REST API")
  public UserJson createUser(String username, String password) {
    try {
      authApi.requestRegisterForm().execute();
      authApi.register(
          username,
          password,
          password,
          ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
      ).execute();
      UserJson createdUser = requireNonNull(userdataApi.currentUser(username).execute().body());
      return createdUser.addTestData(
          new TestData(
              password
          )
      );
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  @Step("Add {1} income invitation(s) for user using REST API")
  public List<UserJson> addIncomeInvitation(UserJson targetUser, int count) {
    final List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      for (int i = 0; i < count; i++) {
        final String username = randomUsername();
        final Response<UserJson> response;
        final UserJson newUser;
        try {
          newUser = createUser(username, UserExtension.DEFAULT_PASSWORD);
          result.add(newUser);
          response = userdataApi.sendInvitation(
              newUser.username(),
              targetUser.username()
          ).execute();
        } catch (IOException e) {
          throw new AssertionError(e);
        }
        assertEquals(200, response.code());

        targetUser.testData()
            .incomeInvitations()
            .add(newUser);
      }
    }
    return result;
  }

  @Nonnull
  @Override
  @Step("Add {1} outcome invitation(s) for user using REST API")
  public List<UserJson> addOutcomeInvitation(UserJson targetUser, int count) {
    final List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      for (int i = 0; i < count; i++) {
        final String username = randomUsername();
        final Response<UserJson> response;
        final UserJson newUser;
        try {
          newUser = createUser(username, UserExtension.DEFAULT_PASSWORD);
          result.add(newUser);
          response = userdataApi.sendInvitation(
              targetUser.username(),
              newUser.username()
          ).execute();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        assertEquals(200, response.code());

        targetUser.testData()
            .outcomeInvitations()
            .add(newUser);
      }
    }
    return result;
  }

  @Nonnull
  @Override
  @Step("Add {1} friend(s) for user using REST API")
  public List<UserJson> addFriend(UserJson targetUser, int count) {
    final List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      for (int i = 0; i < count; i++) {
        final String username = randomUsername();
        final Response<UserJson> response;
        final UserJson newUser;
        try {
          newUser = createUser(username, UserExtension.DEFAULT_PASSWORD);
          result.add(newUser);
          userdataApi.sendInvitation(
              newUser.username(),
              targetUser.username()
          ).execute();
          response = userdataApi.acceptInvitation(targetUser.username(), username).execute();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        assertEquals(200, response.code());

        targetUser.testData()
            .friends()
            .add(response.body());
      }
    }
    return result;
  }

  @Nonnull
  @Override
  @Step("Add {0} other people(s) for user using REST API")
  public List<UserJson> addOtherPeoples(int count) {
    final List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      for (int i = 0; i < count; i++) {
        final UserJson newUser = createUser(randomUsername(), UserExtension.DEFAULT_PASSWORD);
        result.add(newUser);
      }
    }
    return result;
  }
}