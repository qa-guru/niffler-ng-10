package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.service.SpendClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;
import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

public class CategoryExtension implements
    BeforeEachCallback,
    AfterTestExecutionCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

  private final SpendClient spendClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(userAnno -> {
          if (ArrayUtils.isNotEmpty(userAnno.categories())) {
            Category categoryAnno = userAnno.categories()[0];
            CategoryJson category = new CategoryJson(
                null,
                randomCategoryName(),
                userAnno.username(),
                categoryAnno.archived()
            );

            CategoryJson created = spendClient.createCategory(category);
            if (categoryAnno.archived()) {
              CategoryJson archivedCategory = new CategoryJson(
                  created.id(),
                  created.name(),
                  created.username(),
                  true
              );
              created = spendClient.updateCategory(archivedCategory);
            }

            context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                created
            );
          }
        });
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    CategoryJson category = createdCategory();
    if (category != null && !category.archived()) {
      category = new CategoryJson(
          category.id(),
          category.name(),
          category.username(),
          true
      );
      spendClient.updateCategory(category);
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
      ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
  }

  @Override
  public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
      ParameterResolutionException {
    return createdCategory();
  }

  public static CategoryJson createdCategory() {
    final ExtensionContext methodContext = context();
    return methodContext.getStore(NAMESPACE)
        .get(methodContext.getUniqueId(), CategoryJson.class);
  }
}
