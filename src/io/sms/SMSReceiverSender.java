/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io.sms;

import io.Receiver;
import io.ReceiverSender;
import io.Sender;
import javax.wireless.messaging.MessageConnection;

/**
 *
 * @author Tug
 */
public class SMSReceiverSender implements ReceiverSender {

    private MessageConnection connection;
    private SMSReceiver receiver;
    private SMSSender sender;
    private String telNum;

    public SMSReceiverSender(MessageConnection connection, String telNum) {
        this.connection = connection;
        this.telNum = telNum;
        receiver = new SMSReceiver(connection);
        sender = new SMSSender(connection, telNum);
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
