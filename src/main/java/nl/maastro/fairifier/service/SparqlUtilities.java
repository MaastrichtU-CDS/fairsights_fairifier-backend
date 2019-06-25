package nl.maastro.fairifier.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.query.UpdateExecutionException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.maastro.fairifier.web.dto.TripleDto;

public class SparqlUtilities {
    
    private static final Logger logger = LoggerFactory.getLogger(SparqlUtilities.class);
    
    public static HashMap<String, List<String>> performTupleQuery(
            Repository repository, String query) throws Exception {
        logger.info("Executing SPARQL query on RDF repository " + repository.toString() 
                + "; SPARQL query=" + query);
        try (RepositoryConnection connection = repository.getConnection()) {
            TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                return parseQueryResult(result);
            }
        } catch (MalformedQueryException | QueryEvaluationException | RepositoryException e) {
            // Turn these runtime exceptions into a checked exception
            throw new Exception("SPARQL query has failed", e);
        }
    }
    
    private static HashMap<String, List<String>> parseQueryResult(TupleQueryResult queryResult) {
        HashMap<String, List<String>> resultsMap = new HashMap<>();
        while (queryResult.hasNext()) {
            BindingSet bindingSet = queryResult.next();
            bindingSet.forEach(binding -> {
                String variableName = binding.getName();
                Value value = binding.getValue();
                
                List<String> values = resultsMap.getOrDefault(variableName, new ArrayList<>());
                values.add(value.stringValue());
                resultsMap.put(variableName, values);
            });
        }
        logger.debug("Query result: " + resultsMap);
        return resultsMap;
    }
    
    public static void performUpdate(Repository repository, String updateStatement) throws Exception {
        logger.info("Executing SPARQL update on RDF repository " + repository.toString() 
                + "; SPARQL update=" + updateStatement); 
        try (RepositoryConnection connection = repository.getConnection()) {
            Update update = connection.prepareUpdate(QueryLanguage.SPARQL, updateStatement);
            update.execute();
        } catch (MalformedQueryException | UpdateExecutionException | RepositoryException e) {
            // Turn these runtime exceptions into a checked exception
            throw new Exception(e);
        }
    }
    
    public static List<TripleDto> createTriples(List<String> subjects, 
            List<String> predicates, List<String> objects) throws Exception {
        if (subjects.size() != predicates.size() || subjects.size() != objects.size()) {
            throw new Exception("Inequal number of subjects, predicates and objects");
        }
        List<TripleDto> triples = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            TripleDto triple = new TripleDto(subjects.get(i), predicates.get(i), objects.get(i));
            triples.add(triple);
        }
        return triples;
    }
    
//    public static List<List<String>> createTriples(List<String> subjects, 
//            List<String> predicates, List<String> objects) throws Exception {
//        if (subjects.size() != predicates.size() || subjects.size() != objects.size()) {
//            throw new Exception("Inequal number of subjects, predicates and objects");
//        }
//        List<List<String>> triples = new ArrayList<>();
//        for (int i = 0; i < subjects.size(); i++) {
//            List<String> triple = Arrays.asList(subjects.get(i), predicates.get(i), objects.get(i));
//            triples.add(triple);
//        }
//        return triples;
//    }
    
//    public static List<HashMap<String, String>> performQuery(Repository repository, String query) throws Exception {
//        logger.info("Executing SPARQL query on RDF repository " + repository.toString() 
//                + "; SPARQL query=" + query);
//        try (RepositoryConnection connection = repository.getConnection()) {
//            TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, query);
//            try (TupleQueryResult result = tupleQuery.evaluate()) {
//                return parseQueryResult(result);
//            }
//        } catch (MalformedQueryException | QueryEvaluationException | RepositoryException e) {
//            // Turn these runtime exceptions into a checked exception
//            throw new Exception("SPARQL query has failed", e);
//        }
//    }
//    
//    private static List<HashMap<String, String>> parseQueryResult(TupleQueryResult queryResult) {
//        List<HashMap<String, String>> parsedResult = new ArrayList<>();
//        while (queryResult.hasNext()) {
//            BindingSet bindingSet = queryResult.next();
//            HashMap<String, String> map = new HashMap<>();
//            bindingSet.forEach(binding -> {
//                String variableName = binding.getName();
//                Value value = binding.getValue();
//                map.put(variableName, value.stringValue());
//            });
//            parsedResult.add(map);
//        }
//        logger.debug("Query results: " + parsedResult);
//        return parsedResult;
//    }
    
    public static RDFFormat getRdfFormat(String filename) throws Exception {
        return Rio.getParserFormatForFileName(filename)
                .orElseThrow(() -> new Exception("Unable to deduce RDF format from file name '" + filename + "'"));
    }

}
