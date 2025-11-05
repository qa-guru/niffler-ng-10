package guru.qa.niffler.service;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import retrofit2.Response;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public final class AuthApiClient extends RestClient {

  private final AuthApi authApi;

  public AuthApiClient() {
    super("https://auth.niffler-stage.qa.guru/", true);
    this.authApi = create(AuthApi.class);
  }

  public Response<Void> register(String username, String password) throws IOException {
    authApi.requestRegisterForm().execute();
    return authApi.register(
        username,
        password,
        password,
        ThreadSafeCookieStore.INSTANCE.xsrfCookie()
    ).execute();
  }
}
