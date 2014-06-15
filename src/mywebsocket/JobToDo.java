package mywebsocket;

import com.bmarius.sockets.WebSocketClient;
import com.bmarius.sockets.WebSocketManageClients;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public abstract class JobToDo extends Thread {
    
    protected WebSocketClient _client;
    protected Response _response;
    public JSONObject data;
    
    public abstract void run();
    
    public JobToDo(WebSocketClient client, String namespace, String className) {
        this._response = new Response(namespace, className);
        this._client = client;
    }
    
    protected void _sendResponse404() {
        try {
            this._response.code = Response.CODE_NOT_FOUND;
            this._response.clients.add(this._client);
            this._response.send();
        } catch (ResponseException ex) {
            Logger.getLogger(JobToDo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void _sendResponseInvalidData() {
        try {
            this._response.code = Response.CODE_INVALID_DATA;
            this._response.clients.add(this._client);
            this._response.send();
        } catch (ResponseException ex) {
            Logger.getLogger(JobToDo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void _sendToAllClients() {
        try {
            this._response.clients = WebSocketManageClients.clients;
            this._response.send();
        } catch (ResponseException ex) {
            Logger.getLogger(JobToDo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
