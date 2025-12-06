package guru.qa.niffler.test.transactions;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.util.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JdbcAndSpringJdbcTests {

    private static final Config CFG = Config.getInstance();
    private static final String NIFFLER_AUTH_URL = CFG.authJdbcUrl();
    private static final String NIFFLER_USERDATA_URL = CFG.userdataJdbcUrl();
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

    //Отличается от корректного только полем username, который установлен в null.
    static UserEntity createIncorrectUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(null);
        userEntity.setFirstname(null);
        userEntity.setSurname(null);
        userEntity.setFullname(null);
        userEntity.setCurrency(CurrencyValues.USD);
        userEntity.setPhoto(new byte[0]);
        userEntity.setPhotoSmall(new byte[0]);
        return userEntity;
    }

    //Создание юзера в БД niffler-auth в таблице user и в БД niffler-userdata в таблице user
    //без использования транзакции jdbc
    @Test
    public void createUserWithoutJdbcTransaction() {
        AuthUserEntity authUser = createAuthUser();
        UserEntity userEntity = createUserEntity(authUser);
        try (Connection authConnection = DataSources.dataSource(NIFFLER_AUTH_URL).getConnection();
             Connection userdataConnection = DataSources.dataSource(NIFFLER_USERDATA_URL).getConnection()) {
            new AuthUserDaoJdbc().create(authUser);
            new UserdataUserDaoJdbc().create(userEntity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Здесь ищу по именам в БД созданных пользователей, чтобы убедиться, что пользователь был создан.
        // Затем сравниваю результаты с ожидаемыми значениями.
        try (Connection authConnection = DataSources.dataSource(NIFFLER_AUTH_URL).getConnection();
             Connection userdataConnection = DataSources.dataSource(NIFFLER_USERDATA_URL).getConnection()) {
            String resultFromDBAuth = String.valueOf(
                    new AuthUserDaoJdbc().findByUsername(authUser.getUsername()).get().getUsername());
            String resultFromDBUserdata = String.valueOf(
                    new UserdataUserDaoJdbc().findByUsername(authUser.getUsername()).get().getUsername());
            assertTrue(authUser.getUsername().equals(resultFromDBAuth) &&
                    authUser.getUsername().equals(resultFromDBUserdata));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Создание юзера в БД niffler-auth в таблице user и в БД niffler-userdata в таблице user
    //с использованием транзакции jdbc
    @Test
    public void createUserWithJdbcTransaction() {
        AuthUserEntity authUser = createAuthUser();
        UserEntity userEntity = createUserEntity(authUser);
        XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
                NIFFLER_AUTH_URL,
                NIFFLER_USERDATA_URL
        );
        AuthUserDao authUserDao = new AuthUserDaoJdbc();
        UserdataUserDao userdataUserDao = new UserdataUserDaoJdbc();
        xaTransactionTemplate.execute(() -> {
            authUserDao.create(authUser);
            userdataUserDao.create(userEntity);
            return null;
        });
//        // Здесь ищу по именам в БД созданных пользователей, чтобы убедиться, что пользователь был создан.
//        // Затем сравниваю результаты с ожидаемыми значениями.
        Map<String, String> resultOfNames = new HashMap<>();
        xaTransactionTemplate.execute(() -> {
            resultOfNames.put("authUser", authUserDao.findByUsername(authUser.getUsername()).get().getUsername());
            resultOfNames.put("userEntity", userdataUserDao.findByUsername(userEntity.getUsername()).get().getUsername());
            return null;
        });
        assertTrue(authUser.getUsername().equals(resultOfNames.get("authUser")) &&
                authUser.getUsername().equals(resultOfNames.get("userEntity")));
    }

    //Создание юзера в БД niffler-auth в таблице user и в БД niffler-userdata в таблице user
    //без использования транзакции Spring jdbc
    @Test
    public void createUserWithoutSpringJdbcTransaction() {
        AuthUserEntity authUser = createAuthUser();
        UserEntity userEntity = createUserEntity(authUser);
        new AuthUserDaoJdbc().create(authUser);
        new UserdataUserDaoJdbc().create(userEntity);

        // Здесь ищу по именам в БД созданных пользователей, чтобы убедиться, что пользователь был создан.
        // Затем сравниваю результаты с ожидаемыми значениями.
        String authUserName = new AuthUserDaoJdbc().findByUsername(authUser.getUsername()).get().getUsername();
        String userEntityName = new UserdataUserDaoJdbc().findByUsername(userEntity.getUsername()).get().getUsername();
        assertTrue(authUser.getUsername().equals(authUserName) &&
                userEntity.getUsername().equals(userEntityName));
    }

    //Создание юзера в БД niffler-auth в таблице user и в БД niffler-userdata в таблице user
    //c использования транзакции Spring jdbc
    @Test
    public void createUserWithSpringJdbcTransaction() {
        AuthUserEntity authUser = createAuthUser();
        UserEntity userEntity = createUserEntity(authUser);
        XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
                NIFFLER_AUTH_URL,
                NIFFLER_USERDATA_URL
        );
        AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
        UserdataUserDao userdataUserDao = new UserdataUserDaoSpringJdbc();
        xaTransactionTemplate.execute(() -> {
            authUserDao.create(authUser);
            userdataUserDao.create(userEntity);
            return null;
        });
        Map<String, String> resultOfNames = new HashMap<>();
        xaTransactionTemplate.execute(() -> {
            resultOfNames.put("authUser", authUserDao.findByUsername(authUser.getUsername()).get().getUsername());
            resultOfNames.put("userEntity", userdataUserDao.findByUsername(userEntity.getUsername()).get().getUsername());
            return null;
        });
        assertTrue(authUser.getUsername().equals(resultOfNames.get("authUser")) &&
                userEntity.getUsername().equals(resultOfNames.get("userEntity")));
    }

    //Создание юзера в БД niffler-auth в таблице user и в БД niffler-userdata в таблице user
    //Во второй БД будет ошибка и транзакция должна откатиться
    @Test
    public void createUserWithJdbcTransactionNegative() {
        AuthUserEntity authUser = createAuthUser();
        UserEntity userEntity = createIncorrectUserEntity(); // Применяем юзера у которого username=null
        XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
                NIFFLER_AUTH_URL,
                NIFFLER_USERDATA_URL
        );
        AuthUserDao authUserDao = new AuthUserDaoJdbc();
        UserdataUserDao userdataUserDao = new UserdataUserDaoJdbc();
        try {
            xaTransactionTemplate.execute(() -> {
                authUserDao.create(authUser);
                userdataUserDao.create(userEntity);
                return null;
            });
        } catch (RuntimeException e) {
            System.out.println("Транзакция получила ошибку и откатилась");
        }

        // Проверяем, что пользователь НЕ найден в niffler-auth
        Optional<AuthUserEntity> foundInAuth = authUserDao.findByUsername(authUser.getUsername());
        assertFalse(foundInAuth.isPresent());

        // Проверяем, что пользователь НЕ найден в niffler-userdata
        Optional<UserEntity> foundInUserdata = userdataUserDao.findByUsername(authUser.getUsername());
        assertFalse(foundInUserdata.isPresent());
    }

    //Создание юзера в БД niffler-auth в таблице user и в БД niffler-userdata в таблице user
    //В первой БД будет ошибка и транзакция должна откатиться
    @Test
    public void createUserWithJdbcTransactionNegativeSecond() {
        AuthUserEntity authUser = createIncorrectAuthUser(); // Применяем юзера у которого username=null
        UserEntity userEntity = createUserEntity(createAuthUser());
        XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
                NIFFLER_AUTH_URL,
                NIFFLER_USERDATA_URL
        );
        AuthUserDao authUserDao = new AuthUserDaoJdbc();
        UserdataUserDao userdataUserDao = new UserdataUserDaoJdbc();
        try {
            xaTransactionTemplate.execute(() -> {
                authUserDao.create(authUser);
                userdataUserDao.create(userEntity);
                return null;
            });
        } catch (RuntimeException e) {
            System.out.println("Транзакция получила ошибку и откатилась");
        }

        // Проверяем, что пользователь НЕ найден в niffler-auth
        Optional<AuthUserEntity> foundInAuth = authUserDao.findByUsername(authUser.getUsername());
        assertFalse(foundInAuth.isPresent());

        // Проверяем, что пользователь НЕ найден в niffler-userdata
        Optional<UserEntity> foundInUserdata = userdataUserDao.findByUsername(userEntity.getUsername());
        assertFalse(foundInUserdata.isPresent());
    }

}




