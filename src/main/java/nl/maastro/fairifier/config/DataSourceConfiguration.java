package nl.maastro.fairifier.config;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import nl.maastro.fairifier.config.DataSourceConfigurationProperties.DataSourceProperties;
import nl.maastro.fairifier.services.DataSourceService;

@Configuration
@EnableConfigurationProperties(DataSourceConfigurationProperties.class)
public class DataSourceConfiguration {
    
    private DataSourceService dataSourceService;
    private DataSourceConfigurationProperties dataSourceConfigurationProperties;

    public DataSourceConfiguration(
            DataSourceService dataSourceService,
            DataSourceConfigurationProperties dataSourceConfigurationProperties) {
        this.dataSourceService = dataSourceService;
        this.dataSourceConfigurationProperties = dataSourceConfigurationProperties;
    }
    
    @PostConstruct
    public void initializeDataSources() throws Exception {
        for (DataSourceProperties dataSourceProperties : dataSourceConfigurationProperties.getDataSources())  {
            dataSourceService.addDataSource(
                    dataSourceProperties.getName(),
                    dataSourceProperties.getUrl(),
                    dataSourceProperties.getDriverClassName(),
                    dataSourceProperties.getUsername(),
                    dataSourceProperties.getPassword());
        }
    }

}
