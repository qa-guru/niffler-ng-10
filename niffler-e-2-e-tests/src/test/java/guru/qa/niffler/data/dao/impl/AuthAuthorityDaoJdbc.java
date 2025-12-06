package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public void create(AuthAuthorityEntity... authAuthorityEntity) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)")) {
            for (AuthAuthorityEntity authAuthority : authAuthorityEntity) {
                ps.setObject(1, authAuthority.getUser().getId());
                ps.setString(2, authAuthority.getAuthority().name());
                ps.addBatch();
                ps.clearParameters();
            }
            ps.executeBatch();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<AuthAuthorityEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"authority\" ")) {
            ps.execute();
            List<AuthAuthorityEntity> authAuthorities = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthAuthorityEntity ae = new AuthAuthorityEntity();
                    ae.setId(rs.getObject("id", UUID.class));
                    ae.setUser(rs.getObject("user_id", AuthUserEntity.class));
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    authAuthorities.add(ae);
                }
                return authAuthorities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


