package guru.qa.niffler.api;

import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

public interface SpendApi {
  @POST("internal/spends/add")
  Call<SpendJson> createSpend(@Body SpendJson spend);

  @GET("/{id}")
  Call<SpendJson> getSpend(@Path("id") String id,
                           @Query("username") String username);








}
