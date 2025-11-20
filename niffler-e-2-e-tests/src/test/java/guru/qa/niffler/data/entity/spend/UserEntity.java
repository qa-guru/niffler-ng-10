package guru.qa.niffler.data.entity.spend;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class UserEntity implements Serializable {
    private UUID id;
    private String username;
    private String firstname;
    private String surname;
    private String fullname;
    private CurrencyValues currency;
    private byte[] photo;
    private byte[] photoSmall;

    public static UserEntity fromJson(UserJson json) {
        UserEntity ue = new UserEntity();
        ue.setId(json.id());
        ue.setUsername(json.username());
        ue.setFirstname(json.firstname());
        ue.setSurname(json.surname());
        ue.setFullname(json.fullname());
        ue.setCurrency(json.currency());
        ue.setPhoto(json.photo() != null ? json.photo().getBytes() : null);
        ue.setPhotoSmall(json.photoSmall() != null ? json.photoSmall().getBytes() : null);
        return ue;
    }

}
