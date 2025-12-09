package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

@WebTest
public class SpendingTest {

  @User(
      spendings = @Spending(
          amount = 89990.00,
          description = "Обучение Niffler 2.0 юбилейный поток!",
          category = "Обучение"
      )
  )
  @Test
  void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
    final String spendDescription = user.testData().spendings().getFirst().description();
    final String newDescription = "Обучение Niffler Next Generation";

    Selenide.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getSpendingTable()
        .editSpending(spendDescription)
        .setNewSpendingDescription(newDescription)
        .saveSpending();

    new MainPage().getSpendingTable()
        .checkTableContains(newDescription);
  }

  @User
  @Test
  void shouldAddNewSpending(UserJson user) {
    String category = "Friends";
    int amount = 100;
    Date currentDate = new Date();
    String description = RandomDataUtils.randomSentence(3);

    Selenide.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getHeader()
        .addSpendingPage()
        .setNewSpendingCategory(category)
        .setNewSpendingAmount(amount)
        .setNewSpendingDate(currentDate)
        .setNewSpendingDescription(description)
        .saveSpending()
        .checkAlert("New spending is successfully created");

    new MainPage().getSpendingTable()
        .checkTableContains(description);
  }

  @User
  @Test
  void shouldNotAddSpendingWithEmptyCategory(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getHeader()
        .addSpendingPage()
        .setNewSpendingAmount(100)
        .setNewSpendingDate(new Date())
        .saveSpending()
        .checkFormErrorMessage("Please choose category");
  }

  @User
  @Test
  void shouldNotAddSpendingWithEmptyAmount(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getHeader()
        .addSpendingPage()
        .setNewSpendingCategory("Friends")
        .setNewSpendingDate(new Date())
        .saveSpending()
        .checkFormErrorMessage("Amount has to be not less then 0.01");
  }

  @User(
      spendings = @Spending(
          amount = 89990.00,
          description = "Обучение Niffler 2.0 юбилейный поток!",
          category = "Обучение"
      )
  )
  @Test
  void deleteSpendingTest(UserJson user) {
    final String spendDescription = user.testData().spendings().getFirst().description();

    Selenide.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getSpendingTable()
        .deleteSpending(spendDescription)
        .checkTableSize(0);
  }

  @User(
      spendings = @Spending(
          category = "Обучение",
          description = "Обучение Advanced 2.0",
          amount = 79990
      )
  )
  @ScreenShotTest(value = "img/expected-stat.png", rewriteExpected = true)
  void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getStatComponent()
        .checkStatisticBubblesContains("Обучение 79990 ₽")
        .checkStatisticImage(expected)
        .checkBubbles(Color.yellow);
  }

  @User(
      categories = {
          @Category(name = "Поездки"),
          @Category(name = "Ремонт", archived = true),
          @Category(name = "Страховка", archived = true)
      },
      spendings = {
          @Spending(
              category = "Поездки",
              description = "В Москву",
              amount = 9500
          ),
          @Spending(
              category = "Ремонт",
              description = "Цемент",
              amount = 100
          ),
          @Spending(
              category = "Страховка",
              description = "ОСАГО",
              amount = 3000
          )
      }
  )
  @ScreenShotTest(value = "img/expected-stat-archived.png", rewriteExpected = true)
  void statComponentShouldDisplayArchivedCategories(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getStatComponent()
        .checkStatisticBubblesContains("Поездки 9500 ₽", "Archived 3100 ₽")
        .checkStatisticImage(expected)
        .checkBubbles(Color.yellow, Color.green);
  }
}

