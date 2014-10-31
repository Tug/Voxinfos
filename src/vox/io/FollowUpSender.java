/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vox.io;

import io.Sender;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import vox.util.followup.FollowUp;

/**
 *
 * @author Tug
 */
public class FollowUpSender {

    private Sender sender;
    private JSONArray fusJSON;
    
    public FollowUpSender(Sender sender)
    {
        this.sender = sender;
        this.fusJSON = new JSONArray();
    }

    public synchronized void sendSync()
    {
        JSONObject msgJSON = new JSONObject();
        try {
            msgJSON.put("f", fusJSON);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        sender.sendTextMessage(msgJSON.toString());
        fusJSON = new JSONArray();
    }
    
    public void addFollowUp(FollowUp fu) {
        fusJSON.put(VoxMessage.FollowUp_2_JSONArray(fu));
    }

}
