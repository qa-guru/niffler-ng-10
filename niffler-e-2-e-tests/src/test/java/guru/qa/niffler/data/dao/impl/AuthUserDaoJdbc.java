package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserDaoJdbc implements AuthUserDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserEntity create(AuthUserEntity authUserEntity) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES ( ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, authUserEntity.getUsername());
            ps.setString(2, authUserEntity.getPassword());
            ps.setBoolean(3, authUserEntity.getEnabled());
            ps.setBoolean(4, authUserEntity.getAccountNonExpired());
            ps.setBoolean(5, authUserEntity.getAccountNonExpired());
            ps.setBoolean(6, authUserEntity.getCredentialsNonExpired());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            authUserEntity.setId(generatedKey);
            return authUserEntity;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthUserEntity ae = new AuthUserEntity();
                    ae.setId(rs.getObject("id", UUID.class));
                    ae.setUsername(rs.getString("username"));
                    ae.setPassword(rs.getString("password"));
                    ae.setEnabled(rs.getBoolean("enabled"));
                    ae.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    ae.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    ae.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    return Optional.of(ae);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" ")) {
            ps.execute();
            List<AuthUserEntity> authUserAuthorities = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthUserEntity ae = new AuthUserEntity();
                    ae.setId(rs.getObject("id", UUID.class));
                    ae.setUsername(rs.getString("username"));
                    ae.setPassword(rs.getString("password"));
                    ae.setEnabled(rs.getBoolean("enabled"));
                    ae.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    ae.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    ae.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    authUserAuthorities.add(ae);
                }
                return authUserAuthorities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
