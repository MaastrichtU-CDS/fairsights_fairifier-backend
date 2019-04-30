package nl.maastro.fairifier.service;

import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class DataSourceServiceTests {
    
    private static final String H2_URL = "jdbc:h2:file:./src/test/resources/h2/test";
    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String CSV_URL = "jdbc:relique:csv:./src/test/resources/csv";
    private static final String CSV_DRIVER = "org.relique.jdbc.csv.CsvDriver";
    
    private static final String MSSQL_URL = "URL of some test MSSQL database";
    private static final String MSSQL_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    
    private static final String POSTGRESQL_URL = "URL of some test POSTGRESQL database";
    private static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
    
    DataSourceService dataSourceService = new DataSourceService();
    
    @Test
    public void testH2() throws Exception {
        String dataSourceName = "H2_TEST";
        dataSourceService.addDataSource(
                dataSourceName, 
                H2_URL, 
                H2_DRIVER, "sa", null);
        ResultSet resultSet = dataSourceService.performSqlQuery(dataSourceName, 
                "SELECT * FROM TEST");
        Map<String, List<String>> resultMap = DataSourceService.toHashMap(resultSet);
        String age = resultMap.get("AGE").get(2);
        assertEquals("68", age);
    }
    
    @Test
    public void testCsv() throws Exception {
        String dataSourceName = "CSV_TEST";
        dataSourceService.addDataSource(
                dataSourceName, 
                CSV_URL, 
                CSV_DRIVER, null, null);
        ResultSet resultSet = dataSourceService.performSqlQuery(dataSourceName, 
                "SELECT * FROM TEST");
        Map<String, List<String>> resultMap = DataSourceService.toHashMap(resultSet);
        String survivalTime = resultMap.get("Survival.Time.Days").get(4);
        assertEquals("353", survivalTime);
    }
    
    @Ignore
    @Test
    public void testMssql() throws Exception {
        String dataSourceName = "MSSQL_TEST";
        dataSourceService.addDataSource(
                dataSourceName, 
                MSSQL_URL, 
                MSSQL_DRIVER, null, null);
        ResultSet resultSet = dataSourceService.performSqlQuery(dataSourceName, 
                "SELECT * FROM TEST");
        Map<String, List<String>> resultMap = DataSourceService.toHashMap(resultSet);
    }
    
    @Ignore
    @Test
    public void testPostgreSql() throws Exception {
        String dataSourceName = "POSTGRESQL_TEST";
        dataSourceService.addDataSource(
                dataSourceName, 
                POSTGRESQL_URL, 
                POSTGRESQL_DRIVER, null, null);
        ResultSet resultSet = dataSourceService.performSqlQuery(dataSourceName, 
                "SELECT * FROM TEST");
        Map<String, List<String>> resultMap = DataSourceService.toHashMap(resultSet);
    }
    
}
