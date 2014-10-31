/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vox;

import vox.io.FollowUpSender;
import vox.io.ReceiverService;
import vox.io.ReceiverSenderFactory;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import vox.io.PositionSender;
import io.ReceiverSender;
import io.http.HTTPReceiverSender;
import java.util.Date;
import javame.util.Observable;
import javame.util.Observer;
import vox.io.RadarSender;
import vox.util.followup.FollowUp;
import vox.util.followup.FollowUpList;
import vox.util.poi.POI;
import vox.util.poi.POIList;
import vox.util.poi.POIProximityAlerter;
import util.i18n;
/**
 *
 * @author Tug
 */
public class VoxService extends Thread {

    private static VoxService instance = null;

    private Timer timer;
    private VoxServiceTask task;

    private PositionSender positionSender;
    private RadarSender radarSender;
    private FollowUpList fuList;
    private POIList poiList;
    private ReceiverSender receiverSender;
    private ServiceObservable observable;
    private ReceiverService receiverService;
    private ReceiverSender smsReceiverSender;

    public static VoxService getInstance() throws IOException {
        if(instance == null)
            instance = new VoxService();
        return instance;
    }

    public VoxService() throws IOException {
        this.fuList = Config.getInstance().getFollowUpList();
        this.poiList = Config.getInstance().getPOIList();
        this.timer = new Timer();
        this.observable = new ServiceObservable();        
        this.receiverSender = ReceiverSenderFactory.getConfigInstance();
        this.positionSender = new PositionSender(receiverSender.getSender(),
                                        Config.getInstance().getPositionDelay());
        this.radarSender = new RadarSender(positionSender);
        this.receiverService = new ReceiverService();
        receiverService.addReceiver(receiverSender.getReceiver());

        // if using HTTP, allow SMS for push
        // and look if new content
        if(receiverSender instanceof HTTPReceiverSender) {
            smsReceiverSender = ReceiverSenderFactory.getDefaultSMSReceiverSender();
            receiverService.addReceiver(smsReceiverSender.getReceiver());
            receiverSender.getSender().sendTextMessage("");
        }
    }

    public void run() {
        new Thread(receiverSender).start();
        if(smsReceiverSender != null)
            new Thread(smsReceiverSender).start();
        followUpWatcher();
        VoxAudioPlayer.getInstance().beep_pose();
    }

    public void end() {
        if(receiverSender != null) {
            receiverService.end();
            receiverSender.end();
            if(smsReceiverSender != null) {
                smsReceiverSender.end();
            }
        }
        timer.cancel();
    }
    
    public void cancelCurrentFu() {
    	FollowUpSender fuSender = new FollowUpSender(receiverSender.getSender());
        fuSender.addFollowUp(new FollowUp(new Date(), 0));
        fuSender.sendSync();
        if(task != null) {
            task.end();
            task.cancel();
        }
    }

    public static void close() {
        instance = null;
    }

    private void followUpWatcher() {
        FollowUp fu = (FollowUp) fuList.first();
        if(fu != null) scheduleFollowUp(fu);
        fuList.addObserver(new Observer() {
            public void update(Observable observable, Object object) {
                if(object != null && object.equals(fuList.first())) {
                    FollowUp fu = (FollowUp) object;
                    Date start = fu.getStart();
                    long diff = System.currentTimeMillis() - start.getTime();
                    long after = 10 * 60 * 1000; // 10 min
                    // remove followups that are late
                    if(diff > after) fuList.remove(fu);
                    else scheduleFollowUp(fu);
                }
            }
        });
    }

    public boolean isRunning() {
        return task != null && task.isRunning();
    }

    private void scheduleFollowUp(FollowUp fu) {
        if(task != null) {
            if(task.isRunning()) {
                task.mergeFollowUp(fu);
                observable.notifyFollowUp(fu);
                return;
            } else {
                task.cancel();
            }
        }
        task = new VoxServiceTask(fu);
        timer.schedule(task, fu.getStart());
        observable.notifyFollowUp(fu);
    }

    

    public PositionSender getPositionSender() {
        return positionSender;
    }

    public void alertRadar(int radarType) {
        radarSender.sendRadar(radarType);
        VoxAudioPlayer.getInstance().merci();
    }

    public final class ServiceObservable extends Observable {

        private String currentStatus;
        private TimeWatcher timeWatcher;
        private boolean followUpNow = false;

