package nl.maastro.fairifier.helpers;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class FileParser {
    public static ResponseEntity ParseFile(MultipartFile file, RepositoryConnection connection, RDFFormat rdfFormat){
        try {
            connection.begin();
            InputStream stream = file.getInputStream();
            Model model = Rio.parse(stream, "", rdfFormat);
            connection.add(model);
            connection.commit();
        } catch (IOException e) {
            e.printStackTrace();
            connection.rollback();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot parse uploaded file");
        } catch (Exception ex){
            ex.printStackTrace();
            connection.rollback();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok("Uploaded file parsed successfully");
    }
}
