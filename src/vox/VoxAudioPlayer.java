
package vox;

import java.io.IOException;

import java.io.InputStream;
import javame.util.HashMap;
import javame.util.LinkedList;
import javame.util.List;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.ToneControl;
import javax.microedition.media.control.VolumeControl;
import vox.util.poi.POIType;
import vox.Config;
/**
 *
 * @author Tug
 */
public class selectlangue {
    private static langue  FR;
    private static langue  EN;
    public static langue getConfigInstance() throws IOException {
      String value = Config.getInstance().langue();
      if(value.equals("FR")) {
         replang = "/sounds/FR";
      } else if (value.equals("EN")) {
         replang =  "/sounds/EN";
      }
      return null;
   }
  public class VoxAudioPlayer implements PlayerListener
{
           
    public static final String WAV_START = replang + "/debutsuivi.mp3";
    public static final String WAV_END =  replang + "/finsuivi.mp3";
    public static final String WAV_30 =  replang + "/30kmh.mp3";
    public static final String WAV_45 =  replang + "/45kmh.mp3";
    public static final String WAV_50 =  replang + "/50kmh.mp3";
    public static final String WAV_60 =  replang + "/60kmh.mp3";
    public static final String WAV_70 =  replang + "/70kmh.mp3";
    public static final String WAV_80 =  replang + "/80kmh.mp3";
    public static final String WAV_90 =  replang + "/90kmh.mp3";
    public static final String WAV_100 =  replang + "/100kmh.mp3";
    public static final String WAV_110 =  replang + "/110kmh.mp3";
    public static final String WAV_120 =  replang + "/120kmh.mp3";
    public static final String WAV_130 =  replang + "/130kmh.mp3";
    public static final String WAV_MERCI =  replang + "/merci.mp3";
    public static final String WAV_CT =  replang + "/centur.mp3";
    public static final String WAV_PN =  replang + "/pneus.mp3";
    public static final String WAV_PS =  replang + "/pause.mp3";
    public static final String WAV_NI =  replang + "/ctr_pn.mp3";
    public static final String WAV_FR =  replang + "/ctr_fr.mp3";
    public static final String WAV_TR =  replang + "/travo.mp3";
    public static final String WAV_AC =  replang + "/accident.mp3";
    public static final String WAV_EC =  replang + "/ecole.mp3";
    public static final String WAV_BEEP = "/sounds/beep.mp3";
    public static final String WAV_BEEP2 = "/sounds/beep2.mp3";

    private final static HashMap mimeTypes = new HashMap();
    static {
         mimeTypes.put("mp3", "audio/mpeg");
         mimeTypes.put("mid", "audio/midi");
         mimeTypes.put("wav", "audio/x-wav");
    }

    public boolean isRunning = false;
    public final Object lock = new Object();

    private static String getMimeType(String filename) {
        int mid = filename.lastIndexOf('.');
        String extension = filename.substring(mid+1, filename.length());
        return (String) mimeTypes.get(extension);
    }

    public VoxAudioPlayer() {

    }

    public static VoxAudioPlayer instance = null;
    public static VoxAudioPlayer getInstance() {
        if(instance == null) {
            instance = new VoxAudioPlayer();
        }
        return instance;
    }

    public void announcePOI(int poiType)
    {
        String ressource = null;
        switch(poiType) {
            case POIType.RADAR_30:
                ressource = WAV_30;
                break;
            case POIType.RADAR_45:
                ressource = WAV_45;
                break;
            case POIType.RADAR_50:
                ressource = WAV_50;
                break;
            case POIType.RADAR_60:
                ressource = WAV_60;
                 break;
            case POIType.RADAR_70:
                ressource = WAV_70;
                 break;
            case POIType.RADAR_80:
                ressource = WAV_80;
               break;
            case POIType.RADAR_90:
                ressource = WAV_90;
                break;
            case POIType.RADAR_100:
                ressource = WAV_100;
               break;
            case POIType.RADAR_110:
                ressource = WAV_110;
                break;
            case POIType.RADAR_120:
                ressource = WAV_120;
                break;
            case POIType.RADAR_130:
                ressource = WAV_130;
                break;
          case POIType.ECOLE:
                ressource = WAV_EC;
                break;
          case POIType.ACCID:
                ressource = WAV_AC;
                break;
          case POIType.FROUGE:
                ressource = WAV_FR;
                break;
          case POIType.PASNIVO:
                ressource = WAV_NI;
                break;
          case POIType.TRAVO:
                ressource = WAV_TR;
                break;
           default:
                ressource = WAV_50;
                break;
         }
         beep2();
         pause(2);
         playAsync(ressource);
    }
    
    public void pause(int second){
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void beep()
    {
        play(WAV_BEEP);
    }

        public void beep2()
    {
        play(WAV_BEEP2);
    }

     public void beep_pose()
    {
       beep();
       pause(2);
       play(WAV_PS);
    }

    public void beepTone()
    {
        isRunning = true;
        int dur = 1000;
        try {
            Manager.playTone(ToneControl.C4, dur, 100);
            try {
                Thread.sleep(dur+10);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            isRunning = false;
            synchronized(lock) {
                lock.notifyAll();
            }
        } catch (MediaException ex) {
            ex.printStackTrace();
            isRunning = false;
        }
    }

    public void announceStart()
    {
        beep2();
        playAsync(WAV_START);
        pause(2);
        playAsync(WAV_CT);
    }

    public void announceEnd()
    {
        beep2();
        playAsync(WAV_END);
    }
    
    public void merci()
    {
        beep2();
        playAsync(WAV_MERCI);
        pause(2);
        playAsync(WAV_PS);
    }

    public void play(String ressource) {
        Player player = null;
        if(isRunning) {
            synchronized(lock) {
                try {
                    lock.wait(4000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        isRunning = true;
        try {
            InputStream is = getClass().getResourceAsStream(ressource);
            String mimeType = getMimeType(ressource);
            if(mimeType == null) mimeType = "audio/mpeg";
            player = Manager.createPlayer(is, mimeType);
            player.addPlayerListener(this);
            player.start();
        } catch (Exception ex) {
            NotificationService.getInstance().display(ex.getMessage());
            ex.printStackTrace();
            if(player != null) player.close();
            isRunning = false;
        }
    }

    public void playAsync(final String ressource) {
        new Thread(new Runnable() {
            public void run() {
                play(ressource);
            }
        }).start();
    }

    public void playerUpdate(Player player, String event, Object eventData){
        if (event.equals(PlayerListener.STARTED)) {
            ((VolumeControl)player.getControl("VolumeControl")).setLevel(100);
        } else if(event.equals(PlayerListener.CLOSED)) {
            isRunning = false;
            synchronized(lock) {
                lock.notifyAll();
            }
        }
    }

}
