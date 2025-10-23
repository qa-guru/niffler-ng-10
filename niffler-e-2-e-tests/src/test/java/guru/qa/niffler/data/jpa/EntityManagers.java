package guru.qa.niffler.data.jpa;

import guru.qa.niffler.data.tpl.DataSources;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
public class EntityManagers {
  private static final Map<String, EntityManagerFactory> emfs = new ConcurrentHashMap<>();

  private EntityManagers() {
  }

  @Nonnull
  @SuppressWarnings("resource")
  public static EntityManager em(String jdbcUrl) {
    return new ThreadSafeEntityManager(
        emfs.computeIfAbsent(
            jdbcUrl,
            key -> {
              DataSources.dataSource(jdbcUrl);
              return Persistence.createEntityManagerFactory(jdbcUrl);
            }
        ).createEntityManager()
    );
  }
}
