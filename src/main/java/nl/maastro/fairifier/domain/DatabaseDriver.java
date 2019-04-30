package nl.maastro.fairifier.domain;

public enum DatabaseDriver {
    
    H2("org.h2.Driver"),
    MSSQL("com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    POSTGRESQL("org.postgresql.Driver");
    
    private final String driverClassName;
    
    DatabaseDriver(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

}
