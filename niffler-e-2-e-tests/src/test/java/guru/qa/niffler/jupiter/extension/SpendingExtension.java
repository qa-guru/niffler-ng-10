package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

@ParametersAreNonnullByDefault
public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

  private final SpendClient spendClient = SpendClient.getInstance();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(userAnno -> {
              if (ArrayUtils.isNotEmpty(userAnno.spendings())) {

                Optional<UserJson> testUser = UserExtension.createdUser();
                final String username = testUser.isPresent()
                    ? testUser.get().username()
                    : userAnno.username();

                List<SpendJson> result = new ArrayList<>();

                for (Spending spendAnno : userAnno.spendings()) {
                  SpendJson spendJson = new SpendJson(
                      null,
                      new Date(),
                      new CategoryJson(
                          null,
                          spendAnno.category(),
                          username,
                          false
                      ),
                      spendAnno.currency(),
                      spendAnno.amount(),
                      spendAnno.description(),
                      username
                  );

                  SpendJson created = spendClient.createSpend(spendJson);
                  result.add(created);
                }

                if (testUser.isPresent()) {
                  testUser.get().testData().spendings().addAll(
                      result
                  );
                } else {
                  context.getStore(NAMESPACE).put(
                      context.getUniqueId(),
                      result.stream().toArray(SpendJson[]::new)
                  );
                }
              }
            }
        );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
      ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
  }

  @Override
  public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
      ParameterResolutionException {
    return createdSpending();
  }

  public static SpendJson[] createdSpending() {
    final ExtensionContext methodContext = context();
    return methodContext.getStore(NAMESPACE)
        .get(methodContext.getUniqueId(), SpendJson[].class);
  }
}
