package guru.qa.niffler.test.rest;

import guru.qa.niffler.service.AuthApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;

public class RegistrationTest {

  private final AuthApiClient authApiClient = new AuthApiClient();

  @Test
  @Disabled
  void newUserShouldRegisteredByApiCall() throws IOException {
    final String randomUsername = RandomDataUtils.randomUsername();

    final Response<Void> response = authApiClient.register(randomUsername, "12345");
    Assertions.assertEquals(201, response.code());
  }
}
