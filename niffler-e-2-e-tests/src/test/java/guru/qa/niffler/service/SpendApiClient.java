package guru.qa.niffler.service;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    @SneakyThrows
    @Override
    public SpendJson createSpend(SpendJson spend) {
        return spendApi.createSpend(spend)
                .execute()
                .body();
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) throws IOException {
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
        throw new UnsupportedOperationException("Not implemented :(");
    }

    public CategoryJson updateCategory(CategoryJson categoryJson) throws IOException {
        final Response<CategoryJson> resp;
        try {
            resp = spendApi.editCategory(categoryJson).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, resp.code());
        return resp.body();
    }
}
