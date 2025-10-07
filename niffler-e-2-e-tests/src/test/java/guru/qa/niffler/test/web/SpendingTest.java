package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

@ExtendWith(BrowserExtension.class)
public class SpendingTest {

    private static final Config CFG = Config.getInstance();

    @User(
            username = "duck",
            spendings = @Spending(
                    category = "Учеба",
                    amount = 89900,
                    currency = CurrencyValues.RUB,
                    description = "Обучение Niffler 2.0 юбилейный поток!"
            )
    )
    @Test
    void spendingDescriptionShouldBeEditedByTableAction(SpendJson spending) {
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login("duck", "12345");
        new MainPage()
                .editSpending(spending.description())
                .setNewSpendingDescription(newDescription)
                .save()
                .checkThatTableContains(newDescription);
    }

    @User(
            username = "testtest",
            categories = @Category(archived = false)
    )
    @Test
    void categoryShouldBeArchived(CategoryJson categoryJson) throws IOException {
        Selenide.open(CFG.frontUrl(), LoginPage.class).login("testtest", "test");
        new MainPage().goToProfilePage()
                .archivedCategory(categoryJson.name())
                .clickShowArchived()
                .checkArchivedCategory(categoryJson.name());
    }

    @User(
            username = "testtest",
            categories = @Category(archived = true)
    )
    @Test
    void categoryShouldNotBeArchived(CategoryJson categoryJson) throws IOException {
        Selenide.open(CFG.frontUrl(), LoginPage.class).login("testtest", "test");
        new MainPage().goToProfilePage()
                .clickShowArchived()
                .unArchivedCategory(categoryJson.name())
                .checkActiveCategory(categoryJson.name());
    }
}