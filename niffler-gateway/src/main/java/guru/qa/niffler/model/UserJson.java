package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.jaxb.userdata.Currency;
import guru.qa.jaxb.userdata.User;
import guru.qa.niffler.config.NifflerGatewayServiceConfig;
import guru.qa.niffler.validation.IsPhotoString;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

import static guru.qa.niffler.grpc.FriendshipStatus.FRIENDSHIP_STATUS_UNSPECIFIED;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
    @JsonProperty("id")
    UUID id,
    @Size(min = 3, max = 50, message = "Allowed username length should be from 3 to 50 characters")
    @JsonProperty("username")
    String username,
    @JsonProperty("firstname")
    @Size(max = 30, message = "First name can`t be longer than 30 characters")
    String firstname,
    @JsonProperty("surname")
    @Size(max = 50, message = "Surname can`t be longer than 50 characters")
    String surname,
    @JsonProperty("fullname")
    @Size(max = 100, message = "Fullname can`t be longer than 100 characters")
    String fullname,
    @JsonProperty("currency")
    CurrencyValues currency,
    @JsonProperty("photo")
    @IsPhotoString
    @Size(max = NifflerGatewayServiceConfig.ONE_MB)
    String photo,
    @IsPhotoString
    @JsonProperty("photoSmall")
    String photoSmall,
    @JsonProperty("friendshipStatus")
    FriendshipStatus friendshipStatus) {

  public @Nonnull UserJson addUsername(@Nonnull String username) {
    return new UserJson(id, username, firstname, surname, fullname, currency, photo, photoSmall, friendshipStatus);
  }

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
}
