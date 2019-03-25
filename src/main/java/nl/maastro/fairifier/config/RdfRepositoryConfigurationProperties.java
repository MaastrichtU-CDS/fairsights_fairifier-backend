package nl.maastro.fairifier.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fairifier.rdf-repositories")
public class RdfRepositoryConfigurationProperties {

    private String serverUrl;
    private String mappingRepositoryId = "mappings";
    private String ontologyRepositoryId = "ontologies";
    private String dataRepositoryId = "fair-data";
    
    public String getServerUrl() {
        return serverUrl;
    }
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    public String getMappingRepositoryId() {
        return mappingRepositoryId;
    }
    public void setMappingRepositoryId(String mappingRepositoryId) {
        this.mappingRepositoryId = mappingRepositoryId;
    }
    public String getOntologyRepositoryId() {
        return ontologyRepositoryId;
    }
    public void setOntologyRepositoryId(String ontologyRepositoryId) {
        this.ontologyRepositoryId = ontologyRepositoryId;
    }
    public String getDataRepositoryId() {
        return dataRepositoryId;
    }
    public void setDataRepositoryId(String dataRepositoryId) {
        this.dataRepositoryId = dataRepositoryId;
    }
    
}
