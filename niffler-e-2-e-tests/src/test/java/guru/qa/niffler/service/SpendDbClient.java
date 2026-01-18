package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public final class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = SpendRepository.getInstance();

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Override
    @Step("Найти spend по ID {id} пользователя {username}")
    public @Nullable SpendJson findSpendById(String id, String username) {
        throw new UnsupportedOperationException("Not implemented :(");
    }

    @Override
    public List<SpendJson> findSpendsByUserName(String username, CurrencyValues currencyValues,
                                                String from, String to) {
        throw new UnsupportedOperationException("Not implemented :(");
    }

    @Override
    @Step("Создать spend")
    public SpendJson createSpend(SpendJson spend) {
        return xaTxTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity = spendRepository.createCategory(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
            return SpendJson.fromEntity(
                    spendRepository.create(spendEntity)
            );
        });
    }

    @Override
    @Step("Обновить spend")
    public SpendJson updateSpend(SpendJson spend) {
        return SpendJson.fromEntity(spendRepository.update(SpendEntity.fromJson(spend)));
    }

    @Override
    public void deleteSpends(String username, List<String> ids) {
        throw new UnsupportedOperationException("Not implemented :(");
    }

    @Override
    public List<CategoryJson> findAllCategories(String username) {
        throw new UnsupportedOperationException("Not implemented :(");
    }

    @Override
    @Step("Создать категорию")
    public CategoryJson createCategory(CategoryJson category) {
        return xaTxTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            CategoryEntity createdCategory = spendRepository.createCategory(categoryEntity);
            return CategoryJson.fromEntity(createdCategory);
        });
    }

    @Override
    public CategoryJson updateCategory(CategoryJson categoryJson) {
        return xaTxTemplate.execute(() -> CategoryJson.fromEntity(
                        spendRepository.updateCategory(
                                CategoryEntity.fromJson(categoryJson)
                        )
                )
        );
    }

    @Override
    @Step("Найти категорию по имени {categoryName} пользователя {username}")
    public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName,
                                                                String username) {
        return xaTxTemplate.execute(() -> {
            Optional<CategoryEntity> category = spendRepository.findCategoryByUsernameAndCategoryName(username, categoryName);
            return category.map(CategoryJson::fromEntity);
        });
    }
}