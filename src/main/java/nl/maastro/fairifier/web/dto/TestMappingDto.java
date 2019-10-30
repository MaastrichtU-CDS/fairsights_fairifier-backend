package nl.maastro.fairifier.web.dto;

public class TestMappingDto {
    
    private String dataSourceName;
    
    private int limit = 10;
    
    
    public String getDataSourceName() {
        return dataSourceName;
    }
    
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }

}
