package ws.client;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Set;

public class Acl implements IAcl{
    Path filePath;
    String user;

    // public Acl(Path filePath, String user) {
    //     this.filePath = filePath;
    //     this.user = user;
    // }

    public void setUser(String user){
        this.user = "user";
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public void setRwxAcl(Path filePath, String user){
        try{
            UserPrincipal userPrincipal = filePath.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName(user);

            AclFileAttributeView aclView = Files.getFileAttributeView(filePath, AclFileAttributeView.class);
    
            AclEntry aclEntry = AclEntry.newBuilder()
                .setType(AclEntryType.ALLOW)
                .setPrincipal(userPrincipal)
                .setFlags(AclEntryFlag.DIRECTORY_INHERIT, AclEntryFlag.FILE_INHERIT)
                // .setPermissions(readPermissions)
                // .setPermissions(writePermissions)
                // .setPermissions(executePermissions)
                .setPermissions(rwxPermissions)
                .build();
    
            List<AclEntry> acl = aclView.getAcl();
            acl.add(0, aclEntry);
            aclView.setAcl(acl);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private Set<AclEntryPermission> rwxPermissions = Set.of(
        AclEntryPermission.READ_DATA, 
        AclEntryPermission.READ_ACL, 
        AclEntryPermission.READ_ATTRIBUTES, 
        AclEntryPermission.READ_NAMED_ATTRS,
        AclEntryPermission.WRITE_DATA, 
        AclEntryPermission.APPEND_DATA, 
        AclEntryPermission.WRITE_ATTRIBUTES, 
        AclEntryPermission.WRITE_NAMED_ATTRS,
        AclEntryPermission.DELETE,
        AclEntryPermission.DELETE_CHILD,
        AclEntryPermission.EXECUTE
    );

    private Set<AclEntryPermission> readPermissions = Set.of(
        AclEntryPermission.READ_DATA, 
        AclEntryPermission.READ_ACL, 
        AclEntryPermission.READ_ATTRIBUTES, 
        AclEntryPermission.READ_NAMED_ATTRS
    );

    private Set<AclEntryPermission> writePermissions = Set.of(
        AclEntryPermission.WRITE_DATA, 
        AclEntryPermission.APPEND_DATA, 
        AclEntryPermission.WRITE_ATTRIBUTES, 
        AclEntryPermission.WRITE_NAMED_ATTRS,
        AclEntryPermission.DELETE,
        AclEntryPermission.DELETE_CHILD
    );

    private Set<AclEntryPermission> executePermissions = Set.of(
        AclEntryPermission.EXECUTE
    );
}
