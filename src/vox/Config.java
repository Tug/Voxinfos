package vox;

import javame.io.Serializer;
import javame.util.HashMap;
import javame.util.Iterator;
import javame.util.Map;
import javame.util.Map.Entry;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import util.IntegerWrapper;
import vox.util.followup.FollowUpList;
import vox.util.poi.POIList;

public final class Config implements Runnable {

    private static final String VOX_DB = "VoxPrefs";
    private static Config instance = null;

    private String dbName;
    private Map variables;
    private Map constants;

    private static final String KEY_SMS_NUM = "SMS-Num";
    private static final String KEY_SMS_PORT = "SMS-Port";
    private static final String KEY_HTTP_HOST = "HTTP-Host";
    private static final String KEY_HTTP_PORT = "HTTP-Port";
    private static final String KEY_HTTP_POS_URL = "HTTP-Pos-Url";
    private static final String KEY_SOCKET_HOST = "SOCKET-Host";
    private static final String KEY_SIM_GPS = "SIM-Gps";

    private static final String KEY_KEY = "KEY";
    private static final String KEY_FULIST = "FULIST";
    private static final String KEY_POILIST = "POILIST";
    private static final String KEY_POSDELAY = "POSDELAY";
    private static final String KEY_CONNTYPE = "CONNTYPE";
    private static final String KEY_LANG = "LANGUE";
    private static final String KEY_DIST1 = "DIST1";
    private static final String KEY_DIST2= "DIST2";
    private static final String KEY_DIST3 = "DIST3";
    private static final String KEY_DURDEF = "DURDEF";


    private IntegerWrapper positionDelay = new IntegerWrapper(60);
    private boolean simGPS = false;
    
    private static final String[] cellIdProperties = 
            {"Cell-ID", "com.nokia.mid.cellid", "CellID", "phone.cid",
             "com.nokia.mid.cellid", "com.sonyericsson.net.cellid", "phone.cid",
             "com.samsung.cellid", "com.siemens.cellid", "cid" };
    private String cellIdProperty;
    private boolean running;

