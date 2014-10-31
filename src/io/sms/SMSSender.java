
package io.sms;

import io.Sender;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;
import vox.Config;

/**
 *
 * @author Tug
 */
public class SMSSender implements Sender {

    private MessageConnection messageConnection;
    private String destinationNumber;

    public SMSSender(MessageConnection messageConnection, String destinationNumber)
    {
        this.messageConnection = messageConnection;
        this.destinationNumber = destinationNumber;
    }
    
    public void sendTextMessage(String message)
    {
        System.out.println("Sending sms to "+destinationNumber);
        System.out.println("Message content : "+message);
        try {
            TextMessage txtmessage =
                (TextMessage) messageConnection.newMessage(MessageConnection.TEXT_MESSAGE);
            txtmessage.setAddress("sms://"+destinationNumber);
            txtmessage.setPayloadText(message);
            messageConnection.send(txtmessage);
            System.out.println("Message sent");
        }
        catch (Throwable t) {
            System.out.println ("Send caught: ");
            t.printStackTrace();
        }
    }

    public void end()
    {
    }

}
