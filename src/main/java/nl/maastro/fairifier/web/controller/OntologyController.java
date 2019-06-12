package nl.maastro.fairifier.web.controller;

import java.net.URL;
import java.util.List;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import nl.maastro.fairifier.service.OntologyService;

@RestController
@RequestMapping("/api")
public class OntologyController {
    
    private final Logger logger = LoggerFactory.getLogger(OntologyController.class);
    
    private OntologyService ontologyService;
    
    public OntologyController(OntologyService ontologyService) {
        this.ontologyService = ontologyService;
    }
    
    @PostMapping(value="/ontologies")
    public ResponseEntity<List<String>> getOntologies() {
        logger.info("REST request get all ontologies (as list of base URIs)");
        try {
            List<String> baseUris = ontologyService.getOntologyBaseUris();
            return ResponseEntity.ok(baseUris);
        } catch (Exception e) {
            logger.error("Failed to get list of base URIs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("errorMessage", e.getMessage())
                    .build();
        }
    }
    
    @PostMapping(value="/ontology/import/file")
    public ResponseEntity<Void> importOntologyFromFile(
            @RequestParam(name="file") MultipartFile file,
            @RequestParam(name="baseUri") String baseUri,
            @RequestParam(name="format", required=false) RDFFormat format) {
        logger.info("REST request to import ontology from file");
        try {
            ontologyService.addOntology(file, baseUri, format);
            logger.info("Successfully imported new ontology");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to import ontology", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("errorMessage", e.getMessage())
                    .build();
        }
    }
    
    @PostMapping(value="/ontology/import/url")
    public ResponseEntity<Void> importOntologyFromUrl(
            @RequestParam(name="url") URL url,
            @RequestParam(name="baseUri") String baseUri,
            @RequestParam(name="format", required=false) RDFFormat format) {
        logger.info("REST request to import ontology from URL");
        try {
            ontologyService.addOntology(url, baseUri, format);
            logger.info("Successfully imported new ontology");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to import ontology", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("errorMessage", e.getMessage())
                    .build();
        }
    }
    
}
