/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io.datagram;

import io.Receiver;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.microedition.io.PushRegistry;
import javax.microedition.io.UDPDatagramConnection;
import midlets.VOXINFOS;
import javame.util.Observable;

/**
 *
 * @author Tug
 */
public class DatagramReceiver extends Receiver {

    private DatagramConnection conn;
    private boolean end = false;

    public DatagramReceiver(DatagramConnection conn) {
        this.conn = conn;
        new Thread(this).start();
    }

    public void run() {
        try {
            while(!end) {
                Datagram data = conn.newDatagram(conn.getMaximumLength());
                try {
                    conn.receive(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                notifyObservers(data.readUTF());
            }
            conn.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void end() {
        end = true;
    }

}
