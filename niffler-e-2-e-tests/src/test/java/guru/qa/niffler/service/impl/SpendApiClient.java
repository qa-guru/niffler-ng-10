package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static guru.qa.niffler.utils.DateUtils.getDateAsString;

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
    return executeForBody(
        spendApi.createSpend(spend),
        201
    );
  }

  @Override
  @Step("Edit spend using internal REST API")
  @Nullable
  public SpendJson editSpend(SpendJson spend) {
    return executeForBody(
        spendApi.editSpend(spend),
        200
    );
  }

  @Override
  @Step("Get spend using internal REST API by id: {id}")
  @Nullable
  public SpendJson getSpend(String id) {
    return executeForBody(
        spendApi.getSpend(id),
        200
    );
  }

  @Override
  @Step("Get all users spends using internal REST API by username: {username}")
  @Nonnull
  public List<SpendJson> allSpends(String username,
                                   @Nullable CurrencyValues currency,
                                   @Nullable Date from,
                                   @Nullable Date to) {
    final List<SpendJson> result = executeForBody(
        spendApi.allSpends(username, currency, getDateAsString(from, ISO_DATE), getDateAsString(to, ISO_DATE)),
        200
    );
    return result != null ? result : Collections.emptyList();
  }

  @Override
  @Step("Remove spends using internal REST API by username: '{username}' and ids: {ids}")
  public void removeSpends(String username, String... ids) {
    executeNoBody(
        spendApi.removeSpends(username, Arrays.stream(ids).toList()),
        200
    );
  }

  @Override
  @Step("Create category using internal REST API")
  @Nullable
  public CategoryJson createCategory(CategoryJson category) {
    return executeForBody(
        spendApi.addCategory(category),
        200
    );
  }

  @Override
  @Step("Edit category using internal REST API")
  @Nullable
  public CategoryJson updateCategory(CategoryJson category) {
    return executeForBody(
        spendApi.updateCategory(category),
        200
    );
  }

  @Override
  @Step("Get all users categories using internal REST API by username: {username}")
  @Nonnull
  public List<CategoryJson> allCategories(String username) {
    final List<CategoryJson> result = executeForBody(
        spendApi.allCategories(username),
        200
    );
    return result;
  }

  @Override
  public void removeCategory(CategoryJson category) {
    throw new UnsupportedOperationException("Can`t remove category");
  }
}
