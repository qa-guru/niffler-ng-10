package guru.qa.niffler.data;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

//Класс для того, что бы создать соединение к БД
public class Databases {
    //Здесь хранится коннекшен к каждой БД (niffler-auth/niffler-currency/niffler-spend/niffler-userdata)
    // для атомарного доступа ко ключу. Коннекшены вычисляем методом dataSource
    private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
    //Long - id потока, и к этому потоку привязваем Map коннектов, которыми оперирует ntcn
    private static final Map<Long, Map<String, Connection>> threadConnections = new ConcurrentHashMap<>();

    private Databases() {  //Приватный конструктор как у DriverManager, что бы не создавали каждый раз новый, а как синглтон
    }

    // Создаем, что бы складывать в массив function со своим jdbcUrl, и потом его слать в ХА транзакцию
    public record XaFunction<T>(Function<Connection, T> function, String jdbcUrl) {
    }

    public record XaConsumer(Consumer<Connection> function, String jdbcUrl) {
    }

    //Перезагруженный метод, что бы передавать в метод по умолчанию уровень изолированности транзакций
    public static <T> T transaction(Function<Connection, T> function, String jdbcUrl) {
        return transaction(function, jdbcUrl, Connection.TRANSACTION_READ_COMMITTED);
    }

    //Метод возвращающий значение. Мы получаем, а затем передаем коннекшен в функцию,
    // а функция вызывает внутри себя нужные DAO. Работает с одним соединением
    //отличается применение стандартных интерфейсов - здесь Function.
    public static <T> T transaction(Function<Connection, T> function, String jdbcUrl, int isolationLevel) {
        Connection connection = null;
        try {
            connection = connection(jdbcUrl);// Получили коннекшен по jdbcUrl
            connection.setTransactionIsolation(isolationLevel);
            connection.setAutoCommit(false);  // Для того, что бы вручную управлять транзакцией
            T result = function.apply(connection); // Переданная функция (например, лямбда) выполняется с использованием соединения.
            connection.commit();               // Если все хорошо, то комитим транзакцию.
            connection.setAutoCommit(true);   // Для того, что бы вручную управлять транзакцией - здесь возвращаем в исходное состояние
            return result;
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        }
    }

    // На вход мы передаем массив из Function<Connection, T> function, String jdbcUrl.
    //Т.е для каждой БД по ее jdbcUrl мы выполняем function. А так как у нас их массив, то мы
    // для каждой БД выполняем свою лямбду(function).
    public static <T> T xaTransaction(XaFunction<T>... actions) {
        UserTransaction ut = new UserTransactionImp();  // это и есть распределенная транзакция
        try {
            ut.begin();
            T result = null;
            for (XaFunction<T> action : actions) {
                result = action.function.apply(connection(action.jdbcUrl));
            }
            ut.commit();
            return result;
        } catch (Exception e) {
            try {
                ut.rollback();
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    //Перезагруженный метод, что бы передавать в метод по умолчанию уровень изолированности транзакций
    public static void transaction(Consumer<Connection> consumer, String jdbcUrl) {
        transaction(consumer, jdbcUrl, Connection.TRANSACTION_READ_COMMITTED);
    }

    // Метод НЕ возвращающий значение. Мы получаем, а затем передаем коннекшен в функцию,
    // а функция вызывает внутри себя нужные DAO. Работает с одним соединением
    // отличается применением стандартных интерфейсов - здесь Consumer.
    public static void transaction(Consumer<Connection> consumer, String jdbcUrl, int isolationLevel) {
        Connection connection = null;
        try {
            connection = connection(jdbcUrl);
            connection.setTransactionIsolation(isolationLevel);
            connection.setAutoCommit(false);
            consumer.accept(connection);
            connection.commit();
            connection.setAutoCommit(true);
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        }
    }

    // На вход мы передаем массив из Function<Connection, T> function, String jdbcUrl.
    //Т.е для каждой БД по ее jdbcUrl мы выполняем function. А так как у нас их массив, то мы
    // для каждой БД выполняем свою лямбду(function).
    public static void xaTransaction(XaConsumer... actions) {
        UserTransaction ut = new UserTransactionImp();
        try {
            ut.begin(); // После этой строки начинаем действия в разных БД выполнять
            for (XaConsumer action : actions) {
                action.function.accept(connection(action.jdbcUrl));
            }
            ut.commit();// Это строка говорит, что все действия завершены успешно и мы можем закрыть соединения
        } catch (Exception e) {
            try {
                ut.rollback();
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    //Готовит пул соединений к БД
    public static DataSource dataSource(String jdbcUrl) {
        return dataSources.computeIfAbsent(  //computeIfAbsent - вернет значение по ключу если оно есть или вычислить значение из лямбды
                jdbcUrl,
                key -> {
                    AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
                    final String uniqId = StringUtils.substringAfter(jdbcUrl, "5432/");
                    dsBean.setUniqueResourceName(uniqId);
                    dsBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
                    Properties props = new Properties();
                    dsBean.setDefaultIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
                    props.put("URL", jdbcUrl);
                    props.put("user", "postgres");
                    props.put("password", "secret");
                    dsBean.setXaProperties(props);
                    dsBean.setMaxPoolSize(10);
                    return dsBean;
                }
        );
    }

    //Этот метод возвращает пул коннекшенов
    private static Connection connection(String jdbcUrl) throws SQLException {
        return threadConnections.computeIfAbsent(
                Thread.currentThread().threadId(),
                key -> {                    //Здесь мы достаем Мар по key привязанному к текущему потоку
                    try {                         //Либо ее туда кладет, если она отсутствует
                        return new HashMap<>(Map.of(
                                jdbcUrl,
                                dataSource(jdbcUrl).getConnection()
                        ));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        ).computeIfAbsent(                          //Но когда достали Мар мы ищем по jdbc нужный коннекшен
                jdbcUrl,                            //Либо вычисляем и кладем его туда
                key -> {
                    try {
                        return dataSource(jdbcUrl).getConnection();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    //Метод, который закрывает все коннекты к БД, в конце теста. Реализуем через расширение DatabasesExtension
    public static void closeAllConnections() {
        for (Map<String, Connection> connectionMap : threadConnections.values()) {
            for (Connection connection : connectionMap.values()) {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    // NOP
                }
            }
        }
    }
}
