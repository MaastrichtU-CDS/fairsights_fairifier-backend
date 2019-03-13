package nl.maastro.fairifier.web.dto;

public class CreateRepositoryDto {
    
    private String id;
    private String location = "";
    private Object params = new Object();
    private String sesameType = "";
    private String title;
    private String type = "free";

    public CreateRepositoryDto(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSesameType() {
        return sesameType;
    }

    public void setSesameType(String sesameType) {
        this.sesameType = sesameType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }
}
