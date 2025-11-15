package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;

public interface AuthAuthorityDao {
    void create(AuthAuthorityEntity... authAuthorityEntity);
}
