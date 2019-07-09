package nl.maastro.fairifier.web.dto;

public class TestMappingDto {
    
    private String dataSourceName;
    
    private String r2rmlMapping;
    
    private int limit = 10;
    
    
    public String getDataSourceName() {
        return dataSourceName;
    }
    
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }
    
    public String getR2rmlMapping() {
        return r2rmlMapping;
    }
    
    public void setR2rmlMapping(String r2rmlMapping) {
        this.r2rmlMapping = r2rmlMapping;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }

}
