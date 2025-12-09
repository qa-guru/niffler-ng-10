package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

@ParametersAreNonnullByDefault
public class UserExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
  public static final String DEFAULT_PASSWORD = "12345";

  private final UsersClient usersClient = UsersClient.getInstance();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(userAnno -> {
              if ("".equals(userAnno.username())) {
                final String username = RandomDataUtils.randomUsername();
                final UserJson user = usersClient.createUser(username, DEFAULT_PASSWORD);
                final List<UserJson> incomeInvitations = usersClient.addIncomeInvitation(user, userAnno.incomeInvitations());
                final List<UserJson> outcomeInvitations = usersClient.addOutcomeInvitation(user, userAnno.outcomeInvitations());
                final List<UserJson> friends = usersClient.addFriend(user, userAnno.friends());

                final TestData testData = new TestData(
                    DEFAULT_PASSWORD,
                    incomeInvitations,
                    outcomeInvitations,
                    friends,
                    new ArrayList<>(),
                    new ArrayList<>()
                );

                setUser(user.addTestData(testData));
              }
            }
        );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
      ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
  }

  @Override
  public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
      ParameterResolutionException {
    return createdUser().orElseThrow();
  }

  public static void setUser(UserJson testUser) {
    final ExtensionContext context = TestMethodContextExtension.context();
    context.getStore(NAMESPACE).put(
        context.getUniqueId(),
        testUser
    );
  }

  public static Optional<UserJson> createdUser() {
    final ExtensionContext methodContext = context();
    return Optional.ofNullable(methodContext.getStore(NAMESPACE)
        .get(methodContext.getUniqueId(), UserJson.class));
  }
}
