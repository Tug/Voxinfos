
package vox.util.poi;

import util.PersistentList;
import javame.util.Comparator;
import javame.util.Iterator;

public class POIList extends PersistentList {

    public static final Comparator poiComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            POI poi1 = (POI) o1;
            POI poi2 = (POI) o2;
            double lat1 = poi1.getCoordinates().getLatitude();
            double lat2 = poi2.getCoordinates().getLatitude();
            if(lat1 < lat2) return -1;
            else if(lat1 > lat2) return 1;
            else {
                double long1 = poi1.getCoordinates().getLongitude();
                double long2 = poi2.getCoordinates().getLongitude();
                if(long1 < long2) return -1;
                else if(long1 > long2) return 1;
                else return 0;
            }
        }
    };

    public POIList() {
        super(poiComparator);
    }

    public void cleanOld()
    {
        for(Iterator poisIter = iterator(); poisIter.hasNext(); ) {
            POI poi = (POI) poisIter.next();
            if(System.currentTimeMillis() > poi.getExpirationDate().getTime()) {
                poisIter.remove();
            }
        }
    }

}
