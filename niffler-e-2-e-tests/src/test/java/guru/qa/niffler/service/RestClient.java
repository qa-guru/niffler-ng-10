package guru.qa.niffler.service;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Arrays;

import static org.apache.commons.lang.ArrayUtils.isNotEmpty;

@ParametersAreNonnullByDefault
public abstract class RestClient {

  protected static final Config CFG = Config.getInstance();

  private final OkHttpClient client;
  private final Retrofit retrofit;

  public RestClient(String baseUrl) {
    this(baseUrl, false, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.HEADERS, null);
  }

  public RestClient(String baseUrl, boolean followRedirect) {
    this(baseUrl, followRedirect, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.HEADERS, null);
  }

  public RestClient(String baseUrl, boolean followRedirect, Converter.Factory converterFactory) {
    this(baseUrl, followRedirect, converterFactory, HttpLoggingInterceptor.Level.HEADERS, null);
  }

  public RestClient(String baseUrl, Converter.Factory converterFactory) {
    this(baseUrl, false, converterFactory, HttpLoggingInterceptor.Level.HEADERS, null);
  }

  public RestClient(String baseUrl, boolean followRedirect, Interceptor... interceptors) {
    this(baseUrl, followRedirect, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.HEADERS, interceptors);
  }

  public RestClient(String baseUrl, boolean followRedirect, Converter.Factory converterFactory, Interceptor... interceptors) {
    this(baseUrl, followRedirect, converterFactory, HttpLoggingInterceptor.Level.HEADERS, interceptors);
  }

  public RestClient(String baseUrl, boolean followRedirect, Converter.Factory converterFactory, HttpLoggingInterceptor.Level level, @Nullable Interceptor... interceptors) {
    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
        .followRedirects(followRedirect);

    if (isNotEmpty(interceptors)) {
      for (Interceptor interceptor : interceptors) {
        clientBuilder.addNetworkInterceptor(interceptor);
      }
    }

    clientBuilder
        .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(level))
        .addNetworkInterceptor(
            new AllureOkHttp3()
                .setRequestTemplate("http-request.ftl")
                .setResponseTemplate("http-response.ftl")
        )
        .cookieJar(
            new JavaNetCookieJar(
                new CookieManager(
                    ThreadSafeCookieStore.INSTANCE,
                    CookiePolicy.ACCEPT_ALL
                )
            )
        );

    this.client = clientBuilder.build();
    this.retrofit = new Retrofit.Builder()
        .client(this.client)
        .baseUrl(baseUrl)
        .addConverterFactory(converterFactory)
        .build();
  }

  @Nonnull
  public <T> T create(final Class<T> service) {
    return this.retrofit.create(service);
  }

  protected static <T> @Nonnull T executeForBody(Call<T> call, int... expectedStatus) {
    final Response<T> response = doExecute(call);
    assertStatus(response, expectedStatus);
    final T body = response.body();
    if (body == null) {
      final Request rq = response.raw().request();
      throw new AssertionError("Response body is null for "
          + rq.method() + " " + rq.url()
          + " (status=" + response.code() + ")");
    }
    return body;
  }

  protected static void executeNoBody(Call<Void> call, int... expectedStatus) {
    final Response<Void> response = doExecute(call);
    assertStatus(response, expectedStatus);
  }

  private static <T> Response<T> doExecute(Call<T> call) {
    try {
      return call.execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }

  private static void assertStatus(Response<?> response, int... expected) {
    final int code = response.code();
    for (int ok : expected) {
      if (ok == code) return;
    }
    String err = null;
    try {
      if (response.errorBody() != null) {
        err = response.errorBody().string();
      }
    } catch (IOException ignored) {}
    final Request rq = response.raw().request();
    throw new AssertionError(
        "Unexpected HTTP status " + code + " for " + rq.method() + " " + rq.url()
            + ", expected " + Arrays.toString(expected)
            + (err != null && !err.isBlank() ? (", errorBody=" + err) : "")
    );
  }

  @ParametersAreNonnullByDefault
  public static final class EmtyRestClient extends RestClient {
    public EmtyRestClient(String baseUrl) {
      super(baseUrl);
    }

    public EmtyRestClient(String baseUrl, boolean followRedirect) {
      super(baseUrl, followRedirect);
    }

    public EmtyRestClient(String baseUrl, Converter.Factory factory) {
      super(baseUrl, factory);
    }

    public EmtyRestClient(String baseUrl, boolean followRedirect, Converter.Factory factory, HttpLoggingInterceptor.Level level, @Nullable Interceptor... interceptors) {
      super(baseUrl, followRedirect, factory, level, interceptors);
    }
  }
}
