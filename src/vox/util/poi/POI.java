/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vox.util.poi;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;
//import javax.microedition.location.Coordinates;
import org.j4me.bluetoothgps.Coordinates;
import util.DateUtil;
import javame.io.Serializable;

/**
 *
 * @author Tug
 */
public class POI implements Serializable {

    private Coordinates coordinates;
    private Date expirationDate;
    private char poiType;

    public POI() {
        this(0.0,0.0,POIType.RADAR_50,null);
    }

    public POI(double latitude, double longitude, char poiType, Date expirationDate) {
        this.coordinates = new Coordinates(latitude, longitude, 0);
        this.poiType = poiType;
        this.expirationDate = expirationDate;
    }

    public POI(double latitude, double longitude, char poiType) {
        this.coordinates = new Coordinates(latitude, longitude, Float.NaN);
        this.poiType = poiType;
        int hours = 1;
        if(this.poiType >= POIType.RADAR_30 && this.poiType <= POIType.TRAVO) {
            hours =  365 * 24;
        }
        else{
            hours =  4;
        }
        this.expirationDate = DateUtil.addHours(new Date(), hours);
    }

    public Coordinates getCoordinates()
    {
        return coordinates;
    }

    public Date getExpirationDate()
    {
        return expirationDate;
    }

    public char getPoiType()
    {
        return poiType;
    }

    public int getProximityRadius()
    {
        switch(poiType)
        {
            case POIType.RADAR_30:
            case POIType.RADAR_45:
            case POIType.RADAR_50:
            case POIType.RADAR_60:
            case POIType.RADAR_70:
            case POIType.RADAR_80:
            case POIType.ECOLE:
            case POIType.FROUGE:
            case POIType.PASNIVO:
                return 300;
            case POIType.RADAR_90:
            case POIType.RADAR_100:
            case POIType.RADAR_110:
            case POIType.STATION:
            case POIType.ACCID:
            case POIType.TRAVO:
                return 2000;
            case POIType.RADAR_120:
            case POIType.RADAR_130:
                return 4000;
            default:
                return 2000;
        }
    }

    public String getSpeedFromType() {
        switch(poiType)
        {
            case POIType.RADAR_30: return "30";
            case POIType.RADAR_45: return "45";
            case POIType.RADAR_50: return "50";
            case POIType.RADAR_60: return "60";
            case POIType.RADAR_70: return "70";
            case POIType.RADAR_80: return "80";
            case POIType.RADAR_90: return "90";
            case POIType.RADAR_100: return "100";
            case POIType.RADAR_110: return "110";
            case POIType.RADAR_120: return "120";
            case POIType.RADAR_130: return "130";
            case POIType.ECOLE: return "30";
            case POIType.FROUGE: return "30";
            case POIType.PASNIVO: return "30";
            case POIType.STATION: return "110";
            case POIType.TRAVO: return "110";
            case POIType.ACCID: return "110";
        }
        return "50";
    }

    public void serialize(DataOutput output) throws IOException {
        output.writeDouble(coordinates.getLatitude());
        output.writeDouble(coordinates.getLongitude());
        output.writeFloat(coordinates.getAltitude());
        output.writeLong(expirationDate.getTime());
        output.writeChar(poiType);
    }

    public void deserialize(DataInput input) throws IOException {
        double latitude = input.readDouble();
        double longitude = input.readDouble();
        float altitude = input.readFloat();
        this.coordinates = new Coordinates(latitude, longitude, altitude);
        this.expirationDate = new Date(input.readLong());
        this.poiType = input.readChar();
    }
}
