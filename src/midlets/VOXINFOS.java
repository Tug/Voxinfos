/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package midlets;

import javame.location.BluetoothDeviceDiscoverer;
import gui.GPSForm;
import javame.location.GPSLocator;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStoreException;
import org.netbeans.microedition.lcdui.SplashScreen;
import org.netbeans.microedition.lcdui.WaitScreen;
import org.netbeans.microedition.util.SimpleCancellableTask;
import util.i18n;
import vox.io.PositionSender;
import io.ReceiverSender;
import javame.location.BluetoothGPSDevice;
import javame.location.GPSDevice;
import javame.location.InternalGPSDevice;
import javame.location.SimGPSDevice;
import javame.text.DecimalFormat;
import javax.microedition.io.PushRegistry;
import java.io.IOException;
import javax.microedition.io.ConnectionNotFoundException;
import javame.util.Observable;
import vox.Config;
import javame.util.Observer;
import vox.io.DataReceiver;
import vox.util.poi.POIProximityAlerter;
import gui.NotificationItem;
import vox.io.ReceiverSenderFactory;
import vox.util.followup.FollowUp;
import vox.util.followup.FollowUpList;
import vox.VoxService;
import java.util.Date;
import javame.util.HashMap;
import javame.util.Map;
import vox.NotificationService;
import vox.io.ReceiverService;
import vox.io.ConnectionFactory;
import vox.io.FollowUpSender;
import vox.io.VoxMessage;

/**
 * @author Tug
 */
public class VOXINFOS extends MIDlet implements CommandListener
{

    private boolean midletPaused = false;
    private String errInfo = "";
    private int shootyMinutes;
    private InternalGPSDevice internalGPSDevice = null;
    private BluetoothGPSDevice[] bluetoothDevices = null;
    //private GPSDevice currentGPSDevice;
    private Observable nameObservable = new Observable();
    private ReceiverSender receiverSender;
    private Observer proximityObserver;
    private POIProximityAlerter alerter;
    private Observer endOfServiceObserver;
    private NotificationItem notificationItem;
    private NotificationItem followUpStatusNotificationItem;
    private VoxService voxService;
    private ReceiverSender keyReceiverSender;
    private ReceiverService receiverService;
    private Map params;


    //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private Command sendSMSShootyCommand;
    private Command exitCommand2;
    private Command startWeekyCommand;
    private Command startShootyCommand;
    private Command sendPositionCommand;
    private Command itemCommand1;
    private Command cancelCommand;
    private Command itemCommand;
    private Command scanBluetoothGPS;
    private Command exitCommand;
    private Command hideSMS;
    private Command displaySMS;
    private Command backCommand1;
    private Command backCommand;
    private Command okCommand;
    private Command backCommand2;
    private Command editConnectionCommand;
    private Command disconnectCommand;
    private Command okCommand1;
    private Command backCommand3;
    private Command informRadar;
    private Command Retour;
    private Command helpCommand;
    private Command optionsmenu;
    private Command backCommand4;
    private Command okCommand2;
    private WaitScreen loadingScreen;
    private WaitScreen sendPositionScreen;
    private Alert PostitionSentError;
    private Alert PostitionSentSucess;
    private Gauge indicator;
    private Form shootyForm;
    private TextField textField1;
    private StringItem stringItem;
    private Alert SMSShootyError;
    private SplashScreen shootyScreenConfirmation;
    private List sourceChooser;
    private Alert BluetoothSearchSuccess;
    private WaitScreen definingGPSSource;
    private Alert DefiningGPSSourceError;
    private Alert BluetoothSearchError;
    private WaitScreen ScanningBluetoothScreen;
    private GPSForm gpsForm;
    private WaitScreen sendSMSShootyScreen;
    private WaitScreen setConnectionTypeScreen;
    private Alert ConnectionTypeError;
    private Form connectionChooserForm;
    private StringItem stringItem1;
    private ChoiceGroup choiceGroup;
    private WaitScreen waitForKeyScreen;
    private WaitScreen askForKeyScreen;
    private Alert loadServiceFailure;
    private List RadarList;
    private WaitScreen loadServiceScreen;
    private Alert waitForKeyError;
    private SplashScreen exitScreen;
    private Alert askForKeyError;
    private Alert serviceIsNotRunningError;
    private WaitScreen sendRadarScreen;
    private Form askForEmailForm;
    private StringItem stringItem3;
    private TextField textField;
    private Form Help;
    private StringItem stringItem2;
    private Alert noGPSError;
    private Form options;
    private ChoiceGroup choiceGroup2;
    private TextField textField5;
    private TextField textField2;
    private TextField textField4;
    private ChoiceGroup choiceGroup1;
    private TextField textField3;
    private StringItem stringItem4;
    private WaitScreen sendoptionsscreen;
    private Ticker ticker;
    private SimpleCancellableTask task10;
    private Image image2;
    private Font font;
    private SimpleCancellableTask task1;
    private SimpleCancellableTask task;
    private SimpleCancellableTask task3;
    private SimpleCancellableTask task2;
    private SimpleCancellableTask task4;
    private SimpleCancellableTask task5;
    private SimpleCancellableTask task7;
    private SimpleCancellableTask task6;
    private SimpleCancellableTask task8;
    private SimpleCancellableTask task9;
    private SimpleCancellableTask task11;
    //</editor-fold>//GEN-END:|fields|0|

    /**
     * The VOXINFOS constructor.
     */
    public VOXINFOS() {
        loadI18n();
        params = new HashMap();
        params.put("SMS-Num", getMyAppProperty("SMS-Num", "+33678286232"));
        params.put("SMS-Port", getMyAppProperty("SMS-Port", "35680"));
        params.put("HTTP-Host", getMyAppProperty("HTTP-Host", "www.voxinfos.com"));
        params.put("HTTP-Port", getMyAppProperty("HTTP-Port", "80"));
        params.put("HTTP-Pos-Url", getMyAppProperty("HTTP-Pos-Url", "/recepsms.asp"));
        params.put("SOCKET-Host", getMyAppProperty("SOCKET-Host", "socket.voxinfos.fr:8888"));
        params.put("CONN-Type", getMyAppProperty("CONN-Type", "HTTP"));
        params.put("SIM-Gps", getMyAppProperty("SIM-Gps", "false"));
        
/*
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMin = c.get(Calendar.MINUTE);
        FollowUp fu = new FollowUp(currentDay, currentHour, currentMin+1, 10);
        Config.getInstance().getFollowUpList().add(fu);
        Config.getInstance().getPOIList().add(new POI(48.94,2.477,POIType.RADAR_100)); // 2 min
        Config.getInstance().getPOIList().add(new POI(48.93,2.506,POIType.RADAR_100)); // 5 min
*/
    }

    private void loadAll() throws IOException {
        Config.getInstance().loadMidletParams(params);
        loadGPS();
        loadReceiverSender();
        loadNotificationItem();
    }
    
    private String getMyAppProperty(String key, String def) {
    	String value = getAppProperty(key);
    	if(value == null || value.length()==0) {
    		return def;
    	}
    	return value;
    }

    private void loadI18n()
    {
        switch(langue) {
            case "FR":
              i18n.init("fr_FR");
                break;
            case "EN":
               i18n.init("en-US");
                break;
          }
    }

    private void loadGPS() {
        this.internalGPSDevice = new InternalGPSDevice();
        GPSDevice currentGPSDevice = getPreferedGPSDevice();
        if (currentGPSDevice != null) {
            setGPSDeviceSource(currentGPSDevice);
        }
    }

    private void loadReceiverSender() {
        try {
            receiverSender = ReceiverSenderFactory.getConfigInstance();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadNotificationItem() {
        this.notificationItem = NotificationService.getInstance().getItem();
        DataReceiver.getInstance().addObserver(notificationItem);
        this.followUpStatusNotificationItem = new NotificationItem(i18n.s(43));
    }

    private void loadVoxService() throws IOException {
        if(voxService == null) {
            voxService = new VoxService();
            voxService.getServiceObservable().addObserver(followUpStatusNotificationItem);
            voxService.getServiceObservable().notifyNoFollowUp();
            voxService.start();
        }
    }


    //<editor-fold defaultstate="collapsed" desc=" Generated Methods ">//GEN-BEGIN:|methods|0|
    //</editor-fold>//GEN-END:|methods|0|
    //<editor-fold defaultstate="collapsed" desc=" Generated Method: initialize ">//GEN-BEGIN:|0-initialize|0|0-preInitialize
    /**
     * Initilizes the application.
     * It is called only once when the MIDlet is started. The method is called before the <code>startMIDlet</code> method.
     */
    private void initialize() {//GEN-END:|0-initialize|0|0-preInitialize
        // write pre-initialize user code here
//GEN-LINE:|0-initialize|1|0-postInitialize
        // write post-initialize user code here
    }//GEN-BEGIN:|0-initialize|2|
    //</editor-fold>//GEN-END:|0-initialize|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: startMIDlet ">//GEN-BEGIN:|3-startMIDlet|0|3-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Started point.
     */
    public void startMIDlet() {//GEN-END:|3-startMIDlet|0|3-preAction
        // write pre-action user code here
        switchDisplayable(null, getLoadingScreen());//GEN-LINE:|3-startMIDlet|1|3-postAction
        // write post-action user code here
    }//GEN-BEGIN:|3-startMIDlet|2|
    //</editor-fold>//GEN-END:|3-startMIDlet|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: resumeMIDlet ">//GEN-BEGIN:|4-resumeMIDlet|0|4-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
     */
    public void resumeMIDlet() {//GEN-END:|4-resumeMIDlet|0|4-preAction
        // write pre-action user code here
        switchDisplayable(null, getGpsForm());//GEN-LINE:|4-resumeMIDlet|1|4-postAction
        // write post-action user code here
    }//GEN-BEGIN:|4-resumeMIDlet|2|
    //</editor-fold>//GEN-END:|4-resumeMIDlet|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: switchDisplayable ">//GEN-BEGIN:|5-switchDisplayable|0|5-preSwitch
    /**
     * Switches a current displayable in a display. The <code>display</code> instance is taken from <code>getDisplay</code> method. This method is used by all actions in the design for switching displayable.
     * @param alert the Alert which is temporarily set to the display; if <code>null</code>, then <code>nextDisplayable</code> is set immediately
     * @param nextDisplayable the Displayable to be set
     */
    public void switchDisplayable(Alert alert, Displayable nextDisplayable) {//GEN-END:|5-switchDisplayable|0|5-preSwitch
        // write pre-switch user code here
        Display display = getDisplay();//GEN-BEGIN:|5-switchDisplayable|1|5-postSwitch
        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }//GEN-END:|5-switchDisplayable|1|5-postSwitch
        // write post-switch user code here
    }//GEN-BEGIN:|5-switchDisplayable|2|
    //</editor-fold>//GEN-END:|5-switchDisplayable|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|7-commandAction|0|7-preCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:|7-commandAction|0|7-preCommandAction
        // write pre-action user code here
        if (displayable == Help) {//GEN-BEGIN:|7-commandAction|1|497-preAction
            if (command == Retour) {//GEN-END:|7-commandAction|1|497-preAction
                // write pre-action user code here
                switchDisplayable(null, getGpsForm());//GEN-LINE:|7-commandAction|2|497-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|3|443-preAction
        } else if (displayable == RadarList) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|3|443-preAction
                // write pre-action user code here
                RadarListAction();//GEN-LINE:|7-commandAction|4|443-postAction
                // write post-action user code here
            } else if (command == backCommand3) {//GEN-LINE:|7-commandAction|5|454-preAction
                // write pre-action user code here
                switchDisplayable(null, getGpsForm());//GEN-LINE:|7-commandAction|6|454-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|7|270-preAction
        } else if (displayable == ScanningBluetoothScreen) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|7|270-preAction
                // write pre-action user code here
                switchDisplayable(getBluetoothSearchError(), getSourceChooser());//GEN-LINE:|7-commandAction|8|270-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|9|269-preAction
                // write pre-action user code here
                switchDisplayable(getBluetoothSearchSuccess(), getSourceChooser());//GEN-LINE:|7-commandAction|10|269-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|11|513-preAction
        } else if (displayable == askForEmailForm) {
            if (command == exitCommand) {//GEN-END:|7-commandAction|11|513-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|7-commandAction|12|513-postAction
                // write post-action user code here
            } else if (command == okCommand) {//GEN-LINE:|7-commandAction|13|512-preAction
                // write pre-action user code here
                switchDisplayable(null, getAskForKeyScreen());//GEN-LINE:|7-commandAction|14|512-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|15|389-preAction
        } else if (displayable == askForKeyScreen) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|15|389-preAction
                // write pre-action user code here
                switchDisplayable(getAskForKeyError(), getExitScreen());//GEN-LINE:|7-commandAction|16|389-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|17|388-preAction
                // write pre-action user code here
                switchDisplayable(null, getWaitForKeyScreen());//GEN-LINE:|7-commandAction|18|388-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|19|362-preAction
        } else if (displayable == connectionChooserForm) {
            if (command == backCommand2) {//GEN-END:|7-commandAction|19|362-preAction
                // write pre-action user code here
                switchDisplayable(null, getGpsForm());//GEN-LINE:|7-commandAction|20|362-postAction
                // write post-action user code here
            } else if (command == okCommand) {//GEN-LINE:|7-commandAction|21|360-preAction
                // write pre-action user code here
                switchDisplayable(null, getSetConnectionTypeScreen());//GEN-LINE:|7-commandAction|22|360-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|23|283-preAction
        } else if (displayable == definingGPSSource) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|23|283-preAction
                // write pre-action user code here
                switchDisplayable(getDefiningGPSSourceError(), getSourceChooser());//GEN-LINE:|7-commandAction|24|283-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|25|282-preAction
                // write pre-action user code here
                switchDisplayable(null, getGpsForm());//GEN-LINE:|7-commandAction|26|282-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|27|432-preAction
        } else if (displayable == exitScreen) {
            if (command == SplashScreen.DISMISS_COMMAND) {//GEN-END:|7-commandAction|27|432-preAction
                // write pre-action user code here
                exitMIDlet();//GEN-LINE:|7-commandAction|28|432-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|29|364-preAction
        } else if (displayable == gpsForm) {
            if (command == editConnectionCommand) {//GEN-END:|7-commandAction|29|364-preAction
                // write pre-action user code here
                switchDisplayable(null, getConnectionChooserForm());//GEN-LINE:|7-commandAction|30|364-postAction
                // write post-action user code here
            } else if (command == exitCommand) {//GEN-LINE:|7-commandAction|31|309-preAction
                // write pre-action user code here
                switchDisplayable(null, getExitScreen());//GEN-LINE:|7-commandAction|32|309-postAction
                // write post-action user code here
            } else if (command == helpCommand) {//GEN-LINE:|7-commandAction|33|499-preAction
                // write pre-action user code here
                switchDisplayable(null, getHelp());//GEN-LINE:|7-commandAction|34|499-postAction
                // write post-action user code here
            } else if (command == informRadar) {//GEN-LINE:|7-commandAction|35|449-preAction
                // write pre-action user code here
                switchDisplayable(null, getRadarList());//GEN-LINE:|7-commandAction|36|449-postAction
                // write post-action user code here
            } else if (command == itemCommand) {//GEN-LINE:|7-commandAction|37|295-preAction
                // write pre-action user code here
                switchDisplayable(null, getSourceChooser());//GEN-LINE:|7-commandAction|38|295-postAction
                // write post-action user code here
            } else if (command == optionsmenu) {//GEN-LINE:|7-commandAction|39|546-preAction
                // write pre-action user code here
                switchDisplayable(null, getOptions());//GEN-LINE:|7-commandAction|40|546-postAction
                // write post-action user code here
            } else if (command == startShootyCommand) {//GEN-LINE:|7-commandAction|41|293-preAction
                // write pre-action user code here
                hasGPS();//GEN-LINE:|7-commandAction|42|293-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|43|436-preAction
        } else if (displayable == loadServiceScreen) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|43|436-preAction
                // write pre-action user code here
                switchDisplayable(getLoadServiceFailure(), getExitScreen());//GEN-LINE:|7-commandAction|44|436-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|45|435-preAction
                // write pre-action user code here
                switchDisplayable(null, getGpsForm());//GEN-LINE:|7-commandAction|46|435-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|47|519-preAction
        } else if (displayable == loadingScreen) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|47|519-preAction
                // write pre-action user code here
                switchDisplayable(null, getExitScreen());//GEN-LINE:|7-commandAction|48|519-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|49|518-preAction
                // write pre-action user code here
                hasKey();//GEN-LINE:|7-commandAction|50|518-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|51|565-preAction
        } else if (displayable == options) {
            if (command == backCommand) {//GEN-END:|7-commandAction|51|565-preAction
                // write pre-action user code here
                switchDisplayable(null, getGpsForm());//GEN-LINE:|7-commandAction|52|565-postAction
                // write post-action user code here
            } else if (command == okCommand) {//GEN-LINE:|7-commandAction|53|564-preAction
                // write pre-action user code here
                switchDisplayable(null, getSendoptionsscreen());//GEN-LINE:|7-commandAction|54|564-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|55|58-preAction
        } else if (displayable == sendPositionScreen) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|55|58-preAction
                // write pre-action user code here
                switchDisplayable(getPostitionSentError(), getGpsForm());//GEN-LINE:|7-commandAction|56|58-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|57|57-preAction
                // write pre-action user code here
                switchDisplayable(getPostitionSentSucess(), getGpsForm());//GEN-LINE:|7-commandAction|58|57-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|59|461-preAction
        } else if (displayable == sendRadarScreen) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|59|461-preAction
                // write pre-action user code here
//GEN-LINE:|7-commandAction|60|461-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|61|460-preAction
                // write pre-action user code here
                switchDisplayable(null, getRadarList());//GEN-LINE:|7-commandAction|62|460-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|63|346-preAction
        } else if (displayable == sendSMSShootyScreen) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|63|346-preAction
                // write pre-action user code here
                switchDisplayable(getSMSShootyError(), getShootyForm());//GEN-LINE:|7-commandAction|64|346-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|65|345-preAction
                // write pre-action user code here
                switchDisplayable(null, getShootyScreenConfirmation());//GEN-LINE:|7-commandAction|66|345-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|67|562-preAction
        } else if (displayable == sendoptionsscreen) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|67|562-preAction
                // write pre-action user code here
