package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static guru.qa.niffler.utils.DateUtils.getDateAsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public final class SpendApiClient extends RestClient implements SpendClient {

  private static final String ISO_DATE = "yyyy-MM-dd";
  private final SpendApi spendApi;

  public SpendApiClient() {
    super(CFG.spendUrl());
    this.spendApi = create(SpendApi.class);
  }

  @Override
  @Step("Create spend using internal REST API")
  @Nullable
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
  @Step("Edit spend using internal REST API")
  @Nullable
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

  @Override
  @Step("Get spend using internal REST API by id: {id}")
  @Nullable
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

  @Override
  @Step("Get all users spends using internal REST API by username: {username}")
  @Nonnull
  public List<SpendJson> allSpends(String username,
                                   @Nullable CurrencyValues currency,
                                   @Nullable Date from,
                                   @Nullable Date to) {
    final Response<List<SpendJson>> response;
    try {
      response = spendApi.allSpends(username, currency, getDateAsString(from, ISO_DATE), getDateAsString(to, ISO_DATE))
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body() != null
        ? response.body()
        : Collections.emptyList();
  }

  @Override
  @Step("Remove spends using internal REST API by username: '{username}' and ids: {ids}")
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

  @Override
  @Step("Create category using internal REST API")
  @Nullable
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
  @Step("Edit category using internal REST API")
  @Nullable
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

  @Override
  @Step("Get all users categories using internal REST API by username: {username}")
  @Nonnull
  public List<CategoryJson> allCategories(String username) {
    final Response<List<CategoryJson>> response;
    try {
      response = spendApi.allCategories(username)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body() != null
        ? response.body()
        : Collections.emptyList();
  }

  @Override
  public void removeCategory(CategoryJson category) {
    throw new UnsupportedOperationException("Can`t remove category");
  }
}
