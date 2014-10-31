
package io.sms;

import io.Receiver;
import java.io.IOException;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.TextMessage;
import javax.wireless.messaging.BinaryMessage;

public class SMSReceiver extends Receiver implements MessageListener
{
    private MessageConnection messageConnection;

    public SMSReceiver(MessageConnection messageConnection)
    {
        super();
        this.messageConnection = messageConnection;
    }

    public void notifyIncomingMessage(MessageConnection mc)
    {
        try {
            Message message = mc.receive();
            if(message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage)message;
                System.out.println("New message received from "+textMessage.getAddress());
                System.out.println("Message content : "+textMessage.getPayloadText());
                notifyObservers(textMessage.getPayloadText());
            }  else if (message instanceof BinaryMessage) {
                BinaryMessage binaryMessage = (BinaryMessage)message;
                byte[] bytes = ((BinaryMessage)message).getPayloadData();
                String text = "";
                // On ignore l'en-tête.
                for(int i=7; i< bytes.length; i++){
                    text += (char)bytes[i];
                }
                System.out.println("New message received from "+ binaryMessage.getAddress());
                System.out.println("Message content : "+ text);
                notifyObservers(text);
            } else  {
                //Message can be binary or multipart
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            messageConnection.setMessageListener(this);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void end() {
        try {
            messageConnection.setMessageListener(null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
}
