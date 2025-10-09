package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class ProfilePage {
    final SelenideElement showArchivedCategoriesBtn = $(By.xpath("//span[@class=\"MuiSwitch-root MuiSwitch-sizeMedium css-ecvcn9\"]"));
    final SelenideElement unarchiveCategoryBtn = $(By.xpath("(//button[@aria-label=\"Unarchive category\"])[1]"));
    final SelenideElement confirmeArchiveBtn = $(By.xpath("//button[contains(text(),'Archive')]"));
    final SelenideElement confirmeUnarchiveBtn = $(By.xpath("//button[contains(text(),'Unarchive')]"));
    final SelenideElement categoryFld = $("#category");
    final SelenideElement correctUnarchiveMessage = $(By.xpath("//div[contains(text(),'is unarchived')]"));

    public ProfilePage addNewCategory(String name) {
        categoryFld.val(name).pressEnter();
        return this;
    }

    public ProfilePage archiveCategory(String category) {
        SelenideElement archiveBtn = $(By.xpath("//span[contains(text(),'" + category + "')]/../following-sibling::div/button[@aria-label=\"Archive category\"]"));
        archiveBtn.click();
        confirmeArchiveBtn.click();
        return this;
    }

    public ProfilePage unarchiveCategory(String category) {
        showArchivedCategoriesBtn.click();
        SelenideElement unArchiveBtn = $(By.xpath("//span[contains(text(),'" + category + "')]/../following-sibling::div[@class=\"MuiBox-root css-0\"]/button[@aria-label=\"Archive category\"]"));
        unarchiveCategoryBtn.click();
        confirmeUnarchiveBtn.click();
        return this;
    }

    public ProfilePage haveRightMessageAfterUnarchiveCategory(String message) {
        correctUnarchiveMessage.shouldHave(text(message));
        return this;
    }

    public ProfilePage showArchiveCategories() {
        showArchivedCategoriesBtn.click();
        return this;
    }

    public ProfilePage findCategory(String categoryName) {
        SelenideElement category = $(By.xpath("//span[contains(text(),'" + categoryName + "')]"));
        category.shouldBe(visible);
        return this;
    }
}