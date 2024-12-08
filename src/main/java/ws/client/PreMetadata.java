package ws.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PreMetadata{
    long fileSize;
    long chunkSize;

    public PreMetadata(
        @JsonProperty("fileSize") long fileSize, 
        @JsonProperty("chunkSize") long chunkSize
    ){
        this.fileSize = fileSize;
        this.chunkSize = chunkSize;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(long fileSize){
        this.fileSize = fileSize;
    }

    public long getChunkSize(){
        return this.chunkSize;
    }

    public void setChunkSize(long chunkSize){
        this.chunkSize = chunkSize;
    }
}
