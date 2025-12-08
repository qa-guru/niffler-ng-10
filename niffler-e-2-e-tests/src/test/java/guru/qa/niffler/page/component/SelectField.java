package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class SelectField extends BaseComponent<SelectField> {

  public SelectField(SelenideElement self) {
    super(self);
  }

  private final SelenideElement input = self.$("input");

  @Step("Pick value '{0}' from select component ")
  public void setValue(String value) {
    self.click();
    $$("li[role='option']").find(text(value)).click();
  }

  @Step("Check that selected value is equal to '{0}'")
  public void checkSelectValueIsEqualTo(String value) {
    self.shouldHave(text(value));
  }
}
