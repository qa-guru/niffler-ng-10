package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.config.Config;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TruncatedDatabasesExtension implements BeforeAllCallback, AfterEachCallback {

  private static final Config CFG = Config.getInstance();

  @Override
  public void beforeAll(ExtensionContext extensionContext) {
    truncateAll();
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) {
    truncateAll();
  }

  private static void truncateAll() {
    try (
        Connection authCon = DriverManager.getConnection(CFG.authJdbcUrl(), CFG.testDatabaseUsername(), CFG.testDatabasePassword());
        Connection userdataCon = DriverManager.getConnection(CFG.userdataJdbcUrl(), CFG.testDatabaseUsername(), CFG.testDatabasePassword());
        Connection spendCon = DriverManager.getConnection(CFG.spendJdbcUrl(), CFG.testDatabaseUsername(), CFG.testDatabasePassword())
    ) {
      ScriptUtils.executeSqlScript(
          authCon,
          new EncodedResource(
              new ClassPathResource("sql/truncate_auth.sql"),
              UTF_8
          )
      );
      ScriptUtils.executeSqlScript(
          userdataCon,
          new EncodedResource(
              new ClassPathResource("sql/truncate_userdata.sql"),
              UTF_8
          )
      );
      ScriptUtils.executeSqlScript(
          spendCon,
          new EncodedResource(
              new ClassPathResource("sql/truncate_spend.sql"),
              UTF_8
          )
      );
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
