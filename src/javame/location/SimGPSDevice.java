/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package javame.location;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.j4me.bluetoothgps.*;

/**
 *
 * @author Tug
 */
public class SimGPSDevice extends GPSDevice {

    private MockLocationProvider lp;
    private Path path;
    private double speedms;
    private boolean end = false;
    
    class Path {
        private static final double METERS_PER_RADIAN = 6371000;
        private Coordinates start;
        private Coordinates end;
        private long lastUpdate;
        private double course;
        private Coordinates current;

        public Path(Coordinates start, Coordinates end) {
            this.start = start;
            this.end = end;
            this.course = Math.toRadians(start.azimuthTo(end));
            this.current = new Coordinates(start.getLatitude(),
                                           start.getLongitude(),
                                           start.getAltitude());
        }

        public double getCourse() {
            return course;
        }

        public void start() {
            this.lastUpdate = System.currentTimeMillis();
        }

        public Coordinates getCurrentPosition(double speedms) {
            long timestamp = System.currentTimeMillis();
            long timeElapsed = timestamp - lastUpdate;
            this.lastUpdate = timestamp;
            double newDist = speedms * timeElapsed / 1000;
            moveCurrentPosition(newDist);
            return current;
        }

        public void moveCurrentPosition(double distInMeter) {
            double deltaLat  = distInMeter * Math.cos(course);
            double deltaLong = distInMeter * Math.sin(course);
            deltaLat  = Math.toDegrees( deltaLat / METERS_PER_RADIAN );
            deltaLong = Math.toDegrees( deltaLong / METERS_PER_RADIAN );
            double newLat = current.getLatitude() + deltaLat;
            double newLong = current.getLongitude() + deltaLong;
            newLat =  ((newLat+90) % 180.0) - 90; // range [-90, 90]
            newLong = ((newLong+180) % 360.0) - 180; // range [-180, 180]
            //System.out.println(newLat+", "+newLong+", ");
            current.setLatitude(newLat);
            current.setLongitude(newLong);
        }

        public long getTimestamp() {
            return lastUpdate;
        }

    }
    
    public SimGPSDevice() {
        this.path = new Path(new Coordinates(48.95,2.45,0),
                             new Coordinates(48.45,4.50,0));
        this.speedms = 100 / 3.6;
        this.lp = new MockLocationProvider();
    }

    public void run() {
        super.run();
        lp.setState(LocationProvider.AVAILABLE);
        path.start();
        while(!end) {
            try {
                Thread.sleep(3000);
                Coordinates currentPosition = path.getCurrentPosition(speedms);
                QualifiedCoordinates qualifiedCoor =
                        new QualifiedCoordinates( currentPosition.getLatitude(),
                                                  currentPosition.getLongitude(),
                                                  currentPosition.getAltitude(),
                                                  0,0);
                lp.setLocation(qualifiedCoor, (float) speedms);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        lp.close();
    }
    
    public String getName() {
        return "Simulation GPS";
    }

    public LocationProvider getLocationProvider() {
        return lp;
    }

    public void serialize(DataOutput output) throws IOException {
    }

    public void deserialize(DataInput input) throws IOException {
    }

    public void start() {
        new Thread(this).start();
    }

    public void end() {
        super.end();
        end = true;
    }
}
