package nl.maastro.fairifier.service;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OntologyService {
    
    private final Logger logger = LoggerFactory.getLogger(OntologyService.class);
    
    private Repository ontologyRepository;

    public OntologyService(
            @Qualifier("ontologyRepository") Repository ontologyRepository) {
        this.ontologyRepository = ontologyRepository;
    }
    
    public List<String> getOntologyContextIds() throws Exception {
        try (RepositoryConnection connection = ontologyRepository.getConnection()) {
            connection.begin();
            try (RepositoryResult<Resource> contexts = connection.getContextIDs()) {
                List<String> contextIds = new ArrayList<>();
                while (contexts.hasNext()) {
                    Resource context = contexts.next();
                    contextIds.add(context.stringValue());
                }
                return contextIds;
            } finally {
                connection.commit();
            }
        } catch (RepositoryException | RDFParseException e) {
            // Turn these runtime exceptions into a checked exception
            throw new Exception(e);
        }
    }
    
    public boolean isExistingOntology(String contextId) throws Exception {
        List<String> contextIds = getOntologyContextIds();
        return contextIds.contains(contextId.toLowerCase());
    }
    
    public void loadOntology(MultipartFile file, String baseUri, RDFFormat format) throws Exception {
        if (format == null) {
            logger.info("No RDF format provided; trying to deduce RDF format from file extension");
            format = Rio.getParserFormatForFileName(file.getOriginalFilename())
                    .orElseThrow(() -> new Exception(
                            "Unable to deduce RDF format from file name '" + file.getName() + "'"));
            logger.info("Using RDF format: " + format);
        }
        try (InputStream inputStream = file.getInputStream()) {
            loadOntology(inputStream, baseUri, format);
        }
    }
    
    public void loadOntology(InputStream inputStream, String baseUri, RDFFormat format) throws Exception {
        try (RepositoryConnection connection = ontologyRepository.getConnection()) {
            connection.begin();
            Resource context = initializeContext(connection, baseUri.toLowerCase());
            connection.add(inputStream, baseUri, format, context);
            connection.commit();
        } catch (RepositoryException | RDFParseException e) {
            // Turn these runtime exceptions into a checked exception
            throw new Exception(e);
        }
    }
    
    public void loadOntology(URL url, String baseUri, RDFFormat format) throws Exception {
        try (RepositoryConnection connection = ontologyRepository.getConnection()) {
            connection.begin();
            Resource context = initializeContext(connection, baseUri.toLowerCase());
            connection.add(url, baseUri, format, context);        
            connection.commit();
        } catch (RepositoryException | RDFParseException e) {
            // Turn these runtime exceptions into a checked exception
            throw new Exception(e);
        }
    }
    
    private Resource initializeContext(RepositoryConnection connection, String contextId) throws Exception {
        Resource context = getContext(connection, contextId);
        if (context == null) {
            logger.info("Creating new context with id: " + contextId);
            return createContext(contextId);
        } else {
            logger.info("Context already exists for id=" + contextId + "; clearing all statements from context");
            connection.clear(context);
            return context;
        }
    }
    
    private Resource getContext(RepositoryConnection connection, String contextId) {
        RepositoryResult<Resource> contexts = connection.getContextIDs();
        while (contexts.hasNext()) {
            Resource context = contexts.next();
            if (contextId.equals(context.stringValue())) {
                return context;
            }
        }
        return null;
    }
    
    private Resource createContext(String contextId) {
        SimpleValueFactory factory = SimpleValueFactory.getInstance();
        return factory.createIRI(contextId);
    }
}
