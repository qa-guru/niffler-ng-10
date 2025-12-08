package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@ParametersAreNonnullByDefault
public class AuthUserRepositoryJdbc implements AuthUserRepository {

  private static final Config CFG = Config.getInstance();
  private static final String AUTH_URL = CFG.authJdbcUrl();

  @Nonnull
  @SuppressWarnings("resource")
  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    try (PreparedStatement userPs = holder(AUTH_URL).connection().prepareStatement(
        """
             INSERT INTO "user" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)
            VALUES (?, ?, ?, ?, ?, ?)
            """, PreparedStatement.RETURN_GENERATED_KEYS);
         PreparedStatement authorityPs = holder(AUTH_URL).connection().prepareStatement(
             """
                 INSERT INTO "authority" (user_id, authority) VALUES (?, ?)
                 """)) {
      userPs.setString(1, user.getUsername());
      userPs.setString(2, user.getPassword());
      userPs.setBoolean(3, user.getEnabled());
      userPs.setBoolean(4, user.getAccountNonExpired());
      userPs.setBoolean(5, user.getAccountNonLocked());
      userPs.setBoolean(6, user.getCredentialsNonExpired());

      userPs.executeUpdate();

      final UUID generatedKey = extractIdFromStatement(userPs);
      user.setId(generatedKey);

      final List<AuthorityEntity> authorities = user.getAuthorities();
      if (authorities != null && !authorities.isEmpty()) {
        for (AuthorityEntity a : user.getAuthorities()) {
          authorityPs.setObject(1, generatedKey);
          authorityPs.setString(2, a.getAuthority().name());
          authorityPs.addBatch();
          authorityPs.clearParameters();
        }
        authorityPs.executeBatch();
      }

      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @SuppressWarnings("resource")
  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(AUTH_URL).connection().prepareStatement(
        """
               SELECT a.id as authority_id,
               authority,
               user_id as id,
               u.username,
               u.password,
               u.enabled,
               u.account_non_expired,
               u.account_non_locked,
               u.credentials_non_expired
               FROM "user" u join authority a on u.id = a.user_id WHERE u.id = ?
            """
    )) {
      ps.setObject(1, id);
      ps.execute();
      return executeStatementAndMapResultSet(ps);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @SuppressWarnings("resource")
  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    try (PreparedStatement ps = holder(AUTH_URL).connection().prepareStatement(
        "select * from \"user\" u join authority a on u.id = a.user_id where u.username = ?"
    )) {
      ps.setString(1, username);
      ps.execute();
      return executeStatementAndMapResultSet(ps);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  private static Optional<AuthUserEntity> executeStatementAndMapResultSet(PreparedStatement ps) throws SQLException {
    try (ResultSet rs = ps.getResultSet()) {
      AuthUserEntity user = null;
      List<AuthorityEntity> authorityEntities = new ArrayList<>();
      while (rs.next()) {
        if (user == null) {
          user = new AuthUserEntity();
          user.setId(rs.getObject("id", UUID.class));
          user.setUsername(rs.getString("username"));
          user.setPassword(rs.getString("password"));
          user.setEnabled(rs.getBoolean("enabled"));
          user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
          user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
          user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
        }

        AuthorityEntity ae = new AuthorityEntity();
        ae.setUser(user);
        ae.setId(rs.getObject("authority_id", UUID.class));
        ae.setAuthority(Authority.valueOf(rs.getString("authority")));
        authorityEntities.add(ae);
      }
      if (user == null) {
        return Optional.empty();
      } else {
        user.setAuthorities(authorityEntities);
        return Optional.of(user);
      }
    }
  }

  @Nonnull
  private static UUID extractIdFromStatement(PreparedStatement ps) throws SQLException {
    final UUID generatedKey;
    try (ResultSet rs = ps.getGeneratedKeys()) {
      if (rs.next()) {
        generatedKey = rs.getObject("id", UUID.class);
      } else {
        throw new SQLException("Can`t find id in ResultSet");
      }
    }
    return generatedKey;
  }
}
