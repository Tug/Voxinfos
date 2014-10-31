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

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.pim.Contact;
import javax.microedition.pim.Event;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.ToDo;


public class PIMBrowser extends List implements CommandListener{
    
    
    public static final Command SELECT_PIM_ITEM = new Command("Select", Command.OK, 1);
    
    private int listType = PIM.CONTACT_LIST;
    private final Command details = new Command("Details", Command.ITEM, 1);
    private final Command selectList = new Command("Select", Command.OK, 1);
    private final Command back = new Command("Back", Command.BACK, 1);
    private Display display;
    private String selectedList;
    private PIMItem selectedItem;
    private CommandListener commandListener;
    
    
    public PIMBrowser(Display display, int listType) {
        super("", IMPLICIT);
        this.display = display;
        this.listType = listType;
        super.setCommandListener(this);
        showLists();
    }
    
    private void showLists(){
        String[] lists = PIM.getInstance().listPIMLists(listType);
        if(lists.length == 0){
            reportError("No lists for requested PIM type");
            return;
        }
        
        //if only one list open it.
        if(lists.length == 1){
            loadList(lists[0]);
            return;
        }
        
        //if more then one list show them all and let the user choose
        setTitle("select list");
        for(int i=0; i< lists.length; i++){
            append(lists[i], null);
        }
        setSelectCommand(selectList);
    }
    
    private void reportException(Exception e){
        reportError(e.getMessage());
    }
    
