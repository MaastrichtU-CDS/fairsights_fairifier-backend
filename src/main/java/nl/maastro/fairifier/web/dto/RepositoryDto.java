package nl.maastro.fairifier.web.dto;

public class RepositoryDto {
    private String id;
    private String title;
    private String uri;
    private String externalUrl;
    private String type;
    private String sesameType;
    private String location;
    private Boolean readable;
    private Boolean writable;
    private Boolean unsupported;
    private Boolean local;

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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSesameType() {
        return sesameType;
    }

    public void setSesameType(String sesameType) {
        this.sesameType = sesameType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getReadable() {
        return readable;
    }

    public void setReadable(Boolean readable) {
        this.readable = readable;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    public Boolean getUnsupported() {
        return unsupported;
    }

    public void setUnsupported(Boolean unsupported) {
        this.unsupported = unsupported;
    }

    public Boolean getLocal() {
        return local;
    }

    public void setLocal(Boolean local) {
        this.local = local;
    }

    @Override
    public String toString() {
        return "RepositoryDto{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", uri='" + uri + '\'' +
                ", externalUrl='" + externalUrl + '\'' +
                ", type='" + type + '\'' +
                ", sesameType='" + sesameType + '\'' +
                ", location='" + location + '\'' +
                ", readable=" + readable +
                ", writable=" + writable +
                ", unsupported=" + unsupported +
                ", local=" + local +
                '}';
    }
}
