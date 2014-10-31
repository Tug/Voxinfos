/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io.http;

import io.Sender;
import java.io.IOException;
import javax.microedition.io.HttpConnection;

/**
 *
 * @author Tug
 */
public class HTTPSender implements Sender {

    private HttpConnection connection;
    private HTTPConnector connector;
    private HTTPReceiver receiver;
    private StringBuffer keyParam;

    public HTTPSender(HttpConnection connection, HTTPReceiver receiver, StringBuffer keyParam) {
        this.connection = connection;
        this.receiver = receiver;
        this.keyParam = keyParam;
        this.connector = new HTTPConnector(connection);
    }

    public void sendTextMessage(String message)
    {
        String content = "body="+message;
        if(keyParam != null)
            content += "&" + "key="+keyParam.toString();
        try {
            String response = connector.post(content);
            receiver.newMessage(response);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void end()
    {

    }

}
