

package javame.location;

import javame.text.NumberFormat;
import org.j4me.bluetoothgps.*;

/**
 *
 * @author Tugdual
 */
public class GPSUtil
{

    public static LocationProvider createLocationProvider()
    {
        return null;
    }

    public static String getLatitude(Location location)
    {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(4);
        return nf.format(location.getQualifiedCoordinates().getLatitude());
    }

    public static String getLongitude(Location location)
    {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(4);
        return nf.format(location.getQualifiedCoordinates().getLongitude());
    }

    public static String getLatitudeDDMMSS(Location location)
    {
        return Coordinates.convert(
                location.getQualifiedCoordinates().getLatitude(),
                Coordinates.DD_MM_SS);
    }

    public static String getLongitudeDDMMSS(Location location)
    {
        return Coordinates.convert(
                location.getQualifiedCoordinates().getLongitude(),
                Coordinates.DD_MM_SS);
    }

    public static String getSpeed(Location location)
    {
        return ""+location.getSpeed();
    }

    public static String getSpeedKmh(Location location)
    {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);
        return nf.format(location.getSpeed() * 3.6);
    }

    public static String getCourse(Location location)
    {
        return ""+(int)location.getCourse();
    }

}
