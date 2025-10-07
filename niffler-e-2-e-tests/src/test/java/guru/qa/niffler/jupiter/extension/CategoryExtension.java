package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.UUID;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendApiClient spendClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                User.class
        ).ifPresent(userAnno -> {
            Category[] categories = userAnno.categories();
            if (categories.length > 0) {
                Category anno = categories[0];

                CategoryJson createdCategory = spendClient.createCategory(
                        new CategoryJson(
                                UUID.randomUUID(),
                                RandomDataUtils.randomCategoryName(),
                                userAnno.username(),
                                false
                        )
                );

                if (anno.archived()) {
                    createdCategory = spendClient.updateCategory(
                            new CategoryJson(
                                    createdCategory.id(),
                                    createdCategory.name(),
                                    userAnno.username(),
                                    true
                            )
                    );
                }

                context.getStore(NAMESPACE).put(
                        context.getUniqueId(),
                        createdCategory
                );
            }
        });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == CategoryJson.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        CategoryJson categoryJson = context.getStore(CategoryExtension.NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        if (categoryJson != null && !categoryJson.archived()) {
            spendClient.updateCategory(new CategoryJson(
                    categoryJson.id(),
                    categoryJson.name(),
                    categoryJson.username(),
                    true
            ));
        }
    }
}