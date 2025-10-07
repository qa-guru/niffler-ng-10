package guru.qa.niffler.service;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import org.hibernate.AssertionFailure;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    @Override
    public SpendJson createSpend(SpendJson spend) {
        final Response<SpendJson> resp;
        try {
            resp = spendApi.createSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, resp.code());
        return resp.body();
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        final Response<CategoryJson> resp;
        try {
            resp = spendApi.createCategory(category).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, resp.code());
        return resp.body();
    }

    @Override
    public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username) {
        final Response<List<CategoryJson>> resp;
        try {
            resp = spendApi.getAllCategories(false).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        if (!resp.isSuccessful() || resp.body() == null) {
            return Optional.empty();
        }
        return resp.body().stream()
                .filter(c -> categoryName.equals(c.name()) && username.equals(c.username()))
                .findFirst();
    }

    public CategoryJson updateCategory(CategoryJson categoryJson) {
        final Response<CategoryJson> resp;
        try {
            resp = spendApi.editCategory(categoryJson).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, resp.code());
        return resp.body();
    }

    public SpendJson editSpend(SpendJson spend) {
        final Response<SpendJson> resp;
        try {
            resp = spendApi.editSpend(spend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, resp.code());
        return resp.body();
    }

    public SpendJson getSpendById(String id) {
        final Response<SpendJson> resp;
        try {
            resp = spendApi.getSpendById(id).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, resp.code());
        return resp.body();
    }

    public List<SpendJson> getAllSpends(CurrencyValues filterCurrency) {
        final Response<List<SpendJson>> resp;
        try {
            resp = spendApi.getAllSpends(filterCurrency).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, resp.code());
        return resp.body();
    }

    public void removeSpends(List<String> ids) {
        final Response<Void> resp;
        try {
            resp = spendApi.removeSpends(ids).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        if (!resp.isSuccessful()) {
            throw new AssertionFailure("Failed to remove spends, status: " + resp.code());
        }
    }

    public List<CategoryJson> getAllCategories(boolean excludeArchived) {
        final Response<List<CategoryJson>> resp;
        try {
            resp = spendApi.getAllCategories(excludeArchived).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, resp.code());
        return resp.body();
    }
}
