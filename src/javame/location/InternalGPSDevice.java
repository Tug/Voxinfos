

package javame.location;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.j4me.bluetoothgps.*;
import util.i18n;
/**
 *
 * @author Tug
 */
public class InternalGPSDevice extends GPSDevice
{
    private static final String integratedGPSTitleStr = i18n.s(84);
    private int level;
    private LocationProvider lp = null;
    private static String type = null;

    public InternalGPSDevice()
    {
        this.level = 0;
    }

    public InternalGPSDevice(int startLevel)
    {
        this.level = startLevel;
    }

    public String getName() {
        if(type == null) type = "level "+(level-1);
        return integratedGPSTitleStr + " ("+type+")";
    }

    public LocationProvider getLocationProvider()
    {
        if(lp == null) {
            level = 0;
            while(level < 5)
            {
                Criteria criteria = getCriteria(level++);
                try {
                    lp = LocationProvider.getInstance(criteria);
                    if(lp != null) break;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return lp;
    }

    public static Criteria getCriteria(int level)
    {
        Criteria criteria = new Criteria();
        switch(level)
        {
        case 0:
            return null;
        case 1:
            criteria.setPreferredResponseTime(3000);
            criteria.setSpeedAndCourseRequired(true);
            criteria.setAltitudeRequired(false);
            criteria.setAddressInfoRequired(false);
            criteria.setHorizontalAccuracy(100);
            criteria.setHorizontalAccuracy(100);
            criteria.setCostAllowed(false);
            criteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_MEDIUM);
            type = "medium";
            return criteria;
        case 2:
            criteria.setSpeedAndCourseRequired(true);
            criteria.setAltitudeRequired(false);
            criteria.setAddressInfoRequired(false);
            criteria.setHorizontalAccuracy(500);
            criteria.setHorizontalAccuracy(500);
            criteria.setCostAllowed(false);
            return criteria;
        case 3:
            type = "defaut";
            return criteria;
        default:
            return null;
        }
    }

    public void serialize(DataOutput output) throws IOException {
        output.writeInt(level);
    }

    public void deserialize(DataInput input) throws IOException {
        this.level = input.readInt();
    }

}
