package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

public interface UsersClient {

  UserJson createUser(UserJson user);
}
