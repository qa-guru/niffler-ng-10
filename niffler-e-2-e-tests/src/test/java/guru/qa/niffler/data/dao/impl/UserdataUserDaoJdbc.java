package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

public class UserdataUserDaoJdbc implements UserdataUserDao {

  private static final Config CFG = Config.getInstance();
  private static final String URL = CFG.userdataJdbcUrl();

  @Override
  public UserEntity create(UserEntity user) {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement(
        "INSERT INTO \"user\" (username, currency) VALUES (?, ?)",
        PreparedStatement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getCurrency().name());
      ps.executeUpdate();
      final UUID generatedUserId;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedUserId = rs.getObject("id", UUID.class);
        } else {
          throw new IllegalStateException("Can`t find id in ResultSet");
        }
      }
      user.setId(generatedUserId);
      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<UserEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement("SELECT * FROM \"user\" WHERE id = ? ")) {
      ps.setObject(1, id);

      ps.execute();
      ResultSet rs = ps.getResultSet();

      if (rs.next()) {
        return Optional.of(
            UserdataUserEntityRowMapper.instance.mapRow(rs, rs.getRow())
        );
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement("SELECT * FROM \"user\" WHERE username = ? ")) {
      ps.setString(1, username);

      ps.execute();
      ResultSet rs = ps.getResultSet();

      if (rs.next()) {
        return Optional.of(
            UserdataUserEntityRowMapper.instance.mapRow(rs, rs.getRow())
        );
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<UserEntity> findAll() {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement(
        "SELECT * FROM spend")) {
      ps.execute();
      List<UserEntity> result = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          UserEntity ue = new UserEntity();
          ue.setId(rs.getObject("id", UUID.class));
          ue.setUsername(rs.getString("username"));
          ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          ue.setFirstname(rs.getString("firstname"));
          ue.setSurname(rs.getString("surname"));
          ue.setFullname(rs.getString("full_name"));
          ue.setPhoto(rs.getBytes("photo"));
          ue.setPhotoSmall(rs.getBytes("photo_small"));
          result.add(ue);
        }
      }
      return result;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public UserEntity update(UserEntity user) {
    try (PreparedStatement usersPs = holder(URL).connection().prepareStatement(
        """
              UPDATE "user"
                SET currency    = ?,
                    firstname   = ?,
                    surname     = ?,
                    photo       = ?,
                    photo_small = ?
                WHERE id = ?
            """);

         PreparedStatement friendsPs = holder(URL).connection().prepareStatement(
             """
                 INSERT INTO friendship (requester_id, addressee_id, status)
                 VALUES (?, ?, ?)
                 ON CONFLICT (requester_id, addressee_id)
                     DO UPDATE SET status = ?
                 """)
    ) {
      usersPs.setString(1, user.getCurrency().name());
      usersPs.setString(2, user.getFirstname());
      usersPs.setString(3, user.getSurname());
      usersPs.setBytes(4, user.getPhoto());
      usersPs.setBytes(5, user.getPhotoSmall());
      usersPs.setObject(6, user.getId());
      usersPs.executeUpdate();

      for (FriendshipEntity fe : user.getFriendshipRequests()) {
        friendsPs.setObject(1, user.getId());
        friendsPs.setObject(2, fe.getAddressee().getId());
        friendsPs.setString(3, fe.getStatus().name());
        friendsPs.setString(4, fe.getStatus().name());
        friendsPs.addBatch();
        friendsPs.clearParameters();
      }
      friendsPs.executeBatch();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return user;
  }
}
