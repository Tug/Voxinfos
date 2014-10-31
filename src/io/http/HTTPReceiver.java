/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io.http;

import io.Receiver;
import javax.microedition.io.HttpConnection;

/**
 *
 * @author Tug
 */
public class HTTPReceiver extends Receiver {

    private HttpConnection connection;
    
    public HTTPReceiver(HttpConnection connection) {
        this.connection = connection;
    }

    public void end() {
    }

    public void run() {
    }

    public void newMessage(String message) {
        notifyObservers(message);
    }

}
