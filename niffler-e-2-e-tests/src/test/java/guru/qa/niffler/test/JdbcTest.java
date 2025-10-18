package guru.qa.niffler.test;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.impl.AuthUserDaoJdbc;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.*;
import static org.junit.jupiter.api.Assertions.*;

public class JdbcTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void createSpend() {
        SpendDbClient spendDbClient = new SpendDbClient();
        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                RandomDataUtils.randomCategoryName(),
                                RandomDataUtils.randomUsername(),
                                false
                        ),
                        CurrencyValues.EUR,
                        100.00,
                        RandomDataUtils.randomSentence(3),
                        RandomDataUtils.randomUsername()
                )
        );
        assertNotNull(spend);
        assertNotNull(spend.id());
    }

    @Test
    void createAuthUser() {
        AuthUserEntity createdUser = transaction(con -> {
            AuthUserEntity user = new AuthUserEntity();
            user.setUsername(RandomDataUtils.randomUsername());
            user.setPassword(RandomDataUtils.randomSentence(3));
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            return new AuthUserDaoJdbc(con).create(user);
        }, CFG.authJdbcUrl());

        assertNotNull(createdUser.getId());
        
        Optional<AuthUserEntity> foundUser = transaction((Connection con) -> 
            new AuthUserDaoJdbc(con).findById(createdUser.getId()), 
            CFG.authJdbcUrl()
        );

        assertTrue(foundUser.isPresent());
    }

    @Test
    void createAuthorities() {
        String testUsername = RandomDataUtils.randomUsername();
        
        UUID userId = transaction(con -> {
            AuthUserEntity user = new AuthUserEntity();
            user.setUsername(testUsername);
            user.setPassword(RandomDataUtils.randomSentence(3));
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            return new AuthUserDaoJdbc(con).create(user).getId();
        }, CFG.authJdbcUrl());

        transaction((Connection con) -> {
            AuthorityEntity readAuth = new AuthorityEntity();
            readAuth.setUserId(userId);
            readAuth.setAuthority(Authority.read);

            AuthorityEntity writeAuth = new AuthorityEntity();
            writeAuth.setUserId(userId);
            writeAuth.setAuthority(Authority.write);

            new AuthAuthorityDaoJdbc(con).create(readAuth, writeAuth);
        }, CFG.authJdbcUrl());

        Optional<AuthUserEntity> userWithAuthorities = transaction((Connection con) ->
            new AuthUserDaoJdbc(con).findById(userId),
            CFG.authJdbcUrl()
        );

        assertTrue(userWithAuthorities.isPresent());
        assertEquals(testUsername, userWithAuthorities.get().getUsername());
    }

    @Test
    void xaTransactionRollback() {
        String testUsername = RandomDataUtils.randomUsername();
        
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            xaTransaction(
                new XaFunction<>(
                    con -> {
                        AuthUserEntity authUser = new AuthUserEntity();
                        authUser.setUsername(testUsername);
                        authUser.setPassword(RandomDataUtils.randomSentence(3));
                        authUser.setEnabled(true);
                        authUser.setAccountNonExpired(true);
                        authUser.setAccountNonLocked(true);
                        authUser.setCredentialsNonExpired(true);
                        return new AuthUserDaoJdbc(con).create(authUser);
                    },
                    CFG.authJdbcUrl()
                ),
                new XaFunction<>(
                    con -> {
                        throw new RuntimeException();
                    },
                    CFG.userdataJdbcUrl()
                )
            )
        );
        
        assertNotNull(exception);
    }

    @Test
    void transactionIsolationLevel() {
        transaction((Connection con) -> {
            try {
                assertEquals(Connection.TRANSACTION_REPEATABLE_READ, con.getTransactionIsolation());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, CFG.authJdbcUrl(), Connection.TRANSACTION_REPEATABLE_READ);
    }

    @Test
    void xaTransactionIsolationLevel() {
        xaTransaction(
            Connection.TRANSACTION_SERIALIZABLE,
            new XaFunction<>(
                con -> {
                    try {
                        assertEquals(Connection.TRANSACTION_SERIALIZABLE, con.getTransactionIsolation());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                },
                CFG.authJdbcUrl()
            ),
            new XaFunction<>(
                con -> {
                    try {
                        assertEquals(Connection.TRANSACTION_SERIALIZABLE, con.getTransactionIsolation());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                },
                CFG.userdataJdbcUrl()
            )
        );
    }

    @Test
    void createUserInBothDatabases() {
        String username = RandomDataUtils.randomUsername();
        
        UserJson user = new UserDbClient().createUser(
            new UserJson(
                null,
                username,
                RandomDataUtils.randomName(),
                RandomDataUtils.randomSurname(),
                RandomDataUtils.randomName() + " " + RandomDataUtils.randomSurname(),
                CurrencyValues.RUB,
                null,
                null,
                null
            )
        );

        assertNotNull(user);
        assertNotNull(user.id());
        assertEquals(username, user.username());
    }
}
