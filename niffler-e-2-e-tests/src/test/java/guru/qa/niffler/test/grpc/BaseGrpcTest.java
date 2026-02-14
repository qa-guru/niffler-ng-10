package guru.qa.niffler.test.grpc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.grpc.NifflerCurrencyServiceGrpc;
import guru.qa.niffler.grpc.NifflerUserdataServiceGrpc;
import guru.qa.niffler.grpc.PageInfo;
import guru.qa.niffler.jupiter.annotation.meta.GrpcTest;
import guru.qa.niffler.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

@GrpcTest
public abstract class BaseGrpcTest {

  protected static final Config CFG = Config.getInstance();
  protected static final String ID_REGEXP = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
  protected static final PageInfo DEFAULT_PAGE_INFO = PageInfo.newBuilder()
      .setPage(0)
      .setSize(10)
      .build();

  protected static final Channel currencyChannel = ManagedChannelBuilder
      .forAddress(CFG.currencyGrpcAddress(), CFG.currencyGrpcPort())
      .intercept(new GrpcConsoleInterceptor())
      .usePlaintext()
      .build();

  protected static final Channel userdataChannel = ManagedChannelBuilder
      .forAddress(CFG.userdataGrpcAddress(), CFG.userdataGrpcPort())
      .intercept(new GrpcConsoleInterceptor())
      .usePlaintext()
      .build();

  protected static final NifflerCurrencyServiceGrpc.NifflerCurrencyServiceBlockingStub currencyStub
      = NifflerCurrencyServiceGrpc.newBlockingStub(currencyChannel);

  protected static final NifflerUserdataServiceGrpc.NifflerUserdataServiceBlockingStub userdataStub
      = NifflerUserdataServiceGrpc.newBlockingStub(userdataChannel);
}
