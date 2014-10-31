

package javame.location;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.bluetooth.RemoteDevice;
import org.j4me.bluetoothgps.*;

/**
 *
 * @author Tug
 */
public class BluetoothGPSDevice extends GPSDevice
{
    private String name;
    private String address;
    private LocationProvider lp = null;
    
    public BluetoothGPSDevice(String name, String address)
    {
        this.name = name;
        this.address = address;

    }

    public BluetoothGPSDevice()
    {
    }

    public String getName()
    {
        return name;
    }

    public String getAddress()
    {
        return address;
    }

    public static BluetoothGPSDevice[] toBluetoothGPSDeviceList(String[][] devices)
    {
        int len = devices.length;
        BluetoothGPSDevice[] bds = new BluetoothGPSDevice[len];
        for(int i=0; i<len; i++)
        {
            bds[i] = new BluetoothGPSDevice(devices[i][0], devices[i][1]);
        }
        return bds;
    }

    public static BluetoothGPSDevice[] toBluetoothGPSDeviceList(Hashtable devices)
    {
        BluetoothGPSDevice[] bds = new BluetoothGPSDevice[devices.size()];
        int i=0;
        for(Enumeration e = devices.keys(); e.hasMoreElements(); i++) {
            String key = (String) e.nextElement();
            RemoteDevice rd = (RemoteDevice)devices.get(key);
            bds[i] = new BluetoothGPSDevice(key, rd.getBluetoothAddress());
        }
        return bds;
    }

    public LocationProvider getLocationProvider()
    {
        if(lp == null) {
            Criteria criteria = getCriteria(address);
            try {
                lp = LocationProvider.getInstance(criteria);
            } catch (LocationException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        }
        return lp;
    }

    public static Criteria getCriteria(String address)
    {
        Criteria criteria = new Criteria();
        criteria.setAllowLocalLBS(false);
        criteria.setRemoteDeviceAddress(address);
        return criteria;
    }

    public void serialize(DataOutput output) throws IOException {
        output.writeUTF(name);
        output.writeUTF(address);
    }

    public void deserialize(DataInput input) throws IOException {
        this.name = input.readUTF();
        this.address = input.readUTF();
    }

}
