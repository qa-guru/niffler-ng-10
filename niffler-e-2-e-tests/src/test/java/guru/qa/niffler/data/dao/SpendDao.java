package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {
  SpendEntity create(SpendEntity spend);

  List<SpendEntity> findAll();

  List<SpendEntity> findAllByUsername(String username);

  Optional<SpendEntity> findSpendById(UUID id);

  SpendEntity update(SpendEntity spend);

  void deleteSpend(SpendEntity spend);
}
