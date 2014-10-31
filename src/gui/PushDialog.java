

package gui;

import javax.microedition.io.Connector;
import javax.microedition.io.PushRegistry;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Tugdual de Kerviler
 */
public class PushDialog extends MIDlet implements CommandListener
{
    private static final int maxCount = 256;
    // Wait for 2sec
    private static final int DefaultTimeout = 2000;

    private String alertText;
    private Command exitCommand = new Command ("Exit", Command.EXIT, 2);
    private Alert alert;
    private Display display;

    /**
     * initialize the MIDlet with the current display object.
     */
    public PushDialog(String alertText) {
        this.alertText = alertText;
        display = Display.getDisplay (this);
    }

  
    public void startApp () {

        alert = new Alert("Voxinfos alert", alertText, null, AlertType.INFO);
        alert.setTimeout(DefaultTimeout);

        /* Bytes read from the URL update connection. */
        int count;

        /* Check for inbound async connection for sample Finger port. */
        String[] connections = PushRegistry.listConnections (true);

        /* HttpView was started to handle inbound request. */
        String pushProperty = getAppProperty ("MIDlet-Push-1");

        if ((connections != null) && (connections.length > 0)) {
            String newurl = "Pushed URL Placeholder";

            /* Test basic get registry information interfaces. */
            try {
                String midlet = PushRegistry.getMIDlet (connections[0]);
                String filter = PushRegistry.getFilter (connections[0]);
            }
            catch (Exception e) {
                e.printStackTrace ();
            }

            /* Check for socket or datagram connection. */
            if (connections[0].startsWith ("socket://")) {
                try {
                    /* Simple test assumes a server socket connection. */
                    ServerSocketConnection scn =
                        (ServerSocketConnection) Connector.open (connections[0]);
                    SocketConnection sc = (SocketConnection) scn.acceptAndOpen ();

                    /* Read one line of text as a new URL to add to the list. */
                    DataInputStream dis = sc.openDataInputStream ();
                    byte[] buf = new byte[256];
                    int endofline = 0;
                    for (int i = 0; i < maxCount; i++) {
                        buf[i] = dis.readByte ();
                        if (buf[i] == '\n') {
                            endofline = i;
                            break;
                        }
                    }

                    newurl = new String (buf, 0, endofline);

                    dis.close ();

                    sc.close ();
                    scn.close ();
                }
                catch (IOException e) {
                    System.err.println ("******* io exception in push example");
                    e.printStackTrace ();
                }
            }
            else {
                System.err.println ("Unknown connection type");
            }
        }
        else {
            connections = PushRegistry.listConnections (false);
        }

        display.setCurrent (alert);
    }

   
    public void pauseApp () {
    }

   
    public void destroyApp (boolean unconditional) {
    }

   
    public void commandAction (Command c, Displayable s) {
        try {
            if (c == exitCommand) {
                destroyApp (false);
                notifyDestroyed ();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace ();
        }
    }
}