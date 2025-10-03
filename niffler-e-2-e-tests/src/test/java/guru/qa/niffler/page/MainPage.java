package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class MainPage {
  private final SelenideElement spendingTable = $("#spendings");
  private final SelenideElement statisticsElement = $(byText("Statistics"));
  private final SelenideElement menuBtn = $("[aria-label='Menu']");
  private final SelenideElement profile = $("[href='/profile']");


  public MainPage checkThatPageLoaded() {
    spendingTable.should(visible);
    return this;
  }

  public MainPage checkStatisticsExist() {
    statisticsElement.shouldBe(visible);
    return this;
  }

  public EditSpendingPage editSpending(String description) {
    spendingTable.$$("tbody tr").find(text(description)).$$("td").get(5).click();
    return new EditSpendingPage();
  }

  public MainPage checkThatTableContains(String description) {
    spendingTable.$$("tbody tr").find(text(description)).should(visible);
    return this;
  }

  public ProfilePage goToProfilePage(){
    menuBtn.click();
    profile.click();
    assertThat(url(), containsString("/profile"));
    return new ProfilePage();
  }
}
