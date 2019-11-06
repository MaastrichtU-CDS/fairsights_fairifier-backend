package nl.maastro.fairifier.web.dto;

public class TestMappingDto {
    
    private String dataSourceName;
    
    private String baseUri;
    
    private int limit = 10;
    
    public String getDataSourceName() {
        return dataSourceName;
    }
    
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }
    
    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }

}
