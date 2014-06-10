package mywebsocket;

import com.bmarius.sockets.WebSockets;

/**
 *
 * @author szymon
 */
public class MyWebSocket {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WebSockets websocket = new WebSockets();
        websocket.run();
    }
    
}
