package ws.client;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntryPermission;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.hash.Hashing;

public class PostMetadata {
    String fileName;
    String user;
    String signature;
    Set<AclEntryPermission> aclEntry;

    public PostMetadata(
        @JsonProperty("fileName") String fileName, 
        @JsonProperty("user") String user,
        @JsonProperty("signature") String signature,
        @JsonProperty("aclEntry") Set<AclEntryPermission> aclEntry
    ) {
        this.fileName = fileName;
        this.user = user;
        this.signature = signature;
        this.aclEntry = aclEntry;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSignature(){
        return this.signature;
    }

    public void setSignature(String signature){
        this.signature = signature;
    }

    public Set<AclEntryPermission> getAclEntry(){
        return this.aclEntry;
    }

    public void setAclEntry(Set<AclEntryPermission> aclEntry){
        this.aclEntry = aclEntry;
    }
}