package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

  @Override
  public SpendJson createSpend(SpendJson spend) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      final CategoryJson existingCategory = findCategoryByNameAndUsername(spend.category().name(), spend.username())
          .orElseGet(() -> createCategory(spend.category()));

      try (PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
              "VALUES (?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, spend.username());
        ps.setDate(2, new java.sql.Date(spend.spendDate().getTime()));
        ps.setString(3, spend.currency().name());
        ps.setDouble(4, spend.amount());
        ps.setString(5, spend.description());
        ps.setObject(6, existingCategory.id());

        ps.executeUpdate();

        final UUID generatedKey;
        try (ResultSet rs = ps.getGeneratedKeys()) {
          if (rs.next()) {
            generatedKey = rs.getObject("id", UUID.class);
          } else {
            throw new SQLException("Can't find id in ResultSet");
          }
        }

        return new SpendJson(
            generatedKey,
            spend.spendDate(),
            existingCategory,
            spend.currency(),
            spend.amount(),
            spend.description(),
            spend.username()
        );
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public CategoryJson createCategory(CategoryJson category) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO category (name, username, archived) VALUES (?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, category.name());
        ps.setString(2, category.username());
        ps.setBoolean(3, false);

        ps.executeUpdate();

        final UUID generatedKey;
        try (ResultSet rs = ps.getGeneratedKeys()) {
          if (rs.next()) {
            generatedKey = rs.getObject("id", UUID.class);
          } else {
            throw new SQLException("Can't find id in ResultSet");
          }
        }

        return new CategoryJson(
            generatedKey,
            category.name(),
            category.username(),
            false
        );
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT * FROM category WHERE username = ? AND name = ?")) {
        ps.setString(1, username);
        ps.setString(2, categoryName);

        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            return Optional.of(new CategoryJson(
                rs.getObject("id", UUID.class),
                rs.getString("name"),
                rs.getString("username"),
                rs.getBoolean("archived")
            ));
          } else {
            return Optional.empty();
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
