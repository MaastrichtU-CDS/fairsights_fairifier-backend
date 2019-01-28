package nl.maastro.fairifier.helpers;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;

public class RepoConnection {
    public static RepositoryConnection getRepoConnection(String baseUrl, String repositoryId){
        RepositoryManager repoManager = new RemoteRepositoryManager(baseUrl);
        repoManager.initialize();
        Repository repo = repoManager.getRepository(repositoryId);
        repo.initialize();
        return repo.getConnection();
    }
}
