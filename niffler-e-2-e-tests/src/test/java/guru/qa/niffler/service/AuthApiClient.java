package guru.qa.niffler.service;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.config.Config;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class AuthApiClient {

  private static final Config CFG = Config.getInstance();
  private static final CookieManager cm = new CookieManager(null, CookiePolicy.ACCEPT_ALL);

  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(CFG.authUrl())
      .addConverterFactory(JacksonConverterFactory.create())
      .client(new OkHttpClient.Builder()
          .cookieJar(new JavaNetCookieJar(
              cm
          ))
          .build())
      .build();

  private final AuthApi authApi = retrofit.create(AuthApi.class);

  public Response<Void> register(String username, String password) throws IOException {
    authApi.requestRegisterForm().execute();
    return authApi.register(
        username,
        password,
        password,
        cm.getCookieStore().getCookies()
            .stream()
            .filter(c -> c.getName().equals("XSRF-TOKEN"))
            .findFirst()
            .get()
            .getValue()
    ).execute();
  }
}
