package nl.maastro.fairifier.web.controller;

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

import nl.maastro.fairifier.services.MappingService;

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
        logger.info("REST request to upload new mapping");
        try {
            mappingService.updateMappings(file, format);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("errorMessage", e.getMessage())
                    .build();
        }
    }
    
    @GetMapping(value="/mapping/download")
    public void downloadMapping() {
        logger.info("REST request to download mapping");
        // TODO
    }

}
