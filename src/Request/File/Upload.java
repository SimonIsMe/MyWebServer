package Request.File;

import com.bmarius.sockets.WebSocketClient;
import entities.File;
import java.util.Collection;
import model.UploadFile;
import mywebsocket.JobToDo;
import mywebsocket.Response;
import org.json.simple.JSONObject;

/**
 *
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class Upload extends JobToDo {
    
    public Upload(WebSocketClient client) {
        super(client, "File", "Upload");
    }
    
    public void run() {
        System.out.println(this.data.toString());
        
        if (this._validateData() == false) {
            this._sendResponseInvalidData();
            return;
        }
        
        String hash = (String) this.data.get("hash");
        String base64 = (String) this.data.get("base64");
        UploadFile uploadFile = this._client.uploadingFiles.get(hash);
        uploadFile.append(base64);
        
        Response responseToAll = this._response.clone();
        this._response.data.put("ok", true);
        this._sendOnlyToMe();
        
        responseToAll.data = new JSONObject();
        responseToAll.responseId = null;
        responseToAll.data.put("parentId", uploadFile.fileObj.getParentId().getId());
        responseToAll.data.put("id", uploadFile.fileObj.getId());
        responseToAll.data.put("name", uploadFile.fileObj.getName());
        responseToAll.data.put("mimeType", uploadFile.fileObj.getMimeType());
        responseToAll.data.put("percent", 
            uploadFile.uploadSize / uploadFile.totalSize * 100
        );
        responseToAll.sendToAll();
    }
    
    private boolean _validateData() {
        if (this.data.get("hash") == null) {
            return false;
        }
        
        if (this.data.get("base64") == null) {
            return false;
        }
        
        return this._validateFileExist((String) this.data.get("hash"));
    }
    
    private boolean _validateFileExist(String hash) {
        return this._client.uploadingFiles.get(hash) != null;
    }
}