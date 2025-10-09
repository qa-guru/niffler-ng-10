package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class AllPeoplePage {
    private final SelenideElement requestPanel = $("#simple-tabpanel-all");

    public AllPeoplePage checkOutcomeRequest(String outcomeFriend){
        requestPanel
                .$$("tr")
                .find(text(outcomeFriend))
                .$(byText("Waiting...")).shouldBe(visible);
        return this;
    }
}
