/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vox.io;

import javame.util.ArrayList;
import javame.util.List;
import vox.util.followup.FollowUp;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import vox.util.poi.POI;
import vox.Config;
import vox.Config;

/**
 *
 * @author Tug
 */
public class VoxMessage {

    private List pois = null;
    private List fus = null;
    private String info = null;
    private Integer delay = null;
    private String key = null;
    
    public VoxMessage() {
        this.pois = new ArrayList();
        this.fus = new ArrayList();
    }

    public void addPOI(POI poi) {
        pois.add(poi);
    }

    public void addFollowUp(FollowUp fu) {
        fus.add(fu);
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public boolean isEmpty() {
        return  pois == null && fus == null
             && info == null && delay == null
             && key == null;
    }

    public static JSONArray POI_2_JSONArray(POI poi) throws JSONException {
        JSONArray poiJSON = new JSONArray();
        poiJSON.put(poi.getCoordinates().getLatitude());
        poiJSON.put(poi.getCoordinates().getLongitude());
        poiJSON.put(""+poi.getPoiType());
        return poiJSON;
    }

    public static JSONArray FollowUp_2_JSONArray(FollowUp fu) {
        JSONArray fuJSON = new JSONArray();
        fuJSON.put(fu.getDay());
        fuJSON.put(fu.getHours());
        fuJSON.put(fu.getMin());
        fuJSON.put(fu.getDuration());
        return fuJSON;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject smsJSON = new JSONObject();
        if(pois.size() > 0) {
            JSONArray poisJSON = new JSONArray();
            smsJSON.put("p", poisJSON);
            for(int i=0; i<pois.size(); i++) {
                poisJSON.put(POI_2_JSONArray((POI) pois.get(i)));
            }
        }
        if(fus.size() > 0) {
            JSONArray fusJSON = new JSONArray();
            smsJSON.put("f", fusJSON);
            for(int i=0; i<fus.size(); i++) {
                fusJSON.put(FollowUp_2_JSONArray((FollowUp) fus.get(i)));
            }
        }
        if(info != null) {
            smsJSON.put("i", info);
        }
        if(delay != null) {
            smsJSON.put("d", delay);
        }
        return smsJSON;
    }

    public Integer getDelay() {
        return delay;
    }

    public List getFollowUps() {
        return fus;
    }

    public String getInfo() {
        return info;
    }

    public List getPOIs() {
        return pois;
    }

    public String getKey() {
        return key;
    }

    public VoxMessageParser getParser() {
        return new VoxMessageParser();
    }

    public static VoxMessage parse(String text) {
        VoxMessage message = new VoxMessage();
        message.getParser().parse(text);
        return message;
    }

    public class VoxMessageParser {
        private static final String POI_KEY = "p";
        private static final String INFO_KEY = "i";
        private static final String FOLLOWUP_KEY = "f";
        private static final String DELAY_KEY = "d";
        private static final String KEY_KEY = "k";
        /*
         * ex : {'p':[[lat,long,type],[lat,long,type]],
         *       'i':"This is an information string",
         *       'f':[[jour,heure,minute,duree], [jour,heure,minute,duree]],
         *       'd':delayInSec,
         *       'k':"fs5fgr4g"}
         */

        public void VoxMessageParser() {}
        
        public void parse(String smsContent)
        {
            JSONTokener jsonTokener = new JSONTokener(smsContent);
            while(jsonTokener.more()) {
                Object obj = null;
                try {
                    obj = jsonTokener.nextValue();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                if(obj != null && obj instanceof JSONObject) {
                    JSONObject jsonObj = (JSONObject)obj;
                    Object objPOI = jsonObj.opt(POI_KEY);
                    Object objINFO = jsonObj.opt(INFO_KEY);
                    Object objFOLLOWUP = jsonObj.opt(FOLLOWUP_KEY);
                    String objKEY = jsonObj.optString(KEY_KEY, null);
                    int delaySec = jsonObj.optInt(DELAY_KEY, 0);
                    if(objPOI != null && objPOI instanceof JSONArray) {
                        JSONArray arrPOI = (JSONArray)objPOI;
                        int lenArrPOI = arrPOI.length();
                        for(int i=0; i<lenArrPOI; i++) {
                            Object objp = arrPOI.opt(i);
                            if(objp != null && objp instanceof JSONArray) {
                                JSONArray poi = (JSONArray)objp;
                                int lenPoi = poi.length();
                                if(lenPoi >= 3) {
                                    double lat = poi.optDouble(0);
                                    double lon = poi.optDouble(1);
                                    int type = poi.optInt(2);
                                    pois.add(new POI(lat, lon, (char) type));
                                }
                            }
                        }
                    }
                    if(objINFO != null && objINFO instanceof String) {
                        info = (String) objINFO;
                    }
                    if(objFOLLOWUP != null && objFOLLOWUP instanceof JSONArray) {
                        JSONArray arrFOLLOWUP = (JSONArray)objFOLLOWUP;
                        int lenArrFOLLOWUP = arrFOLLOWUP.length();
                        for(int i=0; i<lenArrFOLLOWUP; i++) {
                            Object objf = arrFOLLOWUP.opt(i);
                            if(objf != null && objf instanceof JSONArray) {
                                JSONArray foUp = (JSONArray)objf;
                                int lenFoUp = foUp.length();
                                if(lenFoUp >= 4) {
                                    int day = foUp.optInt(0);
                                    int hours = foUp.optInt(1);
                                    int minutes = foUp.optInt(2);
                                    int duration = foUp.optInt(3); // in min
                                    fus.add(new FollowUp(day, hours, minutes, duration));
                                }
                            }
                        }
                    }
                    if(objKEY != null) {
                        key = objKEY;
                    }
                    if(delaySec > 0) {
                        delay = new Integer(delaySec);
                    }
                }
            }
        }
    }



}
