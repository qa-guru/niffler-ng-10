package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.utils.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@ParametersAreNonnullByDefault
public class SpendConditions {

  @Nonnull
  public static WebElementsCondition haveSpendings(SpendJson... expected) {
    return haveSpendings(List.of(expected));
  }

  @Nonnull
  public static WebElementsCondition haveSpendings(List<SpendJson> expected) {
    return new WebElementsCondition() {

      private final List<String> expectedElements = mapExpectedElements(toList(), expected);

      @NotNull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (expected.size() != elements.size()) {
          final String message = String.format("List size mismatch (expected: %s, actual: %s)", expected.size(), elements.size());
          return rejected(message, elements);
        }

        final List<String> actualElements = mapActualElements(toList(), elements);

        if (!expectedElements.equals(actualElements)) {
          final String message = String.format(
              "Spendings list mismatch (expected: %s, actual: %s)", expectedElements, actualElements
          );
          return rejected(message, actualElements);
        }
        return accepted();
      }

      @Nonnull
      @Override
      public String toString() {
        return expectedElements.toString();
      }
    };
  }

  @Nonnull
  public static WebElementsCondition haveSpendingsInAnyOrder(SpendJson... expected) {
    return haveSpendingsInAnyOrder(List.of(expected));
  }

  @Nonnull
  public static WebElementsCondition haveSpendingsInAnyOrder(List<SpendJson> expected) {
    return new WebElementsCondition() {

      private final Set<String> expectedElements = mapExpectedElements(toSet(), expected);

      @NotNull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (expected.size() != elements.size()) {
          final String message = String.format("List size mismatch (expected: %s, actual: %s)", expected.size(), elements.size());
          return rejected(message, elements);
        }

        final Set<String> actualElements = mapActualElements(toSet(), elements);

        if (!expectedElements.equals(actualElements)) {
          final String message = String.format(
              "Spendings list mismatch (expected: %s, actual: %s)", expectedElements, actualElements
          );
          return rejected(message, actualElements);
        }

        return accepted();
      }

      @Override
      public String toString() {
        return expectedElements.toString();
      }
    };
  }

  @Nonnull
  public static WebElementsCondition containsSpending(SpendJson... expected) {
    return containsSpending(List.of(expected));
  }

  @Nonnull
  public static WebElementsCondition containsSpending(List<SpendJson> expected) {
    return new WebElementsCondition() {

      private final Set<String> expectedElements = mapExpectedElements(toSet(), expected);

      @NotNull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        final Set<String> actualElements = mapActualElements(toSet(), elements);

        if (!actualElements.containsAll(expectedElements)) {
          final String message = String.format(
              "Spendings list is missing some expected elements (expected: %s, actual: %s)", expectedElements, actualElements
          );
          return rejected(message, actualElements);
        }

        return accepted();
      }

      @Nonnull
      @Override
      public String toString() {
        return expectedElements.toString();
      }
    };
  }

  @Nonnull
  private static <T extends Collection<String>> T mapExpectedElements(Collector<String, ?, T> collector, List<SpendJson> expected) {
    if (expected.isEmpty()) {
      throw new IllegalArgumentException("No expected spendings given");
    }

    return expected.stream()
        .map(s -> "Category: '" + s.category().name() + "' , Spend: " + s.amount() + " " + s.currency().symbol + " - " + s.description() + " - " + DateUtils.getDateAsString(s.spendDate(), "MMM d, yyyy"))
        .collect(collector);
  }

  @Nonnull
  private static <T extends Collection<String>> T mapActualElements(Collector<String, ?, T> collector, List<WebElement> elements) {
    return elements.stream()
        .map(e -> {
          final List<WebElement> cells = e.findElements(By.cssSelector("td"));

          final String category = cells.get(1).getText();
          final String amount = cells.get(2).getText();
          final String description = cells.get(3).getText();
          final String date = cells.get(4).getText();
          final double amountValue = Double.parseDouble(amount.split("\\s+")[0]);
          final String amountSymbol = amount.split("\\s+")[1];

          return "Category: '" + category + "' , Spend: " + amountValue + " " + amountSymbol + " - " + description + " - " + date;
        })
        .collect(collector);
  }
}
