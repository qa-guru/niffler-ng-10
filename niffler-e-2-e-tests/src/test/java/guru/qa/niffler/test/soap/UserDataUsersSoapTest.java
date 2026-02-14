package guru.qa.niffler.test.soap;

import guru.qa.jaxb.userdata.AllUsersRequest;
import guru.qa.jaxb.userdata.Currency;
import guru.qa.jaxb.userdata.CurrentUserRequest;
import guru.qa.jaxb.userdata.FriendshipStatus;
import guru.qa.jaxb.userdata.UpdateUserRequest;
import guru.qa.jaxb.userdata.UserResponse;
import guru.qa.jaxb.userdata.UsersResponse;
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
public class UserDataUsersSoapTest extends BaseSoapTest {

  @Test
  @User
  void currentUserTest(UserJson user) throws Exception {
    CurrentUserRequest cur = new CurrentUserRequest();
    cur.setUsername(user.username());

    final UserResponse currentUserResponse = wsClient.currentUser(cur);

    step("Check that response contains ID (GUID)", () ->
        assertTrue(currentUserResponse.getUser().getId().matches(ID_REGEXP))
    );
    step("Check that response contains username", () ->
        assertEquals(user.username(), currentUserResponse.getUser().getUsername())
    );
    step("Check that response contains default currency (RUB)", () ->
        assertEquals(Currency.RUB, currentUserResponse.getUser().getCurrency())
    );
    step("Check that response contains default friends state (VOID), only for SOAP", () ->
        assertEquals(FriendshipStatus.VOID, currentUserResponse.getUser().getFriendshipStatus())
    );
  }

  @Test
  @User
  void updateUserTest(UserJson user) throws Exception {
    final String fullname = "Pizzly Pizzlyvich";

    UpdateUserRequest uir = new UpdateUserRequest();
    guru.qa.jaxb.userdata.User xmlUser = new guru.qa.jaxb.userdata.User();
    xmlUser.setUsername(user.username());
    xmlUser.setCurrency(Currency.USD);
    xmlUser.setFullname(fullname);
    uir.setUser(xmlUser);

    final UserResponse updateUserInfoResponse = wsClient.updateUserInfo(uir);

    step("Check that response contains ID (GUID)", () ->
        assertTrue(updateUserInfoResponse.getUser().getId().matches(ID_REGEXP))
    );
    step("Check that response contains username", () ->
        assertEquals(user.username(), updateUserInfoResponse.getUser().getUsername())
    );
    step("Check that response contains updated currency (USD)", () ->
        assertEquals(Currency.USD, updateUserInfoResponse.getUser().getCurrency())
    );
    step("Check that response contains updated fullname (Pizzly Pizzlyvich)", () ->
        assertEquals(fullname, updateUserInfoResponse.getUser().getFullname())
    );
  }

  @Test
  @User
  @Order(1)
  void allUsersTest(UserJson user) throws Exception {
    AllUsersRequest aur = new AllUsersRequest();
    aur.setUsername(user.username());

    final UsersResponse allUsersResponse = wsClient.allUsersRequest(aur);

    step("Check that all users list is not empty", () ->
        assertFalse(allUsersResponse.getUser().isEmpty())
    );
  }
}
