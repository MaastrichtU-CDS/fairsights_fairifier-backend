package nl.maastro.fairifier.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("graphdb")
public class GraphDbProperties {
    private String baseUrl;
    private String fairMappingDbId;
    private String fairMappingDbTitle;
    private String fairDataDbId;
    private String fairDataDbTitle;
    private String fairOntoDbId;
    private String fairOntoDbTitle;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getFairMappingDbId() {
        return fairMappingDbId;
    }

    public void setFairMappingDbId(String fairMappingDbId) {
        this.fairMappingDbId = fairMappingDbId;
    }

    public String getFairMappingDbTitle() {
        return fairMappingDbTitle;
    }

    public void setFairMappingDbTitle(String fairMappingDbTitle) {
        this.fairMappingDbTitle = fairMappingDbTitle;
    }

    public String getFairDataDbId() {
        return fairDataDbId;
    }

    public void setFairDataDbId(String fairDataDbId) {
        this.fairDataDbId = fairDataDbId;
    }

    public String getFairDataDbTitle() {
        return fairDataDbTitle;
    }

    public void setFairDataDbTitle(String fairDataDbTitle) {
        this.fairDataDbTitle = fairDataDbTitle;
    }

    public String getFairOntoDbId() {
        return fairOntoDbId;
    }

    public void setFairOntoDbId(String fairOntoDbId) {
        this.fairOntoDbId = fairOntoDbId;
    }

    public String getFairOntoDbTitle() {
        return fairOntoDbTitle;
    }

    public void setFairOntoDbTitle(String fairOntoDbTitle) {
        this.fairOntoDbTitle = fairOntoDbTitle;
    }
}
