package guru.qa.niffler.data;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//Класс для того, что бы создать соединение к БД
public class Databases {
  private Databases() {  //Приватный конструктор как у DriverManager, что бы не создавали каждый раз новый, а как синглтон
  }
    //Здесь хранится коннекшен к каждой БД (niffler-auth/niffler-currency/niffler-spend/niffler-userdata)
    // для атомарного доступа ко ключу. Коннекшены вычисляем методом dataSource
  private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

  private static DataSource dataSource(String jdbcUrl) {
    return dataSources.computeIfAbsent(  //computeIfAbsent - вернет значение по ключу если оно есть или вычислить значение из лямбды
        jdbcUrl,
        key -> {
          PGSimpleDataSource ds = new PGSimpleDataSource();
          ds.setUser("postgres");
          ds.setPassword("secret");
          ds.setUrl(key);
          return ds;
        }
    );
  }

  public static Connection connection(String jdbcUrl) throws SQLException {
    return dataSource(jdbcUrl).getConnection();
  }
}
