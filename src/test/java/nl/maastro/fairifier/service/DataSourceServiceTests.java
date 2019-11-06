package nl.maastro.fairifier.service;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import nl.maastro.fairifier.domain.DatabaseDriver;

@RunWith(SpringRunner.class)
public class DataSourceServiceTests {
    
    private static final String H2_URL = "jdbc:h2:file:./src/test/resources/h2/test";
    private static final String CSV_URL = "jdbc:relique:csv:./testCsv";
    private static final String MSSQL_URL = "URL of some test MSSQL database";
    private static final String POSTGRESQL_URL = "URL of some test POSTGRESQL database";
        
    DataSourceService dataSourceService = new DataSourceService();
    
    @Test
    public void testH2() throws Exception {
        String dataSourceName = "H2_TEST";
        dataSourceService.addDataSource(
                dataSourceName, 
                H2_URL, 
                DatabaseDriver.H2.getDriverClassName(), "sa", null);
        Map<String, List<String>> result = dataSourceService.performSqlQuery(dataSourceName, 
                "SELECT * FROM TEST");
        String age = result.get("AGE").get(2);
        assertEquals("68", age);
    }
    
    @Test
    public void testCsv() throws Exception {
        String dataSourceName = "CSV_TEST";
        dataSourceService.addDataSource(
                dataSourceName, 
                CSV_URL, 
                DatabaseDriver.CSV.getDriverClassName(), null, null);
        Map<String, List<String>> result = dataSourceService.performSqlQuery(dataSourceName, 
                "SELECT * FROM TEST");
        String survivalTime = result.get("Survival.Time.Days").get(4);
        assertEquals("353", survivalTime);
    }
    
    @Ignore
    @Test
    public void testMssql() throws Exception {
        String dataSourceName = "MSSQL_TEST";
        dataSourceService.addDataSource(
                dataSourceName, 
                MSSQL_URL, 
                DatabaseDriver.SQLSERVER.getDriverClassName(), null, null);
        Map<String, List<String>> result = dataSourceService.performSqlQuery(dataSourceName, 
                "SELECT * FROM TEST");
    }
    
    @Ignore
    @Test
    public void testPostgreSql() throws Exception {
        String dataSourceName = "POSTGRESQL_TEST";
        dataSourceService.addDataSource(
                dataSourceName, 
                POSTGRESQL_URL, 
                DatabaseDriver.POSTGRESQL.getDriverClassName(), null, null);
        Map<String, List<String>> result = dataSourceService.performSqlQuery(dataSourceName, 
                "SELECT * FROM TEST");
    }
    
}
