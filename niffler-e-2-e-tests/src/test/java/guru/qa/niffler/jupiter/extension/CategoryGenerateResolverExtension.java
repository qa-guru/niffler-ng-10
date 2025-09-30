package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.model.CategoryModel;
import guru.qa.niffler.model.NewUserModel;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class CategoryGenerateResolverExtension implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryModel.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(CreateSpendingExtension.NAMESPACE).get(extensionContext.getUniqueId(), CategoryModel.class);
    }
}
