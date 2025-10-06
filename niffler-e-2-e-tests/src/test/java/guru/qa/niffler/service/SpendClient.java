package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import org.springframework.data.web.PagedModel;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SpendClient {

  SpendJson createSpend(SpendJson spend);

  CategoryJson createCategory(CategoryJson category);

  Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username);

  SpendJson getUserById(String id, String userName);

  List<SpendJson> getSpends(String username, CurrencyValues filterCurrency, Date from, Date to);

  SpendJson editSpend(SpendJson spend);

  Void deleteSpends(String username,List<String> ids);

  PagedModel<SpendJson> getspends(String username,
                                  CurrencyValues filterCurrency,
                                  Date from, Date to,
                                  String searchQuery,
                                  Integer page,
                                  Integer size,List<String> sort);

  List<CategoryJson> getCategories(String username, boolean excludeArchived);

  CategoryJson addCategory(CategoryJson category);


  CategoryJson updateCategory(CategoryJson categoryJson);
}
