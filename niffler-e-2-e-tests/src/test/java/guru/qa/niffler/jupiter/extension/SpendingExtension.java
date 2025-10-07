package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.service.SpendClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

  private final SpendClient spendClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(
        context.getRequiredTestMethod(),
        User.class
    ).ifPresent(userAnno -> {
        Spending[] spendings = userAnno.spendings();
        if (spendings.length > 0) {
            Spending anno = spendings[0];
            
            final SpendJson created = spendClient.createSpend(
                new SpendJson(
                    null,
                    new Date(),
                    new CategoryJson(
                        null,
                        anno.category(),
                        userAnno.username(),
                        false
                    ),
                    anno.currency(),
                    anno.amount(),
                    anno.description(),
                    userAnno.username()
                )
            );
            context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                created
            );
        }
    });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
  }

  @Override
  public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), SpendJson.class);
  }
}
