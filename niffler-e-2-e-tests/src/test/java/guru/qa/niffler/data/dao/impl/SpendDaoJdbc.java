package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SpendDaoJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance()

    @Override
    public SpendEntity create(SpendEntity spend) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO spend_(" +
                            " id," +
                            " username," +
                            " spend_date," +
                            " currency," +
                            " amount," +
                            " description," +
                            " category_id" +
                            " )" +
                            "VALUES(?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setObject(1, spend.getId());
                ps.setObject();
                ps.setObject();
                ps.setObject();
                ps.setObject();
                ps.setObject();
                ps.setObject();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
