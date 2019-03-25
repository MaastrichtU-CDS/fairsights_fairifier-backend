package nl.maastro.fairifier.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OntologyController {
    
    private final Logger logger = LoggerFactory.getLogger(OntologyController.class);
    
    @PostMapping(value="/ontology/import/file")
    public void importOntologyFromFile() {
        logger.info("REST request to import ontology from file");
        // TODO
    }
    
    @PostMapping(value="/ontology/import/url")
    public void importOntologyFromUrl() {
        logger.info("REST request to import ontology from URL");
        // TODO
    }

}
