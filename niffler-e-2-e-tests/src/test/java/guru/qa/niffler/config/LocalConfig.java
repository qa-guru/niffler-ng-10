package guru.qa.niffler.config;


import javax.annotation.Nonnull;

enum LocalConfig implements Config {
  INSTANCE;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://localhost:3000/";
  }

  @Nonnull
  @Override
  public String authUrl() {
    return "http://localhost:9000/";
  }

  @Nonnull
  @Override
  public String authJdbcUrl() {
    return "jdbc:postgresql://localhost:5432/niffler-auth";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "http://localhost:8090/";
  }

  @Nonnull
  @Override
  public String userdataUrl() {
    return "http://localhost:8089/";
  }

  @Nonnull
  @Override
  public String userdataJdbcUrl() {
    return "jdbc:postgresql://localhost:5432/niffler-userdata";
  }

  @Nonnull
  @Override
  public String spendUrl() {
    return "http://localhost:8093/";
  }

  @Nonnull
  @Override
  public String spendJdbcUrl() {
    return "jdbc:postgresql://localhost:5432/niffler-spend";
  }

  @Nonnull
  @Override
  public String currencyJdbcUrl() {
    return "jdbc:postgresql://localhost:5432/niffler-currency";
  }

  @Nonnull
  @Override
  public String githubUrl() {
    return "https://api.github.com/";
  }
}
