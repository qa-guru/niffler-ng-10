package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.UserDataDAO;
import guru.qa.niffler.data.entity.spend.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class UserDataDAOJdbc implements UserDataDAO {
    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity createUser(UserEntity user) {
        try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO user (username, firstname," +
                            " surname, fullname, " +
                            " currency, photo, " +
                            " photoSmall) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getFirstname());
                ps.setString(3, user.getSurname());
                ps.setString(4, user.getFullname());
                ps.setString(5, user.getCurrency().name());
                ps.setBytes(6, user.getPhoto());
                ps.setBytes(7, user.getPhotoSmall());

                ps.executeUpdate();

                final UUID generatedKey;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKey = rs.getObject("id", UUID.class);
                    } else {
                        throw new SQLException("Can`t find id in ResultSet");
                    }
                }
                user.setId(generatedKey);
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM user WHERE id = ?"
            )) {
                ps.setObject(1, id);
                ps.execute(); // Делаем execute тк делаем SELECT a не INSERT
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        UserEntity ce = new UserEntity();
                        ce.setId(rs.getObject("id", UUID.class));
                        ce.setUsername(rs.getString("username"));
                        ce.setFirstname(rs.getString("firstname"));
                        ce.setFullname(rs.getString("full_name"));
                        ce.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                        ce.setPhoto(rs.getBytes("photo"));
                        ce.setPhotoSmall(rs.getBytes("photo_small"));
                        return Optional.of(ce);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM user WHERE username = ?"
            )) {
                ps.setObject(1, username);
                ps.execute(); // Делаем execute тк делаем SELECT a не INSERT
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        UserEntity ce = new UserEntity();
                        ce.setId(rs.getObject("id", UUID.class));
                        ce.setUsername(rs.getString("username"));
                        ce.setFirstname(rs.getString("firstname"));
                        ce.setFullname(rs.getString("full_name"));
                        ce.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                        ce.setPhoto(rs.getBytes("photo"));
                        ce.setPhotoSmall(rs.getBytes("photo_small"));
                        return Optional.of(ce);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UserEntity user) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM user WHERE id = ? "
            )) {
                ps.setObject(1, user.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
