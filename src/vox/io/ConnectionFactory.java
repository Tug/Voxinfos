/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package vox.io;

import java.io.IOException;
import javame.util.HashMap;
import javame.util.Iterator;
import javame.util.Map;
import javame.util.Set;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.SocketConnection;
import javax.wireless.messaging.MessageConnection;
import vox.Config;

/**
 *
 * @author Tug
 */
public class ConnectionFactory {

    public static HashMap map = new HashMap();

    public static MessageConnection getMessageConnection(String port) throws IOException {
        return (MessageConnection) getConnection("sms://:" + port);
    }

    public static HttpConnection getHTTPConnection(String url) throws IOException {
        return (HttpConnection) getConnection("http://"+url);
    }

    public static DatagramConnection getDatagramConnection(String url) throws IOException {
        return (DatagramConnection) getConnection("datagram://"+url);
    }

    public static SocketConnection getSocketConnection(String url) throws IOException {
        SocketConnection sc = (SocketConnection) getConnection("socket://"+url);
        sc.setSocketOption(SocketConnection.DELAY, 0);
        sc.setSocketOption(SocketConnection.LINGER, 0);
        sc.setSocketOption(SocketConnection.KEEPALIVE, 0);
        sc.setSocketOption(SocketConnection.RCVBUF, 256);
        sc.setSocketOption(SocketConnection.SNDBUF, 256);
        return sc;
    }

    public static Connection getConnection(String url) throws IOException {
        if(map.containsKey(url)) {
            return (Connection) map.get(url);
        }
        Connection conn = Connector.open(url);
        map.put(url, conn);
        return conn;
    }

    public static void closeAll() {
        Set entries = map.entrySet();
        Iterator it = entries.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Connection conn = (Connection) entry.getValue();
            if(conn != null) {
                try {
                    conn.close();
                } catch (IOException ex) { }
            }
            map.remove(entry.getKey());
        }
        map = new HashMap();
    }
}
