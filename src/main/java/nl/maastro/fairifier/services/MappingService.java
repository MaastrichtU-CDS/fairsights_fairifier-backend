package nl.maastro.fairifier.services;

import java.io.File;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
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
            logger.info("Successfully updated R2RML mappings");
        } catch (Exception e) {
            logger.error("Failed to update R2RML mappings", e);
            throw e;
        }
    }
    
}
