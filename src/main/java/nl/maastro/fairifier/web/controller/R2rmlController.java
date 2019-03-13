package nl.maastro.fairifier.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class R2rmlController {
//    private final Logger LOGGER = LoggerFactory.getLogger(R2rmlController.class);
//    private GraphDbProperties graphDbProperties;
//    private RepositoryManager repoManager;
//    private Repository mappingRepo;
//    private Repository dataRepo;
//    private Repository ontoRepo;
//    private String repoBaseUrl;
//
//    public R2rmlController(GraphDbProperties graphDbProperties) {
//        this.graphDbProperties = graphDbProperties;
//        checkRepos();
//        repoBaseUrl = graphDbProperties.getBaseUrl() + "/repositories/" + graphDbProperties.getFairDataDbId();
//    }
//
//    private void checkRepos(){
//        repoManager = new RemoteRepositoryManager(graphDbProperties.getBaseUrl());
//        repoManager.initialize();
//        mappingRepo = repoManager.getRepository(graphDbProperties.getFairMappingDbId());
//        dataRepo = repoManager.getRepository(graphDbProperties.getFairDataDbId());
//        ontoRepo = repoManager.getRepository(graphDbProperties.getFairOntoDbId());
//        repoManager.shutDown();
//    }
//
//    @PostMapping(value = "/uploadontologyfile", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity uploadOntologyFile(@RequestParam("rdf file") MultipartFile file){
//        if(ontoRepo == null){
//            checkRepos();
//        }
//        ontoRepo.initialize();
//        ResponseEntity parseResponse = FileParser.ParseFile(file, ontoRepo.getConnection(), RDFFormat.RDFXML);
//        if(parseResponse.getStatusCode().is2xxSuccessful()){
//            return ResponseEntity.ok("Uploaded ontology processed successfully");
//        }else{
//            return parseResponse;
//        }
//    }
//
//    @PostMapping(value = "/uploaddefaultontology", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity uploadDefaultOntology(){
//        fillOntologyDb();
//        return ResponseEntity.ok().build();
//    }
//
//    private void fillOntologyDb() {
//        try {
//            URL url = new URL("http://sparql.cancerdata.org/namespace/roo/sparql");
//            RepositoryConnection connection = RepoConnection.getRepoConnection(graphDbProperties.getBaseUrl(), graphDbProperties.getFairOntoDbId());
//            connection.begin();
//            connection.add(url, graphDbProperties.getBaseUrl(), RDFFormat.RDFXML);
//            connection.commit();
//        } catch (IOException ex){
//            repoManager.removeRepository(graphDbProperties.getFairOntoDbId());
//            ex.getMessage();
//        } catch (Exception ex){
//            repoManager.removeRepository(graphDbProperties.getFairOntoDbId());
//            ex.getMessage();
//        }
//    }
//
//    @PostMapping(value = "/uploadmapping", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity uploadMapping(@RequestParam("ttl File") MultipartFile file){
//        if(mappingRepo == null){
//            checkRepos();
//        }
//        mappingRepo.initialize();
//        ResponseEntity parseResponse = FileParser.ParseFile(file, mappingRepo.getConnection(), RDFFormat.N3);
//        if(parseResponse.getStatusCode().is2xxSuccessful()){
//            return ResponseEntity.ok("Uploaded mapping processed succesfull");
//        }else{
//            return parseResponse;
//        }
//    }
//
//    @DeleteMapping(value = "/clearcurrentmapping", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity clearMapping(){
//        if(mappingRepo == null){
//            checkRepos();
//        }
//        mappingRepo.initialize();
//        RepositoryConnection connection = mappingRepo.getConnection();
//        try {
//            connection.begin();
//            connection.clear();
//            connection.commit();
//        }catch (Exception ex){
//            LOGGER.error("Cannot clear ontology: " + ex.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
}
