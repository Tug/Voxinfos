/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vox.io;

import vox.util.followup.FollowUpList;
import vox.util.poi.POIList;
import javame.util.List;
import javame.util.Observable;
import vox.Config;
import vox.util.followup.FollowUp;
import vox.util.poi.POI;
import util.i18n;

public class DataReceiver extends Observable
{
    private FollowUpList fuList;
    private POIList poiList;

    private static DataReceiver instance;

    public DataReceiver()
    {
        this.fuList = Config.getInstance().getFollowUpList();
        this.poiList = Config.getInstance().getPOIList();
    }

    public static DataReceiver getInstance() {
        if(instance == null) {
            instance = new DataReceiver();
        }
        return instance;
    }

    public void receiveMessage(String text) {
        new Thread(new MessageReceiver(text)).start();
    }

    class MessageReceiver implements Runnable {

        private String text;

        public MessageReceiver(String text) {
            this.text = text;
        }

        public void run() {
            System.out.println(i18n.s(68));
            VoxMessage message = VoxMessage.parse(text);
            if(message.isEmpty()) {
                notifyObservers(i18n.s(67));
                return;
            }
            List pois = message.getPOIs();
            List fus = message.getFollowUps();
            String info = message.getInfo();
            String key = message.getKey();
            Integer delay = message.getDelay();
            for (int i=0; i<pois.size(); i++) {
                poiList.add((POI) pois.get(i));
            }
            for (int i=0; i<fus.size(); i++) {
                fuList.add((FollowUp) fus.get(i));
            }

            /*FollowUp fu2 = (FollowUp) fuList.first();
            if(fu2 != null) {
                try {
                    VOXINFOS.wakeMeUp(fu2.getStart());
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (ConnectionNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }*/
            if(key != null) {
                Config.getInstance().setKey(key);
            }
            if(delay != null) {
                Config.getInstance().setPositionDelay(delay.intValue());
            }

            String notifStr = "";
            if(info != null)
                notifStr += info+ ". ";
            if(pois.size() > 0 || fus.size() > 0) {
                if(pois.size() > 0) {
                    notifStr += pois.size()+i18n.s(75)+poiList.size()+")";
                    if(fus.size() > 0)
                        notifStr += i18n.s(76);
                }
                if(fus.size() > 0) {
                    notifStr += fus.size()+i18n.s(77)+fuList.size()+").";
                }
                notifStr += ".";
            }
            if(key != null) {
                notifStr += i18n.s(78);
            }
            if(!notifStr.equals("")) {
                notifyObservers(notifStr);
            }
        }
    }

}