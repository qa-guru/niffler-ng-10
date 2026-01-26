package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyJson;
import guru.qa.niffler.model.FriendJson;
import guru.qa.niffler.model.SessionJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class GatewayApiClient extends RestClient {

  private static final Config CFG = Config.getInstance();
  private final GatewayApi gatewayApi;

  public GatewayApiClient() {
    super(CFG.gatewayUrl());
    this.gatewayApi = create(GatewayApi.class);
  }


  @Step("Send REST GET('/api/categories/all') request to niffler-gateway")
  @Nonnull
  public List<CategoryJson> allCategories(String bearerToken) {
    return executeForBody(gatewayApi.allCategories(bearerToken), 200);
  }

  @Step("Send REST POST('/api/categories/add') request to niffler-gateway")
  @Nonnull
  public CategoryJson addCategory(String bearerToken, CategoryJson category) {
    return executeForBody(gatewayApi.addCategory(bearerToken, category),  200);
  }

  @Step("Send REST GET('/api/currencies/all') request to niffler-gateway")
  @Nonnull
  public List<CurrencyJson> allCurrencies(String bearerToken) {
    return executeForBody(gatewayApi.allCurrencies(bearerToken), 200);
  }

  @Step("Send REST PATCH('/api/spends/edit') request to niffler-gateway")
  @Nonnull
  public SpendJson editSpend(String bearerToken, SpendJson spend) {
    return executeForBody(gatewayApi.editSpend(bearerToken, spend), 200);
  }

  @Step("Send REST DELETE('/api/spends/remove') request to niffler-gateway")
  public void removeSpends(String bearerToken, List<String> ids) {
    executeNoBody(gatewayApi.removeSpends(bearerToken, ids), 200);
  }

  @Step("Send REST DELETE('/api/friends/remove') request to niffler-gateway")
  public void removeFriend(String bearerToken, String targetUsername) {
    executeNoBody(gatewayApi.removeFriend(bearerToken, targetUsername), 200);
  }

  @Step("Send REST POST('/api/spends/add') request to niffler-gateway")
  @Nonnull
  public SpendJson addSpend(String bearerToken, SpendJson spend) {
    return executeForBody(gatewayApi.addSpend(bearerToken, spend), 201);
  }

  @Step("Send REST POST('/api/users/update') request to niffler-gateway")
  @Nonnull
  public UserJson updateUser(String bearerToken, UserJson user) {
    return executeForBody(gatewayApi.updateUser(bearerToken, user), 200);
  }

  @Step("Send REST POST('/api/invitations/send') request to niffler-gateway")
  @Nonnull
  public UserJson sendInvitation(String bearerToken, FriendJson friend) {
    return executeForBody(gatewayApi.sendInvitation(bearerToken, friend), 200);
  }

  @Step("Send REST POST('/api/invitations/accept') request to niffler-gateway")
  @Nonnull
  public UserJson acceptInvitation(String bearerToken, FriendJson friend) {
    return executeForBody(gatewayApi.acceptInvitation(bearerToken, friend), 200);
  }

  @Step("Send REST POST('/api/invitations/decline') request to niffler-gateway")
  @Nonnull
  public UserJson declineInvitation(String bearerToken, FriendJson friend) {
    return executeForBody(gatewayApi.declineInvitation(bearerToken, friend), 200);
  }

  @Step("Send REST GET('/api/users/current') request to niffler-gateway")
  @Nonnull
  public UserJson currentUser(String bearerToken) {
    return executeForBody(gatewayApi.currentUser(bearerToken), 200);
  }

  @Step("Send REST GET('/api/session/current') request to niffler-gateway")
  @Nonnull
  public SessionJson currentSession(String bearerToken) {
    return executeForBody(gatewayApi.currentSession(bearerToken), 200);
  }
}
