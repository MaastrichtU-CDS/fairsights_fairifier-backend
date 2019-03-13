package nl.maastro.fairifier.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MappingController {
    
    private final Logger logger = LoggerFactory.getLogger(MappingController.class);
    
    @PutMapping(value="/mapping/import")
    public void uploadMapping() {
        logger.info("REST request to upload new mapping");
        // TODO
    }
    
    @GetMapping(value="/mapping/dump")
    public void downloadMapping() {
        logger.info("REST request to download mapping");
        // TODO
    }

}
