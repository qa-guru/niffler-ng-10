package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.service.SpendClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import static guru.qa.niffler.util.RandomDataUtils.randomCategoryName;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

    private final SpendClient spendClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(
                        context.getRequiredTestMethod(),
                        User.class)
                .ifPresent(
                        annotation -> {
                            if (annotation.categories().length > 0) {
                                Category categoryFirst = annotation.categories()[0];
                                CategoryJson newCategory = new CategoryJson(
                                        null,
                                        randomCategoryName(),
                                        annotation.username(),
                                        false
                                );
                                CategoryJson created = spendClient.createCategory(newCategory);
                                if (categoryFirst.archived()) {
                                    CategoryJson archivedCategory = new CategoryJson(
                                            created.id(),
                                            newCategory.name(),
                                            newCategory.username(),
                                            true
                                    );
                                    created = spendClient.updateCategory(archivedCategory);
                                }
                                context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                            }
                        }
                );
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        CategoryJson category = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
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
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }

}
