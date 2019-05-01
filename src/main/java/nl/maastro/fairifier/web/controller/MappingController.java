package nl.maastro.fairifier.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import nl.maastro.fairifier.service.MappingService;

@RestController
@RequestMapping("/api")
public class MappingController {
    
    private final Logger logger = LoggerFactory.getLogger(MappingController.class);
    
    private MappingService mappingService;
    
    public MappingController(MappingService mappingService) {
        this.mappingService = mappingService;
    }
    
    @PutMapping(value="/mapping/upload")
    public ResponseEntity<Void> uploadMapping(
            @RequestParam(name="file") MultipartFile file,
            @RequestParam(name="format", required=false) RDFFormat format) {
        
        logger.info("REST request to upload new R2RML mapping");
        try {
            mappingService.updateMappings(file, format);
            logger.info("Successfully uploaded new R2RML mapping");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to upload new R2RML mapping", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("errorMessage", e.getMessage())
                    .build();
        }
    }
    
    @GetMapping(value="/mapping/download")
    public ResponseEntity<Void> downloadMapping(
            @RequestParam(required=false, defaultValue="RDFXML") RDFFormat format, 
            HttpServletResponse response) {
        logger.info("REST request to download current R2RML mappings");
        
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
            String timeStamp = formatter.format(new Date());
            String extension = format.getDefaultFileExtension();
            String fileName = "r2ml-mappings-" + timeStamp + "." + extension;
            response.setContentType("application/x-download");
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);  
            mappingService.getMappings(format, response.getOutputStream());
            response.flushBuffer();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to download R2RML mappings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("errorMessage", e.getMessage())
                    .build();
        }
    }
    
    @GetMapping(value="/mapping/query")
    public ResponseEntity<String> getSqlQuery() {
        logger.info("REST request to get SQL query of current R2RML mapping");
        try {
            String sqlQuery = mappingService.getSqlQuery();
            return ResponseEntity.ok(sqlQuery);
        } catch (Exception e) {
            logger.error("Failed to get SQL query from R2RML mapping", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("errorMessage", e.getMessage())
                    .build();
        }
    }
    
    @PutMapping(value="/mapping/sqlquery")
    public ResponseEntity<Void> updateSqlQuery(
            @RequestParam String newSqlQuery) {
        
        logger.info("REST request to update SQL query of current R2RML mapping");
        try {
            mappingService.updateSqlQuery(newSqlQuery);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to update SQL query", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("errorMessage", e.getMessage())
                    .build();
        }
    }

}
