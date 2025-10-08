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

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(String username, String password, UserType.Type empty) {
    }

    private static final Queue<StaticUser> EMPTY = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_FRIEND = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST = new ConcurrentLinkedQueue<>();


    static {
        EMPTY.add(new StaticUser("empty_user_hm", "111", UserType.Type.EMPTY));
        WITH_FRIEND.add(new StaticUser("user_with_friend", "111", UserType.Type.WITH_FRIEND));
        WITH_INCOME_REQUEST.add(new StaticUser("income_req_user", "111", UserType.Type.WITH_INCOME_REQUEST));
        WITH_OUTCOME_REQUEST.add(new StaticUser("outcome_req_user", "111", UserType.Type.WITH_OUTCOME_REQUEST));
    }

    private StaticUser getUser(UserType.Type type) {
        switch (type) {
            case EMPTY -> {
                return EMPTY.poll();
            }
            case WITH_FRIEND -> {
                return WITH_FRIEND.poll();
            }
            case WITH_INCOME_REQUEST -> {
                return WITH_INCOME_REQUEST.poll();
            }
            case WITH_OUTCOME_REQUEST -> {
                return WITH_OUTCOME_REQUEST.poll();
            }
            default -> {
                return null;
            }

        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
                .map(parameter -> parameter.getAnnotation(UserType.class))
                .forEach(ut -> {
                    Optional<StaticUser> user = Optional.empty();
                    StopWatch sw = StopWatch.createStarted();
                    while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                        user = Optional.ofNullable(getUser(ut.empty()));
                    }
                    Allure.getLifecycle().updateTestCase(testCase ->
                            testCase.setStart(new Date().getTime())
                    );
                    user.ifPresentOrElse(
                            u ->
                                    ((Map<UserType.Type, StaticUser>) context.getStore(NAMESPACE)
                                            .getOrComputeIfAbsent(
                                                    context.getUniqueId(),
                                                    key -> new HashMap<>()
                                            )).put(ut.empty(), u)
                            ,
                            () -> {
                                throw new IllegalStateException("Can`t obtain user after 30s.");
                            }
                    );
                });
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Map<UserType.Type, StaticUser> map = context.getStore(NAMESPACE)
                .get(context.getUniqueId(), Map.class);
        for (Map.Entry<UserType.Type, StaticUser> e : map.entrySet()) {
            if (e.getKey() == UserType.Type.EMPTY) {
                EMPTY.add(e.getValue());
            }
            if (e.getKey() == UserType.Type.WITH_FRIEND) {
                WITH_FRIEND.add(e.getValue());
            }
            if (e.getKey() == UserType.Type.WITH_OUTCOME_REQUEST) {
                WITH_OUTCOME_REQUEST.add(e.getValue());
            }
            if (e.getKey() == UserType.Type.WITH_INCOME_REQUEST) {
                WITH_INCOME_REQUEST.add(e.getValue());
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
        HashMap<UserType.Type, StaticUser> usersMap = (HashMap<UserType.Type, StaticUser>) extensionContext
                .getStore(NAMESPACE)
                .get(extensionContext.getUniqueId());
        UserType user = parameterContext.getParameter().getAnnotation(guru.qa.niffler.jupiter.annotation.UserType.class);
        return usersMap.get(user.empty());
    }
}
