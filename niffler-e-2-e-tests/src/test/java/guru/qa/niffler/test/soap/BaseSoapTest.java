package guru.qa.niffler.test.soap;

import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.service.impl.UserdataSoapClient;

@SoapTest
public abstract class BaseSoapTest {

  protected static final String ID_REGEXP = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
  protected static final UserdataSoapClient wsClient = new UserdataSoapClient();
}
