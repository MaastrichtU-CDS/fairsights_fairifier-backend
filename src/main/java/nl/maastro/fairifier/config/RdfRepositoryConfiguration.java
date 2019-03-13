package nl.maastro.fairifier.config;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.eclipse.rdf4j.repository.http.config.HTTPRepositoryConfig;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RdfRepositoryConfigurationProperties.class)
public class RdfRepositoryConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(RdfRepositoryConfiguration.class);
    
    private final RdfRepositoryConfigurationProperties configurationProperties;
    private RemoteRepositoryManager repositoryManager;
    
    public RdfRepositoryConfiguration(RdfRepositoryConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
        this.repositoryManager = new RemoteRepositoryManager(configurationProperties.getServerUrl());
        this.repositoryManager.initialize();
    }
        
    @Bean
    public RemoteRepositoryManager rdfRepositoryManager() {
        return this.repositoryManager;
    }
    
    @Bean 
    public Repository mappingRepository() throws Exception {
        return getRepository(this.configurationProperties.getMappingRepositoryId());
    }
    
    @Bean
    Repository ontologyRepository() throws Exception {
        return getRepository(this.configurationProperties.getOntologyRepositoryId());
    }
    
    @Bean
    Repository dataRepository() throws Exception {
        return getRepository(this.configurationProperties.getDataRepositoryId());
    }
    
    private Repository getRepository(String repositoryId) throws Exception {
        Repository repository = repositoryManager.getRepository(repositoryId);
        if (repository == null) {
            repository = createRepository(repositoryId);
        }
        return repository;
    }
    
    private Repository createRepository(String repositoryId) throws Exception {
        try {
            HTTPRepositoryConfig httpRepositoryConfig = new HTTPRepositoryConfig();
            RepositoryConfig repositoryConfig = new RepositoryConfig(repositoryId, httpRepositoryConfig);
            this.repositoryManager.addRepositoryConfig(repositoryConfig);
            Repository repository = this.repositoryManager.getRepository(repositoryId);
            logger.info("Created new RDF repository " + repositoryId);
            return repository;
        } catch (RepositoryException | RepositoryConfigException e) {
            throw new Exception("Failed to create RDF repository " + repositoryId, e);
        }
    }

}