    private Config(String dbName) {
        this.dbName = dbName;
        this.variables = new HashMap();
        try {
            load();
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
        if(!variables.containsKey(KEY_KEY)) {
            variables.put(KEY_KEY, new StringBuffer());
        }
        if(!variables.containsKey(KEY_FULIST)) {
            variables.put(KEY_FULIST, new FollowUpList());
        }
        if(!variables.containsKey(KEY_POILIST)) {
            variables.put(KEY_POILIST, new POIList());
        }
        if(!variables.containsKey(KEY_CONNTYPE)) {
            variables.put(KEY_CONNTYPE, "HTTP");
        }
         if(!variables.containsKey(KEY_LANG)) {
            variables.put(KEY_LANG, "FR");
        }
         if(!variables.containsKey(KEY_DIST1)) {
            variables.put(KEY_DIST1, "4000");
        }
         if(!variables.containsKey(KEY_DIST2)) {
            variables.put(KEY_DIST2, "2000");
        }
         if(!variables.containsKey(KEY_DIST3)) {
            variables.put(KEY_DIST3, "300");
        }
           if(!variables.containsKey(KEY_DURDEF)) {
            variables.put(KEY_DURDEF, "60");
        }

        String posDelayStr = (String) variables.get(KEY_POSDELAY);
        if(posDelayStr != null) {
            setPositionDelay(Integer.parseInt(posDelayStr));
        }
        //new Thread(this);
        /*for(int i=0; i<cellIdProperties.length; i++) {
            String cellIdProp = cellIdProperties[i];
            String cellId = System.getProperty(cellIdProp);
            if(cellId != null && !cellId.equals("")) cellIdProperty = cellIdProp;
        }*/
    }

    public void run() {
        running = true;
        while(running) {
            synchronized(this) {
                try {
                    this.wait(1000);
                    try {
                        this.save();
                    } catch (RecordStoreException ex) {
                        ex.printStackTrace();
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static Config getInstance() {
        if(instance == null) {
            instance = new Config(VOX_DB);
        }
        return instance;
    }

    public void loadMidletParams(Map params) {
        this.constants = params;
        if("true".equals((String) params.get(KEY_SIM_GPS))) {
            simGPS = true;
        }
    }

    public String getSMSPort() {
        return (String) constants.get(KEY_SMS_PORT);
    }

    public String getSocketUrl() {
        return (String) constants.get(KEY_SOCKET_HOST);
    }

    public boolean isSimGPS() {
        return simGPS;
    }

    public IntegerWrapper getPositionDelay() {
        return positionDelay;
    }

    public String getHTTPPosUrl() {
        String url = (String) constants.get(KEY_HTTP_HOST);
        String httpPort = (String) constants.get(KEY_HTTP_PORT);
        if(!httpPort.equals("80")) {
            url += ":"+httpPort;
        }
        return url + constants.get(KEY_HTTP_POS_URL);
    }

    public void setPositionDelay(int delayPositionSec) {
        positionDelay.setValue(delayPositionSec);
        variables.put(KEY_POSDELAY, String.valueOf(delayPositionSec));
    }

    public String getSMSNum() {
        return (String) constants.get(KEY_SMS_NUM);
    }

    public String getConnectionType() {
        return (String) variables.get(KEY_CONNTYPE);
    }

    public void setConnectionType(String connectionType) {
        variables.put(KEY_CONNTYPE, connectionType);
    }
    public String getlangue() {
        return (String) variables.get(KEY_LANG);
    }
    public void setlangue(String langue) {
        variables.put(KEY_LANG, langue);
    }
    public String getdistance1() {
        return (String) variables.get(KEY_DIST1);
    }
    public void setdistance1(String distance1) {
        variables.put(KEY_DIST1, distance1);
    }
    public String getdistance2() {
        return (String) variables.get(KEY_DIST2);
    }
    public void setdistance2(String distance2) {
        variables.put(KEY_DIST2, distance2);
    }

    public String getdistance3() {
        return (String) variables.get(KEY_DIST3);
    }
    public void setdistance3(String distance3) {
        variables.put(KEY_DIST3, distance3);
    }
 
     public String getduredef() {
        return (String) variables.get(KEY_DURDEF);
    }
    public void setduredef(String duredef) {
        variables.put(KEY_DURDEF, duredef);
    }

    public StringBuffer getKeyBuffer() {
        return (StringBuffer) variables.get(KEY_KEY);
    }

    public String getKey() {
        return getKeyBuffer().toString();
    }

    public void setKey(String keyStr) {
        StringBuffer keyBuffer = getKeyBuffer();
        keyBuffer.setLength(0);
        keyBuffer.append(keyStr);
    }

    public boolean hasKey() {
        return getKeyBuffer().length() > 0;
    }

    public String getCellId() {
        return System.getProperty(cellIdProperty);
    }

    public FollowUpList getFollowUpList() {
        return (FollowUpList) variables.get(KEY_FULIST);
    }

    public POIList getPOIList() {
        return (POIList) variables.get(KEY_POILIST);
    }

    public void set(String key, Object value) {
        variables.put(key, value);
        //synchronized(this) {
        //    this.notify();
        //}
    }

    public Object get(String key) {
        if(variables.containsKey(key)) {
            return variables.get(key);
        }
        return null;
    }

    public void delete(String key) {
        variables.remove(key);
    }

    private void load() throws RecordStoreException {
        RecordStore rs = null;
        RecordEnumeration re = null;

        try {
          rs = RecordStore.openRecordStore(dbName, true);
          re = rs.enumerateRecords(null, null, false);
          while (re.hasNextElement()) {
            byte[] raw = re.nextRecord();
            String pref = new String(raw);
            // Parse out the name.
            int index = pref.indexOf('|');
            String name = pref.substring(0, index);
            String value = pref.substring(index + 1);
            Object obj = Serializer.deserializeClass(value.getBytes());
            variables.put(name, obj);
          }
        } finally {
          if (re != null)
            re.destroy();
          if (rs != null)
            rs.closeRecordStore();
        }
    }

    public void save() throws RecordStoreException {
        RecordStore rs = null;
        RecordEnumeration re = null;
        RecordStore.deleteRecordStore(dbName);
        try {
          rs = RecordStore.openRecordStore(dbName, true);
          re = rs.enumerateRecords(null, null, false);

          Iterator keysIt = variables.entrySet().iterator();
          while (keysIt.hasNext()) {
            Map.Entry entry = (Entry) keysIt.next();
            String keyStr = (String) entry.getKey();
            Object value = entry.getValue();
            if(value != null) {
                String valueStr = new String(Serializer.serializeClass(value));
                String pref = keyStr + "|" + valueStr;
                byte[] raw = pref.getBytes();
                rs.addRecord(raw, 0, raw.length);
            }
          }
        } finally {
          if (re != null)
            re.destroy();
          if (rs != null)
            rs.closeRecordStore();
        }
    }

    public static void close() {
        //instance.running = false;
        instance = null;
    }
}
