package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {
    private final SelenideElement friendPanel = $("#simple-tabpanel-friends");

    public FriendsPage checkThatFriendsListIsEmpty() {
        friendPanel.shouldHave(text("There are no users yet"));
        return this;
    }

    public FriendsPage checkThatFriendExist(String friendName) {
        friendPanel
                .$$("tr")
                .find(text(friendName))
                .$(byText("Unfriend")).shouldBe(visible);
        return this;
    }

    public FriendsPage checkThatIncomeInviteExist(String peopleName) {
        friendPanel
                .$$("tr")
                .find(text(peopleName))
                .$(byText("Accept")).shouldBe(visible);
        return this;
    }
}
