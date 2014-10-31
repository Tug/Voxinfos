/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vox.io;

import javame.location.GPSUtil;
import org.j4me.bluetoothgps.Location;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author Tug
 */
public class Position {

    public static JSONArray format(Location location) throws JSONException {
        JSONArray locJSON = new JSONArray();
        locJSON.put(Double.parseDouble(GPSUtil.getLatitude(location)));
        locJSON.put(Double.parseDouble(GPSUtil.getLongitude(location)));
        locJSON.put(Double.parseDouble(GPSUtil.getCourse(location)));
        locJSON.put(Double.parseDouble(GPSUtil.getSpeedKmh(location)));
        return locJSON;
    }
}
