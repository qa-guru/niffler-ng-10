package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class CategoryDaoJdbc implements CategoryDao {

    private static final Config CFG = Config.getInstance();
    @Override
    public CategoryEntity create(CategoryEntity category) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"category\" (username, name, archived) " +
                        "VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, category.getUsername());
            ps.setString(2, category.getName());
            ps.setBoolean(3, category.isArchived());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            category.setId(generatedKey);
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"category\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute(); // Делаем execute тк делаем SELECT a не INSERT
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    CategoryEntity ce = new CategoryEntity();
                    ce.setId(rs.getObject("id", UUID.class));
                    ce.setUsername(rs.getString("username"));
                    ce.setName(rs.getString("name"));
                    ce.setArchived(rs.getBoolean("archived"));
                    return Optional.of(ce);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findByUsernameAndCategoryName(String username, String categoryName) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"category\" WHERE username = ? AND name = ?"
        )) {
            ps.setString(1, username);
            ps.setString(2, categoryName);
            ps.execute(); // Делаем execute тк делаем SELECT a не INSERT
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    CategoryEntity ce = new CategoryEntity();
                    ce.setId(rs.getObject("id", UUID.class));
                    ce.setUsername(rs.getString("username"));
                    ce.setName(rs.getString("name"));
                    ce.setArchived(rs.getBoolean("archived"));
                    return Optional.of(ce);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"category\" WHERE username = ? "
        )) {
            ps.setString(1, username);
            ps.execute();
            List<CategoryEntity> categoriesList = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    CategoryEntity ce = new CategoryEntity();
                    ce.setId(rs.getObject("id", UUID.class));
                    ce.setUsername(rs.getString("username"));
                    ce.setName(rs.getString("name"));
                    ce.setArchived(rs.getBoolean("archived"));
                    categoriesList.add(ce);
                }
                return categoriesList;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(CategoryEntity category) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"category\" WHERE id = ? "
        )) {
            ps.setObject(1, category.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryEntity update(CategoryEntity category) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "UPDATE \"category\" SET name = ?, archived = ? " +
                        "WHERE id = ?"
        )) {
            ps.setString(1, category.getName());
            ps.setBoolean(2, category.isArchived());
            ps.setObject(3, category.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return category;
    }

    @Override
    public List<CategoryEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"category\" ")) {
            ps.execute();
            List<CategoryEntity> categoriesList = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    CategoryEntity ce = new CategoryEntity();
                    ce.setId(rs.getObject("id", UUID.class));
                    ce.setUsername(rs.getString("username"));
                    ce.setName(rs.getString("name"));
                    ce.setArchived(rs.getBoolean("archived"));
                    categoriesList.add(ce);
                }
                return categoriesList;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


