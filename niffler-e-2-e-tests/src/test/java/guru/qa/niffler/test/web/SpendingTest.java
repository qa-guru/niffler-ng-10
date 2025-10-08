package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class SpendingTest {

  private static final Config CFG = Config.getInstance();

  @User(
      username = "duck",
      spendings = @Spending(
          amount = 89990.00,
          description = "Обучение Niffler 2.0 юбилейный поток!",
          category = "Обучение"
      )
  )
  @Test
  void mainPageShouldBeDisplayedAfterSuccessLogin(SpendJson spendJson) {
    final String newDescription = "Обучение Niffler Next Generation";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin("duck", "12345")
        .checkThatPageLoaded()
        .editSpending(spendJson.description())
        .setNewSpendingDescription(newDescription)
        .save()
        .checkThatTableContainsSpending(newDescription);
  }
}
