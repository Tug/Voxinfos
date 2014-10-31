
package gui;

import javame.location.GPSUtil;
import javame.util.Observable;
import javame.util.Observer;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import org.j4me.bluetoothgps.*;

import util.i18n;

/**
 *
 * @author Tug
 */
public class GPSForm extends Form
{
    private StringItem titleSource;
    private StringItem latitude;
    private StringItem longitude;
    private StringItem speed;
    private StringItem angle;
    private StringItem stateText;
    private Observer sourceObserver;
    private Observer locationObserver;
    private Observer stateObserver;

    public GPSForm() {
        super("");
        this.titleSource = new StringItem("", "");
        this.stateText = new StringItem(i18n.s("GPS")+" : ", "");
        this.latitude = new StringItem(i18n.s("lat")+" : ", "0.0");
        this.longitude = new StringItem(i18n.s("long")+" : ", "0.0");
        this.speed = new StringItem(i18n.s("vit")+" : ", "0.0");
        this.angle = new StringItem(i18n.s("cap")+" : ", "0");
        titleSource.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_AFTER);
        stateText.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_AFTER);
        latitude.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_AFTER);
        longitude.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_AFTER);
        speed.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_AFTER);
        angle.setLayout(Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_AFTER);
        append(titleSource);
        //append(new Spacer(100, 3));
        append(stateText);
        //append(new Spacer(100, 3));
        append(latitude);
        //append(new Spacer(100, 3));
        append(longitude);
        //append(new Spacer(100, 3));
        append(speed);
        //append(new Spacer(100, 3));
        //append(angle);
        locationObserver = new Observer() {
            public void update(Observable observable, Object object) {
                if(object instanceof Location) {
                    updateForm((Location)object);
                }
            }
        };
        sourceObserver = new Observer() {
            public void update(Observable observable, Object object) {
                if(object instanceof String) {
                	titleSource.setText((String)object);
                }
            }
        };
        stateObserver = new Observer() {
            public void update(Observable observable, Object object) {
                if(object instanceof Integer) {
                    setStateString(((Integer)object).intValue());
                }
            }
        };
    }

    private void updateForm(Location location)
    {
        String latitudeStr = GPSUtil.getLatitudeDDMMSS(location);
        String longitudeStr = GPSUtil.getLongitudeDDMMSS(location);
        String speedStr = GPSUtil.getSpeedKmh(location) + " km/h";
        String angleStr = GPSUtil.getCourse(location) + " ";
        latitude.setText("" + latitudeStr);
        longitude.setText("" + longitudeStr);
        speed.setText("" + speedStr);
        //angle.setText("" + angleStr);
    }

    public void setStateString(int state) {
        String stateStr = i18n.s(63);
        switch (state) {
            case LocationProvider.AVAILABLE:
                stateStr = i18n.s(64);
                break;
            case LocationProvider.OUT_OF_SERVICE:
                stateStr = i18n.s(65);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                stateStr = i18n.s(66);
                break;
        }
        stateText.setText(stateStr);
    }

    public Observer getSourceObserver() {
        return sourceObserver;
    }

    public Observer getLocationObserver() {
        return locationObserver;
    }

    public Observer getStateObserver() {
        return stateObserver;
    }

}
