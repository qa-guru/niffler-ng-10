package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.service.SpendClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class CreateSpendingExtension implements BeforeEachCallback {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateSpendingExtension.class);
  private final SpendClient spendClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(
        context.getRequiredTestMethod(),
        Spending.class
    ).ifPresent(
        anno -> {
          final SpendJson created = spendClient.createSpend(
              new SpendJson(
                  null,
                  new Date(),
                  new CategoryJson(
                      null,
                      anno.category(),
                      anno.username(),
                      false
                  ),
                  anno.currency(),
                  anno.amount(),
                  anno.description(),
                  anno.username()
              )
          );
          context.getStore(NAMESPACE).put(
              context.getUniqueId(),
              created
          );
        }
    );
  }
}
