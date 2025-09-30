package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.PressEnter;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ProfilePage {
    final SelenideElement showArchiveCategoriesBtn = $(By.xpath("//span[@class=\"MuiButtonBase-root MuiSwitch-switchBase MuiSwitch-colorPrimary PrivateSwitchBase-root MuiSwitch-switchBase MuiSwitch-colorPrimary css-1voo1et\"]"));
    final SelenideElement unarchiveCategory = $(By.xpath("//button[@class=\"MuiButtonBase-root MuiIconButton-root MuiIconButton-colorInherit MuiIconButton-sizeLarge css-1obba8g\"]"));
    final SelenideElement archiveCategoryBtn = $(By.xpath("//button[@aria-label=\"Archive category\"]"));
    final SelenideElement confirmeArchiveBtn = $(By.xpath("//button[contains(text(),'Archive')]"));
    final SelenideElement confirmeBtn = $(By.xpath("//button[contains(text(),'Unarchive')]"));
    final SelenideElement categoryFld = $("#category");
    final SelenideElement messageIfArchiveOd =


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
        showArchiveCategoriesBtn.shouldBe(visible).click();
        unarchiveCategory.shouldBe(visible).click();
        confirmeBtn.shouldBe(visible).click();
        return this;
    }

    public String checkTextInArchiveMessage(String s){

    }







}
