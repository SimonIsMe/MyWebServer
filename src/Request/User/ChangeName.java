package Request.User;

import com.sun.org.apache.xerces.internal.util.FeatureState;
import mywebsocket.JobToDo;
import mywebsocket.ThreadQueues;
import mywebsocket.User;
import mywebsocket.UserRepository;

/**
 *
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class ChangeName extends JobToDo {
    
    public void run() {
        
        User user = UserRepository.getInstance().getUser(123);
        
        if (user.isLock()) {
            ThreadQueues.getInstance().addJobToDo(this);
        } else {
            user.lock();
            user.setFirstName("Andrzej");
            user.save(); 
            user.unlock();
        }
    }
    
}

