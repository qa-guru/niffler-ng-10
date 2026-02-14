package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.jaxb.userdata.Currency;
import guru.qa.jaxb.userdata.User;
import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.projection.UserWithStatus;
import jakarta.annotation.Nonnull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static guru.qa.niffler.grpc.FriendshipStatus.FRIENDSHIP_STATUS_UNSPECIFIED;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJsonBulk(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("fullname")
    String fullname,
    @JsonProperty("currency")
    CurrencyValues currency,
    @JsonProperty("photoSmall")
    String photoSmall,
    @JsonProperty("friendshipStatus")
    FriendshipStatus friendshipStatus) implements IUserJson {

  @Override
  public String photo() {
    return null;
  }

  @Override
  public String firstname() {
    return null;
  }

  @Override
  public String surname() {
    return null;
  }

  public @Nonnull User toJaxbUser() {
    User jaxbUser = new User();
    jaxbUser.setId(id != null ? id.toString() : null);
    jaxbUser.setUsername(username);
    jaxbUser.setFullname(fullname);
    jaxbUser.setCurrency(Currency.valueOf(currency.name()));
    jaxbUser.setPhotoSmall(photoSmall);
    jaxbUser.setFriendshipStatus(friendshipStatus() == null ?
        guru.qa.jaxb.userdata.FriendshipStatus.VOID :
        guru.qa.jaxb.userdata.FriendshipStatus.valueOf(friendshipStatus().name()));
    return jaxbUser;
  }

  public static @Nonnull UserJsonBulk fromJaxb(@Nonnull User jaxbUser) {
    return new UserJsonBulk(
        jaxbUser.getId() != null ? UUID.fromString(jaxbUser.getId()) : null,
        jaxbUser.getUsername(),
        jaxbUser.getFullname(),
        CurrencyValues.valueOf(jaxbUser.getCurrency().name()),
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
    if (fullname != null) {
      grpcUserBuilder.setFullname(fullname);
    }
    if (currency != null) {
      grpcUserBuilder.setCurrency(guru.qa.niffler.grpc.CurrencyValues.valueOf(currency.name()));
    }
    if (photoSmall != null) {
      grpcUserBuilder.setPhotoSmall(photoSmall);
    }
    grpcUserBuilder.setFriendshipStatus(friendshipStatus() == null ?
        FRIENDSHIP_STATUS_UNSPECIFIED :
        guru.qa.niffler.grpc.FriendshipStatus.valueOf(friendshipStatus().name()));
    return grpcUserBuilder.build();
  }

  public static @Nonnull UserJsonBulk fromGrpc(@Nonnull guru.qa.niffler.grpc.User grpcUser) {
    return new UserJsonBulk(
        UUID.fromString(grpcUser.getId()),
        grpcUser.getUsername(),
        grpcUser.getFullname(),
        CurrencyValues.valueOf(grpcUser.getCurrency().name()),
        grpcUser.getPhotoSmall(),
        grpcUser.getFriendshipStatus() != FRIENDSHIP_STATUS_UNSPECIFIED
            ? FriendshipStatus.valueOf(grpcUser.getFriendshipStatus().name())
            : null
    );
  }

  public static @Nonnull UserJsonBulk fromFriendEntityProjection(@Nonnull UserWithStatus projection) {
    return new UserJsonBulk(
        projection.id(),
        projection.username(),
        projection.fullname(),
        projection.currency(),
        projection.photoSmall() != null && projection.photoSmall().length > 0 ? new String(projection.photoSmall(), StandardCharsets.UTF_8) : null,
        projection.status() == guru.qa.niffler.data.FriendshipStatus.PENDING ? FriendshipStatus.INVITE_RECEIVED : FriendshipStatus.FRIEND
    );
  }

  public static @Nonnull UserJsonBulk fromUserEntityProjection(@Nonnull UserWithStatus projection) {
    return new UserJsonBulk(
        projection.id(),
        projection.username(),
        projection.fullname(),
        projection.currency(),
        projection.photoSmall() != null && projection.photoSmall().length > 0 ? new String(projection.photoSmall(), StandardCharsets.UTF_8) : null,
        projection.status() == guru.qa.niffler.data.FriendshipStatus.PENDING ? FriendshipStatus.INVITE_SENT : null
    );
  }
}
