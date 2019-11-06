package nl.maastro.fairifier.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

import nl.maastro.fairifier.config.DataSourceConfigurationProperties.DataSourceProperties;
import nl.maastro.fairifier.domain.DatabaseDriver;

@Service
public class DataSourceService {
    
    private final Logger logger = LoggerFactory.getLogger(DataSourceService.class);
    
    HashMap<String, DataSource> dataSources = new HashMap<>();
    HashMap<DataSource, DataSourceProperties> dataSourceProperties = new HashMap<>();
    
    public HashMap<String, DataSource> getDataSources() {
        return this.dataSources;
    }
    
    public DataSource addDataSource(String name, String url, String driverClassName, 
            String username, String password) throws Exception {
        DataSourceProperties properties = new DataSourceProperties();
        properties.setName(name);
        properties.setUrl(url);
        properties.setDriverClassName(driverClassName);
        properties.setUsername(username);
        properties.setPassword(password);
        return addDataSource(properties);
    }
    
    public DataSource addDataSource(DataSourceProperties dataSourceProperties) throws Exception {
        String name = dataSourceProperties.getName();
        DataSource dataSource = dataSources.get(name);
        if (dataSource != null) {
            throw new Exception("DataSource with name " + name + " already exists");
        }
        dataSource = DataSourceBuilder.create()
                .url(dataSourceProperties.getUrl())
                .driverClassName(dataSourceProperties.getDriverClassName())
                .username(dataSourceProperties.getUsername())
                .password(dataSourceProperties.getPassword())
                .build();
        validateDataSource(dataSource, dataSourceProperties);
        this.dataSources.put(name, dataSource);
        this.dataSourceProperties.put(dataSource, dataSourceProperties);
        logger.info("Added datasource: " + name);
        return dataSource;
    }
    
    public DataSource getDataSource(String dataSourceName) throws Exception {
        DataSource dataSource = dataSources.get(dataSourceName);
        if (dataSource == null) {
            throw new Exception("No DataSource found for dataSourceName=" + dataSourceName); 
        }
        return dataSource;
    }
    
    public DataSourceProperties getDataSourceProperties(DataSource dataSource) {
        return this.dataSourceProperties.get(dataSource);
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
    
    private void validateDataSource(DataSource dataSource, 
            DataSourceProperties dataSourceProperties) throws Exception {
        logger.info("Validating DataSource: " + dataSource);
        try {
            DatabaseDriver driver = DatabaseDriver.fromDriverClassName(
                    dataSourceProperties.getDriverClassName());
            String validationQuery = driver.getValidationQuery();
            if (validationQuery != null) {
                performSqlQuery(dataSource, validationQuery);
            }
        } catch (Exception e) {
            throw new Exception("Invalid DataSource", e);
        }
    }
    
    public DatabaseDriver getDatabaseDriver(String dataSourceName) throws Exception {
        DataSource dataSource = getDataSource(dataSourceName);
        return getDatabaseDriver(dataSource);
    }

    public DatabaseDriver getDatabaseDriver(DataSource dataSource) {
        DataSourceProperties dataSourceProperties = this.dataSourceProperties.get(dataSource);
        return DatabaseDriver.fromDriverClassName(dataSourceProperties.getDriverClassName());
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
