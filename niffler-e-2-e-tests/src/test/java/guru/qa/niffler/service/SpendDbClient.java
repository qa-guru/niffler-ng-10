package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

public class SpendDbClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

  private final SpendRepository spendRepository = new SpendRepositoryHibernate();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.spendJdbcUrl()
  );

  @Override
  public SpendJson createSpend(SpendJson spend) {
    return xaTransactionTemplate.execute(() -> {
          SpendEntity spendEntity = SpendEntity.fromJson(spend);
          if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = spendRepository.createCategory(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
          }
          return SpendJson.fromEntity(
              spendRepository.create(spendEntity)
          );
        }
    );
  }

  @Override
  public CategoryJson createCategory(CategoryJson category) {
    return xaTransactionTemplate.execute(() -> CategoryJson.fromEntity(
            spendRepository.createCategory(
                CategoryEntity.fromJson(category)
            )
        )
    );
  }

  @Override
  public CategoryJson updateCategory(CategoryJson category) {
    return xaTransactionTemplate.execute(() -> CategoryJson.fromEntity(
            spendRepository.updateCategory(
                CategoryEntity.fromJson(category)
            )
        )
    );
  }
}
