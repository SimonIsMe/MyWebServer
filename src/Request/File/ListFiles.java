package Request.File;

import com.bmarius.sockets.WebSocketClient;
import entities.File;
import mywebsocket.JobToDo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class ListFiles extends JobToDo {
    
    public ListFiles(WebSocketClient client) {
        super(client, "File", "ListFiles");
    }
    
    public void run() {
        System.out.println(this.data.toString());
        
        if (this._validateData() == false) {
            this._sendResponseInvalidData();
            return;
        }
        
        Long id = (Long) this.data.get("id");
        File folder = (File) this._session.get(File.class, id);
        
        Object[] files = folder.getFileCollection().toArray();
        JSONArray list = new JSONArray();
        File file;
        for (int i = 0; i < files.length; i++) {
            file = (File) files[i];
            
            JSONObject fileObj = new JSONObject();
            fileObj.put("id", file.getId());
            fileObj.put("name", file.getName());
            fileObj.put("createdAt", file.getCreatedAt() + "");
            fileObj.put("mimeType", file.getMimeType());
            list.add(fileObj);
        }
        
        JSONArray path = new JSONArray();
        this._createPathTo(folder, path);
        
        this._response.data.put("list", list);
        this._response.data.put("path", path);
        this._sendOnlyToMe();
    }
    
    private void _createPathTo(File folder, JSONArray path) {
        JSONObject folderObj = new JSONObject();
        folderObj.put("name", folder.getName());
        folderObj.put("id", folder.getId());
        path.add(folderObj);
        
        if (folder.getParentId() != null) {
            this._createPathTo(folder.getParentId(), path);
        }
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
        
        if (folder.getMimeType() != null) {
            return false;
        }
        
        return true;
    }
}