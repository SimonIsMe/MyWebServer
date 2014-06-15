package com.bmarius.sockets;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import mywebsocket.JobToDo;
import mywebsocket.Response;
import mywebsocket.ResponseException;
import mywebsocket.ThreadQueues;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class WebSocketClient implements Runnable {

    public Socket socket;
    private InputStream in = null;
    private OutputStream out = null;

    public void run() {
        try {
            
            ThreadQueues.getInstance();
            
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
            String line;
            while(true) {
                line = this._read();
                this._parseRequest(line);
            }
        } catch (IOException ex) {
            if (ex.getMessage().equals("Wrong opcode: 8")) {
                System.out.println("Connection closed");
                WebSocketManageClients.clients.remove(this);
                return;
            }
            Logger.getLogger(WebSocketClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * A method which is using to parsing incoming json request, and
     * calling a specific object extending by JobToDo class
     */
    private void _parseRequest(String line) {
        
        System.out.println("###   " + line);
        
//        line = "{\"namespace\": \"Uer\", \"className\": \"ChangeName\", \"data\": {\"userId\":123, \"firstName\":\"Andrzej\"}}";
        JSONObject obj;
        try {
            obj = (JSONObject) JSONValue.parse(line);
            obj.get("namespace");
            obj.get("className");
        } catch (Exception ex) {
            this._sendIncorrectJsonResponse();
            return;
        }
        
        if (obj.get("namespace") == null || obj.get("className") == null) {
            this._sendNotFoundResponse(
                (String) obj.get("namespace"), 
                (String) obj.get("className")
            );
            return;
        }
        
        String className = (String) obj.get("className");
        String namespace = (String) obj.get("namespace");
        
        System.out.println("###   " + line);
        System.out.println("Request." + namespace + "." + className);
        
        try {
            Constructor classObject = 
                    Class.forName("Request." + namespace + "." + className)
                            .getConstructor(WebSocketClient.class);
            
            JobToDo jobToDo = (JobToDo) classObject.newInstance(this);
            jobToDo.data = (JSONObject) ((JSONObject) obj.get("data"));
            
            ThreadQueues.getInstance().addJobToDo(jobToDo);
            
        } catch (ClassNotFoundException ex) {
            this._sendNotFoundResponse(namespace, className);
        } catch (InstantiationException ex) {
            this._sendNotFoundResponse(namespace, className);
        } catch (IllegalAccessException ex) {
            this._sendNotFoundResponse(namespace, className);
        } catch (NoSuchMethodException ex) {
            this._sendNotFoundResponse(namespace, className);
        } catch (SecurityException ex) {
            this._sendNotFoundResponse(namespace, className);
        } catch (IllegalArgumentException ex) {
            this._sendNotFoundResponse(namespace, className);
        } catch (InvocationTargetException ex) {
            this._sendNotFoundResponse(namespace, className);
        }        
    }
    
    private void _sendIncorrectJsonResponse() {
        Response response = new Response("", "", this);
        response.code = Response.CODE_ERROR;
        response.data = "{\"text\":\"Incorrect JSON text.\"}";
        try {
            response.send();
        } catch (ResponseException ex) {
            Logger.getLogger(WebSocketClient.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
    
    private void _sendNotFoundResponse(String namespace, String className) {
        Response response = new Response(namespace, className, this);
        response.code = Response.CODE_NOT_FOUND;
        response.data = "{\"text\":\"Not found\"}";
        try {
            response.send();
        } catch (ResponseException ex) {
            Logger.getLogger(WebSocketClient.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     *
     * @param message to send
     * @throws java.io.IOException
     */
    public void send(String message) throws IOException {
        /**
         * get message bytes
         */

        System.out.println(out);
        byte[] utf = message.getBytes("UTF8");

        /**
         * write 129 bytes
         */
        out.write(129);

        /**
         * writing message length
         */
        if(utf.length > 65535) {
            out.write(127);
            out.write(utf.length >> 16);
            out.write(utf.length >> 8);
            out.write(utf.length);
        }
        else if(utf.length>125) {
            out.write(126);
            out.write(utf.length >> 8);
            out.write(utf.length);
        }
        else {
            out.write(utf.length);
        }

        out.write(utf);
    }
    
    private String _read() throws IOException {

        int opcode = in.read();
        //boolean whole = (opcode & 0b10000000) !=0;
        opcode = opcode & 0xF;

        if(opcode!=1)
            throw new IOException("Wrong opcode: " + opcode);

        int len = in.read();
        boolean encoded = (len >= 128);

        if(encoded)
            len -= 128;

        if(len == 127) {
            len = (in.read() << 16) | (in.read() << 8) | in.read();
        }
        else if(len == 126) {
            len = (in.read() << 8) | in.read();
        }

        byte[] key = null;

        if(encoded) {
            key = new byte[4];
            readFully(key);
        }

        byte[] frame = new byte[len];

        readFully(frame);

        if(encoded) {
            for(int i=0; i<frame.length; i++) {
                frame[i] = (byte) (frame[i] ^ key[i%4]);
            }
        }

        return new String(frame, "UTF8");
    }
    
    private void readFully(byte[] b) throws IOException {

        int readen = 0;
        while(readen<b.length)
        {
            int r = in.read(b, readen, b.length-readen);
            if(r==-1)
                break;
            readen+=r;
        }
    }
    
}
