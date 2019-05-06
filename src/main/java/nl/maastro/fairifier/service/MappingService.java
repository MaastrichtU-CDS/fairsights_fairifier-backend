package nl.maastro.fairifier.service;

import java.io.File;
import java.io.OutputStream;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.query.UpdateExecutionException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MappingService {
    
    private final Logger logger = LoggerFactory.getLogger(MappingService.class);
    private Repository mappingRepository;
    
    public MappingService(@Qualifier("mappingRepository") Repository mappingRepository) {
        this.mappingRepository =  mappingRepository;
    }
    
    public File createBackup() {
        // TODO
        return null;
    }
    
    public void getMappings(RDFFormat format, OutputStream stream) {
        try (RepositoryConnection connection = mappingRepository.getConnection()) {
            connection.begin();
            RDFHandler writer = Rio.createWriter(format, stream);
            connection.export(writer);
            connection.commit();
        }
    }
    
    public void updateMappings(MultipartFile file, RDFFormat format) throws Exception {
        if (format == null) {
            logger.info("No RDF format provided; trying to deduce RDF format from file extension");
            format = Rio.getParserFormatForFileName(file.getOriginalFilename())
                    .orElseThrow(() -> new Exception(
                            "Unable to deduce RDF format from file name '"
                            + file.getName() + "'"));
            logger.info("Using RDF format: " + format);
        }
        
        createBackup();
        
        try (RepositoryConnection connection = mappingRepository.getConnection()) {
            connection.begin();
            connection.clear();
            connection.add(file.getInputStream(), null, format);
            connection.commit();
            
        } catch (RepositoryException | RDFParseException e) {
            // Turn these runtime exceptions into a checked exception
            throw new Exception(e);
        }
    }
    
    public String getSqlQuery() throws Exception {
        
        String sparqlQuery = "PREFIX rr: <http://www.w3.org/ns/r2rml#> " 
                + "SELECT DISTINCT ?sqlQuery " 
                + "WHERE { " 
                + "    ?s rr:sqlQuery ?sqlQuery . " 
                + "}";
        
        logger.info("Executing SPARQL query on mapping repository: " + sparqlQuery);
        
        try (RepositoryConnection connection = mappingRepository.getConnection()) {
            TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery);
            
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                String sqlQueryString = null;
                if (result.hasNext()) {
                    BindingSet bindingSet = result.next();
                    Value sqlQueryValue = bindingSet.getValue("sqlQuery");
                    if (sqlQueryValue != null) {
                        sqlQueryString =  sqlQueryValue.stringValue();
                    }
                }
                
                if (sqlQueryString == null) {
                    logger.warn("No SQL query found in R2RML mapping");
                }
                return sqlQueryString;
            }
        } catch (MalformedQueryException | QueryEvaluationException | RepositoryException e) {
            // Turn these runtime exceptions into a checked exception
            throw new Exception(e);
        }
    }
    
    public void updateSqlQuery(String newSqlQuery) throws Exception {
        
        String sparqlUpdate = "PREFIX rr: <http://www.w3.org/ns/r2rml#> " 
                + "DELETE { ?s rr:sqlQuery ?oldSqlQuery } " 
                + "INSERT { ?s rr:sqlQuery \"\"\"" + newSqlQuery + "\"\"\" } "
                + "WHERE  { ?s rr:sqlQuery ?oldSqlQuery }";
        
        logger.info("Executing SPARQL update on mapping repository: " + sparqlUpdate);
                
        try (RepositoryConnection connection = mappingRepository.getConnection()) {
            Update update = connection.prepareUpdate(QueryLanguage.SPARQL, sparqlUpdate);
            update.execute();
        } catch (MalformedQueryException | UpdateExecutionException | RepositoryException e) {
            // Turn these runtime exceptions into a checked exception
            throw new Exception(e);
        }
    }
    
    public void test(int limit) {
        
    }
    
    public void execute() {
        
    }
    
    
    
}

