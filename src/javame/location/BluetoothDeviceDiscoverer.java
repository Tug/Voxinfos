/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javame.location;

import java.io.*;
import org.j4me.bluetoothgps.LocationProvider;


public class BluetoothDeviceDiscoverer {


    protected static Object bluetoothLock = new Object();

    public static BluetoothGPSDevice[] findDevices() throws Exception
    {
        String[][] devices = null;
        String errorText = null;

        synchronized ( bluetoothLock )
        {
            // Search for Bluetooth devices (this takes several seconds).
            try {
                devices = LocationProvider.discoverBluetoothDevices();
                if(devices == null) {
                    // The operation failed for an unknown Bluetooth reason.
                    errorText = "La recherche de périphérique bluetooth a échoué.";
                }
            }
            catch(SecurityException e) {
               
            }
            catch(IOException e) {
               
            }

            if(errorText != null) {
                throw new Exception ("Erreur recherche");
            }
            else {
                if(devices.length == 0) {
                    String message =
                        "Aucun périphériques détecté.\n" +
                        "Assurez-vous que votre GPS bluetooth est allumé et proche de vous.";
                    throw new Exception(message);
                }
            }
            return BluetoothGPSDevice.toBluetoothGPSDeviceList(devices);
        }
    }

}
