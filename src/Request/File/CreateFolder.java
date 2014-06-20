package Request.File;

import com.bmarius.sockets.WebSocketClient;
import entities.File;
import mywebsocket.JobToDo;
import mywebsocket.ThreadQueues;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import repositories.FileRepository;

/**
 *
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class CreateFolder extends JobToDo {
    
    public CreateFolder(WebSocketClient client) {
        super(client, "File", "CreateFolder");
    }
    
    public void run() {
        System.out.println(this.data.toString());
    }
    
    private boolean _validateData() {
        
        String name = (String) this.data.get("name");
        if (name == null) {
            return false;
        }
        
        if (this.data.get("parentId") == null) {
            return false;
        }
        int parentId = (int) this.data.get("parentId");
        
        return true;
    }
}