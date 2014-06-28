package Request.User;

import com.bmarius.sockets.WebSocketClient;
import com.sun.org.apache.xerces.internal.util.FeatureState;
import entities.User;
import java.util.logging.Level;
import java.util.logging.Logger;
import mywebsocket.JobToDo;
import mywebsocket.Response;
import mywebsocket.ResponseException;
import mywebsocket.ThreadQueues;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import repositories.UserRepository;

/**
 *
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class ChangeName extends JobToDo {
    
    public ChangeName(WebSocketClient client) {
        super(client, "User", "ChangeName");
    }
    
    public void run() {
        
        System.out.println("Zmieniam nazwę!!!!!");

        if (this._validateData() == false) {
            System.out.println("Nieprawidłowe dane");
            this._sendResponseInvalidData();
            return;
        }
        
        Long userId = (Long) this.data.get("id");
        
        User user = UserRepository.getInstance().getUser(userId);
        if (user == null) {
            this._sendResponse404();
            return;
        }
        
        if (user.isLock()) {
            ThreadQueues.getInstance().addJobToDo(this);
        } else {
            user.lock();
            
            user.setFirstname((String) this.data.get("firstname"));
            user.setLastname((String) this.data.get("lastname"));
            
            SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
            Session session = sessionFactory.getCurrentSession();
            Transaction tx = session.beginTransaction();

            session.saveOrUpdate(user);
            
            tx.commit();
            user.unlock();
            
            this._response.data = this.data;
            this._sendToAllClients();
        }
    }
    
    private boolean _validateData() {
        if (this.data.get("id") == null) {
            return false;
        }
        if (this.data.get("firstname") == null) {
            return false;
        }
        if (this.data.get("lastname") == null) {
            return false;
        }
        
        return true;
    }
}

