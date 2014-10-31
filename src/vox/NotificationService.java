/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vox;

import gui.NotificationItem;

/**
 *
 * @author Tug
 */
public class NotificationService {

    public static NotificationService instance;

    public NotificationItem item;

    public NotificationService() {
        this.item = new NotificationItem();
    }

    public static NotificationService getInstance() {
        if(instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public void display(String message) {
        item.update(null, message);
    }

    public NotificationItem getItem() {
        return item;
    }

}
