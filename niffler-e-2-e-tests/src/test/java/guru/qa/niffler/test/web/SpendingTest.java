package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.AuthApiClient;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.service.SpendClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.UUID;

@ExtendWith(BrowserExtension.class)
public class SpendingTest {

    private static final Config CFG = Config.getInstance();

    @Spending(
            username = "duck",
            category = "Учеба",
            amount = 89900,
            currency = CurrencyValues.RUB,
            description = "Обучение Niffler 2.0 юбилейный поток!"
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

    @Category(
            username = "bazz12"
    )
    @Test
    void categoryShouldBeArchived(CategoryJson categoryJson) throws IOException {
        final SpendApiClient spendClient = new SpendApiClient();
        CategoryJson resp = spendClient.updateCategory(new CategoryJson(categoryJson.id(), categoryJson.name(), categoryJson.username(), true));
        Assertions.assertTrue(resp.archived());
        spendClient.updateCategory(new CategoryJson(categoryJson.id(), categoryJson.name(), categoryJson.username(), false));
    }

    @Category(
            username = "bazz12"
    )
    @Test
    void categoryShouldNotBeArchived(CategoryJson categoryJson) throws IOException {
        final SpendApiClient spendClient = new SpendApiClient();
        CategoryJson resp = spendClient.updateCategory(new CategoryJson(categoryJson.id(), categoryJson.name(), categoryJson.username(), true));
        Assertions.assertTrue(resp.archived());
        resp = spendClient.updateCategory(new CategoryJson(categoryJson.id(), categoryJson.name(), categoryJson.username(), false));
        Assertions.assertFalse(resp.archived());
    }
}
