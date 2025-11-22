package guru.qa.niffler.data.tpl;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DataSources {
  private DataSources() {
  }
    //Здесь хранится коннекшен к каждой БД (niffler-auth/niffler-currency/niffler-spend/niffler-userdata)
    // для атомарного доступа ко ключу. Коннекшены вычисляем методом dataSource
  private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

  //Готовит пул соединений к БД
  public static DataSource dataSource(String jdbcUrl) {
    return dataSources.computeIfAbsent(
        jdbcUrl,
        key -> {
          AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
          final String uniqId = StringUtils.substringAfter(jdbcUrl, "5432/");
          dsBean.setUniqueResourceName(uniqId);
          dsBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
          Properties props = new Properties();
          props.put("URL", jdbcUrl);
          props.put("user", "postgres");
          props.put("password", "secret");
          dsBean.setXaProperties(props);
          dsBean.setPoolSize(3);  // ставим 3 потому что запускаем тесты в 3 потока
          dsBean.setMaxPoolSize(10);
          return dsBean;
        }
    );
  }
}
