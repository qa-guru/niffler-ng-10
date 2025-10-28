package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {
    private final SelenideElement tableWithAllFriends = $("#friends");
    private final SelenideElement requestsTable = $("#requests");
    private final SelenideElement allPeoplePage = $(byText("All people"));

    @Step("Check that person is friend")
    public FriendsPage checkExistingFriends(String expectedUsername) {
        tableWithAllFriends.shouldHave(text(expectedUsername));
        return this;
    }

    public FriendsPage checkNoExistingFriends() {
        tableWithAllFriends.$$("tr").shouldHave(size(0));
        return this;
    }

    public FriendsPage checkExistingInvitations(String expectedUsernames) {
        requestsTable.$$("tr").shouldHave(textsInAnyOrder(expectedUsernames));
        return this;
    }

    public PeoplePage goToPeoplePage() {
        allPeoplePage.click();
        return new PeoplePage();
    }
}
