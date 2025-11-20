package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.spend.UserEntity;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.stream.Stream;

import static guru.qa.niffler.data.Databases.dataSource;
import static guru.qa.niffler.data.Databases.xaTransaction;

public class UserDbClient implements UserClient {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();


    public UserJson createUserSpringJdbc(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = new AuthUserDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .create(authUser);

        AuthAuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthAuthorityEntity ae = new AuthAuthorityEntity();
                    ae.setUserId(createdAuthUser.getId());
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthAuthorityEntity[]::new);

        new AuthAuthorityDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .create(authorityEntities);

        return UserJson.fromEntity(
                new UserdataUserDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl()))
                        .create(
                                UserEntity.fromJson(user)
                        )
        );
    }


    @Override
    public UserJson create(UserJson userJson) {
        return UserJson.fromEntity(
                xaTransaction(   // 1-ая транзакция к базе данных niffler-auth. В нем я делаю вставку в таблицу user вторым
                        // шагом в таблицу authority для каждого значения по enum Authority
                        new Databases.XaFunction<>(
                                connection -> {
                                    AuthUserEntity authUserEntity = new AuthUserEntity();
                                    authUserEntity.setUsername(userJson.username());
                                    authUserEntity.setPassword(pe.encode("12345"));
                                    authUserEntity.setEnabled(true);
                                    authUserEntity.setAccountNonExpired(true);
                                    authUserEntity.setAccountNonLocked(true);
                                    authUserEntity.setCredentialsNonExpired(true);
                                    new AuthUserDaoJdbc(connection).create(authUserEntity);
                                    //Поскольку надо вставить две строки в таблицу authority для одного user_id,
                                    // то мы используем stream. Создаем stream из значений enum Authority что бы обработать каждое значение
                                    Stream<Authority> authorityStream = Arrays.stream(Authority.values());
                                    //Stream из AuthAuthorityEntity с каждым значением Authority
                                    Stream<AuthAuthorityEntity> entityStream = authorityStream.map(a -> {
                                        AuthAuthorityEntity authAuthEntity = new AuthAuthorityEntity();
                                        authAuthEntity.setUserId(authUserEntity.getId());
                                        authAuthEntity.setAuthority(a);
                                        return authAuthEntity;
                                    });
                                    //Когда заполнили entityStream несколькими значениями AuthAuthorityEntity, преобразуем его в массив, что бы принял метод
                                    AuthAuthorityEntity[] entities = entityStream.toArray(AuthAuthorityEntity[]::new);
                                    //Заполняем
                                    new AuthAuthorityDaoJdbc(connection).create(entities);
                                    return null;
                                }, CFG.authJdbcUrl()
                        ),
                        // 2-ая транзакция к базе данных niffler-userdata. В нем я делаю вставку в таблицу user c данными из UserJson
                        // и возвращаю объект userEntity, что бы потом преобразовать его в возвращаемый UserJson
                        new Databases.XaFunction<>(
                                connection -> {
                                    UserEntity userEntity = UserEntity.fromJson(userJson);
                                    new UserdataUserDaoJdbc(connection).create(userEntity);
                                    return userEntity;
                                }
                                , CFG.userdataJdbcUrl()
                        ))
        );
    }
}