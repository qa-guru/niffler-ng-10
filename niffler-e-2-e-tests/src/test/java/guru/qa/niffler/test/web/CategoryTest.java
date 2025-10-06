package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.extension.CategoryGenerateExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(CategoryGenerateExtension.class)
public class CategoryTest {
    private static final Config CFG = Config.getInstance();

    @Category(
            username = "mikeVasovsky_test_3.1",
            archived = true
    )
    @Test
    public void checkCorrectMessageAfterArchiveCategory(CategoryJson category) {
         Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("mikeVasovsky_test_3.1", "1111")
                .goToProfilePage()
                 .showArchiveCategories()
                 .findCategory(category.name());
    }

    @Category(
            username = "mikeVasovsky_test_3.1",
            archived = false
    )
    @Test
    public void checkCorrectMessageAfterUnarchiveCategory(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("mikeVasovsky_test_3.1", "1111")
                .goToProfilePage()
                .addNewCategory(category.name())
                .archiveCategory(category.name())
                .unarchiveCategory(category.name())
                .haveRightMessageAfterUnarchiveCategory(String.format("Category %s is unarchived", category.name()));
    }


}
