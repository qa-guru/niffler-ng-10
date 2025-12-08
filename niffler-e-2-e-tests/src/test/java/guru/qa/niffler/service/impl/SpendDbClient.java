package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class SpendDbClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

  private final SpendRepository spendRepository = SpendRepository.getInstance();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.spendJdbcUrl()
  );

  @Override
  @Step("Create spend using SQL INSERT")
  @Nullable
  public SpendJson createSpend(SpendJson spend) {
    return requireNonNull(
        xaTransactionTemplate.execute(
            () -> SpendJson.fromEntity(
                spendRepository.create(
                    SpendEntity.fromJson(spend)
                )
            )
        )
    );
  }

  @Override
  @Step("Edit spend using SQL UPDATE")
  @Nullable
  public SpendJson editSpend(SpendJson spend) {
    return requireNonNull(
        xaTransactionTemplate.execute(
            () -> SpendJson.fromEntity(
                spendRepository.update(
                    SpendEntity.fromJson(spend)
                )
            )
        )
    );
  }

  @Override
  @Step("Find spend using SQL SELECT")
  @Nullable
  public SpendJson getSpend(String id) {
    return spendRepository.findById(UUID.fromString(id))
        .map(SpendJson::fromEntity)
        .orElse(null);
  }

  @Nonnull
  @Override
  public List<SpendJson> allSpends(String username, @Nullable CurrencyValues currency, @Nullable Date from, @Nullable Date to) {
    return requireNonNull(
        xaTransactionTemplate.execute(
            () ->
                spendRepository.all(
                    username, currency, from, to
                ).stream().map(SpendJson::fromEntity).toList()
        )
    );
  }

  @Override
  public void removeSpends(String username, String... ids) {
    xaTransactionTemplate.execute(
        () -> {
          for (String id : ids) {
            Optional<SpendEntity> spend = spendRepository.findById(UUID.fromString(id));
            spend.ifPresent(spendRepository::remove);
          }
          return null;
        }
    );
  }

  @Override
  @Step("Create category using SQL INSERT")
  @Nonnull
  public CategoryJson createCategory(CategoryJson category) {
    return requireNonNull(
        xaTransactionTemplate.execute(
            () -> CategoryJson.fromEntity(
                spendRepository.createCategory(
                    CategoryEntity.fromJson(category)
                )
            )
        )
    );
  }

  @Override
  @Step("Update category using SQL UPDATE")
  @NotNull
  public CategoryJson updateCategory(CategoryJson category) {
    return requireNonNull(
        xaTransactionTemplate.execute(
            () -> CategoryJson.fromEntity(
                spendRepository.updateCategory(
                    CategoryEntity.fromJson(category)
                )
            )
        )
    );
  }

  @Override
  @Step("Remove category using SQL DELETE")
  public void removeCategory(CategoryJson category) {
    xaTransactionTemplate.execute(
        () -> {
          spendRepository.removeCategory(
              CategoryEntity.fromJson(category)
          );
          return null;
        }
    );
  }

  @Nonnull
  @Override
  public List<CategoryJson> allCategories(String username) {
    return requireNonNull(
        xaTransactionTemplate.execute(
            () ->
                spendRepository.allCategories(
                    username
                ).stream().map(CategoryJson::fromEntity).toList()
        )
    );
  }
}
