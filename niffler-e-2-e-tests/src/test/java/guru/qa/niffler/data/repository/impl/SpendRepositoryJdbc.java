package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendRepositoryJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    private static final SpendDao spendDao = new SpendDaoJdbc();
    private static final CategoryDao categoryDao = new CategoryDaoJdbc();

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Override
    public @Nonnull SpendEntity create(SpendEntity spend) {
        return xaTxTemplate.execute(() -> {
            if (spend.getCategory() != null) {
                CategoryEntity category = categoryDao.findById(spend.getCategory().getId())
                        .orElseGet(() -> categoryDao.create(spend.getCategory()));
                spend.setCategory(category);
            }
            return spendDao.create(spend);
        });
    }

    @Override
    @Nonnull
    public SpendEntity update(SpendEntity spend) {
        return spendDao.update(spend);
    }

    @Override
    @Nonnull
    public CategoryEntity createCategory(CategoryEntity category) {
        return categoryDao.create(category);
    }

    @Nonnull
    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        return categoryDao.updateCategory(category);
    }

    @Override
    @Nonnull
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findById(id);
    }

    @Override
    @Nonnull
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
        return categoryDao.findCategoryByUsernameAndCategoryName(username, name);
    }

    @Override
    @Nonnull
    public Optional<SpendEntity> findById(UUID id) {
        return spendDao.findById(id).map(spendEntity -> {
            spendEntity.setCategory(categoryDao.findById(spendEntity.getCategory().getId()).get());
            return spendEntity;
        });
    }

    @Override
    @Nonnull
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        return spendDao.findByUsernameAndSpendDescription(username, description);
    }

    @Override
    public void remove(SpendEntity spend) {
        spendDao.delete(spend);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        categoryDao.delete(category);
    }
}
