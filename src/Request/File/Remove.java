package Request.File;

import com.bmarius.sockets.WebSocketClient;
import entities.File;
import java.util.Collection;
import mywebsocket.JobToDo;
import mywebsocket.Response;

/**
 *
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class Remove extends JobToDo {
    
    public Remove(WebSocketClient client) {
        super(client, "File", "Remove");
    }
    
    public void run() {
        System.out.println(this.data.toString());
        
        if (this._validateData() == false) {
            this._sendResponseInvalidData();
            return;
        }
        
        Long id = (Long) this.data.get("id");
        
        File file = (File) this._session.get(File.class, id);
        this._removeRecursive(file);
        
        this._session.getTransaction().commit();
        
        this._response.data = this.data;
        this._sendToAllClients();
    }
    
    private void _removeRecursive(File file) {
        Response response = new Response("File", "Remove");
        response.code = Response.CODE_OK;
        
        if (file.getMimeType() == null) {
            //  usuwam katalog
            Object[] fileCollection =  file.getFileCollection().toArray();
            for (int i = 0; i < fileCollection.length; i++) {
                this._removeRecursive((File) fileCollection[i]);
            }
        }
        this._session.delete(file);
        
        response.data.put("id", file.getId());
        response.sendToAll();
    }
    
    private boolean _validateData() {
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