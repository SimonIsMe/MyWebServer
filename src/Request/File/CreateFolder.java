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
        
    }
    
    private boolean _validateData() {
        
        return true;
    }
}

