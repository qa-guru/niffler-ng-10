package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.service.impl.SpendDbClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;
import java.util.List;

@ParametersAreNonnullByDefault
public interface SpendClient {
  @Nonnull
  static SpendClient getInstance() {
    return "api".equals(System.getProperty("client.impl"))
        ? new SpendApiClient()
        : new SpendDbClient();
  }

  @Nullable
  SpendJson createSpend(SpendJson spend);

  @Nullable
  SpendJson editSpend(SpendJson spend);

  @Nullable
  SpendJson getSpend(String id);

  @Nonnull
  List<SpendJson> allSpends(String username,
                            @Nullable CurrencyValues currency,
                            @Nullable Date from,
                            @Nullable Date to);

  void removeSpends(String username, String... ids);

  @Nullable
  CategoryJson createCategory(CategoryJson category);

  @Nullable
  CategoryJson updateCategory(CategoryJson category);

  @Nonnull
  List<CategoryJson> allCategories(String username);

  void removeCategory(CategoryJson category);
}
