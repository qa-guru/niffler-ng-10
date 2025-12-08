package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.model.CurrencyValues;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@ParametersAreNonnullByDefault
public class SpendRepositoryJdbc implements SpendRepository {

  private final String SPEND_URL = Config.getInstance().spendJdbcUrl();

  @Nonnull
  @SuppressWarnings("resource")
  @Override
  public SpendEntity create(SpendEntity spend) {
    final UUID categoryId = spend.getCategory().getId();
    if (categoryId == null) {
      spend.getCategory().setId(
          createCategory(spend.getCategory()).getId()
      );
    }
    try (PreparedStatement ps = holder(SPEND_URL).connection().prepareStatement(
        """
              INSERT INTO "spend" (username, spend_date, currency, amount, description, category_id)
              VALUES ( ?, ?, ?, ?, ?, ?)
            """,
        Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, spend.getUsername());
      ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());

      ps.executeUpdate();

      final UUID generatedKey = extractIdFromStatement(ps);
      spend.setId(generatedKey);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return spend;
  }

  @Nonnull
  @SuppressWarnings("resource")
  @Override
  public SpendEntity update(SpendEntity spend) {
    try (PreparedStatement ps = holder(SPEND_URL).connection().prepareStatement(
        """
              UPDATE "spend"
                SET spend_date  = ?,
                    currency    = ?,
                    amount      = ?,
                    description = ?,
                    category_id = ?
                WHERE id = ?
            """);
    ) {
      ps.setDate(1, new java.sql.Date(spend.getSpendDate().getTime()));
      ps.setString(2, spend.getCurrency().name());
      ps.setDouble(3, spend.getAmount());
      ps.setString(4, spend.getDescription());
      ps.setObject(5, spend.getCategory().getId());
      ps.setObject(6, spend.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return spend;
  }

  @Nonnull
  @Override
  public Optional<SpendEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(SPEND_URL).connection().prepareStatement(
        """
               select
               c.id as category_id,
               c.name as category_name,
               c.archived as category_archived,
               s.id,
               s.username,
               s.spend_date,
               s.currency,
               s.amount,
               s.description
               from spend s join category c on s.category_id = c.id
               where s.id = ? order by s.spend_date desc
            """
    )) {
      ps.setObject(1, id);
      ps.execute();
      final List<SpendEntity> spendEntities = mapStatementToSpends(ps);
      return spendEntities.isEmpty()
          ? Optional.empty()
          : Optional.of(spendEntities.getFirst());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @SuppressWarnings("resource")
  @Override
  public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
    try (PreparedStatement ps = holder(SPEND_URL).connection().prepareStatement(
        """
               select
               c.id as category_id,
               c.name as category_name,
               c.archived as category_archived,
               s.id,
               s.username,
               s.spend_date,
               s.currency,
               s.amount,
               s.description
               from spend s join category c on s.category_id = c.id
               where s.username = ? and s.description = ? order by s.spend_date desc
            """
    )) {
      ps.setString(1, username);
      ps.setString(2, description);
      ps.execute();
      final List<SpendEntity> spendEntities = mapStatementToSpends(ps);
      return spendEntities.isEmpty()
          ? Optional.empty()
          : Optional.of(spendEntities.getFirst());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public List<SpendEntity> all(String username, @Nullable CurrencyValues currency, @Nullable Date from, @Nullable Date to) {
    final StringBuilder sql = new StringBuilder(
        """
            select
              c.id as category_id,
              c.name as category_name,
              c.archived as category_archived,
              s.id,
              s.username,
              s.spend_date,
              s.currency,
              s.amount,
              s.description
            from spend s
            join category c on s.category_id = c.id
            where s.username = ?
            """
    );
    if (currency != null) {
      sql.append(" and s.currency = ? ");
    }
    if (from != null) {
      sql.append(" and s.spend_date >= ? ");
    }
    if (to != null) {
      sql.append(" and s.spend_date <= ? ");
    }
    sql.append(" order by s.spend_date desc ");

    try (PreparedStatement ps = holder(SPEND_URL).connection().prepareStatement(sql.toString())) {
      ps.setString(1, username);
      if (currency != null) {
        ps.setString(2, currency.name());
      }
      if (from != null) {
        ps.setDate(3, new java.sql.Date(from.getTime()));
      }
      if (to != null) {
        ps.setDate(4, new java.sql.Date(to.getTime()));
      }
      ps.execute();
      return mapStatementToSpends(ps);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  @SuppressWarnings("resource")
  public void remove(SpendEntity spend) {
    try (PreparedStatement ps = holder(SPEND_URL).connection().prepareStatement(
        "DELETE FROM spend WHERE id = ?"
    )) {
      ps.setObject(1, spend.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public CategoryEntity createCategory(CategoryEntity category) {
    try (PreparedStatement ps = holder(SPEND_URL).connection().prepareStatement(
        "INSERT INTO category (username, name, archived) " +
            "VALUES (?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, category.getUsername());
      ps.setString(2, category.getName());
      ps.setBoolean(3, category.isArchived());

      ps.executeUpdate();

      final UUID generatedKey = extractIdFromStatement(ps);
      category.setId(generatedKey);
      return category;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public CategoryEntity updateCategory(CategoryEntity category) {
    try (PreparedStatement ps = holder(SPEND_URL).connection().prepareStatement(
        """
              UPDATE "category"
                SET name     = ?,
                    archived = ?
                WHERE id = ?
            """);
    ) {
      ps.setString(1, category.getName());
      ps.setBoolean(2, category.isArchived());
      ps.setObject(3, category.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return category;
  }

  @Nonnull
  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    try (PreparedStatement ps = holder(SPEND_URL).connection().prepareStatement(
        "SELECT * FROM category WHERE id = ?"
    )) {
      ps.setObject(1, id);
      ps.execute();
      final List<CategoryEntity> category = mapStatementToCategory(ps);
      return category.isEmpty()
          ? Optional.empty()
          : Optional.of(category.getFirst());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @SuppressWarnings("resource")
  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
    try (PreparedStatement ps = holder(SPEND_URL).connection().prepareStatement(
        "SELECT * FROM category WHERE username = ? and name = ?"
    )) {
      ps.setString(1, username);
      ps.setString(2, name);
      ps.execute();
      final List<CategoryEntity> category = mapStatementToCategory(ps);
      return category.isEmpty()
          ? Optional.empty()
          : Optional.of(category.getFirst());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public List<CategoryEntity> allCategories(String username) {
    try (PreparedStatement ps = holder(SPEND_URL).connection().prepareStatement(
        "SELECT * FROM category")) {
      ps.execute();
      return mapStatementToCategory(ps);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  @SuppressWarnings("resource")
  public void removeCategory(CategoryEntity category) {
    try (PreparedStatement ps = holder(SPEND_URL).connection().prepareStatement(
        "DELETE FROM category WHERE id = ?"
    )) {
      ps.setObject(1, category.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
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

  @Nonnull
  private static List<SpendEntity> mapStatementToSpends(PreparedStatement ps) throws SQLException {
    List<SpendEntity> result = new ArrayList<>();
    try (ResultSet rs = ps.getResultSet()) {
      while (rs.next()) {
        SpendEntity spend = new SpendEntity();
        spend.setId(rs.getObject("id", UUID.class));
        spend.setUsername(rs.getString("username"));
        spend.setSpendDate(rs.getDate("spend_date"));
        spend.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
        spend.setAmount(rs.getDouble("amount"));
        spend.setDescription(rs.getString("description"));

        CategoryEntity category = new CategoryEntity();
        category.setId(rs.getObject("category_id", UUID.class));
        category.setUsername(rs.getString("username"));
        category.setName(rs.getString("category_name"));
        category.setArchived(rs.getBoolean("category_archived"));

        spend.setCategory(category);
        result.add(spend);
      }
    }
    return result;
  }

  @Nonnull
  private static List<CategoryEntity> mapStatementToCategory(PreparedStatement ps) throws SQLException {
    List<CategoryEntity> result = new ArrayList<>();
    try (ResultSet rs = ps.getResultSet()) {
      while (rs.next()) {
        CategoryEntity ce = new CategoryEntity();
        ce.setId(rs.getObject("id", UUID.class));
        ce.setUsername(rs.getString("username"));
        ce.setName(rs.getString("name"));
        ce.setArchived(rs.getBoolean("archived"));
        result.add(ce);
      }
    }
    return result;
  }
}