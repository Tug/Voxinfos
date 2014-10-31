
package javame.location;

import org.j4me.bluetoothgps.*;
import javame.util.Observable;
import javame.util.Observer;

/**
 *
 * @author Tug
 */
public class GPSLocator {

    private static GPSLocator instance;

    private GPSDevice gpsDevice;
    private Location currentLocation;
    private Integer currentState;
    private Observable locationObs;
    private Observable stateObs;

    public GPSLocator() {
        locationObs = new Observable();
        stateObs = new Observable();
        locationObs.addObserver(new Observer() {
            public void update(Observable observable, Object object) {
                currentLocation = (Location) object;
            }
        });
        stateObs.addObserver(new Observer() {
            public void update(Observable observable, Object object) {
                currentState = (Integer) object;
            }
        });
    }

    public static GPSLocator getInstance() {
        if(instance == null)
            instance = new GPSLocator();
        return instance;
    }

    public void addLocationObserver(Observer o) {
        locationObs.addObserver(o);
        if(currentLocation != null)
            o.update(locationObs, currentLocation);
    }

    public void addStateObserver(Observer o) {
        stateObs.addObserver(o);
        if(currentState != null)
            o.update(stateObs, currentState);
    }

    public void deleteLocationObserver(Observer o) {
        locationObs.deleteObserver(o);
    }

    public void deleteStateObserver(Observer o) {
        stateObs.deleteObserver(o);
    }

    public Observable getLocationObservable() {
        return locationObs;
    }

    public Observable getStateObservable() {
        return stateObs;
    }

    public GPSDevice getGPSDevice() {
        return gpsDevice;
    }

    public void setGPSDevice(GPSDevice newGpsDevice) {
        if(this.gpsDevice != null) {
            this.gpsDevice.end();
        }
        this.gpsDevice = newGpsDevice;
        gpsDevice.setLocationObservable(locationObs);
        gpsDevice.setStateObservable(stateObs);
        new Thread(gpsDevice).start();
    }

    public void end() {
        if(gpsDevice != null) {
            gpsDevice.end();
        }
    }

    public static void close() {
        instance.end();
        instance = null;
    }

    public boolean hasLocation() {
        return currentLocation != null;
    }
    
}
