package guru.qa.niffler.model;

import java.util.List;

public record TestData(String password,
                       List<UserJson> incomeInvitations,
                       List<UserJson> outcomeInvitations,
                       List<UserJson> friends,
                       List<CategoryJson> categories,
                       List<SpendJson> spendings
) {
}
