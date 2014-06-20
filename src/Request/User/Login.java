package Request.User;

import com.bmarius.sockets.WebSocketClient;
import com.bmarius.sockets.WebSocketManageClients;
import entities.User;
import mywebsocket.JobToDo;
import mywebsocket.Response;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class Login extends JobToDo {

    public Login(WebSocketClient client) {
        super(client, "User", "Login");
    }

    public void run() {

        if (this._validateData() == false) {
            System.out.println("Nieprawidłowe dane");
            this._sendResponseInvalidData();
            return;
        }

        String sessionId = (String) this.data.get("sessionId");
        
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.beginTransaction();

        entities.Session userSession = 
            (entities.Session) session.get(entities.Session.class, sessionId);
        
        if (userSession == null) {
            this._sendResponseInvalidData();
            return;
        }
        
        WebSocketManageClients.addClientsSession(
            userSession.getUserId().getId(),
            this._client
        );

        tx.commit();

        this.data.put("userId", userSession.getUserId().getId());
        this.data.put("userFirstname", userSession.getUserId().getFirstname());
        this.data.put("userLastname", userSession.getUserId().getLastname());
        
        this._response.data = this.data.toString();
        this._sendToAllClientsExceptMe();
        
        Response responseToMe = new Response("User", "Login");
        this.data.put("hello", true);
        responseToMe.data = this.data.toString();
        responseToMe.sendOnlyMe(this._client);
    }

    private boolean _validateData() {
        if (this.data.get("sessionId") == null) {
            return false;
        }

        return true;
    }
}
