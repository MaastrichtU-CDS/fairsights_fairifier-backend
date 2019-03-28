package nl.maastro.fairifier.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

@Service
public class DataSourceService {
    
    private final Logger logger = LoggerFactory.getLogger(DataSourceService.class);
    
    HashMap<String, DataSource> dataSources = new HashMap<>();
    
    public DataSource addDataSource(String name, String url, String driver, String username, String password) {
        DataSource dataSource = dataSources.get(name);
        if (dataSource != null) {
            logger.warn("DataSource with name '" + name + "' already exists; dataSource will be overwritten");
        }
        dataSource = DataSourceBuilder.create()
                .url(url)
                .driverClassName(driver)
                .username(username)
                .password(password)
                .build();
        dataSources.put(name, dataSource);
        return dataSource;
    }
    
    public ResultSet performSqlQuery(String dataSourceName, String sqlQuery) throws SQLException {
        DataSource dataSource = dataSources.get(dataSourceName);
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        return statement.executeQuery(sqlQuery);
    }
        
}
