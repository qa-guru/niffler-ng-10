package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.AuthUserRepositorySpringJdbc;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface AuthUserRepository {

  @Nonnull
  static AuthUserRepository getInstance() {
    return switch (System.getProperty("repository", "jpa")) {
      case "jpa" -> new AuthUserRepositoryHibernate();
      case "jdbc" -> new AuthUserRepositoryJdbc();
      case "sjdbc" -> new AuthUserRepositorySpringJdbc();
      default -> throw new IllegalArgumentException("Unknown repository type: " + System.getProperty("repository"));
    };
  }

  @Nonnull
  AuthUserEntity create(AuthUserEntity user);

  @Nonnull
  Optional<AuthUserEntity> findById(UUID id);

  @Nonnull
  Optional<AuthUserEntity> findByUsername(String username);
}
