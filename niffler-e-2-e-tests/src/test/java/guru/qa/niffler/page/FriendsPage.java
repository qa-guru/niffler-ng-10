package guru.qa.niffler.page;

import com.codeborne.selenide.*;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.openqa.selenium.Keys.ENTER;

public class FriendsPage {
    private final SelenideElement friendsTab = $("[href=\"/people/friends\"]");
    private final SelenideElement allTab = $("[href=\"/people/all\"]");
    private final ElementsCollection friendsRows = $$("#friends tr");
    private final ElementsCollection peopleRows = $$("#all tr");
    private final ElementsCollection requestRows = $$("#requests tr");

    private final SelenideElement searchInput = $("[aria-label='search']");


    public FriendsPage checkEmptyStateOfFriends() {
        friendsRows.shouldBe(CollectionCondition.size(0));
        return this;
    }

    public FriendsPage checkThatFriendIsVisible(String expectedFriendName) {
        friendsRows.findBy(Condition.text(expectedFriendName))
                .shouldBe(Condition.visible);
        return this;
    }

    public FriendsPage find(String user) {
        searchInput.val(user).sendKeys(ENTER);
        return this;
    }

    public FriendsPage clickTabAllPeople() {
        allTab.click();
        return this;
    }

    public FriendsPage clickTabFriends() {
        friendsTab.click();
        return this;
    }

    public FriendsPage checkStatusWaiting(String user) {
        peopleRows.findBy(Condition.text(user))
                .shouldHave(Condition.text("Waiting..."));
        return this;
    }


    public FriendsPage checkFriendsRequest(String user) {
        requestRows.findBy(Condition.text(user))
                .$$("button")
                .findBy(Condition.text("Accept"))
                .shouldBe(visible);
        return this;
    }
}