//GEN-LINE:|7-commandAction|68|562-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|69|561-preAction
                // write pre-action user code here
                switchDisplayable(null, getExitScreen());//GEN-LINE:|7-commandAction|70|561-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|71|372-preAction
        } else if (displayable == setConnectionTypeScreen) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|71|372-preAction
                // write pre-action user code here
                switchDisplayable(getConnectionTypeError(), getExitScreen());//GEN-LINE:|7-commandAction|72|372-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|73|371-preAction
                // write pre-action user code here
                switchDisplayable(null, getExitScreen());//GEN-LINE:|7-commandAction|74|371-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|75|208-preAction
        } else if (displayable == shootyForm) {
            if (command == exitCommand2) {//GEN-END:|7-commandAction|75|208-preAction
                // write pre-action user code here
                switchDisplayable(null, getGpsForm());//GEN-LINE:|7-commandAction|76|208-postAction
                // write post-action user code here
            } else if (command == sendSMSShootyCommand) {//GEN-LINE:|7-commandAction|77|144-preAction
                // write pre-action user code here
                switchDisplayable(null, getSendSMSShootyScreen());//GEN-LINE:|7-commandAction|78|144-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|79|171-preAction
        } else if (displayable == shootyScreenConfirmation) {
            if (command == SplashScreen.DISMISS_COMMAND) {//GEN-END:|7-commandAction|79|171-preAction
                // write pre-action user code here
                switchDisplayable(null, getGpsForm());//GEN-LINE:|7-commandAction|80|171-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|81|226-preAction
        } else if (displayable == sourceChooser) {
            if (command == List.SELECT_COMMAND) {//GEN-END:|7-commandAction|81|226-preAction
                // write pre-action user code here
                sourceChooserAction();//GEN-LINE:|7-commandAction|82|226-postAction
                // write post-action user code here
            } else if (command == cancelCommand) {//GEN-LINE:|7-commandAction|83|260-preAction
                // write pre-action user code here
                switchDisplayable(null, getGpsForm());//GEN-LINE:|7-commandAction|84|260-postAction
                // write post-action user code here
            } else if (command == itemCommand1) {//GEN-LINE:|7-commandAction|85|279-preAction
                // write pre-action user code here
                switchDisplayable(null, getDefiningGPSSource());//GEN-LINE:|7-commandAction|86|279-postAction
                // write post-action user code here
            } else if (command == scanBluetoothGPS) {//GEN-LINE:|7-commandAction|87|265-preAction
                // write pre-action user code here
                switchDisplayable(null, getScanningBluetoothScreen());//GEN-LINE:|7-commandAction|88|265-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|89|406-preAction
        } else if (displayable == waitForKeyScreen) {
            if (command == WaitScreen.FAILURE_COMMAND) {//GEN-END:|7-commandAction|89|406-preAction
                // write pre-action user code here
                switchDisplayable(getWaitForKeyError(), getExitScreen());//GEN-LINE:|7-commandAction|90|406-postAction
                // write post-action user code here
            } else if (command == WaitScreen.SUCCESS_COMMAND) {//GEN-LINE:|7-commandAction|91|405-preAction
                // write pre-action user code here
                switchDisplayable(null, getLoadServiceScreen());//GEN-LINE:|7-commandAction|92|405-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|93|7-postCommandAction
        }//GEN-END:|7-commandAction|93|7-postCommandAction
        // write post-action user code here
    }//GEN-BEGIN:|7-commandAction|94|
    //</editor-fold>//GEN-END:|7-commandAction|94|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sendPositionScreen ">//GEN-BEGIN:|54-getter|0|54-preInit
    /**
     * Returns an initiliazed instance of sendPositionScreen component.
     * @return the initialized component instance
     */
    public WaitScreen getSendPositionScreen() {
        if (sendPositionScreen == null) {//GEN-END:|54-getter|0|54-preInit
            // write pre-init user code here
            sendPositionScreen = new WaitScreen(getDisplay());//GEN-BEGIN:|54-getter|1|54-postInit
            sendPositionScreen.setTitle(i18n.s(81));
            sendPositionScreen.setCommandListener(this);
            sendPositionScreen.setText(i18n.s(2));
            sendPositionScreen.setTask(getTask1());//GEN-END:|54-getter|1|54-postInit
            // write post-init user code here
        }//GEN-BEGIN:|54-getter|2|
        return sendPositionScreen;
    }
    //</editor-fold>//GEN-END:|54-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: PostitionSentSucess ">//GEN-BEGIN:|61-getter|0|61-preInit
    /**
     * Returns an initiliazed instance of PostitionSentSucess component.
     * @return the initialized component instance
     */
    public Alert getPostitionSentSucess() {
        if (PostitionSentSucess == null) {//GEN-END:|61-getter|0|61-preInit
            // write pre-init user code here
            PostitionSentSucess = new Alert(i18n.s(3), i18n.s(4), null, null);//GEN-BEGIN:|61-getter|1|61-postInit
            PostitionSentSucess.setIndicator(getIndicator());
            PostitionSentSucess.setTimeout(5000);//GEN-END:|61-getter|1|61-postInit
            // write post-init user code here
        }//GEN-BEGIN:|61-getter|2|
        return PostitionSentSucess;
    }
    //</editor-fold>//GEN-END:|61-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: PostitionSentError ">//GEN-BEGIN:|62-getter|0|62-preInit
    /**
     * Returns an initiliazed instance of PostitionSentError component.
     * @return the initialized component instance
     */
    public Alert getPostitionSentError() {
        if (PostitionSentError == null) {//GEN-END:|62-getter|0|62-preInit
            // write pre-init user code here
            PostitionSentError = new Alert(i18n.s(5), "Erreur envoi du SMS "+errInfo, null, AlertType.ALARM);//GEN-BEGIN:|62-getter|1|62-postInit
            PostitionSentError.setTimeout(5000);//GEN-END:|62-getter|1|62-postInit
            // write post-init user code here
        }//GEN-BEGIN:|62-getter|2|
        return PostitionSentError;
    }
    //</editor-fold>//GEN-END:|62-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task1 ">//GEN-BEGIN:|76-getter|0|76-preInit
    /**
     * Returns an initiliazed instance of task1 component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask1() {
        if (task1 == null) {//GEN-END:|76-getter|0|76-preInit
            // write pre-init user code here
            task1 = new SimpleCancellableTask();//GEN-BEGIN:|76-getter|1|76-execute
            task1.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|76-getter|1|76-execute
                    // write task-execution user code here
                    try {
                        ReceiverSender receiverSender = ReceiverSenderFactory.getConfigInstance();
                        PositionSender posSender = new PositionSender(receiverSender.getSender());
                        posSender.addExtra("time", VoxMessage.FollowUp_2_JSONArray(new FollowUp(new Date(), 0)));
                        posSender.sendPositionSync(); // sync
                    } catch (Exception e) {
                        //handle exception
                        System.out.println("Error : " + e.toString());
                        errInfo = e.toString();
                        throw e;
                    }
                }//GEN-BEGIN:|76-getter|2|76-postInit
            });//GEN-END:|76-getter|2|76-postInit
            // write post-init user code here
        }//GEN-BEGIN:|76-getter|3|
        return task1;
    }
    //</editor-fold>//GEN-END:|76-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sendSMSShootyCommand ">//GEN-BEGIN:|143-getter|0|143-preInit
    /**
     * Returns an initiliazed instance of sendSMSShootyCommand component.
     * @return the initialized component instance
     */
    public Command getSendSMSShootyCommand() {
        if (sendSMSShootyCommand == null) {//GEN-END:|143-getter|0|143-preInit
            // write pre-init user code here
            sendSMSShootyCommand = new Command("Ok", Command.OK, 0);//GEN-LINE:|143-getter|1|143-postInit
            // write post-init user code here
        }//GEN-BEGIN:|143-getter|2|
        return sendSMSShootyCommand;
    }
    //</editor-fold>//GEN-END:|143-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: shootyForm ">//GEN-BEGIN:|142-getter|0|142-preInit
    /**
     * Returns an initiliazed instance of shootyForm component.
     * @return the initialized component instance
     */
    public Form getShootyForm() {
        if (shootyForm == null) {//GEN-END:|142-getter|0|142-preInit
            // write pre-init user code here
            shootyForm = new Form("Shooty", new Item[] { getTextField1(), getStringItem() });//GEN-BEGIN:|142-getter|1|142-postInit
            shootyForm.addCommand(getSendSMSShootyCommand());
            shootyForm.addCommand(getExitCommand2());
            shootyForm.setCommandListener(this);//GEN-END:|142-getter|1|142-postInit
            // write post-init user code here
            shootyMinutes = Integer.parseInt(getTextField1().getString());
            shootyForm.setItemStateListener(new ItemStateListener() {
                public void itemStateChanged(Item item) {
                    System.out.println("command action !");
                    if (item.equals(getTextField1())) {
                        String minStr = ((TextField) item).getString();
                        getStringItem().setText(getEstimCost(minStr));
                        shootyMinutes = Integer.parseInt(minStr);
                    }
                }
            });
        }//GEN-BEGIN:|142-getter|2|
        return shootyForm;
    }
    //</editor-fold>//GEN-END:|142-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: SMSShootyError ">//GEN-BEGIN:|151-getter|0|151-preInit
    /**
     * Returns an initiliazed instance of SMSShootyError component.
     * @return the initialized component instance
     */
    public Alert getSMSShootyError() {
        if (SMSShootyError == null) {//GEN-END:|151-getter|0|151-preInit
            // write pre-init user code here
            SMSShootyError = new Alert(i18n.s(7), i18n.s(8)+" "+errInfo, null, AlertType.ALARM);//GEN-BEGIN:|151-getter|1|151-postInit
            SMSShootyError.setTimeout(5000);//GEN-END:|151-getter|1|151-postInit
            // write post-init user code here
        }//GEN-BEGIN:|151-getter|2|
        return SMSShootyError;
    }
    //</editor-fold>//GEN-END:|151-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task ">//GEN-BEGIN:|148-getter|0|148-preInit
    /**
     * Returns an initiliazed instance of task component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask() {
        if (task == null) {//GEN-END:|148-getter|0|148-preInit
            // write pre-init user code here
            task = new SimpleCancellableTask();//GEN-BEGIN:|148-getter|1|148-execute
            task.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|148-getter|1|148-execute
                    ReceiverSender receiverSender = ReceiverSenderFactory.getConfigInstance();
                    FollowUpSender fuSender = new FollowUpSender(receiverSender.getSender());
                    fuSender.addFollowUp(new FollowUp(new Date(), shootyMinutes));
                    fuSender.sendSync();
                }//GEN-BEGIN:|148-getter|2|148-postInit
            });//GEN-END:|148-getter|2|148-postInit
            // write post-init user code here
        }//GEN-BEGIN:|148-getter|3|
        return task;
    }
    //</editor-fold>//GEN-END:|148-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: textField1 ">//GEN-BEGIN:|156-getter|0|156-preInit
    /**
     * Returns an initiliazed instance of textField1 component.
     * @return the initialized component instance
     */
    public TextField getTextField1() {
        if (textField1 == null) {//GEN-END:|156-getter|0|156-preInit
            // write pre-init user code here
            textField1 = new TextField(i18n.s(9), duredef, 3, TextField.NUMERIC);//GEN-LINE:|156-getter|1|156-postInit
            // write post-init user code here
        }//GEN-BEGIN:|156-getter|2|
        return textField1;
    }
    //</editor-fold>//GEN-END:|156-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem ">//GEN-BEGIN:|157-getter|0|157-preInit
    /**
     * Returns an initiliazed instance of stringItem component.
     * @return the initialized component instance
     */
    public StringItem getStringItem() {
        if (stringItem == null) {//GEN-END:|157-getter|0|157-preInit
            // write pre-init user code here
            stringItem = new StringItem(i18n.s(10), getEstimCost(textField1.getString()), Item.PLAIN);//GEN-LINE:|157-getter|1|157-postInit
            // write post-init user code here
        }//GEN-BEGIN:|157-getter|2|
        return stringItem;
    }
    //</editor-fold>//GEN-END:|157-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: shootyScreenConfirmation ">//GEN-BEGIN:|170-getter|0|170-preInit
    /**
     * Returns an initiliazed instance of shootyScreenConfirmation component.
     * @return the initialized component instance
     */
    public SplashScreen getShootyScreenConfirmation() {
        if (shootyScreenConfirmation == null) {//GEN-END:|170-getter|0|170-preInit
            // write pre-init user code here
            shootyScreenConfirmation = new SplashScreen(getDisplay());//GEN-BEGIN:|170-getter|1|170-postInit
            shootyScreenConfirmation.setTitle(i18n.s(11));
            shootyScreenConfirmation.setCommandListener(this);
            shootyScreenConfirmation.setFullScreenMode(true);
            shootyScreenConfirmation.setImage(getImage2());
            shootyScreenConfirmation.setText(i18n.s(12));
            shootyScreenConfirmation.setTimeout(5000);
            shootyScreenConfirmation.setAllowTimeoutInterrupt(false);//GEN-END:|170-getter|1|170-postInit
            // write post-init user code here
        }//GEN-BEGIN:|170-getter|2|
        return shootyScreenConfirmation;
    }
    //</editor-fold>//GEN-END:|170-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitCommand2 ">//GEN-BEGIN:|207-getter|0|207-preInit
    /**
     * Returns an initiliazed instance of exitCommand2 component.
     * @return the initialized component instance
     */
    public Command getExitCommand2() {
        if (exitCommand2 == null) {//GEN-END:|207-getter|0|207-preInit
            // write pre-init user code here
            exitCommand2 = new Command(i18n.s(13), Command.EXIT, 0);//GEN-LINE:|207-getter|1|207-postInit
            // write post-init user code here
        }//GEN-BEGIN:|207-getter|2|
        return exitCommand2;
    }
    //</editor-fold>//GEN-END:|207-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sourceChooser ">//GEN-BEGIN:|225-getter|0|225-preInit
    /**
     * Returns an initiliazed instance of sourceChooser component.
     * @return the initialized component instance
     */
    public List getSourceChooser() {
        if (sourceChooser == null) {//GEN-END:|225-getter|0|225-preInit
            // write pre-init user code here
            sourceChooser = new List(i18n.s(14), Choice.IMPLICIT);//GEN-BEGIN:|225-getter|1|225-postInit
            sourceChooser.addCommand(getCancelCommand());
            sourceChooser.addCommand(getScanBluetoothGPS());
            sourceChooser.addCommand(getItemCommand1());
            sourceChooser.setCommandListener(this);
            sourceChooser.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
            sourceChooser.setSelectCommand(getItemCommand1());
            sourceChooser.setSelectedFlags(new boolean[] {  });//GEN-END:|225-getter|1|225-postInit
            // write post-init user code here
            refreshDeviceList();
        }//GEN-BEGIN:|225-getter|2|
        return sourceChooser;
    }
    //</editor-fold>//GEN-END:|225-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: sourceChooserAction ">//GEN-BEGIN:|225-action|0|225-preAction
    /**
     * Performs an action assigned to the selected list element in the sourceChooser component.
     */
    public void sourceChooserAction() {//GEN-END:|225-action|0|225-preAction
        // enter pre-action user code here
        String __selectedString = getSourceChooser().getString(getSourceChooser().getSelectedIndex());//GEN-LINE:|225-action|1|225-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|225-action|2|
    //</editor-fold>//GEN-END:|225-action|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sendPositionCommand ">//GEN-BEGIN:|232-getter|0|232-preInit
    /**
     * Returns an initiliazed instance of sendPositionCommand component.
     * @return the initialized component instance
     */
    public Command getSendPositionCommand() {
        if (sendPositionCommand == null) {//GEN-END:|232-getter|0|232-preInit
            // write pre-init user code here
            sendPositionCommand = new Command("Tester SMS", Command.SCREEN, 0);//GEN-LINE:|232-getter|1|232-postInit
            // write post-init user code here
        }//GEN-BEGIN:|232-getter|2|
        return sendPositionCommand;
    }
    //</editor-fold>//GEN-END:|232-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: startShootyCommand ">//GEN-BEGIN:|234-getter|0|234-preInit
    /**
     * Returns an initiliazed instance of startShootyCommand component.
     * @return the initialized component instance
     */
    public Command getStartShootyCommand() {
        if (startShootyCommand == null) {//GEN-END:|234-getter|0|234-preInit
            // write pre-init user code here
            startShootyCommand = new Command(i18n.s(15), Command.SCREEN, 0);//GEN-LINE:|234-getter|1|234-postInit
            // write post-init user code here
        }//GEN-BEGIN:|234-getter|2|
        return startShootyCommand;
    }
    //</editor-fold>//GEN-END:|234-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: startWeekyCommand ">//GEN-BEGIN:|236-getter|0|236-preInit
    /**
     * Returns an initiliazed instance of startWeekyCommand component.
     * @return the initialized component instance
     */
    public Command getStartWeekyCommand() {
        if (startWeekyCommand == null) {//GEN-END:|236-getter|0|236-preInit
            // write pre-init user code here
            startWeekyCommand = new Command("Lancer Weeky", Command.SCREEN, 0);//GEN-LINE:|236-getter|1|236-postInit
            // write post-init user code here
        }//GEN-BEGIN:|236-getter|2|
        return startWeekyCommand;
    }
    //</editor-fold>//GEN-END:|236-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemCommand ">//GEN-BEGIN:|256-getter|0|256-preInit
    /**
     * Returns an initiliazed instance of itemCommand component.
     * @return the initialized component instance
     */
    public Command getItemCommand() {
        if (itemCommand == null) {//GEN-END:|256-getter|0|256-preInit
            // write pre-init user code here
            itemCommand = new Command(i18n.s(83), Command.ITEM, 0);//GEN-LINE:|256-getter|1|256-postInit
            // write post-init user code here
        }//GEN-BEGIN:|256-getter|2|
        return itemCommand;
    }
    //</editor-fold>//GEN-END:|256-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelCommand ">//GEN-BEGIN:|259-getter|0|259-preInit
    /**
     * Returns an initiliazed instance of cancelCommand component.
     * @return the initialized component instance
     */
    public Command getCancelCommand() {
        if (cancelCommand == null) {//GEN-END:|259-getter|0|259-preInit
            // write pre-init user code here
            cancelCommand = new Command(i18n.s(18), Command.EXIT, 0);//GEN-LINE:|259-getter|1|259-postInit
            // write post-init user code here
        }//GEN-BEGIN:|259-getter|2|
        return cancelCommand;
    }
    //</editor-fold>//GEN-END:|259-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: scanBluetoothGPS ">//GEN-BEGIN:|264-getter|0|264-preInit
    /**
     * Returns an initiliazed instance of scanBluetoothGPS component.
     * @return the initialized component instance
     */
    public Command getScanBluetoothGPS() {
        if (scanBluetoothGPS == null) {//GEN-END:|264-getter|0|264-preInit
            // write pre-init user code here
            scanBluetoothGPS = new Command("Scan bt", Command.OK, 0);//GEN-LINE:|264-getter|1|264-postInit
            // write post-init user code here
        }//GEN-BEGIN:|264-getter|2|
        return scanBluetoothGPS;
    }
    //</editor-fold>//GEN-END:|264-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: BluetoothSearchError ">//GEN-BEGIN:|266-getter|0|266-preInit
    /**
     * Returns an initiliazed instance of BluetoothSearchError component.
     * @return the initialized component instance
     */
    public Alert getBluetoothSearchError() {
        if (BluetoothSearchError == null) {//GEN-END:|266-getter|0|266-preInit
            // write pre-init user code here
            BluetoothSearchError = new Alert(i18n.s(56), task2.getFailureMessage(), null, null);//GEN-BEGIN:|266-getter|1|266-postInit
            BluetoothSearchError.setTimeout(5000);//GEN-END:|266-getter|1|266-postInit
            // write post-init user code here
        }//GEN-BEGIN:|266-getter|2|
        return BluetoothSearchError;
    }
    //</editor-fold>//GEN-END:|266-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: ScanningBluetoothScreen ">//GEN-BEGIN:|268-getter|0|268-preInit
    /**
     * Returns an initiliazed instance of ScanningBluetoothScreen component.
     * @return the initialized component instance
     */
    public WaitScreen getScanningBluetoothScreen() {
        if (ScanningBluetoothScreen == null) {//GEN-END:|268-getter|0|268-preInit
            // write pre-init user code here
            ScanningBluetoothScreen = new WaitScreen(getDisplay());//GEN-BEGIN:|268-getter|1|268-postInit
            ScanningBluetoothScreen.setTitle("Bluetooth");
            ScanningBluetoothScreen.setCommandListener(this);
            ScanningBluetoothScreen.setText(i18n.s(19));
            ScanningBluetoothScreen.setTask(getTask2());//GEN-END:|268-getter|1|268-postInit
            // write post-init user code here
        }//GEN-BEGIN:|268-getter|2|
        return ScanningBluetoothScreen;
    }
    //</editor-fold>//GEN-END:|268-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task2 ">//GEN-BEGIN:|271-getter|0|271-preInit
    /**
     * Returns an initiliazed instance of task2 component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask2() {
        if (task2 == null) {//GEN-END:|271-getter|0|271-preInit
            // write pre-init user code here
            task2 = new SimpleCancellableTask();//GEN-BEGIN:|271-getter|1|271-execute
            task2.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|271-getter|1|271-execute
                    bluetoothDevices = BluetoothDeviceDiscoverer.findDevices();
                    refreshDeviceList();
                }//GEN-BEGIN:|271-getter|2|271-postInit
            });//GEN-END:|271-getter|2|271-postInit
            // write post-init user code here
        }//GEN-BEGIN:|271-getter|3|
        return task2;
    }
    //</editor-fold>//GEN-END:|271-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: BluetoothSearchSuccess ">//GEN-BEGIN:|272-getter|0|272-preInit
    /**
     * Returns an initiliazed instance of BluetoothSearchSuccess component.
     * @return the initialized component instance
     */
    public Alert getBluetoothSearchSuccess() {
        if (BluetoothSearchSuccess == null) {//GEN-END:|272-getter|0|272-preInit
            // write pre-init user code here
            BluetoothSearchSuccess = new Alert(i18n.s(20), ""+bluetoothDevices.length+" périphériques trouvés!", null, AlertType.ALARM);//GEN-BEGIN:|272-getter|1|272-postInit
            BluetoothSearchSuccess.setTimeout(5000);//GEN-END:|272-getter|1|272-postInit
            // write post-init user code here
        }//GEN-BEGIN:|272-getter|2|
        return BluetoothSearchSuccess;
    }
    //</editor-fold>//GEN-END:|272-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: itemCommand1 ">//GEN-BEGIN:|275-getter|0|275-preInit
    /**
     * Returns an initiliazed instance of itemCommand1 component.
     * @return the initialized component instance
     */
    public Command getItemCommand1() {
        if (itemCommand1 == null) {//GEN-END:|275-getter|0|275-preInit
            // write pre-init user code here
            itemCommand1 = new Command(i18n.s(22), Command.ITEM, 0);//GEN-LINE:|275-getter|1|275-postInit
            // write post-init user code here
        }//GEN-BEGIN:|275-getter|2|
        return itemCommand1;
    }
    //</editor-fold>//GEN-END:|275-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: definingGPSSource ">//GEN-BEGIN:|281-getter|0|281-preInit
    /**
     * Returns an initiliazed instance of definingGPSSource component.
     * @return the initialized component instance
     */
    public WaitScreen getDefiningGPSSource() {
        if (definingGPSSource == null) {//GEN-END:|281-getter|0|281-preInit
            // write pre-init user code here
            definingGPSSource = new WaitScreen(getDisplay());//GEN-BEGIN:|281-getter|1|281-postInit
            definingGPSSource.setTitle(i18n.s(23));
            definingGPSSource.setCommandListener(this);
            definingGPSSource.setText("Connect....");
            definingGPSSource.setTask(getTask3());//GEN-END:|281-getter|1|281-postInit
            // write post-init user code here
        }//GEN-BEGIN:|281-getter|2|
        return definingGPSSource;
    }
    //</editor-fold>//GEN-END:|281-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task3 ">//GEN-BEGIN:|284-getter|0|284-preInit
    /**
     * Returns an initiliazed instance of task3 component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask3() {
        if (task3 == null) {//GEN-END:|284-getter|0|284-preInit
            // write pre-init user code here
            task3 = new SimpleCancellableTask();//GEN-BEGIN:|284-getter|1|284-execute
            task3.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|284-getter|1|284-execute
                    // write task-execution user code here
                    int id = sourceChooser.getSelectedIndex();
                    GPSDevice nextDevice = null;
                    if (internalGPSDevice != null) {
                        if (id == 0) {
                            nextDevice = internalGPSDevice;
                        }
                        id--;
                    }
                    if (id >= 0 && bluetoothDevices != null && bluetoothDevices.length > id) {
                        try {
                            bluetoothDevices[id].getLocationProvider();
                        } catch(Exception e) {
                            throw new Exception("Erreur source bluetooth non compatible");
                        }
                        nextDevice = bluetoothDevices[id];
                    }
                    if (nextDevice == null) {
                        throw new Exception("Erreur selection!");
                    }
                    setGPSDeviceSource(nextDevice);
                }//GEN-BEGIN:|284-getter|2|284-postInit
            });//GEN-END:|284-getter|2|284-postInit
            // write post-init user code here
        }//GEN-BEGIN:|284-getter|3|
        return task3;
    }
    //</editor-fold>//GEN-END:|284-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: DefiningGPSSourceError ">//GEN-BEGIN:|287-getter|0|287-preInit
    /**
     * Returns an initiliazed instance of DefiningGPSSourceError component.
     * @return the initialized component instance
     */
    public Alert getDefiningGPSSourceError() {
        if (DefiningGPSSourceError == null) {//GEN-END:|287-getter|0|287-preInit
            // write pre-init user code here
            DefiningGPSSourceError = new Alert(i18n.s(56), task3.getFailureMessage(), null, null);//GEN-BEGIN:|287-getter|1|287-postInit
            DefiningGPSSourceError.setTimeout(5000);//GEN-END:|287-getter|1|287-postInit
            // write post-init user code here
        }//GEN-BEGIN:|287-getter|2|
        return DefiningGPSSourceError;
    }
    //</editor-fold>//GEN-END:|287-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: gpsForm ">//GEN-BEGIN:|291-getter|0|291-preInit
    /**
     * Returns an initiliazed instance of gpsForm component.
     * @return the initialized component instance
     */
    public GPSForm getGpsForm() {
        if (gpsForm == null) {//GEN-END:|291-getter|0|291-preInit
            // write pre-init user code here
            gpsForm = new GPSForm();//GEN-BEGIN:|291-getter|1|291-postInit
            gpsForm.setTitle("");
            gpsForm.addCommand(getStartShootyCommand());
            gpsForm.addCommand(getItemCommand());
            gpsForm.addCommand(getExitCommand());
            gpsForm.addCommand(getEditConnectionCommand());
            gpsForm.addCommand(getInformRadar());
            gpsForm.addCommand(getHelpCommand());
            gpsForm.addCommand(getOptionsmenu());
            gpsForm.setCommandListener(this);//GEN-END:|291-getter|1|291-postInit
            // write post-init user code here
            GPSLocator.getInstance().addLocationObserver(gpsForm.getLocationObserver());
            GPSLocator.getInstance().addStateObserver(gpsForm.getStateObserver());
            nameObservable.addObserver(gpsForm.getSourceObserver());
            GPSDevice currentDevice = GPSLocator.getInstance().getGPSDevice();
            if(currentDevice != null)
                nameObservable.notifyObservers(currentDevice.getName());
            gpsForm.append(notificationItem);
            gpsForm.append(followUpStatusNotificationItem);
            //gpsForm.insert(0, notificationItem);
            //gpsForm.insert(0, followUpStatusNotificationItem);
        }//GEN-BEGIN:|291-getter|2|
        return gpsForm;
    }
    //</editor-fold>//GEN-END:|291-getter|2|



    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitCommand ">//GEN-BEGIN:|308-getter|0|308-preInit
    /**
     * Returns an initiliazed instance of exitCommand component.
     * @return the initialized component instance
     */
    public Command getExitCommand() {
        if (exitCommand == null) {//GEN-END:|308-getter|0|308-preInit
            // write pre-init user code here
            exitCommand = new Command(i18n.s(25), Command.EXIT, 0);//GEN-LINE:|308-getter|1|308-postInit
            // write post-init user code here
        }//GEN-BEGIN:|308-getter|2|
        return exitCommand;
    }
    //</editor-fold>//GEN-END:|308-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: displaySMS ">//GEN-BEGIN:|320-getter|0|320-preInit
    /**
     * Returns an initiliazed instance of displaySMS component.
     * @return the initialized component instance
     */
    public Command getDisplaySMS() {
        if (displaySMS == null) {//GEN-END:|320-getter|0|320-preInit
            // write pre-init user code here
            displaySMS = new Command("afficher", "afficher SMS entrants", Command.ITEM, 0);//GEN-LINE:|320-getter|1|320-postInit
            // write post-init user code here
        }//GEN-BEGIN:|320-getter|2|
        return displaySMS;
    }
    //</editor-fold>//GEN-END:|320-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand ">//GEN-BEGIN:|331-getter|0|331-preInit
    /**
     * Returns an initiliazed instance of backCommand component.
     * @return the initialized component instance
     */
    public Command getBackCommand() {
        if (backCommand == null) {//GEN-END:|331-getter|0|331-preInit
            // write pre-init user code here
            backCommand = new Command("Back", Command.BACK, 0);//GEN-LINE:|331-getter|1|331-postInit
            // write post-init user code here
        }//GEN-BEGIN:|331-getter|2|
        return backCommand;
    }
    //</editor-fold>//GEN-END:|331-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand1 ">//GEN-BEGIN:|335-getter|0|335-preInit
    /**
     * Returns an initiliazed instance of backCommand1 component.
     * @return the initialized component instance
     */
    public Command getBackCommand1() {
        if (backCommand1 == null) {//GEN-END:|335-getter|0|335-preInit
            // write pre-init user code here
            backCommand1 = new Command("Back", Command.BACK, 0);//GEN-LINE:|335-getter|1|335-postInit
            // write post-init user code here
        }//GEN-BEGIN:|335-getter|2|
        return backCommand1;
    }
    //</editor-fold>//GEN-END:|335-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: hideSMS ">//GEN-BEGIN:|336-getter|0|336-preInit
    /**
     * Returns an initiliazed instance of hideSMS component.
     * @return the initialized component instance
     */
    public Command getHideSMS() {
        if (hideSMS == null) {//GEN-END:|336-getter|0|336-preInit
            // write pre-init user code here
            hideSMS = new Command("masquer", "masquer SMS entrants", Command.ITEM, 0);//GEN-LINE:|336-getter|1|336-postInit
            // write post-init user code here
        }//GEN-BEGIN:|336-getter|2|
        return hideSMS;
    }
    //</editor-fold>//GEN-END:|336-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sendSMSShootyScreen ">//GEN-BEGIN:|344-getter|0|344-preInit
    /**
     * Returns an initiliazed instance of sendSMSShootyScreen component.
     * @return the initialized component instance
     */
    public WaitScreen getSendSMSShootyScreen() {
        if (sendSMSShootyScreen == null) {//GEN-END:|344-getter|0|344-preInit
            // write pre-init user code here
            sendSMSShootyScreen = new WaitScreen(getDisplay());//GEN-BEGIN:|344-getter|1|344-postInit
            sendSMSShootyScreen.setTitle(i18n.s(23));
            sendSMSShootyScreen.setCommandListener(this);
            sendSMSShootyScreen.setImage(getImage2());
            sendSMSShootyScreen.setText(i18n.s(30));
            sendSMSShootyScreen.setTask(getTask());//GEN-END:|344-getter|1|344-postInit
            // write post-init user code here
        }//GEN-BEGIN:|344-getter|2|
        return sendSMSShootyScreen;
    }
    //</editor-fold>//GEN-END:|344-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task4 ">//GEN-BEGIN:|347-getter|0|347-preInit
    /**
     * Returns an initiliazed instance of task4 component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask4() {
        if (task4 == null) {//GEN-END:|347-getter|0|347-preInit
            // write pre-init user code here
            task4 = new SimpleCancellableTask();//GEN-BEGIN:|347-getter|1|347-execute
            task4.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|347-getter|1|347-execute
                    // write task-execution user code here
                }//GEN-BEGIN:|347-getter|2|347-postInit
            });//GEN-END:|347-getter|2|347-postInit
            // write post-init user code here
        }//GEN-BEGIN:|347-getter|3|
        return task4;
    }
    //</editor-fold>//GEN-END:|347-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: connectionChooserForm ">//GEN-BEGIN:|355-getter|0|355-preInit
    /**
     * Returns an initiliazed instance of connectionChooserForm component.
     * @return the initialized component instance
     */
    public Form getConnectionChooserForm() {
        if (connectionChooserForm == null) {//GEN-END:|355-getter|0|355-preInit
            // write pre-init user code here
            connectionChooserForm = new Form("Connect mode", new Item[] { getChoiceGroup(), getStringItem1() });//GEN-BEGIN:|355-getter|1|355-postInit
            connectionChooserForm.addCommand(getOkCommand());
            connectionChooserForm.addCommand(getBackCommand2());
            connectionChooserForm.setCommandListener(this);//GEN-END:|355-getter|1|355-postInit
            // write post-init user code here
            String connType = Config.getInstance().getConnectionType();
            ChoiceGroup choiceGroup = getChoiceGroup();
            for(int i=0; i<choiceGroup.size(); i++) {
                if(choiceGroup.getString(i).equals(connType)) {
                    getChoiceGroup().setSelectedIndex(i, true);
                    break;
                }
            }
        }//GEN-BEGIN:|355-getter|2|
        return connectionChooserForm;
    }
    //</editor-fold>//GEN-END:|355-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceGroup ">//GEN-BEGIN:|356-getter|0|356-preInit
    /**
     * Returns an initiliazed instance of choiceGroup component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceGroup() {
        if (choiceGroup == null) {//GEN-END:|356-getter|0|356-preInit
            // write pre-init user code here
            choiceGroup = new ChoiceGroup(i18n.s(31), Choice.EXCLUSIVE);//GEN-BEGIN:|356-getter|1|356-postInit
            choiceGroup.append("SMS", null);
            choiceGroup.append("HTTP", null);
            choiceGroup.setSelectedFlags(new boolean[] { false, false });//GEN-END:|356-getter|1|356-postInit
            // write post-init user code here
        }//GEN-BEGIN:|356-getter|2|
        return choiceGroup;
    }
    //</editor-fold>//GEN-END:|356-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand ">//GEN-BEGIN:|359-getter|0|359-preInit
    /**
     * Returns an initiliazed instance of okCommand component.
     * @return the initialized component instance
     */
    public Command getOkCommand() {
        if (okCommand == null) {//GEN-END:|359-getter|0|359-preInit
            // write pre-init user code here
            okCommand = new Command("Ok", Command.OK, 0);//GEN-LINE:|359-getter|1|359-postInit
            // write post-init user code here
        }//GEN-BEGIN:|359-getter|2|
        return okCommand;
    }
    //</editor-fold>//GEN-END:|359-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand2 ">//GEN-BEGIN:|361-getter|0|361-preInit
    /**
     * Returns an initiliazed instance of backCommand2 component.
     * @return the initialized component instance
     */
    public Command getBackCommand2() {
        if (backCommand2 == null) {//GEN-END:|361-getter|0|361-preInit
            // write pre-init user code here
            backCommand2 = new Command("Back", Command.BACK, 0);//GEN-LINE:|361-getter|1|361-postInit
            // write post-init user code here
        }//GEN-BEGIN:|361-getter|2|
        return backCommand2;
    }
    //</editor-fold>//GEN-END:|361-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: editConnectionCommand ">//GEN-BEGIN:|363-getter|0|363-preInit
    /**
     * Returns an initiliazed instance of editConnectionCommand component.
     * @return the initialized component instance
     */
    public Command getEditConnectionCommand() {
        if (editConnectionCommand == null) {//GEN-END:|363-getter|0|363-preInit
            // write pre-init user code here
            editConnectionCommand = new Command("Connexion", Command.ITEM, 0);//GEN-LINE:|363-getter|1|363-postInit
            // write post-init user code here
        }//GEN-BEGIN:|363-getter|2|
        return editConnectionCommand;
    }
    //</editor-fold>//GEN-END:|363-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem1 ">//GEN-BEGIN:|369-getter|0|369-preInit
    /**
     * Returns an initiliazed instance of stringItem1 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem1() {
        if (stringItem1 == null) {//GEN-END:|369-getter|0|369-preInit
            // write pre-init user code here
            stringItem1 = new StringItem(i18n.s(32), i18n.s(33));//GEN-LINE:|369-getter|1|369-postInit
            // write post-init user code here
        }//GEN-BEGIN:|369-getter|2|
        return stringItem1;
    }
    //</editor-fold>//GEN-END:|369-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: setConnectionTypeScreen ">//GEN-BEGIN:|370-getter|0|370-preInit
    /**
     * Returns an initiliazed instance of setConnectionTypeScreen component.
     * @return the initialized component instance
     */
    public WaitScreen getSetConnectionTypeScreen() {
        if (setConnectionTypeScreen == null) {//GEN-END:|370-getter|0|370-preInit
            // write pre-init user code here
            setConnectionTypeScreen = new WaitScreen(getDisplay());//GEN-BEGIN:|370-getter|1|370-postInit
            setConnectionTypeScreen.setTitle(i18n.s(34));
            setConnectionTypeScreen.setCommandListener(this);
            setConnectionTypeScreen.setTask(getTask5());//GEN-END:|370-getter|1|370-postInit
            // write post-init user code here
        }//GEN-BEGIN:|370-getter|2|
        return setConnectionTypeScreen;
    }
    //</editor-fold>//GEN-END:|370-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task5 ">//GEN-BEGIN:|373-getter|0|373-preInit
    /**
     * Returns an initiliazed instance of task5 component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask5() {
        if (task5 == null) {//GEN-END:|373-getter|0|373-preInit
            // write pre-init user code here
            task5 = new SimpleCancellableTask();//GEN-BEGIN:|373-getter|1|373-execute
            task5.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|373-getter|1|373-execute
                    // write task-execution user code here
                    int index = choiceGroup.getSelectedIndex();
                    if(index != -1) {
                        if(!Config.getInstance().hasKey()) {
                            throw new Exception("Clef non recue");
                        }
                        String connectionType = choiceGroup.getString(index);
                        Config.getInstance().setConnectionType(connectionType);
                    }
                }//GEN-BEGIN:|373-getter|2|373-postInit
            });//GEN-END:|373-getter|2|373-postInit
            // write post-init user code here
        }//GEN-BEGIN:|373-getter|3|
        return task5;
    }
    //</editor-fold>//GEN-END:|373-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: ConnectionTypeError ">//GEN-BEGIN:|376-getter|0|376-preInit
    /**
     * Returns an initiliazed instance of ConnectionTypeError component.
     * @return the initialized component instance
     */
    public Alert getConnectionTypeError() {
        if (ConnectionTypeError == null) {//GEN-END:|376-getter|0|376-preInit
            // write pre-init user code here
            ConnectionTypeError = new Alert(i18n.s(56), task5.getFailureMessage(), null, null);//GEN-BEGIN:|376-getter|1|376-postInit
            ConnectionTypeError.setTimeout(Alert.FOREVER);//GEN-END:|376-getter|1|376-postInit
            // write post-init user code here
        }//GEN-BEGIN:|376-getter|2|
        return ConnectionTypeError;
    }
    //</editor-fold>//GEN-END:|376-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: hasKey ">//GEN-BEGIN:|381-if|0|381-preIf
    /**
     * Performs an action assigned to the hasKey if-point.
     */
    public void hasKey() {//GEN-END:|381-if|0|381-preIf
        // enter pre-if user code here
        if (Config.getInstance().hasKey()) {//GEN-LINE:|381-if|1|382-preAction
            // write pre-action user code here
            switchDisplayable(null, getLoadServiceScreen());//GEN-LINE:|381-if|2|382-postAction
            // write post-action user code here
        } else {//GEN-LINE:|381-if|3|383-preAction
            // write pre-action user code here
            switchDisplayable(null, getAskForEmailForm());//GEN-LINE:|381-if|4|383-postAction
            // write post-action user code here
        }//GEN-LINE:|381-if|5|381-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|381-if|6|
    //</editor-fold>//GEN-END:|381-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: askForKeyScreen ">//GEN-BEGIN:|387-getter|0|387-preInit
    /**
     * Returns an initiliazed instance of askForKeyScreen component.
     * @return the initialized component instance
     */
    public WaitScreen getAskForKeyScreen() {
        if (askForKeyScreen == null) {//GEN-END:|387-getter|0|387-preInit
            // write pre-init user code here
            askForKeyScreen = new WaitScreen(getDisplay());//GEN-BEGIN:|387-getter|1|387-postInit
            askForKeyScreen.setTitle(i18n.s(36));
            askForKeyScreen.setCommandListener(this);
            askForKeyScreen.setImage(getImage2());
            askForKeyScreen.setText(i18n.s(37));
            askForKeyScreen.setTask(getTask6());//GEN-END:|387-getter|1|387-postInit
            // write post-init user code here
        }//GEN-BEGIN:|387-getter|2|
        return askForKeyScreen;
    }
    //</editor-fold>//GEN-END:|387-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task6 ">//GEN-BEGIN:|390-getter|0|390-preInit
    /**
     * Returns an initiliazed instance of task6 component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask6() {
        if (task6 == null) {//GEN-END:|390-getter|0|390-preInit
            // write pre-init user code here
            task6 = new SimpleCancellableTask();//GEN-BEGIN:|390-getter|1|390-execute
            task6.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|390-getter|1|390-execute
                    // write task-execution user code here
                    /*
                    HttpConnection conn = ConnectionFactory.getHTTPConnection(Config.HTTP_LOGIN_URL);
                    HTTPConnector http = new HTTPConnector(conn);
                    Credentials credentials = new Credentials(getLoginScreen().getUsername(),
                                                              getLoginScreen().getPassword());
                    String params = "abo_login="+credentials.getUsername()
                                    +"&abo_password="+credentials.getPassword()
                                    +"&action=Valider";

                    http.post(params);
                    if(conn.getResponseCode() == HttpConnection.HTTP_OK) {
                        Config.getInstance().setCredentials(credentials);
                    } else {
                        throw new Exception("indentifiants incorrects");
                    }
                    */
                	String email = textField.getString();
                    keyReceiverSender = ReceiverSenderFactory.getDefaultSMSReceiverSender();
                    keyReceiverSender.getSender().sendTextMessage("{\"key\":1, \"email\":\""+email+"\"}");
                    receiverService = new ReceiverService();
                    receiverService.addReceiver(keyReceiverSender.getReceiver());
                    new Thread(keyReceiverSender).start();
                    notificationItem.update(null, i18n.s(38));
                }//GEN-BEGIN:|390-getter|2|390-postInit
            });//GEN-END:|390-getter|2|390-postInit
            // write post-init user code here
        }//GEN-BEGIN:|390-getter|3|
        return task6;
    }
    //</editor-fold>//GEN-END:|390-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: disconnectCommand ">//GEN-BEGIN:|396-getter|0|396-preInit
    /**
     * Returns an initiliazed instance of disconnectCommand component.
     * @return the initialized component instance
     */
    public Command getDisconnectCommand() {
        if (disconnectCommand == null) {//GEN-END:|396-getter|0|396-preInit
            // write pre-init user code here
            disconnectCommand = new Command("Item", Command.ITEM, 0);//GEN-LINE:|396-getter|1|396-postInit
            // write post-init user code here
        }//GEN-BEGIN:|396-getter|2|
        return disconnectCommand;
    }
    //</editor-fold>//GEN-END:|396-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: waitForKeyScreen ">//GEN-BEGIN:|404-getter|0|404-preInit
    /**
     * Returns an initiliazed instance of waitForKeyScreen component.
     * @return the initialized component instance
     */
    public WaitScreen getWaitForKeyScreen() {
        if (waitForKeyScreen == null) {//GEN-END:|404-getter|0|404-preInit
            // write pre-init user code here
            waitForKeyScreen = new WaitScreen(getDisplay());//GEN-BEGIN:|404-getter|1|404-postInit
            waitForKeyScreen.setTitle(i18n.s(23));
            waitForKeyScreen.setCommandListener(this);
            waitForKeyScreen.setImage(getImage2());
            waitForKeyScreen.setText(i18n.s(39));
            waitForKeyScreen.setTask(getTask7());//GEN-END:|404-getter|1|404-postInit
            // write post-init user code here
        }//GEN-BEGIN:|404-getter|2|
        return waitForKeyScreen;
    }
    //</editor-fold>//GEN-END:|404-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task7 ">//GEN-BEGIN:|407-getter|0|407-preInit
    /**
     * Returns an initiliazed instance of task7 component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask7() {
        if (task7 == null) {//GEN-END:|407-getter|0|407-preInit
            // write pre-init user code here
            task7 = new SimpleCancellableTask();//GEN-BEGIN:|407-getter|1|407-execute
            task7.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|407-getter|1|407-execute
                    // write task-execution user code here
                    int maxSeconds = 120;
                    int sec = 0;
                    while(sec < maxSeconds) {
                        synchronized(this) {
                            wait(2000);
                        }
                        if(Config.getInstance().hasKey()) {
                            if(receiverService != null)
                                receiverService.end();
                            if(keyReceiverSender != null)
                                keyReceiverSender.end();
                            return;
                        }
                        sec += 3;
                    }
                    throw new Exception(i18n.s(40));
                }//GEN-BEGIN:|407-getter|2|407-postInit
            });//GEN-END:|407-getter|2|407-postInit
            // write post-init user code here
        }//GEN-BEGIN:|407-getter|3|
        return task7;
    }
    //</editor-fold>//GEN-END:|407-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: askForKeyError ">//GEN-BEGIN:|423-getter|0|423-preInit
    /**
     * Returns an initiliazed instance of askForKeyError component.
     * @return the initialized component instance
     */
    public Alert getAskForKeyError() {
        if (askForKeyError == null) {//GEN-END:|423-getter|0|423-preInit
            // write pre-init user code here
            askForKeyError = new Alert(i18n.s(56), task6.getFailureMessage(), null, null);//GEN-BEGIN:|423-getter|1|423-postInit
            askForKeyError.setTimeout(Alert.FOREVER);//GEN-END:|423-getter|1|423-postInit
            // write post-init user code here

        }//GEN-BEGIN:|423-getter|2|
        return askForKeyError;
    }
    //</editor-fold>//GEN-END:|423-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: waitForKeyError ">//GEN-BEGIN:|427-getter|0|427-preInit
    /**
     * Returns an initiliazed instance of waitForKeyError component.
     * @return the initialized component instance
     */
    public Alert getWaitForKeyError() {
        if (waitForKeyError == null) {//GEN-END:|427-getter|0|427-preInit
            // write pre-init user code here
            waitForKeyError = new Alert(i18n.s(56), task7.getFailureMessage(), null, null);//GEN-BEGIN:|427-getter|1|427-postInit
            waitForKeyError.setTimeout(Alert.FOREVER);//GEN-END:|427-getter|1|427-postInit
            // write post-init user code here
        }//GEN-BEGIN:|427-getter|2|
        return waitForKeyError;
    }
    //</editor-fold>//GEN-END:|427-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitScreen ">//GEN-BEGIN:|431-getter|0|431-preInit
    /**
     * Returns an initiliazed instance of exitScreen component.
     * @return the initialized component instance
     */
    public SplashScreen getExitScreen() {
        if (exitScreen == null) {//GEN-END:|431-getter|0|431-preInit
            // write pre-init user code here
            exitScreen = new SplashScreen(getDisplay());//GEN-BEGIN:|431-getter|1|431-postInit
            exitScreen.setTitle("Exit");
            exitScreen.setCommandListener(this);
            exitScreen.setImage(getImage2());
            exitScreen.setText(i18n.s(41));
            exitScreen.setTimeout(1000);//GEN-END:|431-getter|1|431-postInit
            // write post-init user code here
        }//GEN-BEGIN:|431-getter|2|
        return exitScreen;
    }
    //</editor-fold>//GEN-END:|431-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: loadServiceScreen ">//GEN-BEGIN:|434-getter|0|434-preInit
    /**
     * Returns an initiliazed instance of loadServiceScreen component.
     * @return the initialized component instance
     */
    public WaitScreen getLoadServiceScreen() {
        if (loadServiceScreen == null) {//GEN-END:|434-getter|0|434-preInit
            // write pre-init user code here
            loadServiceScreen = new WaitScreen(getDisplay());//GEN-BEGIN:|434-getter|1|434-postInit
            loadServiceScreen.setTitle(i18n.s(23));
            loadServiceScreen.setCommandListener(this);
            loadServiceScreen.setImage(getImage2());
            loadServiceScreen.setText(i18n.s(42));
            loadServiceScreen.setTask(getTask8());//GEN-END:|434-getter|1|434-postInit
            // write post-init user code here
        }//GEN-BEGIN:|434-getter|2|
        return loadServiceScreen;
    }
    //</editor-fold>//GEN-END:|434-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task8 ">//GEN-BEGIN:|437-getter|0|437-preInit
    /**
     * Returns an initiliazed instance of task8 component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask8() {
        if (task8 == null) {//GEN-END:|437-getter|0|437-preInit
            // write pre-init user code here
            task8 = new SimpleCancellableTask();//GEN-BEGIN:|437-getter|1|437-execute
            task8.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|437-getter|1|437-execute
                    // write task-execution user code here
                    loadVoxService();
                }//GEN-BEGIN:|437-getter|2|437-postInit
            });//GEN-END:|437-getter|2|437-postInit
            // write post-init user code here
        }//GEN-BEGIN:|437-getter|3|
        return task8;
    }
    //</editor-fold>//GEN-END:|437-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: loadServiceFailure ">//GEN-BEGIN:|440-getter|0|440-preInit
    /**
     * Returns an initiliazed instance of loadServiceFailure component.
     * @return the initialized component instance
     */
    public Alert getLoadServiceFailure() {
        if (loadServiceFailure == null) {//GEN-END:|440-getter|0|440-preInit
            // write pre-init user code here
            loadServiceFailure = new Alert(i18n.s(44), task8.getFailureMessage(), null, null);//GEN-BEGIN:|440-getter|1|440-postInit
            loadServiceFailure.setTimeout(Alert.FOREVER);//GEN-END:|440-getter|1|440-postInit
            // write post-init user code here
        }//GEN-BEGIN:|440-getter|2|
        return loadServiceFailure;
    }
    //</editor-fold>//GEN-END:|440-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: RadarList ">//GEN-BEGIN:|442-getter|0|442-preInit
    /**
     * Returns an initiliazed instance of RadarList component.
     * @return the initialized component instance
     */
    public List getRadarList() {
        if (RadarList == null) {//GEN-END:|442-getter|0|442-preInit
            // write pre-init user code here
            RadarList = new List(i18n.s(45), Choice.IMPLICIT);//GEN-BEGIN:|442-getter|1|442-postInit
            RadarList.append(i18n.s(46), null);
            RadarList.append(i18n.s(47), null);
            RadarList.append(i18n.s(48), null);
            RadarList.append(i18n.s(49), null);
            RadarList.append(i18n.s(50), null);
            RadarList.append(i18n.s(51), null);
            RadarList.append(i18n.s(52), null);
            RadarList.append(i18n.s(53), null);
            RadarList.append(i18n.s(54), null);
            RadarList.addCommand(getBackCommand3());
            RadarList.setCommandListener(this);
            RadarList.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);
            RadarList.setSelectedFlags(new boolean[] { false, false, false, false, false, false, false, false, false });
            RadarList.setFont(0, getFont());
            RadarList.setFont(1, getFont());
            RadarList.setFont(2, getFont());
            RadarList.setFont(3, getFont());
            RadarList.setFont(4, getFont());
            RadarList.setFont(5, getFont());
            RadarList.setFont(6, getFont());
            RadarList.setFont(7, getFont());
            RadarList.setFont(8, getFont());//GEN-END:|442-getter|1|442-postInit
            // write post-init user code here
        }//GEN-BEGIN:|442-getter|2|
        return RadarList;
    }
    //</editor-fold>//GEN-END:|442-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: RadarListAction ">//GEN-BEGIN:|442-action|0|442-preAction
    /**
     * Performs an action assigned to the selected list element in the RadarList component.
     */
    public void RadarListAction() {//GEN-END:|442-action|0|442-preAction
        // enter pre-action user code here
        String __selectedString = getRadarList().getString(getRadarList().getSelectedIndex());//GEN-BEGIN:|442-action|1|446-preAction
        if (__selectedString != null) {
            if (__selectedString.equals(i18n.s(46))) {//GEN-END:|442-action|1|446-preAction
                // write pre-action user code here
                switchDisplayable(null, getSendRadarScreen());//GEN-LINE:|442-action|2|446-postAction
                // write post-action user code here
            } else if (__selectedString.equals(i18n.s(47))) {//GEN-LINE:|442-action|3|445-preAction
                // write pre-action user code here
                switchDisplayable(null, getSendRadarScreen());//GEN-LINE:|442-action|4|445-postAction
                // write post-action user code here
            } else if (__selectedString.equals(i18n.s(48))) {//GEN-LINE:|442-action|5|447-preAction
                // write pre-action user code here
                switchDisplayable(null, getSendRadarScreen());//GEN-LINE:|442-action|6|447-postAction
                // write post-action user code here
            } else if (__selectedString.equals(i18n.s(49))) {//GEN-LINE:|442-action|7|487-preAction
                // write pre-action user code here
                switchDisplayable(null, getSendRadarScreen());//GEN-LINE:|442-action|8|487-postAction
                // write post-action user code here
            } else if (__selectedString.equals(i18n.s(50))) {//GEN-LINE:|442-action|9|488-preAction
                // write pre-action user code here
                switchDisplayable(null, getSendRadarScreen());//GEN-LINE:|442-action|10|488-postAction
                // write post-action user code here
            } else if (__selectedString.equals(i18n.s(51))) {//GEN-LINE:|442-action|11|483-preAction
                // write pre-action user code here
                switchDisplayable(null, getSendRadarScreen());//GEN-LINE:|442-action|12|483-postAction
                // write post-action user code here
            } else if (__selectedString.equals(i18n.s(52))) {//GEN-LINE:|442-action|13|484-preAction
                // write pre-action user code here
                switchDisplayable(null, getSendRadarScreen());//GEN-LINE:|442-action|14|484-postAction
                // write post-action user code here
            } else if (__selectedString.equals(i18n.s(53))) {//GEN-LINE:|442-action|15|485-preAction
                // write pre-action user code here
                switchDisplayable(null, getSendRadarScreen());//GEN-LINE:|442-action|16|485-postAction
                // write post-action user code here
            } else if (__selectedString.equals(i18n.s(54))) {//GEN-LINE:|442-action|17|486-preAction
                // write pre-action user code here
                switchDisplayable(null, getSendRadarScreen());//GEN-LINE:|442-action|18|486-postAction
                // write post-action user code here
            }//GEN-BEGIN:|442-action|19|442-postAction
        }//GEN-END:|442-action|19|442-postAction
        // enter post-action user code here
    }//GEN-BEGIN:|442-action|20|
    //</editor-fold>//GEN-END:|442-action|20|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: informRadar ">//GEN-BEGIN:|448-getter|0|448-preInit
    /**
     * Returns an initiliazed instance of informRadar component.
     * @return the initialized component instance
     */
    public Command getInformRadar() {
        if (informRadar == null) {//GEN-END:|448-getter|0|448-preInit
            // write pre-init user code here
            informRadar = new Command(i18n.s(45), Command.ITEM, 0);//GEN-LINE:|448-getter|1|448-postInit
            // write post-init user code here
        }//GEN-BEGIN:|448-getter|2|
        return informRadar;
    }
    //</editor-fold>//GEN-END:|448-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand3 ">//GEN-BEGIN:|453-getter|0|453-preInit
    /**
     * Returns an initiliazed instance of backCommand3 component.
     * @return the initialized component instance
     */
    public Command getBackCommand3() {
        if (backCommand3 == null) {//GEN-END:|453-getter|0|453-preInit
            // write pre-init user code here
            backCommand3 = new Command("Back", Command.BACK, 0);//GEN-LINE:|453-getter|1|453-postInit
            // write post-init user code here
        }//GEN-BEGIN:|453-getter|2|
        return backCommand3;
    }
    //</editor-fold>//GEN-END:|453-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand1 ">//GEN-BEGIN:|456-getter|0|456-preInit
    /**
     * Returns an initiliazed instance of okCommand1 component.
     * @return the initialized component instance
     */
    public Command getOkCommand1() {
        if (okCommand1 == null) {//GEN-END:|456-getter|0|456-preInit
            // write pre-init user code here
            okCommand1 = new Command("Ok", Command.OK, 0);//GEN-LINE:|456-getter|1|456-postInit
            // write post-init user code here
        }//GEN-BEGIN:|456-getter|2|
        return okCommand1;
    }
    //</editor-fold>//GEN-END:|456-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sendRadarScreen ">//GEN-BEGIN:|459-getter|0|459-preInit
    /**
     * Returns an initiliazed instance of sendRadarScreen component.
     * @return the initialized component instance
     */
    public WaitScreen getSendRadarScreen() {
        if (sendRadarScreen == null) {//GEN-END:|459-getter|0|459-preInit
            // write pre-init user code here
            sendRadarScreen = new WaitScreen(getDisplay());//GEN-BEGIN:|459-getter|1|459-postInit
            sendRadarScreen.setTitle(i18n.s(23));
            sendRadarScreen.setCommandListener(this);
            sendRadarScreen.setImage(getImage2());
            sendRadarScreen.setText(i18n.s(55));
            sendRadarScreen.setTask(getTask9());//GEN-END:|459-getter|1|459-postInit
            // write post-init user code here
        }//GEN-BEGIN:|459-getter|2|
        return sendRadarScreen;
    }
    //</editor-fold>//GEN-END:|459-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task9 ">//GEN-BEGIN:|462-getter|0|462-preInit
    /**
     * Returns an initiliazed instance of task9 component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask9() {
        if (task9 == null) {//GEN-END:|462-getter|0|462-preInit
            // write pre-init user code here
            task9 = new SimpleCancellableTask();//GEN-BEGIN:|462-getter|1|462-execute
            task9.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|462-getter|1|462-execute
                    // write task-execution user code here
                    if(voxService != null) {
                        String selectedString = getRadarList().getString(getRadarList().getSelectedIndex());
                        int radarType = 1;
                        if(selectedString.equalsIgnoreCase("Controle temporaire")) {
                            radarType = 1;
                        } else if(selectedString.equalsIgnoreCase("Controle permanent")) {
                            radarType = 0;
                        } else if(selectedString.equalsIgnoreCase("Controle roulant")) {
                            radarType = 2;
                        } else if(selectedString.equalsIgnoreCase("Ecole")) {
                            radarType = 3;
                       } else if(selectedString.equalsIgnoreCase("Bouchons")) {
                            radarType = 4;
                       } else if(selectedString.equalsIgnoreCase("Accident")) {
                            radarType = 5;
                       } else if(selectedString.equalsIgnoreCase("Travaux")) {
                            radarType = 6;
                       } else if(selectedString.equalsIgnoreCase("Controle feu rouge")) {
                            radarType = 7;
                       } else if(selectedString.equalsIgnoreCase("Controle passage a niveau")) {
                            radarType = 8;
                       }
                        voxService.alertRadar(radarType);
                    }
                    Thread.sleep(2000);
                }//GEN-BEGIN:|462-getter|2|462-postInit
            });//GEN-END:|462-getter|2|462-postInit
            // write post-init user code here
        }//GEN-BEGIN:|462-getter|3|
        return task9;
    }
    //</editor-fold>//GEN-END:|462-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: serviceisrunning ">//GEN-BEGIN:|467-if|0|467-preIf
    /**
     * Performs an action assigned to the serviceisrunning if-point.
     */
    public void serviceisrunning() {//GEN-END:|467-if|0|467-preIf
        // enter pre-if user code here
        if (voxService.isRunning()) {//GEN-LINE:|467-if|1|468-preAction
            // write pre-action user code here
            switchDisplayable(null, getRadarList());//GEN-LINE:|467-if|2|468-postAction
            // write post-action user code here
        } else {//GEN-LINE:|467-if|3|469-preAction
            // write pre-action user code here
            switchDisplayable(null, getServiceIsNotRunningError());//GEN-LINE:|467-if|4|469-postAction
            // write post-action user code here
        }//GEN-LINE:|467-if|5|467-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|467-if|6|
    //</editor-fold>//GEN-END:|467-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: serviceIsNotRunningError ">//GEN-BEGIN:|472-getter|0|472-preInit
    /**
     * Returns an initiliazed instance of serviceIsNotRunningError component.
     * @return the initialized component instance
     */
    public Alert getServiceIsNotRunningError() {
        if (serviceIsNotRunningError == null) {//GEN-END:|472-getter|0|472-preInit
            // write pre-init user code here
            serviceIsNotRunningError = new Alert(i18n.s(56), i18n.s(57), null, AlertType.ALARM);//GEN-BEGIN:|472-getter|1|472-postInit
            serviceIsNotRunningError.setTimeout(Alert.FOREVER);//GEN-END:|472-getter|1|472-postInit
            // write post-init user code here
        }//GEN-BEGIN:|472-getter|2|
        return serviceIsNotRunningError;
    }
    //</editor-fold>//GEN-END:|472-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: hasGPS ">//GEN-BEGIN:|474-if|0|474-preIf
    /**
     * Performs an action assigned to the hasGPS if-point.
     */
    public void hasGPS() {//GEN-END:|474-if|0|474-preIf
        // enter pre-if user code here
        if (GPSLocator.getInstance().hasLocation()) {//GEN-LINE:|474-if|1|475-preAction
            // write pre-action user code here
            switchDisplayable(null, getShootyForm());//GEN-LINE:|474-if|2|475-postAction
            // write post-action user code here
        } else {//GEN-LINE:|474-if|3|476-preAction
            // write pre-action user code here
            switchDisplayable(null, getNoGPSError());//GEN-LINE:|474-if|4|476-postAction
            // write post-action user code here
        }//GEN-LINE:|474-if|5|474-postIf
        // enter post-if user code here
    }//GEN-BEGIN:|474-if|6|
    //</editor-fold>//GEN-END:|474-if|6|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: noGPSError ">//GEN-BEGIN:|480-getter|0|480-preInit
    /**
     * Returns an initiliazed instance of noGPSError component.
     * @return the initialized component instance
     */
    public Alert getNoGPSError() {
        if (noGPSError == null) {//GEN-END:|480-getter|0|480-preInit
            // write pre-init user code here
            noGPSError = new Alert(i18n.s(56), i18n.s(58), null, AlertType.ALARM);//GEN-BEGIN:|480-getter|1|480-postInit
            noGPSError.setTimeout(Alert.FOREVER);//GEN-END:|480-getter|1|480-postInit
            // write post-init user code here
        }//GEN-BEGIN:|480-getter|2|
        return noGPSError;
    }
    //</editor-fold>//GEN-END:|480-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: Retour ">//GEN-BEGIN:|496-getter|0|496-preInit
    /**
     * Returns an initiliazed instance of Retour component.
     * @return the initialized component instance
     */
    public Command getRetour() {
        if (Retour == null) {//GEN-END:|496-getter|0|496-preInit
            // write pre-init user code here
            Retour = new Command(i18n.s(13), Command.HELP, 0);//GEN-LINE:|496-getter|1|496-postInit
            // write post-init user code here
        }//GEN-BEGIN:|496-getter|2|
        return Retour;
    }
    //</editor-fold>//GEN-END:|496-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: helpCommand ">//GEN-BEGIN:|498-getter|0|498-preInit
    /**
     * Returns an initiliazed instance of helpCommand component.
     * @return the initialized component instance
     */
    public Command getHelpCommand() {
        if (helpCommand == null) {//GEN-END:|498-getter|0|498-preInit
            // write pre-init user code here
            helpCommand = new Command(i18n.s(80), Command.HELP, 0);//GEN-LINE:|498-getter|1|498-postInit
            // write post-init user code here
        }//GEN-BEGIN:|498-getter|2|
        return helpCommand;
    }
    //</editor-fold>//GEN-END:|498-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: Help ">//GEN-BEGIN:|495-getter|0|495-preInit
    /**
     * Returns an initiliazed instance of Help component.
     * @return the initialized component instance
     */
    public Form getHelp() {
        if (Help == null) {//GEN-END:|495-getter|0|495-preInit
            // write pre-init user code here
            Help = new Form(i18n.s(80), new Item[] { getStringItem2() });//GEN-BEGIN:|495-getter|1|495-postInit
            Help.addCommand(getRetour());
            Help.setCommandListener(this);//GEN-END:|495-getter|1|495-postInit
            // write post-init user code here
        }//GEN-BEGIN:|495-getter|2|
        return Help;
    }
    //</editor-fold>//GEN-END:|495-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem2 ">//GEN-BEGIN:|502-getter|0|502-preInit
    /**
     * Returns an initiliazed instance of stringItem2 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem2() {
        if (stringItem2 == null) {//GEN-END:|502-getter|0|502-preInit
            // write pre-init user code here
            stringItem2 = new StringItem("VOXINFOS", i18n.s("help"), Item.PLAIN);//GEN-BEGIN:|502-getter|1|502-postInit
            stringItem2.setLayout(ImageItem.LAYOUT_LEFT | ImageItem.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_EXPAND | Item.LAYOUT_VEXPAND);//GEN-END:|502-getter|1|502-postInit
            // write post-init user code here
        }//GEN-BEGIN:|502-getter|2|
        return stringItem2;
    }
    //</editor-fold>//GEN-END:|502-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: indicator ">//GEN-BEGIN:|505-getter|0|505-preInit
    /**
     * Returns an initiliazed instance of indicator component.
     * @return the initialized component instance
     */
    public Gauge getIndicator() {
        if (indicator == null) {//GEN-END:|505-getter|0|505-preInit
            // write pre-init user code here
            indicator = new Gauge(null, false, 100, 50);//GEN-LINE:|505-getter|1|505-postInit
            // write post-init user code here
        }//GEN-BEGIN:|505-getter|2|
        return indicator;
    }
    //</editor-fold>//GEN-END:|505-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: askForEmailForm ">//GEN-BEGIN:|509-getter|0|509-preInit
    /**
     * Returns an initiliazed instance of askForEmailForm component.
     * @return the initialized component instance
     */
    public Form getAskForEmailForm() {
        if (askForEmailForm == null) {//GEN-END:|509-getter|0|509-preInit
            // write pre-init user code here
            askForEmailForm = new Form(i18n.s(60), new Item[] { getStringItem3(), getTextField() });//GEN-BEGIN:|509-getter|1|509-postInit
            askForEmailForm.addCommand(getOkCommand());
            askForEmailForm.addCommand(getExitCommand());
            askForEmailForm.setCommandListener(this);//GEN-END:|509-getter|1|509-postInit
            // write post-init user code here
        }//GEN-BEGIN:|509-getter|2|
        return askForEmailForm;
    }
    //</editor-fold>//GEN-END:|509-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem3 ">//GEN-BEGIN:|510-getter|0|510-preInit
    /**
     * Returns an initiliazed instance of stringItem3 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem3() {
        if (stringItem3 == null) {//GEN-END:|510-getter|0|510-preInit
            // write pre-init user code here
            stringItem3 = new StringItem("", i18n.s(61), Item.PLAIN);//GEN-BEGIN:|510-getter|1|510-postInit
            stringItem3.setLayout(ImageItem.LAYOUT_DEFAULT | ImageItem.LAYOUT_NEWLINE_BEFORE | ImageItem.LAYOUT_NEWLINE_AFTER | Item.LAYOUT_VEXPAND);//GEN-END:|510-getter|1|510-postInit
            // write post-init user code here
        }//GEN-BEGIN:|510-getter|2|
        return stringItem3;
    }
    //</editor-fold>//GEN-END:|510-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: textField ">//GEN-BEGIN:|511-getter|0|511-preInit
    /**
     * Returns an initiliazed instance of textField component.
     * @return the initialized component instance
     */
    public TextField getTextField() {
        if (textField == null) {//GEN-END:|511-getter|0|511-preInit
            // write pre-init user code here
            textField = new TextField("xxx@xxx", "", 64, TextField.EMAILADDR);//GEN-BEGIN:|511-getter|1|511-postInit
            textField.setInitialInputMode("UCB_BASIC_LATIN");//GEN-END:|511-getter|1|511-postInit
            // write post-init user code here
        }//GEN-BEGIN:|511-getter|2|
        return textField;
    }
    //</editor-fold>//GEN-END:|511-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: loadingScreen ">//GEN-BEGIN:|517-getter|0|517-preInit
    /**
     * Returns an initiliazed instance of loadingScreen component.
     * @return the initialized component instance
     */
    public WaitScreen getLoadingScreen() {
        if (loadingScreen == null) {//GEN-END:|517-getter|0|517-preInit
            // write pre-init user code here
            loadingScreen = new WaitScreen(getDisplay());//GEN-BEGIN:|517-getter|1|517-postInit
            loadingScreen.setTitle(i18n.s(82));
            loadingScreen.setTicker(getTicker());
            loadingScreen.setCommandListener(this);
            loadingScreen.setImage(getImage2());
            loadingScreen.setText(".............");
            loadingScreen.setTask(getTask10());//GEN-END:|517-getter|1|517-postInit
            // write post-init user code here
        }//GEN-BEGIN:|517-getter|2|
        return loadingScreen;
    }
    //</editor-fold>//GEN-END:|517-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task10 ">//GEN-BEGIN:|520-getter|0|520-preInit
    /**
     * Returns an initiliazed instance of task10 component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask10() {
        if (task10 == null) {//GEN-END:|520-getter|0|520-preInit
            // write pre-init user code here
            task10 = new SimpleCancellableTask();//GEN-BEGIN:|520-getter|1|520-execute
            task10.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|520-getter|1|520-execute
                    // write task-execution user code here
                    loadAll();
                }//GEN-BEGIN:|520-getter|2|520-postInit
            });//GEN-END:|520-getter|2|520-postInit
            // write post-init user code here
        }//GEN-BEGIN:|520-getter|3|
        return task10;
    }
    //</editor-fold>//GEN-END:|520-getter|3|




    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: ticker ">//GEN-BEGIN:|526-getter|0|526-preInit
    /**
     * Returns an initiliazed instance of ticker component.
     * @return the initialized component instance
     */
    public Ticker getTicker() {
        if (ticker == null) {//GEN-END:|526-getter|0|526-preInit
            // write pre-init user code here
            ticker = new Ticker("");//GEN-LINE:|526-getter|1|526-postInit
            // write post-init user code here
        }//GEN-BEGIN:|526-getter|2|
        return ticker;
    }
    //</editor-fold>//GEN-END:|526-getter|2|





    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: image2 ">//GEN-BEGIN:|529-getter|0|529-preInit
    /**
     * Returns an initiliazed instance of image2 component.
     * @return the initialized component instance
     */
    public Image getImage2() {
        if (image2 == null) {//GEN-END:|529-getter|0|529-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|529-getter|1|529-@java.io.IOException
                image2 = Image.createImage("/res/img/logoVOX.png");
            } catch (java.io.IOException e) {//GEN-END:|529-getter|1|529-@java.io.IOException
                e.printStackTrace();
            }//GEN-LINE:|529-getter|2|529-postInit
            // write post-init user code here
        }//GEN-BEGIN:|529-getter|3|
        return image2;
    }
    //</editor-fold>//GEN-END:|529-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: font ">//GEN-BEGIN:|530-getter|0|530-preInit
    /**
     * Returns an initiliazed instance of font component.
     * @return the initialized component instance
     */
    public Font getFont() {
        if (font == null) {//GEN-END:|530-getter|0|530-preInit
            // write pre-init user code here
            font = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_ITALIC, Font.SIZE_SMALL);//GEN-LINE:|530-getter|1|530-postInit
            // write post-init user code here
        }//GEN-BEGIN:|530-getter|2|
        return font;
    }
    //</editor-fold>//GEN-END:|530-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: options ">//GEN-BEGIN:|531-getter|0|531-preInit
    /**
     * Returns an initiliazed instance of options component.
     * @return the initialized component instance
     */
    public Form getOptions() {
        if (options == null) {//GEN-END:|531-getter|0|531-preInit
            // write pre-init user code here
            options = new Form("Options", new Item[] { getChoiceGroup1(), getChoiceGroup2(), getTextField2(), getTextField3(), getTextField4(), getTextField5(), getStringItem4() });//GEN-BEGIN:|531-getter|1|531-postInit
            options.addCommand(getOkCommand());
            options.addCommand(getBackCommand());
            options.setCommandListener(this);//GEN-END:|531-getter|1|531-postInit
            // write post-init user code here
        }//GEN-BEGIN:|531-getter|2|
        return options;
    }
    //</editor-fold>//GEN-END:|531-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceGroup1 ">//GEN-BEGIN:|532-getter|0|532-preInit
    /**
     * Returns an initiliazed instance of choiceGroup1 component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceGroup1() {
        if (choiceGroup1 == null) {//GEN-END:|532-getter|0|532-preInit
            // write pre-init user code here
            choiceGroup1 = new ChoiceGroup("language ", Choice.MULTIPLE);//GEN-BEGIN:|532-getter|1|532-postInit
            choiceGroup1.append("French/fran\u00E7ais", null);
            choiceGroup1.append("English / Anglais", null);
            choiceGroup1.setSelectedFlags(new boolean[] { true, false });//GEN-END:|532-getter|1|532-postInit
            // write post-init user code here
        }//GEN-BEGIN:|532-getter|2|
        return choiceGroup1;
    }
    //</editor-fold>//GEN-END:|532-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceGroup2 ">//GEN-BEGIN:|533-getter|0|533-preInit
    /**
     * Returns an initiliazed instance of choiceGroup2 component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceGroup2() {
        if (choiceGroup2 == null) {//GEN-END:|533-getter|0|533-preInit
            // write pre-init user code here
            choiceGroup2 = new ChoiceGroup(i18n.s(90), Choice.MULTIPLE);//GEN-BEGIN:|533-getter|1|533-postInit
            choiceGroup2.append("30", null);
            choiceGroup2.append("60", null);
            choiceGroup2.append("90", null);
            choiceGroup2.append("120", null);
            choiceGroup2.append("150", null);
            choiceGroup2.setSelectedFlags(new boolean[] { false, true, false, false, false });//GEN-END:|533-getter|1|533-postInit
            // write post-init user code here
        }//GEN-BEGIN:|533-getter|2|
        return choiceGroup2;
    }
    //</editor-fold>//GEN-END:|533-getter|2|
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: optionsmenu ">//GEN-BEGIN:|545-getter|0|545-preInit
    /**
     * Returns an initiliazed instance of optionsmenu component.
     * @return the initialized component instance
     */
    public Command getOptionsmenu() {
        if (optionsmenu == null) {//GEN-END:|545-getter|0|545-preInit
            // write pre-init user code here
            optionsmenu = new Command(i18n.s(85), i18n.s(86), Command.SCREEN, 0);//GEN-LINE:|545-getter|1|545-postInit
            // write post-init user code here
        }//GEN-BEGIN:|545-getter|2|
        return optionsmenu;
    }
    //</editor-fold>//GEN-END:|545-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: textField2 ">//GEN-BEGIN:|541-getter|0|541-preInit
    /**
     * Returns an initiliazed instance of textField2 component.
     * @return the initialized component instance
     */
    public TextField getTextField2() {
        if (textField2 == null) {//GEN-END:|541-getter|0|541-preInit
            // write pre-init user code here
            textField2 = new TextField(i18n.s(91), "4000", 4, TextField.NUMERIC);//GEN-LINE:|541-getter|1|541-postInit
            // write post-init user code here
        }//GEN-BEGIN:|541-getter|2|
        return textField2;
    }
    //</editor-fold>//GEN-END:|541-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: textField3 ">//GEN-BEGIN:|542-getter|0|542-preInit
    /**
     * Returns an initiliazed instance of textField3 component.
     * @return the initialized component instance
     */
    public TextField getTextField3() {
        if (textField3 == null) {//GEN-END:|542-getter|0|542-preInit
            // write pre-init user code here
            textField3 = new TextField(i18n.s(92), "2000", 4, TextField.NUMERIC);//GEN-LINE:|542-getter|1|542-postInit
            // write post-init user code here
        }//GEN-BEGIN:|542-getter|2|
        return textField3;
    }
    //</editor-fold>//GEN-END:|542-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: textField4 ">//GEN-BEGIN:|543-getter|0|543-preInit
    /**
     * Returns an initiliazed instance of textField4 component.
     * @return the initialized component instance
     */
    public TextField getTextField4() {
        if (textField4 == null) {//GEN-END:|543-getter|0|543-preInit
            // write pre-init user code here
            textField4 = new TextField(i18n.s(93), "300", 4, TextField.NUMERIC);//GEN-LINE:|543-getter|1|543-postInit
            // write post-init user code here
        }//GEN-BEGIN:|543-getter|2|
        return textField4;
    }
    //</editor-fold>//GEN-END:|543-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: textField5 ">//GEN-BEGIN:|544-getter|0|544-preInit
    /**
     * Returns an initiliazed instance of textField5 component.
     * @return the initialized component instance
     */
    public TextField getTextField5() {
        if (textField5 == null) {//GEN-END:|544-getter|0|544-preInit
            // write pre-init user code here
            textField5 = new TextField(i18n.s(94), "1", 1, TextField.ANY);//GEN-LINE:|544-getter|1|544-postInit
            // write post-init user code here
        }//GEN-BEGIN:|544-getter|2|
        return textField5;
    }
    //</editor-fold>//GEN-END:|544-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand4 ">//GEN-BEGIN:|548-getter|0|548-preInit
    /**
     * Returns an initiliazed instance of backCommand4 component.
     * @return the initialized component instance
     */
    public Command getBackCommand4() {
        if (backCommand4 == null) {//GEN-END:|548-getter|0|548-preInit
            // write pre-init user code here
            backCommand4 = new Command("Back", Command.BACK, 0);//GEN-LINE:|548-getter|1|548-postInit
            // write post-init user code here
        }//GEN-BEGIN:|548-getter|2|
        return backCommand4;
    }
    //</editor-fold>//GEN-END:|548-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand2 ">//GEN-BEGIN:|550-getter|0|550-preInit
    /**
     * Returns an initiliazed instance of okCommand2 component.
     * @return the initialized component instance
     */
    public Command getOkCommand2() {
        if (okCommand2 == null) {//GEN-END:|550-getter|0|550-preInit
            // write pre-init user code here
            okCommand2 = new Command("Ok", Command.OK, 0);//GEN-LINE:|550-getter|1|550-postInit
            // write post-init user code here
        }//GEN-BEGIN:|550-getter|2|
        return okCommand2;
    }
    //</editor-fold>//GEN-END:|550-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem4 ">//GEN-BEGIN:|572-getter|0|572-preInit
    /**
     * Returns an initiliazed instance of stringItem4 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem4() {
        if (stringItem4 == null) {//GEN-END:|572-getter|0|572-preInit
            // write pre-init user code here
            stringItem4 = new StringItem(i18n.s(32), i18n.s(33));//GEN-LINE:|572-getter|1|572-postInit
            // write post-init user code here
        }//GEN-BEGIN:|572-getter|2|
        return stringItem4;
    }
    //</editor-fold>//GEN-END:|572-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: sendoptionsscreen ">//GEN-BEGIN:|560-getter|0|560-preInit
    /**
     * Returns an initiliazed instance of sendoptionsscreen component.
     * @return the initialized component instance
     */
    public WaitScreen getSendoptionsscreen() {
        if (sendoptionsscreen == null) {//GEN-END:|560-getter|0|560-preInit
            // write pre-init user code here
            sendoptionsscreen = new WaitScreen(getDisplay());//GEN-BEGIN:|560-getter|1|560-postInit
            sendoptionsscreen.setTitle("sendoptionsScreen");
            sendoptionsscreen.setCommandListener(this);
            sendoptionsscreen.setTask(getTask11());//GEN-END:|560-getter|1|560-postInit
            // write post-init user code here
        }//GEN-BEGIN:|560-getter|2|
        return sendoptionsscreen;
    }
    //</editor-fold>//GEN-END:|560-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task11 ">//GEN-BEGIN:|563-getter|0|563-preInit
    /**
     * Returns an initiliazed instance of task11 component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask11() {
        if (task11 == null) {//GEN-END:|563-getter|0|563-preInit
            // write pre-init user code here
            task11 = new SimpleCancellableTask();//GEN-BEGIN:|563-getter|1|563-execute
            task11.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|563-getter|1|563-execute
                    // write task-execution user code here
                }//GEN-BEGIN:|563-getter|2|563-postInit
            });//GEN-END:|563-getter|2|563-postInit
            // write post-init user code here
        }//GEN-BEGIN:|563-getter|3|
        return task11;
    }
    //</editor-fold>//GEN-END:|563-getter|3|

    private GPSDevice getPreferedGPSDevice() {
        GPSDevice gpsDevice = null;
        if(Config.getInstance().isSimGPS()) {
            SimGPSDevice simGpsDevice = new SimGPSDevice();
            return simGpsDevice;
        }
        if(this.internalGPSDevice != null) {
            gpsDevice = this.internalGPSDevice;
        } else {
            gpsDevice = (GPSDevice) Config.getInstance().get("GPS_DEVICE");
        }
        return gpsDevice;
    }

    private void setGPSDeviceSource(GPSDevice source) {
        Config.getInstance().set("GPS_DEVICE", source);
        GPSLocator.getInstance().setGPSDevice(source);
        nameObservable.notifyObservers(source.getName());
    }

    private void refreshDeviceList() {
        sourceChooser.deleteAll();
        if(this.internalGPSDevice.getLocationProvider() != null) {
            sourceChooser.append(this.internalGPSDevice.getName(), null);
        }
        if (bluetoothDevices != null && bluetoothDevices.length > 0) {
            for (int i = 0; i < bluetoothDevices.length; i++) {
                sourceChooser.append(bluetoothDevices[i].getName(), null);
            }
        }
    }
    

    private String getEstimCost(String minStr) {
        if(minStr == null){
            return "";
        }
        String s = "";
        int min = 0;
        try {
            min = Integer.parseInt(minStr);
            double cost = computeEstimCost(min);
            DecimalFormat format = (DecimalFormat) DecimalFormat.getNumberInstance();
            format.applyPattern("#,#00.00#");
            s = format.format(cost);
        } catch (NumberFormatException nfe) {
            s = i18n.s(79);
        }
        return s;
    }

    private double computeEstimCost(int minutes) {
        return 0.020 * minutes;
    }

    /**
     * Returns a display instance.
     * @return the display instance.
     */
    public Display getDisplay() {
        return Display.getDisplay(this);
    }

    /**
     * Exits MIDlet.
     */
    public void exitMIDlet() {
        //if(voxService != null && voxService.isRunning()) {
        //    hideMIDlet();
        //} else {
            System.out.println("EXIT");
            scheduleMidlet();
            if(voxService != null) {
            	voxService.cancelCurrentFu();
            	voxService.end();
            }
            
            if(internalGPSDevice != null) internalGPSDevice.end();
            if(keyReceiverSender != null) keyReceiverSender.end();
            GPSLocator.getInstance().end();
            try {
                Config.getInstance().save();
            } catch (RecordStoreException ex) {
                ex.printStackTrace();
            }
            // looks like static object are still set when restarting application
            // so need to clear all singleton and factories
            ReceiverSenderFactory.closeAll();
            ConnectionFactory.closeAll();
            Config.close();
            GPSLocator.close();
            VoxService.close();
            notifyDestroyed();
            destroyApp(true);
            System.exit(0);
        //}
    }

    public void scheduleMidlet() {
        FollowUpList fuList = Config.getInstance().getFollowUpList();
        FollowUp first = (FollowUp) fuList.first();
        if(first != null) {
            try {
                wakeMeUp(first.getStart().getTime() - 30 * 1000);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (ConnectionNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void wakeMeUp(long date) throws ClassNotFoundException, ConnectionNotFoundException, IOException {
        if(date > new Date().getTime())
            PushRegistry.registerAlarm(VOXINFOS.class.getName(), date);
    }

    /**
     * Exits MIDlet.
     */
    public void hideMIDlet() {
        System.out.println("HIDDEN");
        switchDisplayable(null, getGpsForm());
        switchDisplayable(null, null);
    }

    /**
     * Called when MIDlet is started.
     * Checks whether the MIDlet have been already started and initialize/starts or resumes the MIDlet.
     */
    public void startApp() {
        if (midletPaused) {
            resumeMIDlet();
        } else {
            initialize();
            startMIDlet();
        }
        midletPaused = false;
    }

    /**
     * Called when MIDlet is paused.
     */
    public void pauseApp() {
        System.out.println("PAUSED");
        midletPaused = true;
    }

    /**
     * Called to signal the MIDlet to terminate.
     * @param unconditional if true, then the MIDlet has to be unconditionally terminated and all resources has to be released.
     */
    public void destroyApp(boolean unconditional) {
    }
}
