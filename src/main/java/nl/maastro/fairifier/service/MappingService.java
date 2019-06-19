package nl.maastro.fairifier.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.rdf4j.RDF4J;
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

import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.rdf4j.repository.OntopRepository;

@Service
public class MappingService {
    
    private final Logger logger = LoggerFactory.getLogger(MappingService.class);
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddhhmmss");
    
    private static final String BACKUP_DIRECTORY = ".\\backup";
    
    private Repository mappingRepository;
    private DataSourceService dataSourceService;
    
    public MappingService(
            @Qualifier("mappingRepository") Repository mappingRepository,
            DataSourceService dataSourceService) {
        this.mappingRepository = mappingRepository;
        this.dataSourceService = dataSourceService;
    }
    
    @PostConstruct
    public static void createBackupDirectory() {
        File backupDirectory = new File(BACKUP_DIRECTORY);
        if (!backupDirectory.isDirectory()) {
            backupDirectory.mkdirs();
        }
    }
    
    public File createBackup() throws Exception {
        RDFFormat rdfFormat = RDFFormat.RDFXML;
        String timeStamp = dateFormatter.format(new Date());
        String extension = rdfFormat.getDefaultFileExtension();
        String fileName = "r2ml-mapping-" + timeStamp + "." + extension;
        File file = Paths.get(BACKUP_DIRECTORY, fileName).toFile();
        saveMappingToFile(rdfFormat, file);
        return file;
    }
    
    private void saveMappingToFile(RDFFormat format, File file) throws FileNotFoundException, IOException {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            getMapping(format, outputStream);
        }
    }
    
    public void getMapping(RDFFormat format, OutputStream stream) {
        try (RepositoryConnection connection = mappingRepository.getConnection()) {
            connection.begin();
            RDFHandler writer = Rio.createWriter(format, stream);
            connection.export(writer);
            connection.commit();
        }
    }
    
    public void updateMapping(MultipartFile file, RDFFormat format) throws Exception {
        if (format == null) {
            logger.info("No RDF format provided; trying to deduce RDF format from file extension");
            format = Rio.getParserFormatForFileName(file.getOriginalFilename())
                    .orElseThrow(() -> new Exception(
                            "Unable to deduce RDF format from file name '" + file.getName() + "'"));
            logger.info("Using RDF format: " + format);
        }
        
        try {
            createBackup();
        } catch (Exception e) {
            logger.error("Failed to create backup of current mapping", e);
        }
        
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
    
    public void executeTestMapping(int limit) throws Exception {
        HashMap<String, DataSource> dataSources = dataSourceService.getDataSources();
        if (dataSources.isEmpty()) {
            throw new Exception("No DataSources found; unable to execute mapping");
        }
        
        // Assume there is a single DataSource
        String dataSourceName = dataSources.keySet().iterator().next();
        DataSource dataSource = dataSources.get(dataSourceName);
        logger.info("Executing mapping for dataSource=" + dataSourceName);
        
        Repository virtualRdfRepository = createVirtualRdfRepository(dataSource);
        
        String sparqlQuery = "select * where { "
                + "    ?s ?p ?o . " 
                + "} limit " + limit;
        executeSparqlQuery(virtualRdfRepository, sparqlQuery);
    }
    
    private Repository createVirtualRdfRepository(DataSource dataSource) throws Exception {
        DatabaseMetaData dataSourceMetaData = dataSourceService.getDatabaseMetaData(dataSource);
        String jdbcUrl = dataSourceMetaData.getURL();
        String jdbcDriver = dataSourceMetaData.getDriverName();
        String jdbcUser= dataSourceMetaData.getUserName();
        String jdbcPassword = ""; // leave empty for now
        
        String r2rmlFile = "./mapping.ttl";
        
        OntopSQLOWLAPIConfiguration.Builder<?> builder = OntopSQLOWLAPIConfiguration.defaultBuilder();
        OntopSQLOWLAPIConfiguration repositoryConfiguration = builder
                .jdbcUrl(jdbcUrl)
                .jdbcDriver(jdbcDriver)
                .jdbcUser(jdbcUser)
                .jdbcPassword(jdbcPassword)
                .r2rmlMappingFile(r2rmlFile)
                .enableTestMode()
                .build();
        
        Repository virtualRdfRepository = OntopRepository.defaultRepository(repositoryConfiguration);
        try {
            virtualRdfRepository.initialize();
            return virtualRdfRepository;
        } catch (RepositoryException e) {
            // Turn this runtime exception into a checked exception
            throw new Exception(e);
        }
    }
    
    private void executeSparqlQuery(Repository rdfRepository, String sparqlQuery) {
        try (
                RepositoryConnection connection = rdfRepository.getConnection();
                TupleQueryResult result = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery).evaluate()
        ) {
            logger.info("Parsing query result...");
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                logger.debug(bindingSet.toString());
            }
        }
    }
    
    private Graph getGraph() throws Exception {
        try (RepositoryConnection connection = mappingRepository.getConnection()) {
            connection.begin();
            RDF4J rdf4j = new RDF4J();
            Graph graph = rdf4j.asGraph(connection.getRepository());
            connection.commit();
            return graph;
        } catch (RepositoryException e) {
            // Turn this runtime exceptions into a checked exception
            throw new Exception(e);
        }
    }
    
    public void runOntopExample() throws Exception {
        OntopRDF4JR2RMLMappingExample example = new OntopRDF4JR2RMLMappingExample();
        example.run();
    }
    
    public void execute() {
        
    }
    
}

