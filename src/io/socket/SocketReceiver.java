/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io.socket;

import io.Receiver;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.SocketConnection;

/**
 *
 * @author Tug
 */
public class SocketReceiver extends Receiver {

    private SocketConnection conn;
    private boolean end = false;

    public SocketReceiver(SocketConnection conn) {
        this.conn = conn;
    }

    public void run() {
        try {
            InputStream is = conn.openInputStream();
            while(!end) {
                StringBuffer msgBuffer = new StringBuffer();
                int c = 0;
                while(((c = is.read()) != '\n') && (c != -1)) {
                    msgBuffer.append((char) c) ;
                }
                if(c == -1) break;
                notifyObservers(msgBuffer.toString());
            }
            is.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void end() {
        end = true;
    }

}
