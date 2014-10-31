/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vox.io;

import io.ReceiverSender;
import io.http.HTTPReceiverSender;
import io.sms.SMSReceiverSender;
import io.socket.SocketReceiverSender;
import java.io.IOException;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.SocketConnection;
import javax.wireless.messaging.MessageConnection;
import vox.Config;

/**
 *
 * @author Tug
 */
public class ReceiverSenderFactory {

    private static ReceiverSender socketRS;
    private static ReceiverSender smsRS;
    private static ReceiverSender httpRS;

    public static ReceiverSender getConfigInstance() throws IOException {
      String value = Config.getInstance().getConnectionType();
      if(value.equals("SMS")) {
         return getDefaultSMSReceiverSender();
      } else if (value.equals("HTTP")) {
         return getDefaultHTTPReceiverSender();
      } else if (value.equals("SOCKET")) {
         return getDefaultSocketReceiverSender();
      }
      return null;
   }

    public static ReceiverSender getDefaultSMSReceiverSender() throws IOException {
        if(smsRS == null) {
            String port = Config.getInstance().getSMSPort();
            MessageConnection smsConnection = ConnectionFactory.getMessageConnection(port);
            smsRS = new SMSReceiverSender(smsConnection, Config.getInstance().getSMSNum());
        }
        return smsRS;
    }

    public static ReceiverSender getDefaultSocketReceiverSender() throws IOException {
        if(socketRS == null) {
            String url = Config.getInstance().getSocketUrl();
            SocketConnection socketConnection = (SocketConnection) ConnectionFactory.getSocketConnection(url);
            socketRS = new SocketReceiverSender(socketConnection);
        }
        return socketRS;
    }

    public static ReceiverSender getDefaultHTTPReceiverSender() throws IOException {
        if(httpRS == null) {
            HttpConnection httpConnection = (HttpConnection)
                    ConnectionFactory.getHTTPConnection(Config.getInstance().getHTTPPosUrl());
            StringBuffer keyParam = Config.getInstance().getKeyBuffer();
            httpRS = new HTTPReceiverSender(httpConnection, keyParam);
        }
        return httpRS;
    }

    public static void closeAll() {
        if(socketRS != null) socketRS.end();
        if(smsRS != null) smsRS.end();
        if(httpRS != null) httpRS.end();
        socketRS = null;
        smsRS = null;
        httpRS = null;
    }
}
