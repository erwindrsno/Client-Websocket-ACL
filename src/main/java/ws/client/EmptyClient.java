package ws.client;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.PongFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;

public class EmptyClient extends WebSocketClient {
    Path toBeReceived = Paths.get("toBeReceived");
    FileOutputStream fos;
    Logger logger = LoggerFactory.getLogger(EmptyClient.class);

    boolean readyToReceiveFile = false;

    byte[] fileBytes; //complete file in bytes
    int currIdx = 0;
    long fileSize;
    long chunkSize;

    public EmptyClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public EmptyClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("Connected to server : " + handshakedata.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message){
        if(message.equals("PING")){
            send("PONG");
        }
        else if(message.startsWith("PRE-METADATA~")){
            try{
                fos = new FileOutputStream(toBeReceived.toFile());
            } catch (Exception e){
                e.printStackTrace();
            }
            String json = message.substring(message.indexOf('~')+1);
            ObjectMapper mapper = new ObjectMapper();
            try{
                PreMetadata preMD = mapper.readValue(json, PreMetadata.class);
                this.fileSize = preMD.getFileSize();
                // this.chunkSize = preMD.getChunkSize();
                this.fileBytes = new byte[(int)this.fileSize];
                this.readyToReceiveFile = true;
                logger.info("RECEIVED META DATA");
                send("READY-FILE~");
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        else if(message.startsWith("POST-METADATA~")){
            logger.info("RECEIVED POST META DATA");
            String json = message.substring(message.indexOf('~')+1);
            ObjectMapper mapper = new ObjectMapper();
            try{
                PostMetadata postMD = mapper.readValue(json, PostMetadata.class);

                Path filePath = Paths.get(postMD.getFileName());
                String user = postMD.getUser();
                String hashedServer = postMD.getSignature();

                Set<AclEntryPermission> permissions = postMD.getAclEntry();

                String hashedClient = Hashing.sha256().hashBytes(fileBytes).toString();

                if(hashedClient.equals(hashedServer)){
                    logger.info("FILE VERIFIED");
                }
                else{
                    toBeReceived.toFile().delete();
                    logger.info("FILE CORRUPTED");
                    return;
                }
                // file rename
                // sourcePath is created beforehand
                Files.move(toBeReceived, filePath, StandardCopyOption.REPLACE_EXISTING);

                // UserPrincipal userPrincipal = filePath.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName(user);

                // AclFileAttributeView aclView = Files.getFileAttributeView(filePath, AclFileAttributeView.class);
        
                // AclEntry aclEntry = AclEntry.newBuilder()
                //     .setType(AclEntryType.ALLOW)
                //     .setPrincipal(userPrincipal)
                //     .setFlags(AclEntryFlag.DIRECTORY_INHERIT, AclEntryFlag.FILE_INHERIT)
                //     .setPermissions(permissions)
                //     .build();
        
                // List<AclEntry> acl = aclView.getAcl();
                // acl.add(0, aclEntry);
                // aclView.setAcl(acl);
                // logger.info("OK");
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void onMessage(ByteBuffer buffer) {
        if (this.readyToReceiveFile) {
            try{
                byte[] data = new byte[buffer.remaining()];
        
                buffer.get(data);

                System.arraycopy(data, 0, fileBytes, currIdx, data.length);
                this.currIdx += data.length;

                if(this.currIdx == fileBytes.length){
                    fos.write(fileBytes);
                    fos.close();

                    this.readyToReceiveFile = false;
                    this.currIdx = 0;
                    send("FINISH-FILE~");
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }
}

// Catatan Utama
// - Mekanisme monitoring client sudah ada, dengan cara pinging. Tapi coba cara lain.
// - Protocol SEND_FILE sudah ada, dan sudah coba implementasi.

// Catanan Tugas
// - Coba monitoring menggunakan onOpen dan onClose.