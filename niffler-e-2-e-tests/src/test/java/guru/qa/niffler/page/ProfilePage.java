package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage {
    private final SelenideElement
            avatarOfUser = $("#PersonIcon"),
            uploadNewPictureButton = $(withText("Upload new picture")),
            registerPasskeyButton = $("#:r10:"),
            saveChangesButton = $("#:r11:"),
            userNameField = $("#username"),
            nameOfUserNameField = $("#name"),
            categoryField = $("#category"),
            toggleShowArchived = $(".PrivateSwitchBase-input"),
            makeCategoryArchiveButton = $("[aria-label=Archive category]"),
            confirmButtonToMakeCategoryArchive = $$("[type=button]").findBy(text("Archive")),
            EditCategoryButton = $("[aria-label=Edit category]");


    //Коллекции из списка не архивных и архивных категорий
    private final ElementsCollection
            categories = $$(".MuiChip-filled.MuiChip-colorPrimary"),
            categoriesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");


    @Step("Check Profile Page elements")
    public ProfilePage checkProfilePageElements() {
        avatarOfUser.shouldBe(visible);
        uploadNewPictureButton.shouldBe(visible);
        registerPasskeyButton.shouldBe(visible);
        saveChangesButton.shouldBe(visible);
        userNameField.shouldBe(visible);
        nameOfUserNameField.shouldBe(visible);
        categoryField.shouldBe(visible);
        toggleShowArchived.shouldBe(visible);
        return this;
    }


    @Step("Add new non-archive category")
    public ProfilePage addNewCategory(String category) {
        categoryField.setValue(category).pressEnter();
        return this;
    }

    @Step("Check category: '{category}'")
    public ProfilePage checkCategoryIsDisplayed(String category) {
        categories.find(text(category)).shouldBe(visible);
        return this;
    }

    @Step("Add new archive category")
    public ProfilePage addNewArchiveCategory(String category) {
        categoryField.setValue(category).pressEnter();
        return this;
    }

    @Step("Check archive category from list: '{category}'")
    public ProfilePage checkArchiveCategoryIsDisplayed(String category) {
        categoriesArchived.find(text(category)).shouldBe(visible);
        return this;
    }

    @Step("Check archive category from list: '{category}'")
    public ProfilePage showActiveAndArchivedCategoriesList() {
        toggleShowArchived.click();
        return this;
    }


}
