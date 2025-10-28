package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

import java.util.List;

public interface UsersClient {

  UserJson createUser(String username, String password);

  List<UserJson> addIncomeInvitation(UserJson targetUser, int count);

  List<UserJson> addOutcomeInvitation(UserJson targetUser, int count);

  List<UserJson> addFriend(UserJson targetUser, int count);
}
