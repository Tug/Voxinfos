/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io.socket;

import io.Sender;
import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;

/**
 *
 * @author Tug
 */
public class SocketSender implements Sender {

    private StreamConnection conn;
    private OutputStream os;
    private boolean end = false;

    public SocketSender(StreamConnection conn) throws IOException {
        this.conn = conn;
        this.os = conn.openOutputStream();
    }

    public void sendTextMessage(String message) {
        try {
            os.write((message + "\n").getBytes("UTF-8"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void end()
    {
        if(os != null) {
            try {
                os.close();
            } catch (IOException ex) { }
        }
    }



}
