package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositorySpringJdbc;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserdataUserRepository {

  @Nonnull
  static UserdataUserRepository getInstance() {
    return switch (System.getProperty("repository", "jpa")) {
      case "jpa" -> new UserdataUserRepositoryHibernate();
      case "jdbc" -> new UserdataUserRepositoryJdbc();
      case "sjdbc" -> new UserdataUserRepositorySpringJdbc();
      default -> throw new IllegalArgumentException("Unknown repository type: " + System.getProperty("repository"));
    };
  }

  @Nonnull
  UserEntity create(UserEntity user);

  @Nonnull
  UserEntity update(UserEntity user);

  @Nonnull
  Optional<UserEntity> findById(UUID id);

  @Nonnull
  Optional<UserEntity> findByUsername(String username);

  void addFriendshipRequest(UserEntity requester, UserEntity addressee);

  void addFriend(UserEntity requester, UserEntity addressee);
}
