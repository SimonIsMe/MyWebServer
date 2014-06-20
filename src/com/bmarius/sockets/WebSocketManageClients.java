package com.bmarius.sockets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class WebSocketManageClients {
    
    public static LinkedList<WebSocketClient> clients = new LinkedList<WebSocketClient>();
    public static HashMap<Integer, LinkedList> clientsSessions = new HashMap<Integer, LinkedList>();
    
    public static void addClientsSession(int userId, WebSocketClient webSocketClient) {
        
        LinkedList<WebSocketClient> oneClientSession = 
            WebSocketManageClients.clientsSessions.get(userId);
        if (oneClientSession == null) {
            oneClientSession = new LinkedList<WebSocketClient>();
            WebSocketManageClients.clientsSessions.put(userId, oneClientSession);
        }
        
        oneClientSession.add(webSocketClient);
    }
    
    public static void removeClientsSession(int userId, WebSocketClient webSocketClient) {
        
        LinkedList<WebSocketClient> oneClientSession = 
            WebSocketManageClients.clientsSessions.get(userId);
        if (oneClientSession == null) {
            oneClientSession = new LinkedList<WebSocketClient>();
            WebSocketManageClients.clientsSessions.put(userId, oneClientSession);
        }
        
        oneClientSession.remove(webSocketClient);
    }
    
    public static LinkedList<WebSocketClient> getWebSocketClientsByUserId(LinkedList<Integer> userIds) {
        LinkedList<WebSocketClient> toReturn = new LinkedList<WebSocketClient>();
        
        int userId;
        while (userIds.isEmpty() == false) {
            userId = userIds.getFirst();
            toReturn.addAll(WebSocketManageClients.clientsSessions.get(userId));
        }
        
        return toReturn;
    }
    
    public static LinkedList<WebSocketClient> getWebSocketClientsByUserIdExceptFew(LinkedList<Integer> userIds, LinkedList<WebSocketClient> webSocketClientsToExcept) {
        LinkedList<WebSocketClient> toReturn = new LinkedList<WebSocketClient>();
        
        int userId;
        while (userIds.isEmpty() == false) {
            userId = userIds.removeFirst();
            toReturn.addAll(WebSocketManageClients.clientsSessions.get(userId));
        }
        
        WebSocketClient webSocketClient;
        while (webSocketClientsToExcept.isEmpty() == false) {
            webSocketClient = webSocketClientsToExcept.removeFirst();
            toReturn.remove(webSocketClient);
        }
        
        return toReturn;
    }
}
