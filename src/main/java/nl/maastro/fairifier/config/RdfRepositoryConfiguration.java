package nl.maastro.fairifier.config;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.sail.nativerdf.config.NativeStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RdfRepositoryConfigurationProperties.class)
public class RdfRepositoryConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(RdfRepositoryConfiguration.class);
    
    private final RdfRepositoryConfigurationProperties repositoryConfigurationProperties;
    private RemoteRepositoryManager repositoryManager;
    
    public RdfRepositoryConfiguration(RdfRepositoryConfigurationProperties configurationProperties) {
        this.repositoryConfigurationProperties = configurationProperties;
        this.repositoryManager = new RemoteRepositoryManager(configurationProperties.getServerUrl());
        this.repositoryManager.initialize();
    }
        
    @Bean
    public RemoteRepositoryManager rdfRepositoryManager() {
        return this.repositoryManager;
    }
    
    @Bean 
    public Repository mappingRepository() throws Exception {
        return getRepository(this.repositoryConfigurationProperties.getMappingRepositoryId());
    }
    
    @Bean
    public Repository ontologyRepository() throws Exception {
        return getRepository(this.repositoryConfigurationProperties.getOntologyRepositoryId());
    }
    
    @Bean
    public Repository fairDataRepository() throws Exception {
        return getRepository(this.repositoryConfigurationProperties.getDataRepositoryId());
    }
    
    private Repository getRepository(String repositoryId) throws Exception {
        Repository repository = repositoryManager.getRepository(repositoryId);
        if (repository != null) {
            logger.info("Found existing RDF repository: " + repositoryId);
        } else { 
            repository = createRepository(repositoryId);
        }
        return repository;
    }
    
    private Repository createRepository(String repositoryId) throws Exception {
        try {
            
            
            
//            HTTPRepositoryConfig impl = new HTTPRepositoryConfig(repositoryId);
//            SPARQLRepositoryConfig impl = new SPARQLRepositoryConfig();
            
            SailRepositoryConfig repositoryImplementation = new SailRepositoryConfig(new NativeStoreConfig()); 
            RepositoryConfig repositoryConfig = new RepositoryConfig(repositoryId, repositoryImplementation);
            repositoryConfig.setTitle(repositoryId);
            this.repositoryManager.addRepositoryConfig(repositoryConfig);
            Repository repository = this.repositoryManager.getRepository(repositoryId);
            
//            Model repositoryModel = new Model();
//            RepositoryConfig repositoryConfig = RepositoryConfig.create(repositoryModel, null);
//            this.repositoryManager.addRepositoryConfig(repositoryConfig);
//            Repository repository = this.repositoryManager.getRepository(repositoryId);
            
            
//            HTTPRepository repository = new HTTPRepository(
//                    repositoryConfigurationProperties.getServerUrl(), repositoryId);
//            repository.initialize();
            
            logger.info("Created new RDF repository: " + repositoryId);
            return repository;
        } catch (RepositoryException | RepositoryConfigException e) {
            throw new Exception("Failed to create RDF repository " + repositoryId, e);
        }
    }

}
