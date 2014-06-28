package mywebsocket;

import com.bmarius.sockets.WebSocketClient;
import com.bmarius.sockets.WebSocketManageClients;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.json.simple.JSONObject;

/**
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public abstract class JobToDo extends Thread {

    protected WebSocketClient _client;
    protected Response _response;
    public JSONObject data;
    
    protected SessionFactory _sessionFactory;
    protected Session _session;

    public abstract void run();

    public JobToDo(WebSocketClient client, String namespace, String className) {
        this._response = new Response(namespace, className);
        this._client = client;
        
        this._sessionFactory = new Configuration().configure().buildSessionFactory();
        this._session = this._sessionFactory.getCurrentSession();
    }
    
    public void setResponseId(Long responseId) {
        this._response.responseId = responseId;
    }

    protected void _sendResponse404() {
        this._response.code = Response.CODE_NOT_FOUND;
        this._response.sendOnlyMe(this._client);
    }
    
    protected void _sendOnlyToMe() {
        this._response.sendOnlyMe(this._client);
    }

    protected void _sendResponseInvalidData() {
        this._response.code = Response.CODE_INVALID_DATA;
        this._response.sendOnlyMe(this._client);
    }

    protected void _sendToAllClients() {
        this._response.clients = WebSocketManageClients.clients;
        this._response.sendToAll();
    }
    
    protected void _sendToAllClientsExceptMe() {
        this._response.sendToEverybodyExceptMe(this._client);
    }

}
