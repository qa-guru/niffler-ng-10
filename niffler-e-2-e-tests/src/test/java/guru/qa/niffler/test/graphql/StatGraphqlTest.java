package guru.qa.niffler.test.graphql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.CurrencyValues;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class StatGraphqlTest extends BaseGraphqlTest {

    @Test
    @User(
            categories = {
                    @Category(name = "Food"),
                    @Category(name = "Transport"),
                    @Category(name = "Old Subscription", archived = true),
                    @Category(name = "Legacy Project", archived = true)
            },
            spendings = {
                    @Spending(category = "Food", description = "Lunch", amount = 150, currency = CurrencyValues.RUB),
                    @Spending(category = "Transport", description = "Taxi ride", amount = 50, currency = CurrencyValues.EUR),
                    @Spending(category = "Old Subscription", description = "Netflix old plan", amount = 300, currency = CurrencyValues.USD),
                    @Spending(category = "Legacy Project", description = "Old project expense", amount = 400, currency = CurrencyValues.KZT)
            }
    )
    @ApiLogin
    public void archivedCategoriesAggregation(@Token String bearerToken) {
        ApolloCall<StatQuery.Data> call = apolloClient.query(StatQuery.builder().build())
                .addHttpHeader("authorization", bearerToken);

        ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(call).blockingGet();

        var value = response.dataOrThrow().stat.statByCategories;

        step("2 активных и 1 архивная категория представлены в списке", () -> assertEquals(3, value.size()));
        step("Последняя категория - Архивная", () -> assertEquals("Archived", value.getLast().categoryName));
    }

    @Test
    @User(
            categories = {
                    @Category(name = "Groceries"),
                    @Category(name = "Utilities")
            },
            spendings = {
                    @Spending(category = "Groceries", description = "Weekly shopping", amount = 120, currency = CurrencyValues.EUR),
                    @Spending(category = "Groceries", description = "Fruit", amount = 80, currency = CurrencyValues.EUR),
                    @Spending(category = "Utilities", description = "Electricity bill", amount = 60, currency = CurrencyValues.USD),
                    @Spending(category = "Utilities", description = "Water bill", amount = 40, currency = CurrencyValues.RUB)
            }
    )
    @ApiLogin
    public void spendsByFilterIncludeOnlyEuroCurrency(@Token String bearerToken) {
        ApolloCall<StatQuery.Data> call = apolloClient.query(
                        StatQuery.builder()
                                .statCurrency(guru.qa.type.CurrencyValues.EUR)
                                .build())
                .addHttpHeader("authorization", bearerToken);

        ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(call).blockingGet();
        var value = response.dataOrThrow().stat.currency.rawValue;
        step("Статистика возвращается в EUR", () -> assertEquals(CurrencyValues.EUR.name(), value));
    }

    @Test
    @User(
            categories = {
                    @Category(name = "Entertainment"),
                    @Category(name = "Sports")
            },
            spendings = {
                    @Spending(category = "Entertainment", description = "Movie ticket", amount = 300, currency = CurrencyValues.RUB),
                    @Spending(category = "Entertainment", description = "Concert", amount = 200, currency = CurrencyValues.EUR),
                    @Spending(category = "Sports", description = "Gym membership", amount = 150, currency = CurrencyValues.USD),
                    @Spending(category = "Sports", description = "Yoga class", amount = 100, currency = CurrencyValues.RUB)
            }
    )
    @ApiLogin
    public void spendsReturnRubCurrencyByDefault(@Token String bearerToken){
        ApolloCall<StatQuery.Data> call = apolloClient.query(
                        StatQuery.builder()
                                .build())
                .addHttpHeader("authorization", bearerToken);

        ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(call).blockingGet();
        var value = response.dataOrThrow().stat.currency.rawValue;
        step("Полученная валюта по умолчанию RUB", () -> assertEquals(CurrencyValues.RUB.name(), value));
    }

    @Test
    @User(
            categories = {
                    @Category(name = "Tech"),
                    @Category(name = "Books")
            },
            spendings = {
                    @Spending(category = "Tech", description = "Laptop", amount = 1000, currency = CurrencyValues.EUR),
                    @Spending(category = "Tech", description = "Mouse", amount = 50, currency = CurrencyValues.EUR),
                    @Spending(category = "Books", description = "Novel", amount = 20, currency = CurrencyValues.USD),
                    @Spending(category = "Books", description = "Magazine", amount = 15, currency = CurrencyValues.KZT)
            }
    )
    @ApiLogin
    public void getSpendsFilteredByCurrency(@Token String bearerToken){
        ApolloCall<StatQuery.Data> call = apolloClient.query(
                        StatQuery.builder()
                                .statCurrency(guru.qa.type.CurrencyValues.EUR)
                                .build())
                .addHttpHeader("authorization", bearerToken);

        ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(call).blockingGet();
        var value = response.dataOrThrow().stat.statByCategories.size();
        step("Количество трат с валютой \"Евро\" равно двум", () -> assertEquals(2, value));
    }

    @Test
    @User
    @ApiLogin
    public void statTest(@Token String bearerToken) {
        ApolloCall<StatQuery.Data> call = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(call)
                .blockingGet();

        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        assertEquals(
                0.0,
                result.total
        );
    }
}
