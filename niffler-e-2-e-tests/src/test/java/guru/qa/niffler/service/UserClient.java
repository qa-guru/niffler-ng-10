package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

public interface UserClient {
    UserJson create(UserJson userJson);
}
