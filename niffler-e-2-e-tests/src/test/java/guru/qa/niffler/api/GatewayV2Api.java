package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.model.FriendJson;
import guru.qa.niffler.model.SessionJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.StatisticJson;
import guru.qa.niffler.model.StatisticV2Json;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.page.RestResponsePage;
import org.springframework.data.domain.PageImpl;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface GatewayV2Api {

  @GET("api/v2/friends/all")
  Call<RestResponsePage<UserJson>> allFriends(@Header("Authorization") String bearerToken,
                                              @Query("page") int page,
                                              @Query("size") int size,
                                              @Query("sort") List<String> sort,
                                              @Query("searchQuery") @Nullable String searchQuery);


  @GET("api/v2/spends/all")
  Call<RestResponsePage<SpendJson>> allSpends(@Header("Authorization") String bearerToken,
                                      @Query("page") int page,
                                      @Query("size") int size,
                                      @Query("sort") List<String> sort,
                                      @Query("filterCurrency") @Nullable CurrencyValues filterCurrency,
                                      @Query("filterPeriod") @Nullable DataFilterValues filterPeriod,
                                      @Query("searchQuery") @Nullable String searchQuery);


  @GET("api/v2/stat/total")
  Call<StatisticV2Json> totalStat(@Header("Authorization") String bearerToken,
                                  @Query("statCurrency") @Nullable CurrencyValues statCurrency,
                                  @Query("filterCurrency") @Nullable CurrencyValues filterCurrency,
                                  @Query("filterPeriod") @Nullable DataFilterValues filterPeriod);


  @GET("api/v2/users/all")
  Call<RestResponsePage<UserJson>> allUsers(@Header("Authorization") String bearerToken,
                                    @Query("page") int page,
                                    @Query("size") int size,
                                    @Query("sort") List<String> sort,
                                    @Query("searchQuery") @Nullable String searchQuery);

}
