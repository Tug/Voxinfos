

package javame.location;

import org.j4me.bluetoothgps.*;
import javame.io.Serializable;
import javame.util.Observable;


public abstract class GPSDevice implements Serializable, Runnable, LocationListener
{
    public abstract String getName();
    public abstract LocationProvider getLocationProvider();

    private static final int INTERVAL = 5; // seconds between getting new location information
    private static final int TIMEOUT = -1;  // Default
    private static final int MAX_AGE = -1;  // Default

    private Observable locationObs;
    private Observable stateObs;
    private boolean running;

    public void setLocationObservable(Observable locationObs) {
        this.locationObs = locationObs;
    }

    public void setStateObservable(Observable stateObs) {
        this.stateObs = stateObs;
    }

    public void run() {
        running = true;
        LocationProvider lp = getLocationProvider();
        try {
            lp.getLocation(60);
        } catch (LocationException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        lp.setLocationListener(this, INTERVAL, TIMEOUT, MAX_AGE);
        running = false;
    }

    public void locationUpdated(LocationProvider provider, Location location) {
        if(locationObs != null && location.isValid()) {
            locationObs.notifyObservers(location);
        }
    }

    public void providerStateChanged(LocationProvider provider, int newState) {
        if(stateObs != null) {
            stateObs.notifyObservers(new Integer(newState));
        }
    }

    public void end() {
        running = false;
        getLocationProvider().setLocationListener(null, -1, -1, -1);
        getLocationProvider().close();
    }
    
}
