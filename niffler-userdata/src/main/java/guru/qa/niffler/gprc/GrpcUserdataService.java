package guru.qa.niffler.gprc;


import com.google.protobuf.Empty;
import guru.qa.niffler.gprc.util.GrpcPaginationAndSort;
import guru.qa.niffler.grpc.AcceptInvitationRequest;
import guru.qa.niffler.grpc.AllUsersRequest;
import guru.qa.niffler.grpc.CurrentUserRequest;
import guru.qa.niffler.grpc.DeclineInvitationRequest;
import guru.qa.niffler.grpc.FriendsRequest;
import guru.qa.niffler.grpc.NifflerUserdataServiceGrpc;
import guru.qa.niffler.grpc.PageInfo;
import guru.qa.niffler.grpc.RegisterPushTokenRequest;
import guru.qa.niffler.grpc.RemoveFriendRequest;
import guru.qa.niffler.grpc.SendInvitationRequest;
import guru.qa.niffler.grpc.UpdateUserRequest;
import guru.qa.niffler.grpc.UserResponse;
import guru.qa.niffler.grpc.UsersResponse;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserJsonBulk;
import guru.qa.niffler.service.PushTokenService;
import guru.qa.niffler.service.UserService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

@GrpcService
public class GrpcUserdataService extends NifflerUserdataServiceGrpc.NifflerUserdataServiceImplBase {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcUserdataService.class);

  private final UserService userService;
  private final PushTokenService tokenService;

  @Autowired
  public GrpcUserdataService(UserService userService, PushTokenService tokenService) {
    this.userService = userService;
    this.tokenService = tokenService;
  }

  @Override
  public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
    responseObserver.onNext(
        UserResponse.newBuilder()
            .setUser(
                userService.update(UserJson.fromGrpc(request.getUser()))
                    .toGrpcUser()
            )
            .build()
    );
    responseObserver.onCompleted();
  }

  @Override
  public void currentUser(CurrentUserRequest request, StreamObserver<UserResponse> responseObserver) {
    responseObserver.onNext(
        UserResponse.newBuilder()
            .setUser(
                userService.getCurrentUser(request.getUsername())
                    .toGrpcUser()
            )
            .build()
    );
    responseObserver.onCompleted();
  }

  @Override
  public void allUsers(AllUsersRequest request, StreamObserver<UsersResponse> responseObserver) {
    final PageInfo pageInfo = request.getPageInfo();
    final Page<UserJsonBulk> allUsersPage = userService.allUsers(
        request.getUsername(),
        new GrpcPaginationAndSort(
            pageInfo.getPage(),
            pageInfo.getSize(),
            pageInfo.getSortList()
        ).pageable(),
        request.getSearchQuery()
    );

    responseObserver.onNext(
        UsersResponse.newBuilder()
            .addAllUsers(allUsersPage.getContent().stream().map(UserJsonBulk::toGrpcUser).toList())
            .setTotalElements(allUsersPage.getTotalElements())
            .setTotalPages(allUsersPage.getTotalPages())
            .build()
    );
    responseObserver.onCompleted();
  }

  @Override
  public void sendInvitation(SendInvitationRequest request, StreamObserver<UserResponse> responseObserver) {
    responseObserver.onNext(
        UserResponse.newBuilder()
            .setUser(
                userService.createFriendshipRequest(
                    request.getUsername(),
                    request.getFriendToBeRequested()
                ).toGrpcUser()
            )
            .build()
    );
    responseObserver.onCompleted();
  }

  @Override
  public void acceptInvitation(AcceptInvitationRequest request, StreamObserver<UserResponse> responseObserver) {
    responseObserver.onNext(
        UserResponse.newBuilder()
            .setUser(
                userService.acceptFriendshipRequest(
                    request.getUsername(),
                    request.getFriendToBeAdded()
                ).toGrpcUser()
            )
            .build()
    );
    responseObserver.onCompleted();
  }

  @Override
  public void declineInvitation(DeclineInvitationRequest request, StreamObserver<UserResponse> responseObserver) {
    responseObserver.onNext(
        UserResponse.newBuilder()
            .setUser(
                userService.declineFriendshipRequest(
                    request.getUsername(),
                    request.getInvitationToBeDeclined()
                ).toGrpcUser()
            )
            .build()
    );
    responseObserver.onCompleted();
  }

  @Override
  public void friends(FriendsRequest request, StreamObserver<UsersResponse> responseObserver) {
    final PageInfo pageInfo = request.getPageInfo();
    final Page<UserJsonBulk> allFriendsPage = userService.friends(
        request.getUsername(),
        new GrpcPaginationAndSort(
            pageInfo.getPage(),
            pageInfo.getSize(),
            pageInfo.getSortList()
        ).pageable(),
        request.getSearchQuery()
    );

    responseObserver.onNext(
        UsersResponse.newBuilder()
            .addAllUsers(allFriendsPage.getContent().stream().map(UserJsonBulk::toGrpcUser).toList())
            .setTotalElements(allFriendsPage.getTotalElements())
            .setTotalPages(allFriendsPage.getTotalPages())
            .build()
    );
    responseObserver.onCompleted();
  }

  @Override
  public void removeFriend(RemoveFriendRequest request, StreamObserver<Empty> responseObserver) {
    userService.removeFriend(
        request.getUsername(),
        request.getFriendToBeRemoved()
    );
    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }

  @Override
  public void registerToken(RegisterPushTokenRequest request, StreamObserver<Empty> responseObserver) {
    tokenService.upsert(request.getUsername(), request.getToken(), request.getUserAgent());
    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
