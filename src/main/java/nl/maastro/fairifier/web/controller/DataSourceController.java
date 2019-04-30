package nl.maastro.fairifier.web.controller;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

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

import nl.maastro.fairifier.services.DataSourceService;
import nl.maastro.fairifier.web.dto.DataSourceDto;

@RestController
@RequestMapping("/api")
public class DataSourceController {
    
    private final Logger logger = LoggerFactory.getLogger(DataSourceController.class);
    DataSourceService dataSourceService;
    
    public DataSourceController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }
    
    @PostMapping("/datasource/add")
    public ResponseEntity<Void> addDataSource(@RequestBody DataSourceDto dataSourceDto) {
        logger.info("REST request to add new dataSource: " + dataSourceDto);
        try {
            dataSourceService.addDataSource(
                    dataSourceDto.getName(),
                    dataSourceDto.getUrl(),
                    dataSourceDto.getDriver().getDriverClassName(),
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
            @RequestParam String sqlQuery) {
        logger.info("REST request to perform SQL query on DataSource " + dataSourceName);
        try {
            ResultSet resultSet = dataSourceService.performSqlQuery(dataSourceName, sqlQuery);
            Map<String, List<String>> resultMap = DataSourceService.toHashMap(resultSet);
            return ResponseEntity.ok(resultMap);
        } catch (Exception e) {
            logger.error("Failed to perform SQL query", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("error", e.getLocalizedMessage())
                    .build();
        }
    }

}
