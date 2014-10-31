/*
 * To change this template, choose Tools | Templates
 *  * and open the template in the editor.
 */

package vox.util.poi;

import java.util.Hashtable;
import org.j4me.bluetoothgps.*;
import javame.util.Iterator;
import javame.util.Observable;
import javame.util.Observer;

/**
 *
 * @author Tug
 */
public class POIProximityAlerter extends Observable implements ProximityListener
{
    private Hashtable cooPoi;
    private POIList poiList;

    public POIProximityAlerter(POIList poiList)
    {
        this.poiList = poiList;
        this.cooPoi = new Hashtable();
        addProximityListeners(poiList.iterator());
        poiList.addObserver(new Observer() {
            public void update(Observable observable, Object object) {
                if(object != null) {
                    POI poi = (POI) object;
                    addProximityListener(poi);
                }
            }
        });
        
    }

    public void addProximityListeners(Iterator poisIter)
    {
        while(poisIter.hasNext()) {
            POI poi = (POI) poisIter.next();
            addProximityListener(poi);
        }
    }

    public void addProximityListener(POI poi)
    {
        Coordinates coor = poi.getCoordinates();
        try {
            //LocationProvider lp = GPSLocator.getInstance().getLocationProvider();
            LocationProvider.addProximityListener(this, coor, poi.getProximityRadius());
            cooPoi.put(coor, poi);
        } catch (LocationException ex) {
            ex.printStackTrace();
        }
    }

    public void monitoringStateChanged(boolean bln) {

    }

    public void proximityEvent(Coordinates crdnts, Location lctn) {
        POI poi = (POI) cooPoi.get(crdnts);
        notifyObservers(poi);
    }

}
