package nl.maastro.fairifier.config;

import java.io.FileInputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import nl.maastro.fairifier.config.OntologyConfigurationProperties.OntologyProperties;
import nl.maastro.fairifier.service.OntologyService;

@Configuration
@EnableConfigurationProperties(OntologyConfigurationProperties.class)
public class OntologyConfiguration {
    
    private final Logger logger = LoggerFactory.getLogger(OntologyConfiguration.class);
    
    private OntologyService ontologyService;
    private OntologyConfigurationProperties properties;
    
    public OntologyConfiguration(OntologyService ontologyService,
            OntologyConfigurationProperties properties) {
        this.ontologyService = ontologyService;
        this.properties = properties;
    }
        
    @PostConstruct
    public void loadOntologies() throws Exception {
        logger.info("Loading pre-configured ontologies");
        List<OntologyProperties> preconfiguredOntologies = properties.getOntologies();
        if (preconfiguredOntologies != null) {
            for (OntologyProperties ontology : preconfiguredOntologies) {
                if (ontologyService.isExistingOntologyContext(ontology.getBaseUri())) {
                    logger.info("Found existing ontology for id=" + ontology.getBaseUri());
                } else {
                    try {
                        logger.info("Loading pre-configured ontology: " + ontology);
                        loadOntology(ontology);
                    } catch (Exception e) {
                        logger.error("Failed to load ontology", e);
                    }
                }
            }
        }
    }
    
    private void loadOntology(OntologyProperties ontology) throws Exception {
        RDFFormat rdfFormat = mapRdfFormat(ontology.getRdfFormat());
        if (ontology.getFile() != null) {
            try (FileInputStream inputStream = new FileInputStream(ontology.getFile())) {
                ontologyService.loadOntology(inputStream, ontology.getBaseUri(), rdfFormat);
            }
        } else if (ontology.getUrl() != null) {
            ontologyService.loadOntology(ontology.getUrl(), ontology.getBaseUri(), rdfFormat); 
        } else {
            throw new Exception("Ontology configuration must contain non-null file or URL; " + ontology);
        }
    }
    
    private RDFFormat mapRdfFormat(String rdfFormat) throws Exception {
        if (rdfFormat == null) {
            return null;
        }
        switch (rdfFormat.toUpperCase()) {
            case "RDFXML":
            case "RDF/XML":
                return RDFFormat.RDFXML;
            case "NTRIPLES":
            case "N-TRIPLES":
                return RDFFormat.NTRIPLES;
            case "TURTLE":
                return RDFFormat.TURTLE;
            case "N3":
                return RDFFormat.N3;
            case "TRIX":
                return RDFFormat.TRIX;
            case "TRIG":
                return RDFFormat.TRIG;
            case "BINARY":
            case "BINARYRDF":
                return RDFFormat.BINARY;
            case "NQUADS":
            case "N-QUADS":
                return RDFFormat.NQUADS;
            case "JSONLD":
            case "JSON-LD":
                return RDFFormat.JSONLD;
            case "RDFJSON":
            case "RDF/JSON":
                return RDFFormat.RDFJSON;
            case "RDFA":
                return RDFFormat.RDFA;
            default:
                throw new Exception("Unknown RDF format: " + rdfFormat);
        }
    }

}
