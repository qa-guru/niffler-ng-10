package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class AllPeoplePage {
    public final SelenideElement waitingBtn = $(By.xpath("//span[contains(text(),'Waiting...')]"));

    public AllPeoplePage checkPageHaveWaitingBtn() {
        waitingBtn.shouldBe(visible);
        return this;
    }

}
