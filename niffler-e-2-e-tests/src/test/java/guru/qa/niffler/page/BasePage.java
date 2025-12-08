package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

  protected static final Config CFG = Config.getInstance();

  private final SelenideElement alert = $(".MuiSnackbar-root");
  private final ElementsCollection formErrors = $$("p.Mui-error, .input__helper-text");

  @SuppressWarnings("unchecked")
  @Nonnull
  @Step("Check that alert message appears: {expectedText}")
  public T checkAlert(String text) {
    alert.shouldHave(text(text));
    return (T) this;
  }

  @Step("Check that form error message appears: {expectedText}")
  @SuppressWarnings("unchecked")
  @Nonnull
  public T checkFormErrorMessage(String... expectedText) {
    formErrors.should(CollectionCondition.textsInAnyOrder(expectedText));
    return (T) this;
  }
}
