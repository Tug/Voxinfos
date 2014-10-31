/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vox.io;

import io.Receiver;
import javame.util.ArrayList;
import javame.util.Iterator;
import javame.util.Observable;
import javame.util.Observer;
import vox.io.DataReceiver;

/**
 *
 * @author Tug
 */
public class ReceiverService {

    private DataReceiver dataReceiver;
    private Observer observer;
    private ArrayList receivers;

    public ReceiverService() {
        this.receivers = new ArrayList();
        this.dataReceiver = DataReceiver.getInstance();
        this.observer = new Observer() {
            public void update(Observable observable, Object object) {
                if(object instanceof String) {
                    dataReceiver.receiveMessage((String)object);
                }
            }
        };
    }

    public void addReceiver(Receiver receiver) {
        receiver.addObserver(observer);
        receivers.add(receiver);
    }

    public void removeReceiver(Receiver receiver) {
        receiver.deleteObserver(observer);
        receivers.remove(receiver);
    }

    public void end() {
        Iterator it = receivers.iterator();
        while(it.hasNext()) {
            Receiver receiver = (Receiver) it.next();
            receiver.deleteObserver(observer);
            it.remove();
        }
        observer = null;
    }
}
