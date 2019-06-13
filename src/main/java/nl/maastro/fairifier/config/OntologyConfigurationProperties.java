package nl.maastro.fairifier.config;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fairifier")
public class OntologyConfigurationProperties {
    
    private List<OntologyProperties> ontologies;
    
    public List<OntologyProperties> getOntologies() {
        return ontologies;
    }

    public void setOntologies(List<OntologyProperties> ontologies) {
        this.ontologies = ontologies;
    }
    
    public static class OntologyProperties {
        
        private URL url;
        
        private File file;
        
        private String baseUri;
        
        private String rdfFormat;

        public URL getUrl() {
            return url;
        }

        public void setUrl(URL url) {
            this.url = url;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public String getBaseUri() {
            return baseUri;
        }

        public void setBaseUri(String baseUri) {
            this.baseUri = baseUri;
        }

        public String getRdfFormat() {
            return rdfFormat;
        }

        public void setRdfFormat(String rdfFormat) {
            this.rdfFormat = rdfFormat;
        }

        @Override
        public String toString() {
            return "OntologyProperties [url=" + url + ", file=" + file + ", baseUri=" + baseUri + ", rdfFormat=" 
                    + rdfFormat + "]";
        }
        
    }
    
}
