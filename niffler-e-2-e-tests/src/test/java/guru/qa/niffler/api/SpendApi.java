package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface SpendApi {
    @POST("internal/spends/add")
    Call<SpendJson> createSpend(@Body SpendJson spend);

    @PATCH("internal/spends/edit")
    Call<SpendJson> editSpend(@Body SpendJson spend);

    @GET("internal/spends/{id}")
    Call<SpendJson> getSpendById(@Path("id") String id);

    @GET("internal/spends/all")
    Call<List<SpendJson>> getAllSpends(
            @Query("filterPeriod") String filterPeriod,
            @Query("filterCurrency") CurrencyValues filterCurrency
    );

    @DELETE("/api/spends/remove")
    Call<Void> removeSpends(@Query("ids") List<String> ids);

    @POST("internal/categories/add")
    Call<CategoryJson> createCategory(@Body CategoryJson category);

    @PATCH("internal/categories/update")
    Call<CategoryJson> editCategory(@Body CategoryJson category);

    @GET("internal/categories/all")
    Call<List<CategoryJson>> getAllCategories(@Query("excludeArchived") boolean excludeArchived);

}
