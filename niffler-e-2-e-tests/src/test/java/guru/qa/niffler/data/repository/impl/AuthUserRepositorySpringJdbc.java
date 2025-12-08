package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.extractor.AuthUserEntityExtractor;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.repository.AuthUserRepository;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

  private final String AUTH_URL = Config.getInstance().authJdbcUrl();

  @Nonnull
  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_URL));
    final KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
          """
              INSERT INTO "user" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)
              VALUES (?,?,?,?,?,?)
              """,
          Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getPassword());
      ps.setBoolean(3, user.getEnabled());
      ps.setBoolean(4, user.getAccountNonExpired());
      ps.setBoolean(5, user.getAccountNonLocked());
      ps.setBoolean(6, user.getCredentialsNonExpired());
      return ps;
    }, kh);

    final UUID generatedKey = (UUID) requireNonNull(kh.getKeys()).get("id");
    user.setId(generatedKey);

    final List<AuthorityEntity> authorities = user.getAuthorities();
    if (authorities != null && !authorities.isEmpty()) {
      jdbcTemplate.batchUpdate(
          """
              INSERT INTO authority (user_id, authority) VALUES (? , ?)
              """,
          new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
              ps.setObject(1, authorities.get(i).getUser().getId());
              ps.setString(2, authorities.get(i).getAuthority().name());
            }

            @Override
            public int getBatchSize() {
              return authorities.size();
            }
          }
      );
    }
    return user;
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_URL));
    final List<AuthUserEntity> result = jdbcTemplate.query(
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
            """,
        AuthUserEntityExtractor.instance,
        id
    );
    return result == null || result.isEmpty()
        ? Optional.empty()
        : Optional.of(result.getFirst());
  }

  @Nonnull
  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(AUTH_URL));
    final List<AuthUserEntity> result = jdbcTemplate.query(
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
               FROM "user" u join authority a on u.id = a.user_id WHERE u.username = ?
            """,
        AuthUserEntityExtractor.instance,
        username
    );
    return result == null || result.isEmpty()
        ? Optional.empty()
        : Optional.of(result.getFirst());
  }
}
