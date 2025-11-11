package guru.qa.niffler.config;

enum LocalConfig implements Config {
    INSTANCE;

    @Override
    public String frontUrl() {
        return "http://localhost:3000/";
    }

    @Override
    public String authUrl() {
        return "http://localhost:9000/";
    }

    @Override
    public String authJdbcUrl() {
        return "jdbc:postgresql://localhost:5432/niffler-auth";
    }

    @Override
    public String gatewayUrl() {
        return "http://localhost:8090/";
    }

    @Override
    public String userdataUrl() {
        return "http://localhost:8089/";
    }

    @Override
    public String userdataJdbcUrl() {
        return "jdbc:postgresql://localhost:5432/niffler-userdata";
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
    public String currencyJdbcUrl() {
        return "jdbc:postgresql://localhost:5432/niffler-currency";
    }

    @Override
    public String githubUrl() {
        return "https://api.github.com/";
    }
}
