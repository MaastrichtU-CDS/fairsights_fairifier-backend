package nl.maastro.fairifier.web.dto;

import nl.maastro.fairifier.domain.DatabaseDriver;

public class DataSourceDto {
    
    String name;
    String url;
    DatabaseDriver driver;
    String username; 
    String password;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public DatabaseDriver getDriver() {
        return driver;
    }

    public void setDriver(DatabaseDriver driver) {
        this.driver = driver;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String toString() {
        return "DataSourceDto [name=" + name + ", url=" + url + ", driver=" + driver + ", username=" + username
                + ", password=" + password + "]";
    }

}
