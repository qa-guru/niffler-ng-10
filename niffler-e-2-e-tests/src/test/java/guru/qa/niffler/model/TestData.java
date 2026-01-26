package guru.qa.niffler.model;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public record TestData(
    @Nonnull String password,
    @Nonnull List<UserJson> others,
    @Nonnull List<UserJson> friends,
    @Nonnull List<UserJson> incomeInvitations,
    @Nonnull List<UserJson> outcomeInvitations,
    @Nonnull List<CategoryJson> categories,
    @Nonnull List<SpendJson> spendings
) {

  public TestData(String password) {
    this(password, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
  }

  public TestData(String password, List<UserJson> others, List<UserJson> friends, List<UserJson> incomeInvitations, List<UserJson> outcomeInvitations) {
    this(password, others, friends, incomeInvitations, outcomeInvitations, new ArrayList<>(), new ArrayList<>());
  }

  @Nonnull
  public TestData addCategories(List<CategoryJson> categories) {
    return new TestData(
        this.password,
        this.others,
        this.friends,
        this.incomeInvitations,
        this.outcomeInvitations,
        categories,
        this.spendings
    );
  }

  @Nonnull
  public TestData addSpendings(List<SpendJson> spendings) {
    return new TestData(
        this.password,
        this.others,
        this.friends,
        this.incomeInvitations,
        this.outcomeInvitations,
        this.categories,
        spendings
    );
  }

  @Nonnull
  public String[] othersUsernames() {
    return extractUsernames(others);
  }

  @Nonnull
  public String[] friendsUsernames() {
    return extractUsernames(friends);
  }

  @Nonnull
  public String[] incomeInvitationsUsernames() {
    return extractUsernames(incomeInvitations);
  }

  @Nonnull
  public String[] outcomeInvitationsUsernames() {
    return extractUsernames(outcomeInvitations);
  }

  @Nonnull
  public String[] categoryDescriptions() {
    return categories.stream().map(CategoryJson::name).toArray(String[]::new);
  }

  @Nonnull
  private String[] extractUsernames(List<UserJson> users) {
    return users.stream().map(UserJson::username).toArray(String[]::new);
  }
}
