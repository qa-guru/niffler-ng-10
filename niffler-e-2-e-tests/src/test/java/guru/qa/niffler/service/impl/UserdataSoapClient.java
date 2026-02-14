package guru.qa.niffler.service.impl;

import guru.qa.jaxb.userdata.AcceptInvitationRequest;
import guru.qa.jaxb.userdata.AllUsersPageRequest;
import guru.qa.jaxb.userdata.AllUsersRequest;
import guru.qa.jaxb.userdata.CurrentUserRequest;
import guru.qa.jaxb.userdata.DeclineInvitationRequest;
import guru.qa.jaxb.userdata.FriendsPageRequest;
import guru.qa.jaxb.userdata.FriendsRequest;
import guru.qa.jaxb.userdata.RemoveFriendRequest;
import guru.qa.jaxb.userdata.SendInvitationRequest;
import guru.qa.jaxb.userdata.UpdateUserRequest;
import guru.qa.jaxb.userdata.UserResponse;
import guru.qa.jaxb.userdata.UsersResponse;
import guru.qa.niffler.api.UserdataSoapApi;
import guru.qa.niffler.api.core.converter.SoapConverterFactory;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class UserdataSoapClient extends RestClient {

  private static final Config CFG = Config.getInstance();
  private final UserdataSoapApi userdataSoapApi;

  public UserdataSoapClient() {
    super(CFG.userdataUrl(), false, SoapConverterFactory.create("niffler-userdata"), HttpLoggingInterceptor.Level.BODY);
    this.userdataSoapApi = create(UserdataSoapApi.class);
  }

  @Step("Send SOAP POST('/ws') request to niffler-userdata, endpoint: currentUserRequest")
  @Nullable
  public UserResponse currentUser(CurrentUserRequest currentUserRequest) {
    return executeForBody(
        userdataSoapApi.currentUserRequest(currentUserRequest),
        200
    );
  }

  @Step("Send SOAP POST('/ws') request to niffler-userdata, endpoint: updateUserRequest")
  @Nullable
  public UserResponse updateUserInfo(UpdateUserRequest updateUserRequest) {
    return executeForBody(
        userdataSoapApi.updateUserRequest(updateUserRequest),
        200
    );
  }

  @Step("Send SOAP POST('/ws') request to niffler-userdata, endpoint: allUsersRequest")
  @Nullable
  public UsersResponse allUsersRequest(AllUsersRequest allUsersRequest) {
    return executeForBody(
        userdataSoapApi.allUsersRequest(allUsersRequest),
        200
    );
  }

  @Step("Send SOAP POST('/ws') request to niffler-userdata, endpoint: allUsersPageRequest")
  @Nullable
  public UsersResponse allUsersPageRequest(AllUsersPageRequest allUsersPageRequest) {
    return executeForBody(
        userdataSoapApi.allUsersPageRequest(allUsersPageRequest),
        200
    );
  }

  @Step("Send SOAP POST('/ws') request to niffler-userdata, endpoint: friendsRequest")
  @Nullable
  public UsersResponse friendsRequest(FriendsRequest friendsRequest) {
    return executeForBody(
        userdataSoapApi.friendsRequest(friendsRequest),
        200
    );
  }

  @Step("Send SOAP POST('/ws') request to niffler-userdata, endpoint: friendsPageRequest")
  @Nullable
  public UsersResponse friendsPageRequest(FriendsPageRequest friendsPageRequest) {
    return executeForBody(
        userdataSoapApi.friendsPageRequest(friendsPageRequest),
        200
    );
  }

  @Step("Send SOAP POST('/ws') request to niffler-userdata, endpoint: removeFriendRequest")
  public void removeFriendRequest(RemoveFriendRequest removeFriendRequest) {
    executeNoBody(
        userdataSoapApi.removeFriendRequest(removeFriendRequest),
        202
    );
  }

  @Step("Send SOAP POST('/ws') request to niffler-userdata, endpoint: acceptInvitationRequest")
  @Nullable
  public UserResponse acceptInvitationRequest(AcceptInvitationRequest acceptInvitationRequest) {
    return executeForBody(
        userdataSoapApi.acceptInvitationRequest(acceptInvitationRequest),
        200
    );
  }

  @Step("Send SOAP POST('/ws') request to niffler-userdata, endpoint: declineInvitationRequest")
  @Nullable
  public UserResponse declineInvitationRequest(DeclineInvitationRequest declineInvitationRequest) {
    return executeForBody(
        userdataSoapApi.declineInvitationRequest(declineInvitationRequest),
        200
    );
  }

  @Step("Send SOAP POST('/ws') request to niffler-userdata, endpoint: sendInvitationRequest")
  @Nullable
  public UserResponse sendInvitationRequest(SendInvitationRequest sendInvitationRequest) {
    return executeForBody(
        userdataSoapApi.sendInvitationRequest(sendInvitationRequest),
        200
    );
  }
}
