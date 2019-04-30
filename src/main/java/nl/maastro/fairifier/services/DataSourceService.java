package nl.maastro.fairifier.services;

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

@Service
public class DataSourceService {
    
    private final Logger logger = LoggerFactory.getLogger(DataSourceService.class);
    
    HashMap<String, DataSource> dataSources = new HashMap<>();
    
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
        dataSources.put(name, dataSource);
        logger.info("Added datasource: " + name);
        return dataSource;
    }
    
    public ResultSet performSqlQuery(String dataSourceName, String sqlQuery) throws Exception {
        logger.info("Performing SQL query: " + sqlQuery);
        DataSource dataSource = dataSources.get(dataSourceName);
        if (dataSource == null) {
            throw new Exception("No DataSource found for dataSourceName=" + dataSourceName); 
        }
        Connection connection = dataSource.getConnection();
        connection.setReadOnly(true);
        Statement statement = connection.createStatement();
        return statement.executeQuery(sqlQuery);
    }
    
    public static Map<String, List<String>> toHashMap(ResultSet resultSet) throws SQLException {
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
