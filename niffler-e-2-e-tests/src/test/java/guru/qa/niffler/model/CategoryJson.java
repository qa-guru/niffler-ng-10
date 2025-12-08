package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public record CategoryJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("name")
    String name,
    @JsonProperty("username")
    String username,
    @JsonProperty("archived")
    boolean archived) {

    @Nonnull
    public static CategoryJson fromEntity(CategoryEntity entity) {
        return new CategoryJson(
            entity.getId(),
            entity.getName(),
            entity.getUsername(),
            entity.isArchived()
        );
    }
}
