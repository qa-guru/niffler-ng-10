package guru.qa.niffler.test.fake;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;

//@Disabled
public class JdbcTest {

    @Test
    void txTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name-tx-2",
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx",
                        null
                )
        );

        System.out.println(spend);
    }

    @Test
    void xaTxTest() {
        UserDbClient userDbClient = new UserDbClient();
        UserJson user = userDbClient.create(
                new UserJson(
                        null,
                        "aleksanr_druz",
                        "user-email-xa",
                        "user-password-xa",
                        "user-phone-xa",
                        CurrencyValues.KZT,
                        "",
                        "")
        );
        System.out.println(user);
    }

    @Test
    void springJdbcTest() {
        UserDbClient userDbClient = new UserDbClient();
        UserJson user = userDbClient.createUserSpringJdbc(
                new UserJson(
                        null,
                        "valentin-1",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
        System.out.println(user);
    }
}
