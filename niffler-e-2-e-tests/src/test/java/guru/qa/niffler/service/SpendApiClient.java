package guru.qa.niffler.service;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient implements SpendClient{

  private static final Config CFG = Config.getInstance();

  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(CFG.spendUrl())
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final SpendApi spendApi = retrofit.create(SpendApi.class);

  @Override
  public SpendJson createSpend(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.createSpend(spend)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(201, response.code());
    return response.body();
  }

  @Override
  public CategoryJson createCategory(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.addCategory(category)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  @Override
  public CategoryJson updateCategory(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.updateCategory(category)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public SpendJson editSpend(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.editSpend(spend)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public SpendJson getSpend(String id) {
    final Response<SpendJson> response;
    try {
      response = spendApi.getSpend(id)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public List<SpendJson> allSpends(String username,
                                   CurrencyValues currency,
                                   String from,
                                   String to) {
    final Response<List<SpendJson>> response;
    try {
      response = spendApi.allSpends(username, currency, from, to)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public void removeSpends(String username, String... ids) {
    final Response<Void> response;
    try {
      response = spendApi.removeSpends(username, Arrays.stream(ids).toList())
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
  }

  public List<CategoryJson> allCategory(String username) {
    final Response<List<CategoryJson>> response;
    try {
      response = spendApi.allCategories(username)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }
}
