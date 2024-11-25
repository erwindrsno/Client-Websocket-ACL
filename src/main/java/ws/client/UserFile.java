package ws.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserFile{
    String user;
    String filePath;

    @JsonCreator
    public UserFile(
        @JsonProperty("user") String user, 
        @JsonProperty("file") String filePath) {
        this.user = user;
        this.filePath = filePath;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}