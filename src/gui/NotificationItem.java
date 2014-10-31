/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Graphics;
import javame.util.Observable;
import javame.util.Observer;

/**
 *
 * @author Tug
 */
public class NotificationItem extends CustomItem implements Observer {
    
    private String prefix;
    

    public NotificationItem(String prefix) {
        super("");
        this.prefix = prefix;
    }

    public NotificationItem() {
        this("Notification : ");
    }
    
    public void paint(Graphics g, int w, int h) {
    }

    protected int getPrefContentHeight(int width) {
        return 10;
    }

    protected int getPrefContentWidth(int height) {
        return 200;
    }

    protected int getMinContentHeight() {
        return 10;
    }

    protected int getMinContentWidth() {
        return 50;
    }

    protected boolean traverse(int dir, int viewportWidth, int viewportHeight,
                               int[] visRect_inout) {
        return true;
    }

    public void update(Observable observable, Object object) {
        if(object instanceof String) {
            String str = (String) object;
            this.setLabel(prefix+str);
        }
    }
}
