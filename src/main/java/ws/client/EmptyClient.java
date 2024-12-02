package ws.client;

import java.io.FileOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.PongFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EmptyClient extends WebSocketClient {
    Path sourcePath = Paths.get("toBeReceived");
    FileOutputStream fos;
    IAcl aclSetter;
    Logger logger = LoggerFactory.getLogger(EmptyClient.class);

    public EmptyClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public EmptyClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        try {
            logger.info("Connected to server : " + handshakedata.getHttpStatusMessage());
            fos = new FileOutputStream(sourcePath.toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        if(message.equals("PING")){
            send("PONG");
        }
        // logger.info(json);
        // ObjectMapper mapper = new ObjectMapper();
        // try {
        //     UserFile userFile = mapper.readValue(json, UserFile.class);
        //     Path targetPath = Paths.get(userFile.getFilePath());
        //     String user = userFile.getUser();

        //     // file rename
        //     //sourcePath is created beforehand
        //     Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);


        //     this.aclSetter = new Acl();
        //     this.aclSetter.setRwxAcl(targetPath, user);
        //     logger.info("ACL set");
        // } catch (Exception e) {
        //     logger.info("error occured during setting ACL");
        //     e.printStackTrace();
        // }
    }

    @Override
    public void onMessage(ByteBuffer message) {
        boolean isLast = false;
        logger.info(message.toString());
        try {
            logger.info("Writing to file : " + message.remaining());
            byte[] data = new byte[message.remaining()];
            if (message.remaining() < 10240) {
                isLast = true;
            }

        //     //4byte int
        //     //ada protokol
        //     //signature : 
        //     // sha512, chunk size, hashing, file size

            message.get(data);
            fos.write(data);

            if (isLast) {
                logger.info("File received");
                logger.info("FOS closed");
                fos.close();
            }
        } catch (Exception e) {
            logger.error("Error occured during receiving file");
            e.printStackTrace();
        }
    }

    // @Override
    // public void onMessage(ByteBuffer message) {
    // try {
    // byte[] data = new byte[message.remaining()];
    // logger.info("Received : " + message.remaining());
    // message.get(data);

    // fos.write(data);

    // if(message.remaining() == 0){
    // logger.info("File received");
    // fos.close();
    // }
    // } catch (Exception e) {
    // logger.error("error occured during receiving file");
    // e.printStackTrace();
    // }
    // }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }

    // @Override
    // public void onPing(ByteBuffer ping) {
    //     System.out.println("pinged");
    // }
}