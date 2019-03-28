package nl.maastro.fairifier.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.maastro.fairifier.services.DataSourceService;

@RestController
@RequestMapping("/api")
public class DataSourceController {
    
    private final Logger logger = LoggerFactory.getLogger(DataSourceController.class);
    DataSourceService dataSourceService;
    
    public DataSourceController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }
    
    public ResponseEntity<Void> addDataSource(@RequestBody DataSourceDto dataSourceDto) {
        logger.info("REST request to add new dataSource: " + dataSourceDto);
        try {
            dataSourceService.addDataSource(
                    dataSourceDto.getName(),
                    dataSourceDto.getUrl(),
                    dataSourceDto.getDriver(),
                    dataSourceDto.getUsername(),
                    dataSourceDto.getPassword());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to add dataSource", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("error", e.getLocalizedMessage()).build();
        }
    }
    
    public static class DataSourceDto {
        
        String name;
        String url;
        String driver;
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
        
        public String getDriver() {
            return driver;
        }
        
        public void setDriver(String driver) {
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
    
    
    

}
