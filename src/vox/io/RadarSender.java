package vox.io;

import javame.location.GPSUtil;
import org.j4me.bluetoothgps.Location;
import org.json.JSONArray;
import org.json.JSONException;

public class RadarSender
{
    private PositionSender positionSender;

    public RadarSender(PositionSender positionSender)
    {
        this.positionSender = positionSender;
    }

    public void sendRadar(int radarType)
    {
        Location location = positionSender.getCurrentLocation();
        if(location != null) {
            try {
                JSONArray locJSON = Position.format(location);
                locJSON.put(radarType);
                positionSender.addExtra("rad", locJSON);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

}
