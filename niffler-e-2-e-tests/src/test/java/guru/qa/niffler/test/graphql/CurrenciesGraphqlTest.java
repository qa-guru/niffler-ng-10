package guru.qa.niffler.test.graphql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.CurrenciesQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CurrencyValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CurrenciesGraphqlTest extends BaseGraphqlTest {

    @Test
    @User
    @ApiLogin
    public void allCurrenciesShouldBeReturnedFromGateway(@Token String bearerToken) {
        ApolloCall<CurrenciesQuery.Data> currenciesCall = apolloClient.query(new CurrenciesQuery())
                .addHttpHeader("authorization", bearerToken);

        ApolloResponse<CurrenciesQuery.Data> response = Rx2Apollo.single(currenciesCall)
                .blockingGet();

        final CurrenciesQuery.Data data = response.dataOrThrow();
        List<CurrenciesQuery.Currency> all = data.currencies;
        Assertions.assertEquals(
                CurrencyValues.RUB.name(),
                all.get(0).currency.rawValue
        );
    }
}
