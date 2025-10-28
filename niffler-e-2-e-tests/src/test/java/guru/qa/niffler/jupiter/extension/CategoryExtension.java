package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.SpendDbClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;
import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

public class CategoryExtension implements
    BeforeEachCallback,
    AfterTestExecutionCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

  private final SpendClient spendClient = new SpendDbClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(userAnno -> {
          if (ArrayUtils.isNotEmpty(userAnno.categories())) {

            Optional<UserJson> testUser = UserExtension.createdUser();
            final String username = testUser.isPresent()
                ? testUser.get().username()
                : userAnno.username();

            List<CategoryJson> result = new ArrayList<>();

            for (Category categoryAnno : userAnno.categories()) {
              CategoryJson category = new CategoryJson(
                  null,
                  "".equals(categoryAnno.name()) ? randomCategoryName() : categoryAnno.name(),
                  username,
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
              result.add(created);
            }

            if (testUser.isPresent()) {
              testUser.get().testData().categories().addAll(
                  result
              );
            } else {
              context.getStore(NAMESPACE).put(
                  context.getUniqueId(),
                  result.stream().toArray(CategoryJson[]::new)
              );
            }
          }
        });
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    CategoryJson[] categories = createdCategory();
    if (categories != null) {
      for (CategoryJson category : categories) {
        if (!category.archived()) {
          CategoryJson archivedCategory = new CategoryJson(
              category.id(),
              category.name(),
              category.username(),
              true
          );
          spendClient.updateCategory(archivedCategory);
        }
      }
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
      ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson[].class);
  }

  @Override
  public CategoryJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
      ParameterResolutionException {
    return createdCategory();
  }

  public static CategoryJson[] createdCategory() {
    final ExtensionContext methodContext = context();
    return methodContext.getStore(NAMESPACE)
        .get(methodContext.getUniqueId(), CategoryJson[].class);
  }
}
