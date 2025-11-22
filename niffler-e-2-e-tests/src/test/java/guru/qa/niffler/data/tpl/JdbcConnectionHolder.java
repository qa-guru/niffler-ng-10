package guru.qa.niffler.data.tpl;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
//Хранит в себе информацию о соединениях конекшенах
public class JdbcConnectionHolder implements AutoCloseable {

    // Объект, где храним привязка к нашему URLу т.е. к каждой БД может быть один объект и хранить коннекшен к каждому потоку
    // (если тесты в 3 потока идут к одной БД, то будет 3 коннекшена для каждого потока в одном объекте)
    // Создаем экземпляр объекта для каждой БД
    private final DataSource dataSource;
    // Long - id потока
    private final Map<Long, Connection> threadConnections = new ConcurrentHashMap<>();

    public JdbcConnectionHolder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection connection() {
        return threadConnections.computeIfAbsent(
                Thread.currentThread().threadId(),
                key -> {
                    try {
                        return dataSource.getConnection();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    //Метод для того, что бы закрывать коннекшен для своего потока и удалять его из своей Map
    @Override
    public void close() {
        Optional.ofNullable(threadConnections.remove(Thread.currentThread().threadId()))
                .ifPresent(connection -> {
                    try {
                        if (!connection.isClosed()) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        // NOP
                    }
                });
    }

    //На всякий случай пишем метод, что бы закрывать соединение, если оно осталось
    //Применяем его в экстеншене
    public void closeAllConnections() {
        threadConnections.values().forEach(
                connection -> {
                    try {
                        if (connection != null && !connection.isClosed()) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        // NOP
                    }
                }
        );
    }
}
