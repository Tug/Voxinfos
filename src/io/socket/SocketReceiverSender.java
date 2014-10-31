/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io.socket;

import io.Receiver;
import io.ReceiverSender;
import io.Sender;
import java.io.IOException;
import javax.microedition.io.SocketConnection;

/**
 *
 * @author Tug
 */
public class SocketReceiverSender implements ReceiverSender {

    private SocketConnection connection;
    private SocketReceiver receiver;
    private SocketSender sender;

    public SocketReceiverSender(SocketConnection connection) {
        this.connection = connection;
        receiver = new SocketReceiver(connection);
        try {
            sender = new SocketSender(connection);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Sender getSender() {
        return sender;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void run() {
        new Thread(receiver).start();
    }

    public void end() {
        receiver.end();
        if(connection != null) {
            try {
                connection.close();
            } catch (IOException ex) { }
        }
    }

}