/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package io.http;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import vox.Config;

/**
 *
 * @author Tug
 */
public class HTTPConnector {

    private HttpConnection hc;
    private boolean closed = false;
    private String url;
    
    public HTTPConnector(HttpConnection hc) {
        this.hc = hc;
        this.url = hc.getURL();
    }

    public HttpConnection getMessageConnection() {
        return hc;
    }

    public String get() throws IOException {
        open();
        hc.setRequestMethod(HttpConnection.GET);
        //hc.setRequestProperty("User-Agent", "Profile/MIDP-1.0 Confirguration/CLDC-1.0");

        int respCode = hc.getResponseCode();
        String response = null;
        if (respCode == HttpConnection.HTTP_OK) {
            response = getResponseString(hc);
        } else response = "ERROR "+respCode;
        close();
        return response;
    }

    public String post(String params) throws IOException {
            open();
            hc.setRequestMethod(HttpConnection.POST);
            //hc.setRequestProperty("User-Agent", "Profile/MIDP-2.0 Confirguration/CLDC-1.1");
            //hc.setRequestProperty("Accept_Language","en-US");
            hc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            hc.setRequestProperty("Content-Length", String.valueOf(params.getBytes().length));

            writeRequest(hc, params);
            String response = getResponseString(hc);
            close();
            return response;
    }

    private static String getResponseString(HttpConnection hc) {
        InputStream is = null;
        String dataRead = null;
        try {
            is = hc.openInputStream();
            if(is == null) throw new IOException("Opening Input Stream has failed");
            int length = (int) hc.getLength();

            if (length == -1) {
                //unknown length returned by server.
                //It is more efficient to read the data in chunks, so we
                //will be reading in chunk of 1500 = Maximum MTU possible
                int chunkSize = 1500;
                byte[] data = new byte[chunkSize];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int dataSizeRead = 0;
                while ((dataSizeRead = is.read(data)) != -1) {
                    baos.write(data, 0, dataSizeRead);
                }
                dataRead = new String(baos.toByteArray());
                baos.close();
            } else {
                //known length
                //DataInputStream dis = new DataInputStream(is);
                byte[] data = new byte[length];
                //try to read all the bytes returned from the server.
                int bytesRead = is.read(data);
                dataRead = new String(data);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if(is != null) {
                try {
                     is.close();
                } catch (IOException ex) {}
            }
        }
        return dataRead;
    }

    private static void writeRequest(HttpConnection hc, String content) {
        OutputStream os = null;
        try {
            os = hc.openOutputStream();
            os.write(content.getBytes());
            os.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if(os != null) {
                try {
                    os.close();
                } catch (IOException ex) {}
            }
        }
    }

    private void close() {
        if(hc != null) try {
            hc.close();
        } catch (IOException ex) {}
        closed = true;
    }

    private void open() throws IOException {
        if(closed) {
            hc = (HttpConnection) Connector.open(url);
            closed = false;
        }
    }
}

