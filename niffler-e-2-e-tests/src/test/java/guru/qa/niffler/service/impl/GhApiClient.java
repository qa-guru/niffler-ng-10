package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GhApi;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class GhApiClient extends RestClient {

  private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

  private final GhApi ghApi;

  public GhApiClient() {
    super(CFG.githubUrl());
    this.ghApi = create(GhApi.class);
  }

  @Step("Get state of Github issue by given id: {issueNumber}")
  @Nonnull
  public String issueState(String issueNumber) {
    return executeForBody(
        ghApi.issue(
            "Bearer " + System.getenv(GH_TOKEN_ENV),
            issueNumber
        ),
        200
    ).get("state").asText();
  }
}
