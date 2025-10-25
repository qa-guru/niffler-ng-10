package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.model.StaticUser;
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

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);
    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USERS.add(new StaticUser("TestUser-1", "12345", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("TestUser-2", "12345", "TestUser-5", null, null));
        WITH_FRIEND_USERS.add(new StaticUser("TestUser-5", "12345", "TestUser-2", null, null));
        WITH_INCOME_REQUEST_USERS.add(new StaticUser("TestUser-3", "12345", null, "TestUser-4", null));
        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("TestUser-4", "12345", null, null, "TestUser-3"));

    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class) && p.getType().isAssignableFrom(StaticUser.class))
                .map(p -> p.getAnnotation(UserType.class).value())
                .forEach(ut -> {
                    Optional<StaticUser> user = Optional.empty();
                    StopWatch sw = StopWatch.createStarted();
                    while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                        Queue<StaticUser> queue = getQueueByUserType(ut);
                        user = Optional.ofNullable(queue.poll());
                    }
                    Allure.getLifecycle()
                            .updateTestCase(testCase -> testCase.setStart(new Date().getTime()));
                    user.ifPresentOrElse(u -> ((Map<UserType.Type, StaticUser>) context.getStore(NAMESPACE).getOrComputeIfAbsent(
                                    context.getUniqueId(),
                                    key -> new HashMap<>()
                            )).put(ut, u),
                            () -> {
                                throw new IllegalStateException("Can`t obtain user after 30s.");
                            });

                });
    }

    private Queue<StaticUser> getQueueByUserType(UserType.Type userType) {
        switch (userType) {
            case EMPTY -> {
                return EMPTY_USERS;
            }
            case WITH_FRIEND -> {
                return WITH_FRIEND_USERS;
            }
            case WITH_INCOME_REQUEST -> {
                return WITH_INCOME_REQUEST_USERS;
            }
            case WITH_OUTCOME_REQUEST -> {
                return WITH_OUTCOME_REQUEST_USERS;
            }
            default -> {
                throw new IllegalArgumentException("Unknown user type was provided");
            }
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Map<UserType.Type, StaticUser> users = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        if (users != null) {
            for (Map.Entry<UserType.Type, StaticUser> entry : users.entrySet()) {
                Queue<StaticUser> userQueue = getQueueByUserType(entry.getKey());
                userQueue.add(entry.getValue());
            }
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (StaticUser) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), Map.class)
                .get(parameterContext.getParameter().getAnnotation(UserType.class).value());
    }
}