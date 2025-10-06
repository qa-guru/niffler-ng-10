package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import org.springframework.data.web.PagedModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

  @Override
  public SpendJson createSpend(SpendJson spend) {
    try {
      final JdbcTemplate jdbcTemplate = new JdbcTemplate(
          new SingleConnectionDataSource(
              DriverManager.getConnection(
                  CFG.spendJdbcUrl(),
                  "postgres",
                  "secret"
              ),
              true
          )
      );

      final KeyHolder kh = new GeneratedKeyHolder();
      final CategoryJson existingCategory = findCategoryByNameAndUsername(spend.category().name(), spend.username())
          .orElseGet(() -> createCategory(spend.category()));

      jdbcTemplate.update(conn -> {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO \"spend\" (username, spend_date, currency, amount, description, category_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
        );
        ps.setString(1, spend.username());
        ps.setDate(2, new java.sql.Date(spend.spendDate().getTime()));
        ps.setString(3, spend.currency().name());
        ps.setDouble(4, spend.amount());
        ps.setString(5, spend.description());
        ps.setObject(6, existingCategory.id());
        return ps;
      }, kh);

      return new SpendJson(
          (UUID) kh.getKeys().get("id"),
          spend.spendDate(),
          existingCategory,
          spend.currency(),
          spend.amount(),
          spend.description(),
          spend.username()
      );
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public CategoryJson createCategory(CategoryJson category) {
    try {
      final JdbcTemplate jdbcTemplate = new JdbcTemplate(
          new SingleConnectionDataSource(
              DriverManager.getConnection(
                  CFG.spendJdbcUrl(),
                  "postgres",
                  "secret"
              ),
              true
          )
      );
      final KeyHolder kh = new GeneratedKeyHolder();
      jdbcTemplate.update(conn -> {
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO \"category\" (name, username, archived) " +
                "VALUES (?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
        );
        ps.setString(1, category.name());
        ps.setString(2, category.username());
        ps.setBoolean(3, false);
        return ps;
      }, kh);
      return new CategoryJson(
          (UUID) kh.getKeys().get("id"),
          category.name(),
          category.username(),
          false
      );
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username) {
    try {
      final JdbcTemplate jdbcTemplate = new JdbcTemplate(
          new SingleConnectionDataSource(
              DriverManager.getConnection(
                  CFG.spendJdbcUrl(),
                  "postgres",
                  "secret"
              ),
              true
          )
      );
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(
              "SELECT * FROM \"category\" WHERE username = ? and name = ?",
              (rs, rowNum) -> new CategoryJson(
                  rs.getObject("id", UUID.class),
                  rs.getString("name"),
                  rs.getString("username"),
                  rs.getBoolean("archived")
              ),
              username,
              categoryName
          )
      );
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public SpendJson getUserById(String id, String userName) {
    return null;
  }

  @Override
  public List<SpendJson> getSpends(String username, CurrencyValues filterCurrency, Date from, Date to) {
    return List.of();
  }

  @Override
  public SpendJson editSpend(SpendJson spend) {
    return null;
  }

  @Override
  public Void deleteSpends(String username, List<String> ids) {
    return null;
  }

  @Override
  public PagedModel<SpendJson> getspends(String username, CurrencyValues filterCurrency, Date from, Date to, String searchQuery, Integer page, Integer size, List<String> sort) {
    return null;
  }

  @Override
  public List<CategoryJson> getCategories(String username, boolean excludeArchived) {
    return List.of();
  }

  @Override
  public CategoryJson addCategory(CategoryJson category) {
    return null;
  }

  @Override
  public CategoryJson updateCategory(CategoryJson categoryJson) {
    return null;
  }
}
