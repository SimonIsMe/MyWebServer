package mywebsocket;

import com.bmarius.sockets.WebSocketClient;
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
    public String data = "";
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

    public boolean send() throws ResponseException {

        if (this.namespace == null) {
            throw new ResponseException("There isn't defined a namespace");
        }

        if (this.className == null) {
            throw new ResponseException("There isn't defined a class name");
        }
        
        JSONObject response = new JSONObject();
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
