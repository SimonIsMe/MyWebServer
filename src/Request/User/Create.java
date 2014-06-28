package Request.User;

import com.bmarius.sockets.WebSocketClient;
import entities.User;
import mywebsocket.JobToDo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class Create extends JobToDo {

    public Create(WebSocketClient client) {
        super(client, "User", "Create");
    }

    public void run() {

        if (this._validateData() == false) {
            System.out.println("Nieprawidłowe dane");
            this._sendResponseInvalidData();
            return;
        }

        User user = new User();

        user.setFirstname((String) this.data.get("firstname"));
        user.setLastname((String) this.data.get("lastname"));

        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.beginTransaction();

        Long userId = (Long) session.save(user);

        tx.commit();

        this.data.put("id", userId);
        this._response.data = this.data;
        this._sendToAllClients();
    }

    private boolean _validateData() {
        if (this.data.get("firstname") == null) {
            return false;
        }
        if (this.data.get("lastname") == null) {
            return false;
        }

        return true;
    }
}
