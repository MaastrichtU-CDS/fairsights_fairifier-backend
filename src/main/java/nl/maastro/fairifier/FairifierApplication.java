package nl.maastro.fairifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.maastro.fairifier.config.GraphDbProperties;
import nl.maastro.fairifier.web.dto.CreateRepositoryDto;
import org.apache.log4j.Logger;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class FairifierApplication {

	private static final Logger LOGGER = Logger.getLogger(FairifierApplication.class);
	private static GraphDbProperties graphDbProperties;
	private static RepositoryManager repoManager;

	public FairifierApplication(GraphDbProperties graphDbProperties) {
		FairifierApplication.graphDbProperties = graphDbProperties;
	}

	public static void main(String[] args) {
		SpringApplication.run(FairifierApplication.class, args);
        initialisation();
	}

	private static void initialisation(){
		String uri = graphDbProperties.getBaseUrl() + "rest/repositories";
		boolean hasMappingRepo = checkReposExist(uri, graphDbProperties.getFairMappingDbId());
		boolean hasDataRepo = checkReposExist(uri, graphDbProperties.getFairDataDbId());
		boolean hasOntoRepo = checkReposExist(uri, graphDbProperties.getFairOntoDbId());
		if(!hasMappingRepo){
			hasMappingRepo = createRepo(uri, new CreateRepositoryDto(graphDbProperties.getFairMappingDbId(), graphDbProperties.getFairMappingDbTitle()));
		}
		if(!hasDataRepo){
			hasDataRepo = createRepo(uri, new CreateRepositoryDto(graphDbProperties.getFairDataDbId(), graphDbProperties.getFairMappingDbTitle()));
		}
		if(!hasOntoRepo){
			hasOntoRepo = createRepo(uri, new CreateRepositoryDto(graphDbProperties.getFairOntoDbId(), graphDbProperties.getFairOntoDbTitle()));
		}
		if(!hasDataRepo || !hasMappingRepo || !hasOntoRepo){
			try {
				throw new Exception("Not all required databases are available or could not be created.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else{
			LOGGER.info("Required repositories are available.");
		}
	}

	private static Boolean checkReposExist(String uri, String repositoryId){
		repoManager = new RemoteRepositoryManager(graphDbProperties.getBaseUrl());
		repoManager.initialize();
		if(repoManager.getRepository(repositoryId) == null){
			return false;
		}else{
			return true;
		}
	}

	private static boolean createRepo(String uri, CreateRepositoryDto repoValues){
		LOGGER.info("Try creating repository with Id: " + repoValues.getId());
		if(repoValues == null){
			return false;
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			String json = mapper.writeValueAsString(repoValues);
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
			headers.add("Content-Type", "application/json");
			HttpEntity<String> entity = new HttpEntity<>(json, headers);
			RestTemplate template = new RestTemplate();
			ResponseEntity response = template.exchange(uri, HttpMethod.PUT, entity, String.class);
			return response.getStatusCode().is2xxSuccessful();
		} catch (JsonProcessingException e) {
			LOGGER.error("Error creating database: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
}

