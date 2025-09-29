package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;

import java.io.IOException;
import java.util.Optional;

public interface SpendClient {

    SpendJson createSpend(SpendJson spend);

    CategoryJson createCategory(CategoryJson category) throws IOException;

    Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username);
}
