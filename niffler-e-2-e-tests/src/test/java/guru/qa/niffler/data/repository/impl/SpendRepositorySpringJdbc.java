package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.extractor.SpendEntityRowExtractor;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class SpendRepositorySpringJdbc implements SpendRepository {

  private final String SPEND_URL = Config.getInstance().spendJdbcUrl();

  @Nonnull
  @Override
  public SpendEntity create(SpendEntity spend) {
    final UUID categoryId = spend.getCategory().getId();
    if (categoryId == null) {
      spend.getCategory().setId(
          createCategory(spend.getCategory()).getId()
      );
    }
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_URL));
    final KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
          """
              INSERT INTO "spend" (username, spend_date, currency, amount, description, category_id)
              VALUES ( ?, ?, ?, ?, ?, ?)
              """,
          Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, spend.getUsername());
      ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());
      return ps;
    }, kh);

    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    spend.setId(generatedKey);
    return spend;
  }

  @Nonnull
  @Override
  public SpendEntity update(SpendEntity spend) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_URL));
    jdbcTemplate.update(
        """
              UPDATE "spend"
                SET spend_date  = ?,
                    currency    = ?,
                    amount      = ?,
                    description = ?,
                    category_id = ?
                WHERE id = ?
            """,
        new java.sql.Date(spend.getSpendDate().getTime()),
        spend.getCurrency().name(),
        spend.getAmount(),
        spend.getDescription(),
        spend.getId(),
        spend.getCategory().getId()
    );
    return spend;
  }

  @Nonnull
  @Override
  public CategoryEntity createCategory(CategoryEntity category) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_URL));
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
          """
                INSERT INTO "category" (username, name, archived)
                VALUES (?, ?, ?)
              """,
          Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, category.getUsername());
      ps.setString(2, category.getName());
      ps.setBoolean(3, category.isArchived());
      return ps;
    }, kh);

    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    category.setId(generatedKey);
    return category;
  }

  @Nonnull
  @Override
  public CategoryEntity updateCategory(CategoryEntity category) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_URL));
    jdbcTemplate.update(
        """
              UPDATE "category"
                SET name     = ?,
                    archived = ?
                WHERE id = ?
            """,
        category.getName(),
        category.isArchived(),
        category.getId()
    );
    return category;
  }

  @Nonnull
  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_URL));
    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(
              """
                    SELECT * FROM "category" WHERE id = ?
                  """,
              CategoryEntityRowMapper.instance,
              id
          )
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Nonnull
  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_URL));
    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(
              """
                    SELECT * FROM "category" WHERE username = ? and name = ?
                  """,
              CategoryEntityRowMapper.instance,
              username,
              name
          )
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Nonnull
  @Override
  public List<CategoryEntity> allCategories(String username) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_URL));
    return jdbcTemplate.query(
        """
              SELECT * FROM "category"
            """,
        CategoryEntityRowMapper.instance
    );
  }

  @Nonnull
  @Override
  public Optional<SpendEntity> findById(UUID id) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_URL));
    final List<SpendEntity> result = jdbcTemplate.query(
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
            where s.id = ? order by s.spend_date desc
            """,
        SpendEntityRowExtractor.instance,
        id
    );
    return result == null || result.isEmpty()
        ? Optional.empty()
        : Optional.of(result.getFirst());
  }

  @Nonnull
  @Override
  public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_URL));
    final List<SpendEntity> result = jdbcTemplate.query(
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
            where s.username = ? and s.description = ? order by s.spend_date desc
            """,
        SpendEntityRowExtractor.instance,
        username,
        description
    );
    return result == null || result.isEmpty()
        ? Optional.empty()
        : Optional.of(result.getFirst());
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

    final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_URL));
    final List<SpendEntity> result = jdbcTemplate.query(
        sql.toString(),
        SpendEntityRowExtractor.instance,
        Stream.of(username, currency, from, to).filter(Objects::nonNull).toArray()
    );
    return result != null
        ? result
        : Collections.emptyList();
  }

  @Override
  public void remove(SpendEntity spend) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_URL));
    jdbcTemplate.update("DELETE FROM spend WHERE id = ?", spend.getId());
  }

  @Override
  public void removeCategory(CategoryEntity category) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_URL));
    jdbcTemplate.update("DELETE FROM category WHERE id = ?", category.getId());
  }
}