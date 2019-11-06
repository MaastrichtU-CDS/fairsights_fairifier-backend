package nl.maastro.fairifier.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nl.maastro.fairifier.config.DataSourceConfigurationProperties.DataSourceProperties;
import nl.maastro.fairifier.domain.DatabaseDriver;
import nl.maastro.fairifier.service.DataSourceService;
import nl.maastro.fairifier.utils.SqlUtilities;
import nl.maastro.fairifier.web.dto.DataSourceDto;

@RestController
@RequestMapping("/api")
public class DataSourceController {
    
    private final Logger logger = LoggerFactory.getLogger(DataSourceController.class);
    
    private DataSourceService dataSourceService;
    
    public DataSourceController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }
    
    @GetMapping("/datasources")
    public ResponseEntity<List<DataSourceDto>> getAllDataSources() {
        logger.info("REST request to get list of all DataSources");
        try {
            HashMap<String, DataSource> dataSources = dataSourceService.getDataSources();
            List<DataSourceDto> dataSourceDtoList = new ArrayList<>();
            for (String dataSourceName : dataSources.keySet()) {
                DataSource dataSource = dataSources.get(dataSourceName);
                DataSourceProperties dataSourceProperties = dataSourceService.getDataSourceProperties(dataSource);
                DatabaseDriver driver = dataSourceService.getDatabaseDriver(dataSourceName);
                DataSourceDto dataSourceDto = new DataSourceDto();
                dataSourceDto.setName(dataSourceName);
                dataSourceDto.setDriver(driver);
                dataSourceDto.setUrl(dataSourceProperties.getUrl());
                dataSourceDtoList.add(dataSourceDto);
            }
            return ResponseEntity.ok(dataSourceDtoList);
        } catch (Exception e) {
            logger.error("Failed to get list of all DataSources", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("error", e.getLocalizedMessage())
                    .build();
        }
    }
    
    @PostMapping("/datasource/add")
    public ResponseEntity<Void> addDataSource(@RequestBody DataSourceProperties dataSourceDto) {
        logger.info("REST request to add new dataSource: " + dataSourceDto);
        try {
            dataSourceService.addDataSource(
                    dataSourceDto.getName(),
                    dataSourceDto.getUrl(),
                    dataSourceDto.getDriverClassName(),
                    dataSourceDto.getUsername(),
                    dataSourceDto.getPassword());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to add dataSource", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("error", e.getLocalizedMessage())
                    .build();
        }
    }
    
    @GetMapping("/datasource/query")
    public ResponseEntity<Map<String, List<String>>> addDataSource(
            @RequestParam String dataSourceName,
            @RequestParam String sqlQuery,
            @RequestParam(required=false) Integer resultsLimit) {
        logger.info("REST request to perform SQL query on DataSource " + dataSourceName);
        try {
            if (resultsLimit != null) {
                DatabaseDriver databaseDriver = dataSourceService.getDatabaseDriver(dataSourceName);
                sqlQuery = SqlUtilities.setResultsLimit(sqlQuery, databaseDriver, resultsLimit);
            }
            Map<String, List<String>> result = dataSourceService.performSqlQuery(dataSourceName, sqlQuery);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to perform SQL query", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("error", e.getLocalizedMessage())
                    .build();
        }
    }

}
