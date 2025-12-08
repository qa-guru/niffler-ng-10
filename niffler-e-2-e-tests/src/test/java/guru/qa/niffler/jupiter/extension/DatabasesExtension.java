package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.data.jdbc.Connections;
import guru.qa.niffler.data.jpa.EntityManagers;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DatabasesExtension implements SuiteExtension {
  @Override
  public void afterSuite() {
    Connections.closeAllConnections();
    EntityManagers.closeAllEmfs();
  }
}
