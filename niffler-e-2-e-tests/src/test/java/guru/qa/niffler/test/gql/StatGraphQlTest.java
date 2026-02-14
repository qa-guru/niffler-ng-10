package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CurrencyValues;
import org.junit.jupiter.api.Test;

import java.util.List;

import static guru.qa.type.CurrencyValues.EUR;
import static guru.qa.type.CurrencyValues.RUB;
import static guru.qa.type.CurrencyValues.USD;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatGraphQlTest extends BaseGraphQlTest {

  @Test
  @User(
      categories = {
          @Category(name = "Бар"),
          @Category(name = "Азс")
      },
      spendings = {
          @Spending(description = "Коктейль", category = "Бар", amount = 650, currency = CurrencyValues.RUB, addDaysToSpendDate = -2),
          @Spending(description = "Кофе", category = "Бар", amount = 200, currency = CurrencyValues.RUB, addDaysToSpendDate = -1),
          @Spending(description = "Бензин", category = "Азс", amount = 300, currency = CurrencyValues.RUB),
      }
  )
  @ApiLogin
  void statForOpenPeriodWithoutFilteringTest(@Token String bearerToken) {
    final ApolloCall<StatQuery.Data> apolloCall = apolloClient.query(StatQuery.builder().build())
        .addHttpHeader("Authorization", bearerToken);

    final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(apolloCall).blockingGet();
    final StatQuery.Data responseData = response.dataOrThrow();
    final StatQuery.Stat stat = responseData.stat;

    step("Check that response contains statistic in default user currency (RUB)", () ->
        assertEquals(guru.qa.type.CurrencyValues.RUB, stat.currency)
    );
    step("Check that response contains correct total sum", () ->
        assertEquals(1150.00, stat.total)
    );

    final List<StatQuery.StatByCategory> statByCategories = stat.statByCategories;

    step("Check that response contains all categories stat", () ->
        assertEquals(2, statByCategories.size())
    );

    step("Check that response is sorted by sum", () ->
        assertAll(
            () -> assertEquals("Бар", statByCategories.getFirst().categoryName),
            () -> assertEquals("Азс", statByCategories.getLast().categoryName)
        )
    );

    step("Check that categories contain correct sums in user's default currency (RUB)", () ->
        assertAll(
            () -> assertEquals(850, statByCategories.getFirst().sum),
            () -> assertEquals(CurrencyValues.RUB.name(), statByCategories.getFirst().currency.rawValue),
            () -> assertEquals(300, statByCategories.getLast().sum),
            () -> assertEquals(CurrencyValues.RUB.name(), statByCategories.getLast().currency.rawValue)
        )
    );
  }

  @Test
  @User(
      categories = {
          @Category(name = "Еда"),
          @Category(name = "Транспорт")
      },
      spendings = {
          @Spending(description = "Завтрак", category = "Еда", amount = 300, currency = CurrencyValues.RUB, addDaysToSpendDate = 0),
          @Spending(description = "Обед", category = "Еда", amount = 500, currency = CurrencyValues.RUB, addDaysToSpendDate = -1),
          @Spending(description = "Такси", category = "Транспорт", amount = 700, currency = CurrencyValues.RUB, addDaysToSpendDate = 0)
      }
  )
  @ApiLogin
  void statForTodayPeriodFiltersOnlyTodaysSpends(@Token String bearerToken) {
    final ApolloCall<StatQuery.Data> call = apolloClient.query(
        StatQuery.builder()
            .filterPeriod(guru.qa.type.FilterPeriod.TODAY)
            .build()
    ).addHttpHeader("Authorization", bearerToken);

    final StatQuery.Stat stat = Rx2Apollo.single(call).blockingGet().dataOrThrow().stat;

    step("Check that response contains statistic in default user currency (RUB)", () ->
        assertEquals(guru.qa.type.CurrencyValues.RUB, stat.currency)
    );
    step("Total includes only today's expenses", () ->
        assertEquals(1000.0, stat.total)
    );

    final List<StatQuery.StatByCategory> statByCategories = stat.statByCategories;

    step("Check that response contains all daily categories stat", () ->
        assertEquals(2, statByCategories.size())
    );
    step("Check that response is sorted by sum", () ->
        assertAll(
            () -> assertEquals("Транспорт", statByCategories.getFirst().categoryName),
            () -> assertEquals("Еда", statByCategories.getLast().categoryName)
        )
    );
    step("Check that categories contain correct sums in user's default currency (RUB)", () ->
        assertAll(
            () -> assertEquals(700.0, statByCategories.getFirst().sum),
            () -> assertEquals(CurrencyValues.RUB.name(), statByCategories.getFirst().currency.rawValue),
            () -> assertEquals(300.0, statByCategories.getLast().sum),
            () -> assertEquals(CurrencyValues.RUB.name(), statByCategories.getLast().currency.rawValue)
        )
    );
  }

  @Test
  @User(
      categories = {
          @Category(name = "Продукты"),
          @Category(name = "Подписки")
      },
      spendings = {
          @Spending(description = "Сыр", category = "Продукты", amount = 400, currency = CurrencyValues.RUB, addDaysToSpendDate = -1),
          @Spending(description = "Хлеб", category = "Продукты", amount = 100, currency = CurrencyValues.RUB, addDaysToSpendDate = -8),
          @Spending(description = "Музыка", category = "Подписки", amount = 199, currency = CurrencyValues.RUB, addDaysToSpendDate = -3)
      }
  )
  @ApiLogin
  void statForWeekPeriodExcludesOlderThan7Days(@Token String bearerToken) {
    final ApolloCall<StatQuery.Data> call = apolloClient.query(
        StatQuery.builder()
            .filterPeriod(guru.qa.type.FilterPeriod.WEEK)
            .build()
    ).addHttpHeader("Authorization", bearerToken);

    final StatQuery.Stat stat = Rx2Apollo.single(call).blockingGet().dataOrThrow().stat;

    step("RUB as the default user currency", () ->
        assertEquals(guru.qa.type.CurrencyValues.RUB, stat.currency)
    );
    step("Total excludes the spend from 8 days ago (400 + 199 = 599)", () ->
        assertEquals(599.0, stat.total)
    );

    final List<StatQuery.StatByCategory> statByCategories = stat.statByCategories;

    step("Category 'Продукты' is present only with the -1 day spend", () -> {
      StatQuery.StatByCategory products = statByCategories.stream()
          .filter(c -> c.categoryName.equals("Продукты")).findFirst().orElseThrow();
      assertEquals(400.0, products.sum);
    });
    step("Category 'Подписки' has sum 199", () -> {
      StatQuery.StatByCategory sub = statByCategories.stream()
          .filter(c -> c.categoryName.equals("Подписки")).findFirst().orElseThrow();
      assertEquals(199.0, sub.sum);
    });
  }

  @Test
  @User(
      categories = {
          @Category(name = "Здоровье")
      },
      spendings = {
          @Spending(description = "Аптека", category = "Здоровье", amount = 500, currency = CurrencyValues.RUB, addDaysToSpendDate = -25),
          @Spending(description = "Витамины", category = "Здоровье", amount = 700, currency = CurrencyValues.RUB, addDaysToSpendDate = -2)
      }
  )
  @ApiLogin
  void statForMonthPeriodContainsCorrectFirstAndLastDates(@Token String bearerToken) {
    final ApolloCall<StatQuery.Data> call = apolloClient.query(
        StatQuery.builder()
            .filterPeriod(guru.qa.type.FilterPeriod.MONTH)
            .build()
    ).addHttpHeader("Authorization", bearerToken);

    final StatQuery.StatByCategory cat = Rx2Apollo.single(call).blockingGet()
        .dataOrThrow().stat.statByCategories.getFirst();

    step("Sum for the month = 1200", () -> assertEquals(1200.0, cat.sum));
    step("firstSpendDate is the earliest in the period, lastSpendDate is the latest", () -> {
      assertAll(
          () -> assertNotNull(cat.firstSpendDate),
          () -> assertNotNull(cat.lastSpendDate),
          () -> assertTrue(cat.firstSpendDate.before(cat.lastSpendDate) || cat.firstSpendDate.equals(cat.lastSpendDate))
      );
    });
  }

  @Test
  @User(
      categories = {
          @Category(name = "Книги"),
          @Category(name = "Онлайн-курсы")
      },
      spendings = {
          @Spending(description = "Ebook", category = "Книги", amount = 10, currency = CurrencyValues.USD, addDaysToSpendDate = 0),
          @Spending(description = "Paper", category = "Книги", amount = 500, currency = CurrencyValues.RUB, addDaysToSpendDate = 0),
          @Spending(description = "Course", category = "Онлайн-курсы", amount = 30, currency = CurrencyValues.USD, addDaysToSpendDate = 0)
      }
  )
  @ApiLogin
  void statWithFilterCurrencyUsdIncludesOnlyUsdSpends(@Token String bearerToken) {
    final ApolloCall<StatQuery.Data> call = apolloClient.query(
        StatQuery.builder()
            .filterCurrency(USD)
            .build()
    ).addHttpHeader("Authorization", bearerToken);

    final StatQuery.Stat stat = Rx2Apollo.single(call).blockingGet().dataOrThrow().stat;

    step("Result currency is the user's default (RUB) because statCurrency is not provided", () ->
        assertEquals(guru.qa.type.CurrencyValues.RUB, stat.currency)
    );

    final List<StatQuery.StatByCategory> cats = stat.statByCategories;

    step("No RUB spends (500 RUB) are included—only USD spends", () -> {
      List<String> names = cats.stream().map(c -> c.categoryName).toList();
      assertAll(
          () -> assertTrue(names.contains("Книги")),
          () -> assertTrue(names.contains("Онлайн-курсы"))
      );
    });

    step("Category sums include only USD operations", () -> {
      StatQuery.StatByCategory books = cats.stream().filter(c -> c.categoryName.equals("Книги")).findFirst().orElseThrow();
      StatQuery.StatByCategory courses = cats.stream().filter(c -> c.categoryName.equals("Онлайн-курсы")).findFirst().orElseThrow();

      assertAll(
          () -> assertEquals(CurrencyValues.RUB.name(), books.currency.rawValue),
          () -> assertEquals(666.67, books.sum),
          () -> assertEquals(CurrencyValues.RUB.name(), courses.currency.rawValue),
          () -> assertEquals(2000, courses.sum)
      );
    });
  }

  @Test
  @User(
      categories = {
          @Category(name = "Софта"),
          @Category(name = "Сервисы")
      },
      spendings = {
          @Spending(description = "IDE", category = "Софта", amount = 50, currency = CurrencyValues.USD, addDaysToSpendDate = -2),
          @Spending(description = "Cloud", category = "Сервисы", amount = 20, currency = CurrencyValues.USD, addDaysToSpendDate = -1)
      }
  )
  @ApiLogin
  void statCurrencyUsdReturnsUsdAndTotalsUnchanged(@Token String bearerToken) {
    final ApolloCall<StatQuery.Data> call = apolloClient.query(
        StatQuery.builder()
            .statCurrency(USD)
            .build()
    ).addHttpHeader("Authorization", bearerToken);

    final StatQuery.Stat stat = Rx2Apollo.single(call).blockingGet().dataOrThrow().stat;

    step("statCurrency=USD is respected", () ->
        assertEquals(USD, stat.currency)
    );
    step("total = 70 (no conversion)", () ->
        assertEquals(70.0, stat.total)
    );

    final List<StatQuery.StatByCategory> cats = stat.statByCategories;
    step("Categories are in USD; sums unchanged", () ->
        assertAll(
            () -> assertEquals(50.0, cats.stream().filter(c -> c.categoryName.equals("Софта")).findFirst().orElseThrow().sum),
            () -> assertEquals(CurrencyValues.USD.name(), cats.stream().filter(c -> c.categoryName.equals("Софта")).findFirst().orElseThrow().currency.rawValue),
            () -> assertEquals(20.0, cats.stream().filter(c -> c.categoryName.equals("Сервисы")).findFirst().orElseThrow().sum),
            () -> assertEquals(CurrencyValues.USD.name(), cats.stream().filter(c -> c.categoryName.equals("Сервисы")).findFirst().orElseThrow().currency.rawValue)
        )
    );
  }

  @Test
  @User(
      categories = {
          @Category(name = "Бар"),
          @Category(name = "Кафе"),
          @Category(name = "Старые расходы", archived = true),
          @Category(name = "Очень старые", archived = true)
      },
      spendings = {
          @Spending(description = "Коктейль", category = "Бар", amount = 500, currency = CurrencyValues.RUB, addDaysToSpendDate = 0),
          @Spending(description = "Кофе", category = "Кафе", amount = 300, currency = CurrencyValues.RUB, addDaysToSpendDate = 0),
          @Spending(description = "Арх1", category = "Старые расходы", amount = 50, currency = CurrencyValues.RUB, addDaysToSpendDate = 0),
          @Spending(description = "Арх2", category = "Очень старые", amount = 70, currency = CurrencyValues.RUB, addDaysToSpendDate = 0)
      }
  )
  @ApiLogin
  void archivedCategoriesAreAggregatedAndGoLast(@Token String bearerToken) {
    final ApolloCall<StatQuery.Data> call = apolloClient.query(
        StatQuery.builder().build()
    ).addHttpHeader("Authorization", bearerToken);

    final List<StatQuery.StatByCategory> cats = Rx2Apollo.single(call).blockingGet()
        .dataOrThrow().stat.statByCategories;

    step("Exactly three items: Бар, Кафе, Archived (aggregate of two archived categories)", () ->
        assertEquals(3, cats.size())
    );

    step("The first two are non-archived, sorted by descending sum", () ->
        assertAll(
            () -> assertEquals("Бар", cats.get(0).categoryName),
            () -> assertEquals("Кафе", cats.get(1).categoryName)
        )
    );

    final StatQuery.StatByCategory archived = cats.get(2);
    step("The last item is always 'Archived'", () ->
        assertEquals("Archived", archived.categoryName)
    );
    step("'Archived' sum = 50 + 70 = 120", () ->
        assertEquals(120.0, archived.sum)
    );
  }

  @Test
  @User(
      categories = {
          @Category(name = "Путешествия"),
          @Category(name = "Гаджеты")
      },
      spendings = {
          @Spending(description = "Отель", category = "Путешествия", amount = 200, currency = CurrencyValues.EUR, addDaysToSpendDate = -5),
          @Spending(description = "Аксессуар", category = "Гаджеты", amount = 50, currency = CurrencyValues.USD, addDaysToSpendDate = -5),
          @Spending(description = "Авиабилеты", category = "Путешествия", amount = 100, currency = CurrencyValues.EUR, addDaysToSpendDate = -20),
          @Spending(description = "Старый EUR", category = "Путешествия", amount = 80, currency = CurrencyValues.EUR, addDaysToSpendDate = -40) // out of MONTH
      }
  )
  @ApiLogin
  void monthWithFilterCurrencyEurAndStatCurrencyUsd(@Token String bearerToken) {
    final ApolloCall<StatQuery.Data> call = apolloClient.query(
        StatQuery.builder()
            .filterPeriod(guru.qa.type.FilterPeriod.MONTH)
            .filterCurrency(EUR)
            .statCurrency(USD)
            .build()
    ).addHttpHeader("Authorization", bearerToken);

    final StatQuery.Stat stat = Rx2Apollo.single(call).blockingGet().dataOrThrow().stat;

    step("Response currency is USD", () -> assertEquals(USD, stat.currency));

    final List<StatQuery.StatByCategory> cats = stat.statByCategories;

    step("Only EUR spends within the month are included (200 + 100); the USD spend and the older-than-a-month EUR spend are excluded", () -> {
      StatQuery.StatByCategory travel = cats.stream().filter(c -> c.categoryName.equals("Путешествия")).findFirst().orElseThrow();
      assertEquals(CurrencyValues.USD.name(), travel.currency.rawValue);
      assertEquals(324.0, travel.sum);
    });

    step("Category 'Гаджеты' (USD spend) is absent", () ->
        assertTrue(cats.stream().noneMatch(c -> c.categoryName.equals("Гаджеты")))
    );
  }

  @Test
  @User(
      categories = {
          @Category(name = "Дом")
      },
      spendings = {
          @Spending(description = "Мебель", category = "Дом", amount = 3000, currency = CurrencyValues.RUB, addDaysToSpendDate = -40)
      }
  )
  @ApiLogin
  void emptyStatWhenNoSpendsMatchFilters(@Token String bearerToken) {
    final ApolloCall<StatQuery.Data> call = apolloClient.query(
        StatQuery.builder()
            .filterPeriod(guru.qa.type.FilterPeriod.WEEK)
            .build()
    ).addHttpHeader("Authorization", bearerToken);

    final StatQuery.Stat stat = Rx2Apollo.single(call).blockingGet().dataOrThrow().stat;

    step("total = 0.0", () -> assertEquals(0.0, stat.total));
    step("statByCategories is empty", () -> assertEquals(0, stat.statByCategories.size()));
  }

  @Test
  @User(
      categories = {
          @Category(name = "Разное"),
          @Category(name = "Еда")
      },
      spendings = {
          @Spending(description = "USD spend", category = "Разное", amount = 10, currency = CurrencyValues.USD, addDaysToSpendDate = 0),
          @Spending(description = "EUR spend", category = "Разное", amount = 5, currency = CurrencyValues.EUR, addDaysToSpendDate = 0),
          @Spending(description = "RUB spend", category = "Еда", amount = 300, currency = CurrencyValues.RUB, addDaysToSpendDate = 0)
      }
  )
  @ApiLogin
  void mixedCurrenciesAllConvertedToRub(@Token String bearerToken) {
    final ApolloCall<StatQuery.Data> call = apolloClient.query(
        StatQuery.builder()
            .statCurrency(RUB)
            .build()
    ).addHttpHeader("Authorization", bearerToken);

    final StatQuery.Stat stat = Rx2Apollo.single(call).blockingGet().dataOrThrow().stat;

    step("statCurrency=RUB is respected", () ->
        assertEquals(guru.qa.type.CurrencyValues.RUB, stat.currency)
    );

    final List<StatQuery.StatByCategory> cats = stat.statByCategories;

    step("Both categories are present", () -> {
      List<String> names = cats.stream().map(c -> c.categoryName).toList();
      assertAll(
          () -> assertTrue(names.contains("Разное")),
          () -> assertTrue(names.contains("Еда"))
      );
    });

    step("All sums and total are in RUB", () -> {
      cats.forEach(c -> assertEquals(CurrencyValues.RUB.name(), c.currency.rawValue));
      assertEquals(1326.67, stat.total);
    });
  }
}
