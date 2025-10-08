package guru.qa.niffler.data.entity.spend;

import guru.qa.niffler.model.CategoryJson;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class CategoryEntity implements Serializable {
    private UUID id;
    private String name;
    private String username;
    private boolean archived;


    public static CategoryEntity fromJson(CategoryJson json) {
        CategoryEntity ce = new CategoryEntity();
        ce.setId(json.id());
        ce.setName(json.name());
        ce.setUsername(json.username());
        ce.setArchived(json.archived());
        return ce;
    }
}
