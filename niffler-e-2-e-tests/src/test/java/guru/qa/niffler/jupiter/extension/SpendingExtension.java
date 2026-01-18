package guru.qa.niffler.jupiter.extension;

import com.github.jknack.handlebars.internal.lang3.ArrayUtils;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.service.SpendClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);
    private final SpendClient spendClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                            if (ArrayUtils.isNotEmpty(userAnno.spendings())) {

                                Optional<UserJson> testUser = UserExtension.createdUser();
                                final String username = testUser.isPresent()
                                        ? testUser.get().getUsername()
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
                                    testUser.get().getTestData().spendings().addAll(
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
