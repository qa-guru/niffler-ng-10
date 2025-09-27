package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.NewUser;
import guru.qa.niffler.model.NewUserModel;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Random;

public class UserGenerateExtension implements BeforeEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateSpendingExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                NewUser.class
        ).ifPresent(
                anno -> {
                    Random random = new Random();
                    NewUserModel newUser =   new NewUserModel("user"+Math.abs(random.nextInt()),"111","111"
                    );
                    context.getStore(NAMESPACE).put(
                            context.getUniqueId(),
                            newUser
                    );
                }
        );
    }
}
