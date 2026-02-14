package guru.qa.niffler.api;

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
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserdataSoapApi {

  @Headers({
      "Content-Type: text/xml",
      "Accept-Charset: utf-8"
  })
  @POST("ws")
  Call<UserResponse> currentUserRequest(@Body CurrentUserRequest currentUserRequest);

  @Headers({
      "Content-Type: text/xml",
      "Accept-Charset: utf-8"
  })
  @POST("ws")
  Call<UserResponse> updateUserRequest(@Body UpdateUserRequest updateUserRequest);

  @Headers({
      "Content-Type: text/xml",
      "Accept-Charset: utf-8"
  })
  @POST("ws")
  Call<UsersResponse> allUsersRequest(@Body AllUsersRequest allUsersRequest);

  @Headers({
      "Content-Type: text/xml",
      "Accept-Charset: utf-8"
  })
  @POST("ws")
  Call<UsersResponse> allUsersPageRequest(@Body AllUsersPageRequest allUsersPageRequest);

  @Headers({
      "Content-Type: text/xml",
      "Accept-Charset: utf-8"
  })
  @POST("ws")
  Call<UsersResponse> friendsRequest(@Body FriendsRequest friendsRequest);

  @Headers({
      "Content-Type: text/xml",
      "Accept-Charset: utf-8"
  })
  @POST("ws")
  Call<UsersResponse> friendsPageRequest(@Body FriendsPageRequest friendsPageRequest);

  @Headers({
      "Content-Type: text/xml",
      "Accept-Charset: utf-8"
  })
  @POST("ws")
  Call<Void> removeFriendRequest(@Body RemoveFriendRequest removeFriendRequest);

  @Headers({
      "Content-Type: text/xml",
      "Accept-Charset: utf-8"
  })
  @POST("ws")
  Call<UserResponse> acceptInvitationRequest(@Body AcceptInvitationRequest acceptInvitationRequest);

  @Headers({
      "Content-Type: text/xml",
      "Accept-Charset: utf-8"
  })
  @POST("ws")
  Call<UserResponse> declineInvitationRequest(@Body DeclineInvitationRequest declineInvitationRequest);

  @Headers({
      "Content-Type: text/xml",
      "Accept-Charset: utf-8"
  })
  @POST("ws")
  Call<UserResponse> sendInvitationRequest(@Body SendInvitationRequest sendInvitationRequest);
}
