package guru.qa.niffler.service.grpc;

import guru.qa.niffler.grpc.AcceptInvitationRequest;
import guru.qa.niffler.grpc.AllUsersRequest;
import guru.qa.niffler.grpc.CurrentUserRequest;
import guru.qa.niffler.grpc.DeclineInvitationRequest;
import guru.qa.niffler.grpc.FriendsRequest;
import guru.qa.niffler.grpc.NifflerUserdataServiceGrpc;
import guru.qa.niffler.grpc.RegisterPushTokenRequest;
import guru.qa.niffler.grpc.RemoveFriendRequest;
import guru.qa.niffler.grpc.SendInvitationRequest;
import guru.qa.niffler.grpc.UpdateUserRequest;
import guru.qa.niffler.grpc.UsersResponse;
import guru.qa.niffler.model.FcmTokenJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserDataClient;
import guru.qa.niffler.service.utils.GrpcPaginationAndSort;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@Component
@ParametersAreNonnullByDefault
@ConditionalOnProperty(prefix = "niffler-userdata", name = "client", havingValue = "grpc")
public class GrpcUserDataClient implements UserDataClient {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcCurrencyClient.class);

  @GrpcClient("grpcUserdataClient")
  private NifflerUserdataServiceGrpc.NifflerUserdataServiceBlockingStub userdataStub;

  @Nonnull
  @Override
  public UserJson currentUser(String username) {
    return UserJson.fromGrpc(userdataStub.currentUser(
        CurrentUserRequest.newBuilder()
            .setUsername(username)
            .build()
    ).getUser());
  }

  @Nonnull
  @Override
  public UserJson updateUserInfo(UserJson user) {
    return UserJson.fromGrpc(userdataStub.updateUser(
        UpdateUserRequest.newBuilder()
            .setUser(user.toGrpcUser())
            .build()
    ).getUser());
  }

  @Deprecated
  @Nonnull
  @Override
  public List<UserJson> allUsers(String username, @Nullable String searchQuery) {
    throw new UnsupportedOperationException("Not implemented with gRPC");
  }

  @Nonnull
  @Override
  public Page<UserJson> allUsersV2(String username, Pageable pageable, @Nullable String searchQuery) {
    final AllUsersRequest.Builder reqBuilder = AllUsersRequest.newBuilder()
        .setUsername(username)
        .setPageInfo(new GrpcPaginationAndSort(pageable).pageInfo());

    if (searchQuery != null) {
      reqBuilder.setSearchQuery(searchQuery);
    }

    final UsersResponse response = userdataStub.allUsers(reqBuilder.build());

    return new PageImpl<>(
        response.getUsersList().stream().map(UserJson::fromGrpc).toList(),
        pageable,
        response.getTotalElements()
    );
  }

  @jakarta.annotation.Nonnull
  @Override
  public PagedModel<UserJson> allUsersV3(String username, Pageable pageable, @Nullable String searchQuery) {
    return new PagedModel<>(
        allUsersV2(username, pageable, searchQuery)
    );
  }

  @Deprecated
  @Nonnull
  @Override
  public List<UserJson> friends(String username, @Nullable String searchQuery) {
    throw new UnsupportedOperationException("Not implemented with gRPC");
  }

  @Nonnull
  @Override
  public Page<UserJson> friendsV2(String username, Pageable pageable, @Nullable String searchQuery) {
    final FriendsRequest.Builder reqBuilder = FriendsRequest.newBuilder()
        .setUsername(username)
        .setPageInfo(new GrpcPaginationAndSort(pageable).pageInfo());

    if (searchQuery != null) {
      reqBuilder.setSearchQuery(searchQuery);
    }

    final UsersResponse response = userdataStub.friends(reqBuilder.build());
    return new PageImpl<>(
        response.getUsersList().stream().map(UserJson::fromGrpc).toList(),
        pageable,
        response.getTotalElements()
    );
  }

  @Nonnull
  @Override
  public PagedModel<UserJson> friendsV3(String username, Pageable pageable, @Nullable String searchQuery) {
    return new PagedModel<>(
        friendsV2(username, pageable, searchQuery)
    );
  }

  @Nonnull
  @Override
  public UserJson sendInvitation(String username, String targetUsername) {
    return UserJson.fromGrpc(userdataStub.sendInvitation(
        SendInvitationRequest.newBuilder()
            .setUsername(username)
            .setFriendToBeRequested(targetUsername)
            .build()
    ).getUser());
  }

  @Nonnull
  @Override
  public UserJson acceptInvitation(String username, String targetUsername) {
    return UserJson.fromGrpc(userdataStub.acceptInvitation(
        AcceptInvitationRequest.newBuilder()
            .setUsername(username)
            .setFriendToBeAdded(targetUsername)
            .build()
    ).getUser());
  }

  @Nonnull
  @Override
  public UserJson declineInvitation(String username, String targetUsername) {
    return UserJson.fromGrpc(userdataStub.declineInvitation(
        DeclineInvitationRequest.newBuilder()
            .setUsername(username)
            .setInvitationToBeDeclined(targetUsername)
            .build()
    ).getUser());
  }

  @Override
  public void removeFriend(String username, String targetUsername) {
    userdataStub.removeFriend(RemoveFriendRequest.newBuilder()
        .setUsername(username)
        .setFriendToBeRemoved(targetUsername)
        .build());
  }

  @Override
  public void registerToken(FcmTokenJson fcmTokenJson) {
    userdataStub.registerToken(RegisterPushTokenRequest.newBuilder()
        .setUsername(fcmTokenJson.username())
        .setToken(fcmTokenJson.token())
        .setUserAgent(fcmTokenJson.userAgent())
        .build());
  }
}
