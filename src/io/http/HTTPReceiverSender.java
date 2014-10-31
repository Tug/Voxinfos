/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io.http;

import io.Receiver;
import io.ReceiverSender;
import io.Sender;
import javax.microedition.io.HttpConnection;

/**
 *
 * @author Tug
 */
public class HTTPReceiverSender implements ReceiverSender {

    private HttpConnection connection;
    private HTTPReceiver receiver;
    private HTTPSender sender;
    
    public HTTPReceiverSender(HttpConnection connection, StringBuffer keyParam) {
        this.connection = connection;
        receiver = new HTTPReceiver(connection);
        sender = new HTTPSender(connection, receiver, keyParam);
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
        sender.end();
    }
}
