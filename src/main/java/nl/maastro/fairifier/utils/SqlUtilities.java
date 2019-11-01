package nl.maastro.fairifier.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.maastro.fairifier.domain.DatabaseDriver;

public class SqlUtilities {
    
    private static final Logger logger = LoggerFactory.getLogger(SqlUtilities.class);
    
    private static final String REGEX_SELECT = "(?i)(?s)(SELECT)";
    private static final String REGEX_SELECT_TOP = "(?i)(?s)(SELECT)(\\s+)(TOP)(\\s+)(\\(?)(\\d+)(\\)?)";
    private static final String REGEX_LIMIT = "(?i)(?s)(LIMIT)(\\s+)(\\d+).*";
    
    public static String setResultsLimit(String sqlQuery, DatabaseDriver databaseDriver, int resultsLimit) {
        switch (databaseDriver) {
            case CSV:
            case H2:
            case MYSQL:
            case POSTGRESQL:
                if (sqlQuery.matches(".*" + REGEX_LIMIT  + ".*")) {
                    return sqlQuery.replaceAll(REGEX_LIMIT, "LIMIT " + resultsLimit);
                } else {
                    return sqlQuery + " LIMIT " + resultsLimit;
                }
            case SQLSERVER:
                if (sqlQuery.matches(".*" + REGEX_SELECT_TOP + ".*")) {
                    return sqlQuery.replaceAll(REGEX_SELECT_TOP, "SELECT TOP " + resultsLimit);
                } else {
                    return sqlQuery.replaceAll(REGEX_SELECT, "SELECT TOP " + resultsLimit);
                }
        default:
            logger.warn("Results limit not implemented for databaseDriver: " + databaseDriver);
            return sqlQuery;
        }
    }
    
}
