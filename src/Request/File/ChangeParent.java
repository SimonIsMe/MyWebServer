package Request.File;

import com.bmarius.sockets.WebSocketClient;
import entities.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import mywebsocket.JobToDo;

/**
 *
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class ChangeParent extends JobToDo {
    
    public ChangeParent(WebSocketClient client) {
        super(client, "File", "ChangeParent");
    }
    
    public void run() {
        System.out.println(this.data.toString());
        this._session.beginTransaction();
        
        if (this._validateData() == false) {
            this._sendResponseInvalidData();
            return;
        }
        
        Long id = (Long) this.data.get("id");
        Long parentId = (Long) this.data.get("parentId");
        
        File file = (File) this._session.get(File.class, id);
        File parentFolder = (File) this._session.get(File.class, parentId);
        
        file.setParentId(parentFolder);
        this._session.save(file);
        this._session.getTransaction().commit();
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        this.data.put("name", file.getName());
        this.data.put("mimeType", file.getMimeType());
        this.data.put("createdAt", dateFormat.format(file.getCreatedAt()));
        
        this._response.data = this.data;
        this._sendToAllClients();
    }
    
    private boolean _validateData() {
        if (this.data.get("id") == null) {
            return false;
        }
        
        if (this.data.get("parentId") == null) {
            return false;
        }
        
        Long id = (Long) this.data.get("id");
        Long parentId = (Long) this.data.get("parentId");
        return this._validateFolderExist(parentId) 
            && this._validateFolderExist(id);
    }
    
    private boolean _validateFolderExist(Long folderId) {
        File folder = (File) this._session.get(File.class, folderId);
        
        if (folder == null) {
            return false;
        }
        
        return true;
    }
}