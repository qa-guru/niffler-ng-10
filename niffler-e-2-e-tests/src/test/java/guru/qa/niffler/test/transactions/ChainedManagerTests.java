package guru.qa.niffler.test.transactions;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.util.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

public class ChainedManagerTests {

    private static final Config CFG = Config.getInstance();
    private static final String NIFFLER_AUTH_URL = CFG.authJdbcUrl();
    private static final String NIFFLER_USERDATA_URL = CFG.userdataJdbcUrl();
    private static TransactionTemplate txTemplate = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(
                            dataSource(NIFFLER_AUTH_URL)),
                    new JdbcTransactionManager(
                            dataSource(NIFFLER_USERDATA_URL)))
    );
    private static final PasswordEncoder PE = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    static AuthUserEntity createAuthUser() {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(RandomDataUtils.randomUsername());
        authUser.setPassword(PE.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        return authUser;
    }

    static UserEntity createUserEntity(AuthUserEntity authUserEntity) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(authUserEntity.getUsername());
        userEntity.setFirstname(null);
        userEntity.setSurname(null);
        userEntity.setFullname(null);
        userEntity.setCurrency(CurrencyValues.USD);
        userEntity.setPhoto(new byte[0]);
        userEntity.setPhotoSmall(new byte[0]);
        return userEntity;
    }

    static UserEntity createUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(RandomDataUtils.randomUsername());
        userEntity.setFirstname(null);
        userEntity.setSurname(null);
        userEntity.setFullname(null);
        userEntity.setCurrency(CurrencyValues.USD);
        userEntity.setPhoto(new byte[0]);
        userEntity.setPhotoSmall(new byte[0]);
        return userEntity;
    }

    static AuthUserEntity createIncorrectAuthUser() {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(null);
        authUser.setPassword(PE.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        return authUser;
    }


    @Test
    public void successTransactionTest() {
        txTemplate.execute(status -> {
            AuthUserEntity authUserEntity = createAuthUser();
            UserEntity userEntity = createUserEntity(authUserEntity);
            System.out.println(authUserEntity.getUsername());
            new AuthUserDaoSpringJdbc().create(authUserEntity);
            new UserdataUserDaoSpringJdbc().create(userEntity);
            return null;
        });
    }

    @Test
    public void notSsuccessTransactionTestWithoutRollback() {
        txTemplate.execute(status -> {
            AuthUserEntity authUserEntity = createIncorrectAuthUser();
            UserEntity userEntity = createUserEntity();
            System.out.println(authUserEntity.getUsername() + "  authUserEntity.getUsername()");
            System.out.println(userEntity.getUsername() + "  userEntity.getUsername()");
            new AuthUserDaoSpringJdbc().create(authUserEntity); // Некорректный юзер в первой транзакции
            new UserdataUserDaoSpringJdbc().create(userEntity);
            return null;
        });
    }

    @Test
    public void notSsuccessTransactionTestWithRollback() {
        txTemplate.execute(status -> {
            AuthUserEntity authUserEntity = createIncorrectAuthUser();
            UserEntity userEntity = createUserEntity();
            System.out.println(authUserEntity.getUsername() + "  authUserEntity.getUsername()");
            System.out.println(userEntity.getUsername() + "  userEntity.getUsername()");
            new UserdataUserDaoSpringJdbc().create(userEntity);
            new AuthUserDaoSpringJdbc().create(authUserEntity); // Некорректный юзер во второй транзакции
            return null;
        });
    }

}

