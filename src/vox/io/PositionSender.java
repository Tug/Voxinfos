package vox.io;

import io.Sender;
import javame.location.GPSLocator;
import javame.util.Observable;
import javame.util.Observer;
import org.j4me.bluetoothgps.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.IntegerWrapper;

public class PositionSender implements Observer, Runnable
{
    private Sender sender;
    private boolean end;
    private Location currentLocation;
    private IntegerWrapper delaySec;
    private JSONObject msgJSON;
    private final Object locationLock = new Object();

    public PositionSender(Sender sender, IntegerWrapper delaySec)
    {
        this.sender = sender;
        this.delaySec = delaySec;
        this.msgJSON = new JSONObject();
    }

    public PositionSender(Sender sender)
    {
        this(sender, null);
    }

    public void run()
    {
        end = false;
        GPSLocator.getInstance().addLocationObserver(this);
        while(!end)
        {
            try {
                sendPositionSync();
            } catch (LocationException le) {
                le.printStackTrace();
            } catch (JSONException jse) {
                jse.printStackTrace();
            }
            try {
                Thread.sleep(delaySec.getValue() * 1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public synchronized void sendPositionSync() throws LocationException, JSONException
    {
        if(currentLocation == null) waitForPosition();
        JSONArray locJSON = Position.format(currentLocation);
        msgJSON.put("loc", locJSON);
        sender.sendTextMessage(msgJSON.toString());
        msgJSON = new JSONObject();
    }

    public synchronized void addExtra(String key, Object value) throws JSONException {
        msgJSON.put(key, value);
    }

    public void end()
    {
        end = true;
        GPSLocator.getInstance().deleteLocationObserver(this);
    }

    public void waitForPosition() throws LocationException
    {
        synchronized(locationLock) {
            try {
                locationLock.wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void update(Observable observable, Object object)
    {
        if(object instanceof Location) {
            currentLocation = (Location)object;
            synchronized(locationLock) {
                locationLock.notifyAll();
            }
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

}
