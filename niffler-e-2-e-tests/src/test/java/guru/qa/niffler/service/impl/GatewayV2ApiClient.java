package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.api.GatewayV2Api;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.page.RestResponsePage;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.data.domain.PageImpl;
import retrofit2.Response;
import retrofit2.http.Query;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public final class GatewayV2ApiClient extends RestClient {

  private static final String ISO_DATE = "yyyy-MM-dd";
  private final GatewayV2Api gatewayApi;

  public GatewayV2ApiClient() {
    super(CFG.gatewayUrl(), HttpLoggingInterceptor.Level.BODY);
    this.gatewayApi = create(GatewayV2Api.class);
  }

  @Step("Get all friends and income invitations from gateway using endpoint /api/v2/friends/all")
  @Nullable
  public RestResponsePage<UserJson> allFriends(String bearerToken,
                                       int page,
                                       int size,
                                       List<String> sort,
                                       @Nullable String searchQuery) {
    final Response<RestResponsePage<UserJson>> response;
    try {
      response = gatewayApi.allFriends(bearerToken, page, size, sort, searchQuery)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }
}
