package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.Selenide.*;

public class ProfilePage {
    private final SelenideElement checkbox = $("[type='checkbox']");
    private final ElementsCollection activeCategories = $$x("//div[contains(@class,'MuiGrid-item')][.//*[contains(@class,'MuiChip-colorPrimary')]]");
    private final ElementsCollection archivedCategories = $$x("//div[contains(@class,'MuiGrid-item')][.//*[contains(@class,'MuiChip-colorDefault')]]");
    private final SelenideElement unArchiveBtn = $x("//button[.=\"Unarchive\"]");
    private final SelenideElement archiveBtn = $x("//button[.=\"Archive\"]");


    public ProfilePage clickShowArchived() {
        checkbox.click();
        return this;
    }

    public ProfilePage checkActiveCategory(String categoryName) {
        activeCategories.shouldHave(itemWithText(categoryName));
        return this;
    }

    public ProfilePage checkArchivedCategory(String categoryName) {
        archivedCategories.shouldHave(itemWithText(categoryName));
        return this;
    }

    public ProfilePage unArchivedCategory(String categoryName) {
        archivedCategories.findBy(Condition.text(categoryName))
                .$x(".//button[@aria-label='Unarchive category']")
                .click();
        unArchiveBtn.click();
        return this;
    }

    public ProfilePage archivedCategory(String categoryName) {
        activeCategories.findBy(Condition.text(categoryName))
                .$x(".//button[@aria-label='Archive category']")
                .click();
        archiveBtn.click();
        return this;
    }
}
