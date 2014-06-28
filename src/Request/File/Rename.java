package Request.File;

import com.bmarius.sockets.WebSocketClient;
import entities.File;
import mywebsocket.JobToDo;
import mywebsocket.Response;

/**
 *
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class Rename extends JobToDo {
    
    public Rename(WebSocketClient client) {
        super(client, "File", "Rename");
    }
    
    public void run() {
        System.out.println(this.data.toString());
        
        if (this._validateData() == false) {
            this._sendResponseInvalidData();
            return;
        }
        
        Long id = (Long) this.data.get("id");
        String name = (String) this.data.get("name");
        
        File folder = (File) this._session.get(File.class, id);
        
        folder.setName(name);
        this._session.save(folder);
        this._session.getTransaction().commit();
        
        this._response.data = this.data;
        Response responseToAllCliens = (Response) this._response.clone();
        this._sendOnlyToMe();
        
        responseToAllCliens.responseId = null;
        responseToAllCliens.sendToAll();
    }
    
    private boolean _validateData() {
        String name = (String) this.data.get("name");
        if (name == null) {
            return false;
        }
        
        if (this.data.get("id") == null) {
            return false;
        }
        
        Long parentId = (Long) this.data.get("id");
        return this._validateFolderExist(parentId);
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