package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;

public class ProfilePage {
    private final SelenideElement checkbox = $("[type='checkbox']");
    private final ElementsCollection activeCategories = $$x("//div[contains(@class,'MuiGrid-item')][.//*[contains(@class,'MuiChip-colorPrimary')]]");
    private final ElementsCollection archivedCategories = $$x("//div[contains(@class,'MuiGrid-item')][.//*[contains(@class,'MuiChip-colorDefault')]]");

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
}
