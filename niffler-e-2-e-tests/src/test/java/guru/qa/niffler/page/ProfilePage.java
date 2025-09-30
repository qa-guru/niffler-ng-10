package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.PressEnter;
import guru.qa.niffler.model.CategoryModel;
import org.openqa.selenium.By;

import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class ProfilePage {
    final SelenideElement showArchivedCategoriesBtn = $(By.xpath("//span[@class=\"MuiSwitch-root MuiSwitch-sizeMedium css-ecvcn9\"]"));
    final SelenideElement unarchiveCategoryBtn = $(By.xpath("(//button[@aria-label=\"Unarchive category\"])[1]"));
    final SelenideElement firstArchiveCategoryName = $(By.xpath("(//span[@class=\"MuiChip-label MuiChip-labelMedium css-14vsv3w\"])[1]"));
    final SelenideElement archiveCategoryBtn = $(By.xpath("//button[@aria-label=\"Archive category\"]"));
    final SelenideElement confirmeArchiveBtn = $(By.xpath("//button[contains(text(),'Archive')]"));
    final SelenideElement confirmeUnarchiveBtn = $(By.xpath("//button[contains(text(),'Unarchive')]"));
    final SelenideElement categoryFld = $("#category");
    final SelenideElement correctArchiveMessage = $(By.xpath("//div[contains(text(),'is archived')]"));
    final SelenideElement correctUnarchiveMessage = $(By.xpath("//div[contains(text(),'is unarchived')]"));




    public ProfilePage addNewCategory(String name){
        categoryFld.shouldBe(visible).val(name).pressEnter();
        return this;
    }
    public ProfilePage archiveCategory(){
        archiveCategoryBtn.shouldBe(visible).click();
        confirmeArchiveBtn.shouldBe(visible).click();
        return this;
    }

    public ProfilePage unarchiveCategory(){
        showArchivedCategoriesBtn.shouldBe(visible).click();
        unarchiveCategoryBtn.shouldBe(visible).click();
        confirmeUnarchiveBtn.shouldBe(visible).click();
        return this;
    }

    public String haveRightMessageAfterArchivedCategory(String categoryName ){
        return correctArchiveMessage.shouldBe(visible).getText();
    }
    public String haveRightMessageAfterUnarchiveCategory(String categoryName ){
        return correctUnarchiveMessage.shouldBe(visible).getText();
    }

    public String getNameArchiveCategory(){
        return firstArchiveCategoryName.shouldBe(visible).getText();
    }

    public ProfilePage archiveAllCategories(){
        ElementsCollection allCategories = $$(By.xpath("//button[@aria-label=\"Archive category\"]"));

        for (SelenideElement element: allCategories){
            element.shouldBe(visible).click();
            confirmeArchiveBtn.shouldBe(visible).click();
        }
        return this;
    }


}
