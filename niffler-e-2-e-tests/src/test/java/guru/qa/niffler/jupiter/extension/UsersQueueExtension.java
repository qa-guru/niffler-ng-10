package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class UsersQueueExtension implements
    BeforeTestExecutionCallback,
    AfterTestExecutionCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

  public record StaticUser(String username, String password, String friend, String income, String outcome) {
  }

  private record IndexedUserType(int index, UserType userType) {
  }

  private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
  private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

  static {
    EMPTY_USERS.add(new StaticUser("kilo", "12345", null, null, null));
    WITH_FRIEND_USERS.add(new StaticUser("duck", "12345", "dima", null, null));
    WITH_INCOME_REQUEST_USERS.addAll(
        List.of(
            new StaticUser("dima", "12345", null, "bee", null),
            new StaticUser("bill", "12345", null, "barsik", null)
        )
    );
    WITH_OUTCOME_REQUEST_USERS.addAll(
        List.of(
            new StaticUser("barsik", "12345", null, null, "bill"),
            new StaticUser("bee", "12345", null, null, "dima")
        )
    );
  }

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface UserType {
    Type value() default Type.EMPTY;

    enum Type {
      EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void beforeTestExecution(ExtensionContext context) {
    final Parameter[] params = context.getRequiredTestMethod().getParameters();
    IntStream.range(0, params.length)
        .filter(i ->
            AnnotationSupport.isAnnotated(params[i], UserType.class)
                && params[i].getType().isAssignableFrom(StaticUser.class)
        )
        .mapToObj(i -> new IndexedUserType(i, params[i].getAnnotation(UserType.class)))
        .forEach(iut -> {
          Optional<StaticUser> user = Optional.empty();
          StopWatch sw = StopWatch.createStarted();
          while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
            user = switch (iut.userType.value()) {
              case EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
              case WITH_FRIEND -> Optional.ofNullable(WITH_FRIEND_USERS.poll());
              case WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUEST_USERS.poll());
              case WITH_OUTCOME_REQUEST -> Optional.ofNullable(WITH_OUTCOME_REQUEST_USERS.poll());
            };
          }
          Allure.getLifecycle().updateTestCase(testCase ->
              testCase.setStart(new Date().getTime())
          );
          user.ifPresentOrElse(
              u ->
                  ((Map<IndexedUserType, StaticUser>) context.getStore(NAMESPACE).getOrComputeIfAbsent(
                      context.getUniqueId(),
                      key -> new HashMap<>()
                  )).put(iut, u),
              () -> {
                throw new IllegalStateException("Can`t obtain user after 30s.");
              }
          );
        });
  }

  @Override
  @SuppressWarnings("unchecked")
  public void afterTestExecution(ExtensionContext context) {
    Map<IndexedUserType, StaticUser> usersFromTest = context.getStore(NAMESPACE).get(
        context.getUniqueId(),
        Map.class
    );
    if (usersFromTest != null) {
      for (Map.Entry<IndexedUserType, StaticUser> e : usersFromTest.entrySet()) {
        switch (e.getKey().userType.value()) {
          case EMPTY -> EMPTY_USERS.add(e.getValue());
          case WITH_FRIEND -> WITH_FRIEND_USERS.add(e.getValue());
          case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS.add(e.getValue());
          case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS.add(e.getValue());
        }
      }
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
        && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    final Map<IndexedUserType, StaticUser> usersForTest = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class);
    return usersForTest.get(
        new IndexedUserType(
            parameterContext.getIndex(),
            parameterContext.getParameter().getAnnotation(UserType.class)
        )
    );
  }
}
