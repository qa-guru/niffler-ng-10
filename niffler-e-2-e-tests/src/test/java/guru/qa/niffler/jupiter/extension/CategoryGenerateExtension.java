package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.NewCategory;
import guru.qa.niffler.jupiter.annotation.NewUser;
import guru.qa.niffler.model.CategoryModel;
import guru.qa.niffler.model.NewUserModel;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Random;

public class CategoryGenerateExtension implements BeforeEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateSpendingExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                NewCategory.class
        ).ifPresent(
                anno -> {
                    Random random = new Random();
                    CategoryModel category = new CategoryModel("category "+Math.abs(random.nextInt()));
                context.getStore(NAMESPACE).
                        put(context.getUniqueId(),
                                category);
                }
        );
    }
}
