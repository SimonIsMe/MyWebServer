package com.bmarius.sockets;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Szymon Skrzyński <skrzynski.szymon@gmail.com>
 */
public class WebSocketClient implements Runnable {

    public Socket socket;
    private InputStream in = null;
    private OutputStream out = null;

    public void run() {
        try {
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
            String line;
            while(true) {
                line = this._read();
                this._send(line);
                System.out.println(line);
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
     *
     * @param message to send
     * @throws java.io.IOException
     */
    private void _send(String message) throws IOException {
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
