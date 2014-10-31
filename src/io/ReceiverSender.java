/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io;


/**
 *
 * @author Tug
 */
public interface ReceiverSender extends Runnable {

    Sender getSender();
    Receiver getReceiver();
    void end();
    
}
