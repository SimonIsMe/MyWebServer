package Request.File;

import com.bmarius.sockets.WebSocketClient;
import entities.File;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.UploadFileException;
import mywebsocket.JobToDo;
import mywebsocket.Response;
import org.json.simple.JSONObject;

/**
 *
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class StartUploadFile extends JobToDo {
    
    public StartUploadFile(WebSocketClient client) {
        super(client, "File", "StartUploadFile");
    }
    
    public void run() {
        System.out.println(this.data.toString());
        
        if (this._validateData() == false) {
            this._sendResponseInvalidData();
            return;
        }
        
        Long totalSize = (Long) this.data.get("size");
        Long id = (Long) this.data.get("parentId");
        String name = (String) this.data.get("name");
        String mimeType = (String) this.data.get("mimeType");
        
        File parentFolder = (File) this._session.get(File.class, id);
        this._session.getTransaction().commit();
        model.UploadFile uploadFile = null;
        try {
            uploadFile = new model.UploadFile(parentFolder, name, mimeType, totalSize);
            uploadFile.saveToClient(this._client);   
        } catch (UploadFileException ex) {
            Logger.getLogger(StartUploadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Response response = this._response.clone();
        
        this._response.data.put("hash", uploadFile.hash);
        this._sendOnlyToMe();
    }
    
    private boolean _validateData() {
        if (this.data.get("mimeType") == null) {
            return false;
        }
        
        if (this.data.get("name") == null) {
            return false;
        }
        
        if (this.data.get("parentId") == null) {
            return false;
        }
        
        if (this.data.get("size") == null) {
            return false;
        }
        
        return this._validateFolderExist((Long) this.data.get("parentId"));
    }
    
    private boolean _validateFolderExist(Long folderId) {
        this._session.beginTransaction();
        File folder = (File) this._session.get(File.class, folderId);
        
        if (folder == null) {
            return false;
        }
        
        return true;
    }
}