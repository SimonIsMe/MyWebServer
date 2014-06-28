package mywebsocket;

import com.bmarius.sockets.WebSocketClient;
import com.bmarius.sockets.WebSocketManageClients;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class Response {

    public static final int CODE_OK = 200;
    public static final int CODE_INVALID_DATA = 400;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_ERROR = 500;

    public int code = CODE_OK;
    public String namespace;
    public String className;
    public JSONObject data = new JSONObject();
    public Long responseId;
    public LinkedList<WebSocketClient> clients = new LinkedList<WebSocketClient>();

    public Response() {
    }

    public Response(String namespace, String className) {
        this.namespace = namespace;
        this.className = className;
    }
    
    public Response(String namespace, String className, WebSocketClient client) {
        this.namespace = namespace;
        this.className = className;
        this.clients.add(client);
    }
    
    public Response clone() {
        Response clone = new Response();
        clone.namespace = this.namespace;
        clone.className = this.className;
        clone.data = this.data;
        clone.responseId = this.responseId;
        clone.clients = this.clients;
        return clone;
    }
    
    public boolean sendToEverybodyExceptMe(WebSocketClient webSocketClient) {
        try {
            this.clients = (LinkedList<WebSocketClient>) WebSocketManageClients.clients.clone();
            this.clients.remove(webSocketClient);
            return this.send();
        } catch (ResponseException ex) {
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean sendOnlyMe(WebSocketClient webSocketClient) {
        try {
            this.clients.add(webSocketClient);
            return this.send();
        } catch (ResponseException ex) {
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean sendToAll() {
        try {
            this.clients = WebSocketManageClients.clients;
            return this.send();
        } catch (ResponseException ex) {
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean send(LinkedList<Integer> userIds) {
        try {
            this.clients
                    = WebSocketManageClients.getWebSocketClientsByUserId(userIds);
            return this.send();
        } catch (ResponseException ex) {
            Logger.getLogger(Response.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean send() throws ResponseException {

        if (this.namespace == null) {
            throw new ResponseException("There isn't defined a namespace");
        }

        if (this.className == null) {
            throw new ResponseException("There isn't defined a class name");
        }
        
        JSONObject response = new JSONObject();
        if (this.responseId != null) {
            response.put("responseId", this.responseId);
        }
        response.put("code", this.code);
        response.put("namespace", this.namespace);
        response.put("className", this.className);
        response.put("data", this.data);
        
        boolean toReturn = true;
        for (int i = 0; i < this.clients.size(); i++) {
            try {
                this.clients.get(i).send(response.toString());
            } catch (IOException ex) {
                toReturn = false;
            }
        }
        
        return toReturn;
    }

}
