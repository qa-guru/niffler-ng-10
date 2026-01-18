package guru.qa.niffler.test.graphql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.*;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserGraphqlTest extends BaseGraphqlTest {

    @Test
    @ApiLogin
    @User(friends = 1)
    public void queryForAnotherUserCategories(@Token String bearerToken) {
        ApolloCall<FriendsCategoriesQuery.Data> categoriesCall = apolloClient.query(new FriendsCategoriesQuery())
                .addHttpHeader("authorization", bearerToken);

        ApolloResponse<FriendsCategoriesQuery.Data> response = Rx2Apollo.single(categoriesCall).blockingGet();

        assertNotNull(response.errors);
    }

    @Test
    @ApiLogin
    @User
    public void subqueriesRestrictions(@Token String bearerToken) {
        ApolloCall<FriendsSubqueryQuery.Data> subqueryCall = apolloClient.query(new FriendsSubqueryQuery())
                .addHttpHeader("authorization", bearerToken);

        ApolloResponse<FriendsSubqueryQuery.Data> response = Rx2Apollo.single(subqueryCall).blockingGet();

        assertNotNull(response.errors);
    }
}