        public class TimeWatcher extends Thread {

            private long date;
            private long minLeftAnnounced = -1;
            private boolean running = false;
            
            public TimeWatcher(long date) {
                this.date = date;
            }

            public void run() {
                running = true;
                while(running) {
                    long msLeft = date - System.currentTimeMillis();
                    if(msLeft <= 0 && followUpNow) running = false;
                    else {
                        long minLeft = msLeft/(60*1000);
                        if(minLeftAnnounced != minLeft) {
                            updateTime(minLeft);
                            minLeftAnnounced = minLeft;
                        }
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }

            public void end() {
                running = false;
            }
        }
        
        public void notifyFollowUp(FollowUp fu) {
            if(fu == null) {
                notifyNoFollowUp();
                return;
            }
            long diff = fu.getStart().getTime() - System.currentTimeMillis();
            long before = 3 * 1000; //3 sec
            followUpNow = diff <= before;
            if(followUpNow) {
                setTimeWatcher(fu.getEnd());
            } else {
                setTimeWatcher(fu.getStart().getTime());
            }
        }
        
        private void setTimeWatcher(long date) {
            if(timeWatcher != null) timeWatcher.end();
            timeWatcher = new TimeWatcher(date);
            timeWatcher.start();
        }

        public void updateTime(long nbMinutes) {
            if(followUpNow) {
                currentStatus = i18n.s(69)+nbMinutes+" min)";
            } else {
                currentStatus = i18n.s(70)+nbMinutes+" min";
            }
            notifyObservers(currentStatus);
        }

        public void notifyNoFollowUp() {
            if(timeWatcher != null) timeWatcher.end();
            currentStatus = i18n.s(71);
            notifyObservers(currentStatus);
        }

        public String getCurrentStatus() {
            return currentStatus;
        }

        public synchronized void addObserver(Observer observer) {
            super.addObserver(observer);
            notifyObservers(currentStatus);
        }

    }

    public ServiceObservable getServiceObservable() {
        return observable;
    }

    class VoxServiceTask extends TimerTask {

        private FollowUp fu;
        private boolean running = false;
        private POIProximityAlerter alerter;
        private Observer proximityObserver;
        
        public VoxServiceTask(FollowUp fu) {
            this.fu = fu;
            this.alerter = new POIProximityAlerter(poiList);
            this.proximityObserver = new Observer() {
                public void update(Observable observable, Object object) {
                    if(object != null) {
                        final POI poi = (POI) object;
                        String alertStr = i18n.s(72)+poi.getSpeedFromType()+"km/h";
                        NotificationService.getInstance().display(alertStr);
                        System.out.println(i18n.s(73));
                        new Thread() {
                            public void run() {
                                VoxAudioPlayer.getInstance().announcePOI(poi.getPoiType());
                            }
                        }.start();
                    }
                }
            };
        }

        public void enableAlerts() {
            alerter.addObserver(proximityObserver);
        }

        public void disableAlerts() {
            alerter.deleteObserver(proximityObserver); // ok: does not raise ex if observer not in list
        }

        public void run()
        {
            running = true;
            observable.notifyFollowUp(fu);
            if(fu != null && fu.startsNow()) {
                fuList.remove(fu);
                begin();

                long startTimeMs = System.currentTimeMillis();
                long durationMs = fu.getDuration() * 60 * 1000L;
                while(running) {
                    pause(60 * 1000L);
                    durationMs = fu.getDuration() * 60 * 1000L;
                    running = (System.currentTimeMillis() - startTimeMs < durationMs);
                }
                end();
            }
            running = false;
            observable.notifyFollowUp((FollowUp) fuList.first());
        }

        public void begin() {
            System.out.println(i18n.s(74));
            poiList.cleanOld();
            new Thread(positionSender).start();
            enableAlerts();
            VoxAudioPlayer.getInstance().announceStart();
        }

        public void end() {
            disableAlerts();
            positionSender.end();
            VoxAudioPlayer.getInstance().announceEnd();
        }

        public boolean isRunning() {
            return running;
        }

        public void mergeFollowUp(FollowUp fu2) {
            long newEnd = fu2.getEnd();
            if(newEnd > fu.getEnd()) {
                fu.setEnd(newEnd);
            }
            fuList.remove(fu2);
        }

        public synchronized void pause(long ms) {
            try {
                Thread.sleep(ms); // 1 min wait
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

    }

}
