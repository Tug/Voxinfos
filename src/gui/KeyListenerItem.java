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
 * @author theodore
 */


public class KeyListenerItem extends CustomItem {

    private Observable observable;

    public KeyListenerItem(String label) {
        super(label);
        observable = new Observable();
    }
    
    public void addObserver(Observer o) {
        observable.addObserver(o);
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

    protected void keyPressed(int keyCode) {
        //System.out.println(keyCode);
        observable.notifyObservers(new Integer(keyCode));
    }
    
    protected boolean traverse(int dir, int viewportWidth, int viewportHeight,
                               int[] visRect_inout) {
        return true;
    }
}
