

package gui;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;

/**
 *
 * @author Tug
 */
public class DynamicForm extends Form
{
    private Display display;
    private Thread refreshTread;
    private long refreshRate = 1000; // 1 sec
    private boolean displayed;

    public DynamicForm(Display display, String title, Item[] items)
    {
        super(title);
        this.display = display;
        this.displayed = false;
        refreshTread = new Thread(){
            public void run()
            {
                while(true) {
                    refreshDisplay();
                    synchronized(this) {
                        try {
                            wait(refreshRate);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    public DynamicForm(Display display, String title)
    {
        this(display, title, null);
    }

    public void setRefreshrate(long newRefreshRate)
    {
        this.refreshRate = newRefreshRate;
    }

    public void startDisplaying()
    {
        refreshDisplay();
        displayed = true;
    }

    public void stopDisplaying()
    {
        displayed = false;
    }

    public void refreshDisplay()
    {
        if(displayed)
            display.setCurrent(this);
    }

}
