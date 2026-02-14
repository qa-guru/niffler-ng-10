package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.jaxb.userdata.Currency;
import guru.qa.jaxb.userdata.User;
import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.UserEntity;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static guru.qa.niffler.grpc.FriendshipStatus.FRIENDSHIP_STATUS_UNSPECIFIED;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("firstname")
    String firstname,
    @JsonProperty("surname")
    String surname,
    @JsonProperty("fullname")
    String fullname,
    @JsonProperty("currency")
    CurrencyValues currency,
    @JsonProperty("photo")
    String photo,
    @JsonProperty("photoSmall")
    String photoSmall,
    @JsonProperty("friendshipStatus")
    FriendshipStatus friendshipStatus) implements IUserJson {

  public @Nonnull User toJaxbUser() {
    User jaxbUser = new User();
    jaxbUser.setId(id != null ? id.toString() : null);
    jaxbUser.setUsername(username);
    jaxbUser.setFirstname(firstname);
    jaxbUser.setSurname(surname);
    jaxbUser.setFullname(fullname);
    jaxbUser.setCurrency(currency != null ? Currency.valueOf(currency.name()) : null);
    jaxbUser.setPhoto(photo);
    jaxbUser.setPhotoSmall(photoSmall);
    jaxbUser.setFriendshipStatus(friendshipStatus() == null ?
        guru.qa.jaxb.userdata.FriendshipStatus.VOID :
        guru.qa.jaxb.userdata.FriendshipStatus.valueOf(friendshipStatus().name()));
    return jaxbUser;
  }

  public static @Nonnull UserJson fromJaxb(@Nonnull User jaxbUser) {
    return new UserJson(
        jaxbUser.getId() != null ? UUID.fromString(jaxbUser.getId()) : null,
        jaxbUser.getUsername(),
        jaxbUser.getFirstname(),
        jaxbUser.getSurname(),
        jaxbUser.getFullname(),
        jaxbUser.getCurrency() != null ? CurrencyValues.valueOf(jaxbUser.getCurrency().name()) : null,
        jaxbUser.getPhoto(),
        jaxbUser.getPhotoSmall(),
        (jaxbUser.getFriendshipStatus() != null && jaxbUser.getFriendshipStatus() != guru.qa.jaxb.userdata.FriendshipStatus.VOID)
            ? FriendshipStatus.valueOf(jaxbUser.getFriendshipStatus().name())
            : null
    );
  }

  public @Nonnull guru.qa.niffler.grpc.User toGrpcUser() {
    guru.qa.niffler.grpc.User.Builder grpcUserBuilder = guru.qa.niffler.grpc.User.newBuilder();
    grpcUserBuilder.setUsername(username);
    if (id != null) {
      grpcUserBuilder.setId(id.toString());
    }
    if (firstname != null) {
      grpcUserBuilder.setFirstname(firstname);
    }
    if (surname != null) {
      grpcUserBuilder.setSurname(surname);
    }
    if (fullname != null) {
      grpcUserBuilder.setFullname(fullname);
    }
    if (currency != null) {
      grpcUserBuilder.setCurrency(guru.qa.niffler.grpc.CurrencyValues.valueOf(currency.name()));
    }
    if (photo != null) {
      grpcUserBuilder.setPhoto(photo);
    }
    if (photoSmall != null) {
      grpcUserBuilder.setPhotoSmall(photoSmall);
    }
    grpcUserBuilder.setFriendshipStatus(friendshipStatus() == null ?
        FRIENDSHIP_STATUS_UNSPECIFIED :
        guru.qa.niffler.grpc.FriendshipStatus.valueOf(friendshipStatus().name()));
    return grpcUserBuilder.build();
  }

  public static @Nonnull UserJson fromGrpc(@Nonnull guru.qa.niffler.grpc.User grpcUser) {
    return new UserJson(
        UUID.fromString(grpcUser.getId()),
        grpcUser.getUsername(),
        grpcUser.getFirstname(),
        grpcUser.getSurname(),
        grpcUser.getFullname(),
        CurrencyValues.valueOf(grpcUser.getCurrency().name()),
        grpcUser.getPhoto(),
        grpcUser.getPhotoSmall(),
        grpcUser.getFriendshipStatus() != FRIENDSHIP_STATUS_UNSPECIFIED
            ? FriendshipStatus.valueOf(grpcUser.getFriendshipStatus().name())
            : null
    );
  }

  public static @Nonnull UserJson fromEntity(@Nonnull UserEntity entity, @Nullable FriendshipStatus friendshipStatus) {
    return new UserJson(
        entity.getId(),
        entity.getUsername(),
        entity.getFirstname(),
        entity.getSurname(),
        entity.getFullname(),
        entity.getCurrency(),
        entity.getPhoto() != null && entity.getPhoto().length > 0 ? new String(entity.getPhoto(), StandardCharsets.UTF_8) : null,
        entity.getPhotoSmall() != null && entity.getPhotoSmall().length > 0 ? new String(entity.getPhotoSmall(), StandardCharsets.UTF_8) : null,
        friendshipStatus
    );
  }

  public static @Nonnull UserJson fromEntity(@Nonnull UserEntity entity) {
    return fromEntity(entity, null);
  }
}
