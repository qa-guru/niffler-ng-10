package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {
  private final SelenideElement spendingTable = $("#spendings");
  private final SelenideElement profileMenuBtn = $(By.xpath("//div[@class=\"MuiAvatar-root MuiAvatar-circular MuiAvatar-colorDefault css-1pqo26w\"]"));
  private final SelenideElement profileBtn = $(By.xpath("//a[contains(text(), 'Profile')]"));

  public MainPage checkThatPageLoaded() {
    spendingTable.should(visible);
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
    profileMenuBtn.shouldBe(visible).click();
    profileBtn.shouldBe(visible).click();
    return new ProfilePage();
  }
}
