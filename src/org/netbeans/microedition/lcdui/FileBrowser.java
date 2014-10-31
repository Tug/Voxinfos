/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */


package org.netbeans.microedition.lcdui;

import java.util.*;
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author breh
 * edited: Karol Harezlak
 */

public class FileBrowser extends List implements CommandListener {
    
    public static final Command SELECT_FILE_COMMAND = new Command("Select",Command.OK,1);
    
    private String currDirName;
    private String currFile;
    private Image dirIcon, fileIcon;
    private Image[] iconList;
    private CommandListener commandListener;
    
    /* special string denotes upper directory */
    private final static String UP_DIRECTORY = "..";
    
    /* special string that denotes upper directory accessible by this browser.
     * this virtual directory contains all roots.
     */
    private final static String MEGA_ROOT = "/";
    
    /* separator string as defined by FC specification */
    private final static String SEP_STR = "/";
    
    /* separator character as defined by FC specification */
    private final static char SEP = '/';
    
    private Display display;
    
    private String selectedURL;
    
    private String filter = null;
    
    private String title;
    
    public FileBrowser(Display display) {
        super("", IMPLICIT);
        currDirName = MEGA_ROOT;
        this.display = display;
        super.setCommandListener(this);
        setSelectCommand(SELECT_FILE_COMMAND);
        try {
            dirIcon = Image.createImage("/org/netbeans/microedition/resources/dir.png");
        } catch (IOException e) {
            dirIcon = null;
        }
        try {
            fileIcon = Image.createImage("/org/netbeans/microedition/resources/file.png");
        } catch (IOException e) {
            fileIcon = null;
        }
        iconList = new Image[] { fileIcon, dirIcon };
        
        showDir();
        
    }
    
    private void showDir() {
        new Thread(new Runnable(){
            public void run(){
                try {
                    showCurrDir();
                } catch (SecurityException e) {
                    Alert alert = new Alert("Error",
                            "You are not authorized to access the restricted API",
                            null, AlertType.ERROR);
                    alert.setTimeout(2000);
                    display.setCurrent(alert, FileBrowser.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    public void commandAction(Command c, Displayable d) {
        if(c.equals(SELECT_FILE_COMMAND)){
            List curr = (List)d;
            currFile = curr.getString(curr.getSelectedIndex());
            new Thread(new Runnable() {
                public void run() {
                    if (currFile.endsWith(SEP_STR) || currFile.equals(UP_DIRECTORY)) {
                        openDir(currFile);
                    } else {
                        //switch To Next
                        doDismiss();
                    }
                }
            }).start();
        }else{
            commandListener.commandAction(c, d);
        }
    }
    
    public void setTitle(String title){
        this.title = title;
        super.setTitle(title);
    }
    /**
     * Show file list in the current directory .
     */
    private void showCurrDir() {
        if(title == null){
            super.setTitle(currDirName);
        }
        Enumeration e = null;
        FileConnection currDir = null;

        deleteAll();
        if (MEGA_ROOT.equals(currDirName)) {
            append(UP_DIRECTORY, dirIcon);
            e = FileSystemRegistry.listRoots();
        } else {
            try {
                currDir = (FileConnection) Connector.open("file:///" + currDirName);
                e = currDir.list();
            } catch (IOException ioe) {
                
            }
            
        }
        
        if (e == null) {
            try {
                currDir.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return;
        }
        
        while (e.hasMoreElements()) {
            String fileName = (String)e.nextElement();
            if (fileName.charAt(fileName.length()-1) == SEP) {
                // This is directory
                append(fileName, dirIcon);
            } else {
                // this is regular file
                if(filter == null || fileName.indexOf(filter) > -1 ){
                    append(fileName, fileIcon);
                }
            }
        }
        
        if (currDir != null) {
            try {
                currDir.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        
        
    }
    
    private void openDir(String fileName) {
    /* In case of directory just change the current directory
     * and show it
     */
        if (currDirName.equals(MEGA_ROOT)) {
            if (fileName.equals(UP_DIRECTORY)) {
                // can not go up from MEGA_ROOT
                return;
            }
            currDirName = fileName;
        } else if (fileName.equals(UP_DIRECTORY)) {
            // Go up one directory
            // TODO use setFileConnection when implemented
            int i = currDirName.lastIndexOf(SEP, currDirName.length()-2);
            if (i != -1) {
                currDirName = currDirName.substring(0, i+1);
            } else {
                currDirName = MEGA_ROOT;
            }
        } else {
            currDirName = currDirName + fileName;
        }
        showDir();
    }
    
    
    public FileConnection getSelectedFile() throws IOException{
        return (FileConnection)Connector.open(selectedURL);
    }
    
    public String getSelectedFileURL(){
        return selectedURL;
    }
    
    public void setFilter(String filter){
        this.filter = filter;
    }
    
    protected CommandListener getCommandListener() {
        return commandListener;
    }
    
    /**
     * Sets command listener to this component
     * @param commandListener - command listener to be used
     */
    public void setCommandListener(CommandListener commandListener) {
        this.commandListener = commandListener;
    }
    
    private void doDismiss() {
        selectedURL = "file:///" + currDirName + SEP_STR + currFile;
        CommandListener commandListener = getCommandListener();
        if (commandListener != null) {
            commandListener.commandAction(SELECT_FILE_COMMAND,this);
        }
    }
    
    
}
