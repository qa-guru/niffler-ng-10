package guru.qa.niffler.config;

 enum LocalConfig implements Config {
  INSTANCE;

  @Override
  public String frontUrl() {
   return "http://localhost:3000/";
  }

  @Override
  public String spendUrl() {
   return "http://localhost:8093/";
  }

  @Override
  public String spendJdbcUrl() {
   return "jdbc:postgresql://localhost:5432/niffler-spend";
  }

  @Override
  public String githubUrl() {
   return "https://api.github.com/";
  }
 }
