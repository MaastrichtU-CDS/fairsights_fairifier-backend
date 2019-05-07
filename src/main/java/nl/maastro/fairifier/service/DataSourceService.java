package nl.maastro.fairifier.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

import nl.maastro.fairifier.domain.DatabaseDriver;

@Service
public class DataSourceService {
    
    private final Logger logger = LoggerFactory.getLogger(DataSourceService.class);
    
    HashMap<String, DataSource> dataSources = new HashMap<>();
    
    public HashMap<String, DataSource> getDataSources() {
        return this.dataSources;
    }
    
    public DataSource addDataSource(String name, String url, String driver, 
            String username, String password) throws Exception {
        DataSource dataSource = dataSources.get(name);
        if (dataSource != null) {
            throw new Exception("DataSource with name " + name + " already exists");
        }
        dataSource = DataSourceBuilder.create()
                .url(url)
                .driverClassName(driver)
                .username(username)
                .password(password)
                .build();
        validateDataSource(dataSource);
        dataSources.put(name, dataSource);
        logger.info("Added datasource: " + name);
        return dataSource;
    }
    
    public Map<String, List<String>> performSqlQuery(String dataSourceName, String sqlQuery) throws Exception {
        DataSource dataSource = dataSources.get(dataSourceName);
        if (dataSource == null) {
            throw new Exception("No DataSource found for dataSourceName=" + dataSourceName); 
        }
        return performSqlQuery(dataSource, sqlQuery);
    }
    
    private Map<String, List<String>> performSqlQuery(DataSource dataSource, String sqlQuery) throws Exception {
        logger.info("Performing SQL query: " + sqlQuery);
        try (Connection connection = dataSource.getConnection(); ) {
            connection.setReadOnly(true);
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(sqlQuery)) {
                    return toHashMap(resultSet);
                }
            }
        }
    }
    
    private void validateDataSource(DataSource dataSource) throws Exception {
        logger.info("Validating DataSource: " + dataSource);
        try {
            DatabaseDriver driver = getDatabaseDriver(dataSource);
            String validationQuery = driver.getValidationQuery();
            if (validationQuery != null) {
                performSqlQuery(dataSource, validationQuery);
            }
        } catch (Exception e) {
            throw new Exception("Invalid DataSource", e);
        }
    }
    
    public DatabaseDriver getDatabaseDriver(String dataSourceName) throws Exception {
        DataSource dataSource = dataSources.get(dataSourceName);
        if (dataSource == null) {
            throw new Exception("No DataSource found for dataSourceName=" + dataSourceName); 
        }
        return getDatabaseDriver(dataSource);
    }
    
    private DatabaseDriver getDatabaseDriver(DataSource dataSource) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            return DatabaseDriver.fromProductName(databaseProductName);
        }
    }
    
    private static Map<String, List<String>> toHashMap(ResultSet resultSet) throws SQLException {
        int numberOfColumns = resultSet.getMetaData().getColumnCount();
        Map<String, List<String>> resultHashMap = new HashMap<>();
        for (int columnIndex = 1; columnIndex <= numberOfColumns; columnIndex++) {
            String columnLabel = resultSet.getMetaData().getColumnLabel(columnIndex);
            resultHashMap.put(columnLabel, new ArrayList<String>());
        }
        while (resultSet.next()) {
            for (String columnLabel : resultHashMap.keySet()) {
                resultHashMap.get(columnLabel).add(
                        resultSet.getString(columnLabel));
            }
        }
        return resultHashMap;
    }
    
}