    private void reportError(String error){
        Alert alert = new Alert(error, error, null, AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        display.setCurrent(alert, this);
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
        CommandListener commandListener = getCommandListener();
        if (commandListener != null) {
            commandListener.commandAction(SELECT_PIM_ITEM,this);
        }
    }
    
    public void commandAction(Command command, Displayable displayable) {
        
        if(command.equals(back)){
            removeCommand(back);
            removeCommand(details);
            showLists();
        }else if(command.equals(selectList)){
            loadList(getString(getSelectedIndex()));
            addCommand(back);
        }else if(command.equals(details)){
            selectedItem = getItem(selectedList, getString(getSelectedIndex()));
            showItem(selectedItem);
        }else if(command.equals(SELECT_PIM_ITEM)){
            selectedItem = getItem(selectedList, getString(getSelectedIndex()));
            doDismiss();
        }else if(commandListener != null){
            commandListener.commandAction(command, displayable);
        }
        
    }
    
    
    public int getListType(){
        return listType;
    }
    
    public PIMItem getSelectedItem(){
        return selectedItem;
    }
    
    private void loadList(String listName){
        selectedList = listName;
        Form form = new Form("Loading PIM list");
        form.append("Please wait...");
        display.setCurrent(form);
        deleteAll();
        new Thread(new Runnable(){
            public void run(){
                openList(selectedList);
            }
        }).start();
        
    }
    
    private void openList(String listName) {
        try {
            PIMList list = PIM.getInstance()
            .openPIMList(listType, PIM.READ_WRITE, listName);
            if(getTitle() == null || getTitle().equals("")){
                setTitle(list.getName());
            }
            int fieldCode = getFieldCode(listType);
            Enumeration items = list.items();
            while(items.hasMoreElements()){
                append(((PIMItem)items.nextElement()).getString(fieldCode, 0), null);
            }
            if(size() == 0){
                reportError("List: " + listName + " is Empty");
                return;
            }
            addCommand(details);
            setSelectCommand(SELECT_PIM_ITEM);
        } catch (Exception e) {
            reportException(e);
        }
    }
    
    private PIMItem getItem(String listName, String itemName) {
        try {
            Enumeration items = PIM.getInstance().openPIMList(listType, PIM.READ_WRITE, listName).items();
            int fieldCode = getFieldCode(listType);
            
            while(items.hasMoreElements()){
                PIMItem current = (PIMItem)items.nextElement();
                String name = current.getString(fieldCode, 0);
                if(name.equals(itemName)){
                    return current;
                }
            }
        } catch (Exception e) {
            reportException(e);
        }
        return null;
    }
    
    private int getFieldCode(int listType){
        int fieldCode = 0;
        switch (listType) {
            case PIM.CONTACT_LIST:
                fieldCode = Contact.FORMATTED_NAME;
                break;
            case PIM.EVENT_LIST:
                fieldCode = Event.SUMMARY;
                break;
            case PIM.TODO_LIST:
                fieldCode = ToDo.SUMMARY;
                break;
        }
        return fieldCode;
    }
    
    private void showItem(PIMItem selectedItem) {
        ItemDisplayScreen screen = new ItemDisplayScreen(selectedItem);
        display.setCurrent(screen);
    }
    
    class ItemDisplayScreen extends Form implements CommandListener {
        
        private final Command backCommand = new Command("Back", Command.BACK, 1);
        private final PIMItem item;
        private final Hashtable fieldTable = new Hashtable(); // maps field indices to items
        
        public ItemDisplayScreen(PIMItem item){
            super("PIM Item");
            this.item = item;
            try {
                populateForm();
            } catch (PIMException ex) {
                ex.printStackTrace();
            }
            
            addCommand(backCommand);
            setCommandListener(this);
        }
        
        private boolean isClassField(int field) {
            return item instanceof Contact && field == Contact.CLASS
                    || item instanceof Event && field == Event.CLASS
                    || item instanceof ToDo && field == ToDo.CLASS;
        }
        
        private void populateForm() throws PIMException {
            deleteAll();
            fieldTable.clear();
            int[] fields = item.getPIMList().getSupportedFields();
            for (int i = 0; i < fields.length; i++) {
                int field = fields[i];
                // exclude CLASS field
                if (isClassField(field)) {
                    continue;
                }
                
                if (item.countValues(field) == 0) {
                    continue;
                }
                
                int dataType = item.getPIMList().getFieldDataType(field);
                String label = item.getPIMList().getFieldLabel(field);
                Item formItem = null;
                switch (dataType) {
                    case PIMItem.STRING: {
                        String sValue = item.getString(field, 0);
                        if (sValue == null) {
                            sValue = "";
                        }
                        formItem = new StringItem(label, sValue);
                        break;
                    }
                    case PIMItem.BOOLEAN: {
                        formItem = new StringItem(label,
                                item.getBoolean(field, 0) ? "yes" : "no");
                        break;
                    }
                    case PIMItem.STRING_ARRAY: {
                        String[] a = item.getStringArray(field, 0);
                        if (a != null) {
                            formItem = new StringItem(label, joinStringArray(a));
                        }
                        break;
                    }
                    case PIMItem.DATE: {
                        long time = item.getDate(field, 0);
                        int style = DateField.DATE_TIME;
                        // some fields are date only, without a time.
                        // correct for these fields:
                        if (item instanceof Contact) {
                            switch (field) {
                                case Contact.BIRTHDAY:
                                    style = DateField.DATE;
                                    break;
                            }
                        }
                        formItem = new DateField(label, style);
                        ((DateField)formItem).setDate(new Date(time));
                        break;
                    }
                    case PIMItem.INT: {
                        formItem = new StringItem(label,
                                String.valueOf(item.getInt(field, 0)));
                        break;
                    }
                    case PIMItem.BINARY: {
                        byte[] data = item.getBinary(field, 0);
                        if (data != null) {
                            formItem = new StringItem(label, data.length + " bytes");
                        }
                        break;
                    }
                }
                append(formItem);
            }
        }
        
        public void commandAction(final Command command, Displayable displayable) {
            if (command == backCommand) {
                new Thread( new Runnable() {
                    public void run() {
                        showLists();
                        display.setCurrent(PIMBrowser.this);
                    }
                }).start();
            }
        }

        private String joinStringArray(String[] a) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < a.length; i++) {
                if (a[i] != null && a[i].length() > 0) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(a[i]);
                }
            }
            return sb.toString();
        }
        
    }
    
}
