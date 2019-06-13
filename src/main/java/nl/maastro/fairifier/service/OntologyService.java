package nl.maastro.fairifier.service;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
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
            @Qualifier("ontologyRepository") Repository ontologyRepository,
            DataSourceService dataSourceService) {
        this.ontologyRepository = ontologyRepository;
    }
    
    public List<String> getOntologyBaseUris() throws Exception {
        try (RepositoryConnection connection = ontologyRepository.getConnection()) {
            connection.begin();
            RepositoryResult<Resource> contexts = connection.getContextIDs();
            List<String> ontologyBaseUris = new ArrayList<>();
            while (contexts.hasNext()) {
                Resource context = contexts.next();
                ontologyBaseUris.add(context.stringValue());
            }
            return ontologyBaseUris;
        } catch (RepositoryException | RDFParseException e) {
            // Turn these runtime exceptions into a checked exception
            throw new Exception(e);
        }
    }
    
    public void addOntology(MultipartFile file, String baseUri, RDFFormat format) throws Exception {
        if (format == null) {
            logger.info("No RDF format provided; trying to deduce RDF format from file extension");
            format = Rio.getParserFormatForFileName(file.getOriginalFilename())
                    .orElseThrow(() -> new Exception(
                            "Unable to deduce RDF format from file name '" + file.getName() + "'"));
            logger.info("Using RDF format: " + format);
        }
        try (InputStream inputStream = file.getInputStream()) {
            addOntology(inputStream, baseUri, format);
        }
    }
    
    public void addOntology(InputStream inputStream, String baseUri, RDFFormat format) throws Exception {
        SimpleValueFactory factory = SimpleValueFactory.getInstance();
        IRI context = factory.createIRI(baseUri.toLowerCase());
        try (RepositoryConnection connection = ontologyRepository.getConnection()) {
            connection.begin();
            connection.add(inputStream, baseUri, format, context);
            connection.commit();
            logger.info("Added new ontology: " + baseUri);
        } catch (RepositoryException | RDFParseException e) {
            // Turn these runtime exceptions into a checked exception
            throw new Exception(e);
        }
    }
    
    public void addOntology(URL url, String baseUri, RDFFormat format) throws Exception {
        SimpleValueFactory factory = SimpleValueFactory.getInstance();
        IRI context = factory.createIRI(baseUri.toLowerCase());
        try (RepositoryConnection connection = ontologyRepository.getConnection()) {
            connection.begin();
            connection.add(url, baseUri, format, context);        
            connection.commit();
            logger.info("Added new ontology: " + baseUri);
        } catch (RepositoryException | RDFParseException e) {
            // Turn these runtime exceptions into a checked exception
            throw new Exception(e);
        }
    }
    
}
