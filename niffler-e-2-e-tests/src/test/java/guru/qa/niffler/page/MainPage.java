package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {
    private final SelenideElement spendingTable = $("#spendings");
    private final SelenideElement menuOfUser = $("[data-testid='PersonIcon']");
    private final SelenideElement profileOfUser = $(withText("Profile"));
    private final SelenideElement statisticsPart = $("#stat");
    private final SelenideElement friendsPage = $(byText("Friends"));

    public MainPage checkThatPageLoaded() {
        spendingTable.should(visible);
        statisticsPart.should(visible);
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

    public ProfilePage goToProfilePage() {
        menuOfUser.click();
        profileOfUser.click();
        return new ProfilePage();
    }

    public FriendsPage goToFriendsPage() {
        menuOfUser.click();
        friendsPage.click();
        return new FriendsPage();
    }
}
