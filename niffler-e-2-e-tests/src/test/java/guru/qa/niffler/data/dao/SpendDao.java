package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {
    SpendEntity create(SpendEntity spend);

  /*ДЗ*/  Optional<SpendEntity> findSpendById(UUID id);

  /*ДЗ*/    List<SpendEntity> findAllByUsername(String username);

  /*ДЗ*/  void deleteSpend(SpendEntity spend);

}
