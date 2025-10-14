package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;

public interface SpendClient {

  SpendJson createSpend(SpendJson spend);

  CategoryJson createCategory(CategoryJson category);

  CategoryJson updateCategory(CategoryJson category);

  Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username);

  List<CategoryJson> getAllCategoriesByUsername(String username);

  SpendJson editSpend(SpendJson spend);

  SpendJson getSpendById(String id);

  List<SpendJson> getAllSpends(String username, CurrencyValues currency, String from, String to);

  void deleteSpend (String username, List<String> ids);
}
