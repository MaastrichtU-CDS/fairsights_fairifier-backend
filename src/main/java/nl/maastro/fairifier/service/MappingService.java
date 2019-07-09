package nl.maastro.fairifier.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

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
import nl.maastro.fairifier.config.DataSourceConfigurationProperties.DataSourceProperties;
import nl.maastro.fairifier.web.dto.TripleDto;

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
                + "?s rr:sqlQuery ?sqlQuery . " 
                + "}";
        HashMap<String, List<String>> result = SparqlUtilities.performTupleQuery(
                this.mappingRepository, sparqlQuery);
        List<String> sqlQueryValues = result.get("sqlQuery");
        if (sqlQueryValues == null || sqlQueryValues.isEmpty()) {
            logger.warn("No SQL query found in R2RML mapping");
            return null;
        } else {
            return sqlQueryValues.get(0);
        }
    }
    
    public void updateSqlQuery(String newSqlQuery) throws Exception {
        String sparqlUpdate = "PREFIX rr: <http://www.w3.org/ns/r2rml#> " 
                + "DELETE { ?s rr:sqlQuery ?oldSqlQuery } " 
                + "INSERT { ?s rr:sqlQuery \"\"\"" + newSqlQuery + "\"\"\" } "
                + "WHERE  { ?s rr:sqlQuery ?oldSqlQuery }";
        SparqlUtilities.performUpdate(this.mappingRepository, sparqlUpdate);
    }
    
   public List<TripleDto> getTripleMaps() throws Exception {
        String sparqlQuery = "PREFIX rr: <http://www.w3.org/ns/r2rml#> " 
                + "SELECT ?s ?p ?o " 
                + "WHERE { " 
                + "?s ?p ?o . ?s a rr:TriplesMap . " 
                + "}";
        HashMap<String, List<String>> result = SparqlUtilities.performTupleQuery(
                this.mappingRepository, sparqlQuery);
        List<String> subjects = result.get("s");
        List<String> predicates = result.get("p");
        List<String> objects = result.get("o");
        return SparqlUtilities.createTriples(subjects, predicates, objects);
    }
    
    public List<TripleDto> executeTestMapping(String dataSourceName, String r2rmlMapping, int limit) throws Exception {
        DataSource dataSource = dataSourceService.getDataSource(dataSourceName);
        File tempMappingFile = createTemporaryMappingFile(r2rmlMapping);
        try {
            Repository virtualRdfRepository = createVirtualRdfRepository(dataSource, tempMappingFile);
            String sparqlQuery = "select * where { ?s ?p ?o . }";
            HashMap<String, List<String>> result = SparqlUtilities.performTupleQuery(virtualRdfRepository, sparqlQuery);
            List<String> subjects = result.get("s");
            List<String> predicates = result.get("p");
            List<String> objects = result.get("o");
            return SparqlUtilities.createTriples(subjects, predicates, objects);
        } finally {
            tempMappingFile.delete();
        }
    }
    
    private File createTemporaryMappingFile(String r2rmlMapping) throws IOException {
        String tempFileName = "r2rml-mapping-" + UUID.randomUUID();
        File tempFile = File.createTempFile(tempFileName, ".ttl");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(r2rmlMapping);
            return tempFile;
        }
    }    
    
    private Repository createVirtualRdfRepository(DataSource dataSource, File r2rmlMappingFile) throws Exception {
        DataSourceProperties dataSourceProperties = dataSourceService.getDataSourceProperties(dataSource);
        OntopSQLOWLAPIConfiguration.Builder<?> builder = OntopSQLOWLAPIConfiguration.defaultBuilder();
        OntopSQLOWLAPIConfiguration repositoryConfiguration = builder
                .jdbcUrl(dataSourceProperties.getUrl())
                .jdbcDriver(dataSourceProperties.getDriverClassName())
                .jdbcUser(dataSourceProperties.getUsername())
                .jdbcPassword(dataSourceProperties.getPassword())
                .r2rmlMappingFile(r2rmlMappingFile)
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
    
    public void execute() {
        // TODO
    }
    
}

