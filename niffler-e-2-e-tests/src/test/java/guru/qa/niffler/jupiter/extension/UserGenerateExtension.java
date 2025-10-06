package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.NewUser;
import guru.qa.niffler.model.NewUserModel;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Random;

public class UserGenerateExtension implements BeforeEachCallback, ParameterResolver {

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

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(NewUserModel.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), NewUserModel.class);
    }
}
