package guru.qa.niffler.config;


import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public enum DockerConfig implements Config {
  INSTANCE;

  @Nonnull
  @Override
  public String frontUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String authUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String authJdbcUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String userdataUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String userdataJdbcUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String spendUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String spendJdbcUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String currencyJdbcUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String currencyGrpcAddress() {
    return "";
  }

  @NotNull
  @Override
  public String userdataGrpcAddress() {
    return "";
  }

  @Nonnull
  @Override
  public String githubUrl() {
    return "";
  }
}
