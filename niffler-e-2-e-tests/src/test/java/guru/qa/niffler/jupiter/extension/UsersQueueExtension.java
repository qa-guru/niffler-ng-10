package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.UserType;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(
            String username,
            String password,
            String friend,
            String income,
            String outcome
    ) {
    }

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USERS.add(new StaticUser("empty", "12345", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("with_friend", "12345", "non_empty_first", null, null));
        WITH_INCOME_REQUEST_USERS.add(new StaticUser("with_income", "12345", null, "non_empty_first", null));
        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("with_outcome", "12345", null, null, "with_friend"));
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        Map<UserType.Type, StaticUser> users = new HashMap<>();

        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
                .forEach(p -> {
                    UserType ut = p.getAnnotation(UserType.class);
                    Optional<StaticUser> user = Optional.empty();
                    StopWatch sw = StopWatch.createStarted();
                    while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                        user = Optional.ofNullable(queueByType(ut.value()).poll());
                    }
                    Allure.getLifecycle().updateTestCase(tc ->
                            tc.setStart(new Date().getTime())
                    );
                    user.ifPresentOrElse(
                            u -> users.put(ut.value(), u),
                            () -> {
                                throw new IllegalStateException("Can't obtain user after 30s.");
                            }
                    );
                });
        context.getStore(NAMESPACE).put(context.getUniqueId(), users);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Map<UserType.Type, StaticUser> users = context.getStore(NAMESPACE)
                .get(context.getUniqueId(), Map.class);

        if (users != null) {
            for (Map.Entry<UserType.Type, StaticUser> entry : users.entrySet()) {
                queueByType(entry.getKey()).add(entry.getValue());
            }
            context.getStore(NAMESPACE).remove(context.getUniqueId());
        }
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext,
                                       ExtensionContext extensionContext) throws ParameterResolutionException {
        UserType ut = parameterContext.findAnnotation(UserType.class)
                .orElseThrow();

        Map<UserType.Type, StaticUser> users = extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), Map.class);

        if (users == null) {
            throw new ParameterResolutionException("No users map found in store");
        }

        StaticUser user = users.get(ut.value());
        if (user == null) {
            throw new ParameterResolutionException("No user found for annotation: " + ut.value());
        }
        return user;
    }

    private Queue<StaticUser> queueByType(UserType.Type type) {
        return switch (type) {
            case EMPTY -> EMPTY_USERS;
            case WITH_FRIEND -> WITH_FRIEND_USERS;
            case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS;
            case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS;
        };
    }
}