package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {
    public final SelenideElement acceptBtn = $(By.xpath("(//span[@class=\"MuiButton-icon MuiButton-startIcon MuiButton-iconSizeSmall css-16rzsu1\"])[1]"));
    public final SelenideElement declineBtn = $(By.xpath("(//span[@class=\"MuiButton-icon MuiButton-startIcon MuiButton-iconSizeSmall css-16rzsu1\"])[2]"));
    public final SelenideElement unfriendBtn = $(By.xpath("//button[contains(text(),'Unfriend')]"));

    public FriendsPage shouldPageHaveBtn() {
        acceptBtn.shouldBe(visible);
        declineBtn.shouldBe(visible);
        return this;
    }

    public FriendsPage checkPageHaveUnFriendBtn() {
        unfriendBtn.shouldBe(visible);
        return this;
    }

    public boolean checkFriendsIfUserIsEmpty() {
        if (!unfriendBtn.isDisplayed()) return true;
        else {
            return false;
        }
    }

}
