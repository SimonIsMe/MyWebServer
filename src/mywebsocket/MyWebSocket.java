package mywebsocket;

import Request.User.ChangeName;
import com.bmarius.sockets.WebSockets;

/**
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class MyWebSocket {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WebSockets websocket = new WebSockets();
        websocket.run();
        
        //  odebrałem wiadomośc, że należy zmienić userowi imię
        ChangeName changeName = new ChangeName();     
        ThreadQueues.getInstance().addJobToDo(changeName);
    }
    
}
