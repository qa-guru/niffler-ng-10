package guru.qa.niffler.test.grpc;

import guru.qa.niffler.grpc.AllUsersRequest;
import guru.qa.niffler.grpc.CurrencyValues;
import guru.qa.niffler.grpc.CurrentUserRequest;
import guru.qa.niffler.grpc.FriendshipStatus;
import guru.qa.niffler.grpc.UpdateUserRequest;
import guru.qa.niffler.grpc.UserResponse;
import guru.qa.niffler.grpc.UsersResponse;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDataUsersGrpcTest extends BaseGrpcTest {

  @Test
  @User
  void currentUserTest(UserJson user) {
    final UserResponse currentUserResponse = userdataStub.currentUser(CurrentUserRequest.newBuilder()
        .setUsername(user.username())
        .build());

    step("Check that response contains ID (GUID)", () ->
        assertTrue(currentUserResponse.getUser().getId().matches(ID_REGEXP))
    );
    step("Check that response contains username", () ->
        assertEquals(user.username(), currentUserResponse.getUser().getUsername())
    );
    step("Check that response contains default currency (RUB)", () ->
        assertEquals(CurrencyValues.RUB, currentUserResponse.getUser().getCurrency())
    );
    step("Check that response contains default friends state (UNSPECIFIED)", () ->
        assertEquals(FriendshipStatus.FRIENDSHIP_STATUS_UNSPECIFIED, currentUserResponse.getUser().getFriendshipStatus())
    );
  }

  @Test
  @User
  void updateUserTest(UserJson user) {
    final String fullname = "Pizzly Pizzlyvich";

    final UserResponse updateUserInfoResponse = userdataStub.updateUser(UpdateUserRequest.newBuilder()
        .setUser(guru.qa.niffler.grpc.User.newBuilder()
            .setId(user.id().toString())
            .setUsername(user.username())
            .setCurrency(CurrencyValues.USD)
            .setFullname(fullname)
            .build())
        .build());

    step("Check that response contains ID (GUID)", () ->
        assertTrue(updateUserInfoResponse.getUser().getId().matches(ID_REGEXP))
    );
    step("Check that response contains username", () ->
        assertEquals(user.username(), updateUserInfoResponse.getUser().getUsername())
    );
    step("Check that response contains updated currency (USD)", () ->
        assertEquals(CurrencyValues.USD, updateUserInfoResponse.getUser().getCurrency())
    );
    step("Check that response contains updated fullname (Pizzly Pizzlyvich)", () ->
        assertEquals(fullname, updateUserInfoResponse.getUser().getFullname())
    );
  }

  @Test
  @User
  @Order(1)
  void allUsersTest(UserJson user) {
    final UsersResponse allUsersResponse = userdataStub.allUsers(AllUsersRequest.newBuilder()
        .setUsername(user.username())
        .setPageInfo(DEFAULT_PAGE_INFO)
        .build());

    step("Check that all users list is not empty", () ->
        assertFalse(allUsersResponse.getUsersList().isEmpty())
    );
  }
}
