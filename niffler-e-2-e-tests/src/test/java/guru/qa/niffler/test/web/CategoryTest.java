package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.NewCategory;
import guru.qa.niffler.jupiter.extension.CategoryGenerateExtension;
import guru.qa.niffler.jupiter.extension.CategoryGenerateResolverExtension;
import guru.qa.niffler.model.CategoryModel;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith({CategoryGenerateExtension.class, CategoryGenerateResolverExtension.class})
public class CategoryTest {
    private static final Config CFG = Config.getInstance();
    private LoginPage loginPage = new LoginPage();
    private ProfilePage profilePage = new ProfilePage();

    @NewCategory
    @Test
    public void checkCorrectMessageAfterArchiveCategory(CategoryModel categoryModel){
      String expectedMessage = String.format("Category %s is archived",categoryModel.getCategoryName());
      String resultMessage = Selenide.open(CFG.frontUrl(),LoginPage.class)
                .login("mikeVasovsky","111")
                .goToProfilePage()
                .addNewCategory(categoryModel.getCategoryName())
                .archiveCategory()
                .haveRightMessageAfterArchivedCategory(categoryModel.getCategoryName());
        assertEquals(expectedMessage,resultMessage);
    }

    @NewCategory
    @Test
    public void checkCorrectMessageAfterUnarchiveCategory(CategoryModel categoryModel){
        String expectedMessage = String.format("Category %s is unarchived",categoryModel.getCategoryName());
        String resultMessage = Selenide.open(CFG.frontUrl(),LoginPage.class)
                .login("mikeVasovsky","111")
                .goToProfilePage()
                .archiveAllCategories()
                .addNewCategory(categoryModel.getCategoryName())
                .archiveCategory()
                .unarchiveCategory()
                .haveRightMessageAfterUnarchiveCategory(categoryModel.getCategoryName());
        assertEquals(expectedMessage,resultMessage);
    }



}
