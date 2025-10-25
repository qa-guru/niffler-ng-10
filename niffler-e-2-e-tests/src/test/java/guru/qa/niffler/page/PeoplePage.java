package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class PeoplePage {
    private final SelenideElement tableWithAllPeple = $("#all");

    public PeoplePage checkWaitingOfUserInvitations(String expectedUsernames) {
        tableWithAllPeple.$$("tr").find(text(expectedUsernames)).shouldHave(text("Waiting..."));
        return this;
    }
}
