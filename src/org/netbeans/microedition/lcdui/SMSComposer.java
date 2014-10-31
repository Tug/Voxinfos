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

import javax.microedition.io.Connector;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import org.netbeans.microedition.lcdui.laf.ColorSchema;
import org.netbeans.microedition.lcdui.laf.DefaultColorSchema;

public class SMSComposer extends Canvas implements CommandListener{
    
    private String phoneNumber = "";
    private String message = "";
    private Display display;
    private int portNum = 50000;
    public static Command SEND_COMMAND = new Command("Send",Command.OK, 1);
    private boolean useTextBoxForInput = true;
    private boolean inputTextIsActive = false;
    private int borderStyle;
    private int hiBorderStyle;
    private Font inputFont;
    private static final int borderPadding = 2;
    private static final int labelPadding = 2;
    private int phoneNumberY;
    private int messageY;
    private int phoneNumberX;
    private int messageX;
    private int inputFieldsWidth;
    private int inputFieldsHeight;
    private int phoneNumberLabelWidth;
    private int messageLabelWidth;
    private static final int ACTIVE_PHONE_NUMBER = 1;
    private static final int ACTIVE_MESSAGE = 2;
    private static final int ACTIVE_NOTHING = 0;
    private int activeField = ACTIVE_PHONE_NUMBER;
    private String phoneNumberLabel;
    private String messageLabel;
    private CommandListener l;
    private ColorSchema colorSchema = new DefaultColorSchema();
    private InputTextBox phoneBox;
    private InputTextBox msgBox;
    private InputTextBox current = null;
    
    /** Creates a new instance of SMSComposer */
    public SMSComposer(Display display) {
        this.display = display;
        setDefaulBorderStyles();
        setDefaultFonts();
        addCommand(SEND_COMMAND);
        super.setCommandListener(this);
        
        // default values
        this.phoneNumberLabel = "Phone Number:";
        this.messageLabel = "Message:";
        phoneBox = new InputTextBox(phoneNumberLabel, 20, TextField.PHONENUMBER);
        msgBox = new InputTextBox(messageLabel, 160, TextField.ANY);
    }
    
    public void setPhoneNumber(String phoneNumber){
        if (phoneNumber == null) {
            phoneNumber = "";
        }
        this.phoneNumber = phoneNumber;
    }
    
    public void setMessage(String message){
        if (message == null) {
            message = "";
        }
        this.message = message;
    }
    
    public void setPort(int portNum){
        this.portNum = portNum;
    }
    
    private void reportException(Exception e){
        e.printStackTrace();
        reportError(e.getMessage());
    }
    
    private void reportError(String error){
        Alert alert = new Alert("Error", error, null, AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        display.setCurrent(alert, this);
    }
    
    private void sendSMS(){
        String address = "sms://" + phoneNumber + ":" + portNum;
        MessageConnection smsconn = null;
        try {
            /** Open the message connection. */
            smsconn = (MessageConnection)Connector.open(address);
            TextMessage txtmessage = (TextMessage)smsconn.newMessage(
                    MessageConnection.TEXT_MESSAGE);
            txtmessage.setAddress(address);
            txtmessage.setPayloadText(message);
            smsconn.send(txtmessage);
            smsconn.close();
        } catch (Exception e) {
            reportException(e);
        }
    }
    public void setDefaulBorderStyles() {
        borderStyle = getDisplay().getBorderStyle(false);
        hiBorderStyle = getDisplay().getBorderStyle(true);
    }
    
    public void setDefaultFonts() {
        inputFont = Font.getFont(Font.FONT_INPUT_TEXT);
    }
    
    protected void showNotify() {
        computeMetrics();
    }
    
    protected void sizeChanged(int w, int h) {
        computeMetrics();
    }
    
    
    private int computeYMetrics(int baseY) {
        phoneNumberY = baseY + labelPadding;
        messageY = phoneNumberY + inputFieldsHeight + labelPadding;
        return messageY + inputFieldsHeight;
    }
    
    
    private void computeMetrics() {
        
        final int width = getWidth();
        final int height = getHeight();
        final int centerY = height/2;
        final int centerX = width/2;
        int visibleInputFieldLength = 12;
        inputFieldsWidth = inputFont.charWidth('X')*visibleInputFieldLength + 2*borderPadding;
        if (inputFieldsWidth > width) {
            inputFieldsWidth = width - 2;
        }
        inputFieldsHeight = inputFont.getHeight() + 2*borderPadding;
        
        int componentsHeight = computeYMetrics(0);
        
        int newbaseY = (height - componentsHeight)/4;
        if (newbaseY > 0) {
            computeYMetrics(newbaseY);
        }
        
        phoneNumberLabelWidth = inputFont.stringWidth(phoneNumberLabel);
        messageLabelWidth = inputFont.stringWidth(messageLabel);
        final int labelWidth = Math.max(phoneNumberLabelWidth, messageLabelWidth);
        phoneNumberLabelWidth = labelWidth;
        messageLabelWidth = labelWidth;
        
        phoneNumberX = centerX - (phoneNumberLabelWidth + labelPadding + inputFieldsWidth)/2;
        if (phoneNumberX < 0) {
            phoneNumberX = 0;
            inputFieldsWidth = width - labelWidth - labelPadding - 1;
        }
        messageX = centerX - (messageLabelWidth + labelPadding + inputFieldsWidth)/2;
        if (messageX < 0) {
            messageY = 0;
            inputFieldsWidth = width - labelWidth - labelPadding - 1;
        }
        
    }
    
    private static void setColorByState(Graphics g, int baseColor, int hiColor, boolean active) {
        if (active) {
            g.setColor(hiColor);
        } else {
            g.setColor(baseColor);
        }
    }
    
    
    private static void setStyleByState(Graphics g, int baseStyle, int hiStyle, boolean active) {
        if (active) {
            g.setStrokeStyle(baseStyle);
        } else {
            g.setStrokeStyle(hiStyle);
        }
    }
    
    public ColorSchema getColorSchema(){
        return colorSchema;
    }
    protected void paint(Graphics g) {
        //System.out.println("CLIPX: "+g.getClipX()+","+g.getClipY()+","+g.getClipWidth()+","+g.getClipHeight());
        int width = getWidth();
        int height = getHeight();
        //System.out.println("WIdth = "+width+", hei="+height);
        int centerX = width/2;
        
        getColorSchema().paintBackground(g,false);
        
        // draw phoneNumber
        g.setFont(inputFont);
        //g.setColor(textboxBackgroundColor);
        
        int x,y,w,h;
        //x = center - inputFieldsWidth/2;
        x = phoneNumberX + phoneNumberLabelWidth + labelPadding;
        y = phoneNumberY;// - inputFieldsHeight + borderPadding;
        w = inputFieldsWidth;
        h = inputFieldsHeight;
        g.setColor(0xffffff);
        g.fillRoundRect(x,y,w,h,6,6);
        boolean phoneNumberActive = activeField == ACTIVE_PHONE_NUMBER;
        setColorByState(g,getColorSchema().getColor(Display.COLOR_BORDER),getColorSchema().getColor(Display.COLOR_HIGHLIGHTED_BORDER),phoneNumberActive);
        setStyleByState(g,borderStyle,hiBorderStyle,phoneNumberActive);
        g.drawRoundRect(x,y,w,h,6,6);
        setColorByState(g,getColorSchema().getColor(Display.COLOR_FOREGROUND),getColorSchema().getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND),phoneNumberActive);
        g.setClip(x+borderPadding,y+borderPadding,w-2*borderPadding,h-2*borderPadding);
        g.drawString(phoneNumber,x + borderPadding, phoneNumberY + borderPadding, Graphics.LEFT | Graphics.TOP);
        g.setClip(0,0,width,height);
        g.setColor(getColorSchema().getColor(Display.COLOR_FOREGROUND));
        g.drawString(phoneNumberLabel,phoneNumberX,phoneNumberY + borderPadding,Graphics.LEFT | Graphics.TOP);
        // draw message
        boolean messageActive = activeField == ACTIVE_MESSAGE;
        setColorByState(g,getColorSchema().getColor(Display.COLOR_BACKGROUND),getColorSchema().getColor(Display.COLOR_HIGHLIGHTED_BACKGROUND),messageActive);
        //x = center - inputFieldsWidth/2;
        x = messageX + messageLabelWidth + labelPadding;
        y = messageY;// - inputFieldsHeight + borderPadding;
        w = inputFieldsWidth;
        h = inputFieldsHeight;
        g.setColor(0xffffff);
        g.fillRoundRect(borderPadding,y + inputFieldsHeight+ labelPadding ,width - 2*borderPadding ,height - (y + inputFieldsHeight+ labelPadding) - 2*borderPadding,6,6);
        setColorByState(g,getColorSchema().getColor(Display.COLOR_BORDER),getColorSchema().getColor(Display.COLOR_HIGHLIGHTED_BORDER),messageActive);
        setStyleByState(g,borderStyle,hiBorderStyle,messageActive);
        g.drawRoundRect(borderPadding,y + inputFieldsHeight + labelPadding,width - 2*borderPadding ,height - (y + inputFieldsHeight+ labelPadding) - 2*borderPadding,6,6);
        setColorByState(g,getColorSchema().getColor(Display.COLOR_FOREGROUND),getColorSchema().getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND),messageActive);
        
        g.setClip(borderPadding,y + inputFieldsHeight + labelPadding ,width - 2*borderPadding ,height - (y + inputFieldsHeight+ labelPadding) - 2*borderPadding);
        
        int messageLength = message.length();
        int currentWidth = 0;
        int startPoint = 0;
        int line = 1;
        
        for(int i=0; i<messageLength; i++){
            char c = message.charAt(i);
            int cWidth = inputFont.stringWidth(""+c);
            currentWidth += cWidth;
            if(currentWidth >= width - 3*borderPadding){
                //go back one char
                i--;
                String subMsg = message.substring(startPoint, i);
                g.drawString(subMsg,2* borderPadding,y + line*(inputFieldsHeight) + labelPadding + borderPadding,Graphics.LEFT | Graphics.TOP);
                startPoint = i;
                currentWidth = 0;
                line++;
            }
        }
        String subMsg = message.substring(startPoint, messageLength);
        g.drawString(subMsg,2* borderPadding,y + line*(inputFieldsHeight) + labelPadding + borderPadding,Graphics.LEFT | Graphics.TOP);
        
        
        g.setClip(0,0,width,height);
        g.setColor(getColorSchema().getColor(Display.COLOR_FOREGROUND));
        g.drawString(messageLabel,messageX,messageY + borderPadding,Graphics.LEFT | Graphics.TOP);
        
    }
    
    
    
    private void moveActiveField(boolean down) {
        int add = down ? 1 : -1;
        activeField += add;
        if (activeField > ACTIVE_MESSAGE) {
            activeField = ACTIVE_PHONE_NUMBER;
        } else if (activeField < ACTIVE_PHONE_NUMBER) {
            activeField = ACTIVE_MESSAGE;
        }
        repaint();
    }
    
    
    private void fireSendEvent() {
        CommandListener l = getCommandListener();
        if (l != null) {
            l.commandAction(SEND_COMMAND,this);
        }
    }
    
    protected void keyReleased(int keyCode) {
        final int gameAction = getGameAction(keyCode);
        if (inputTextIsActive) {
        } else {
            switch (gameAction) {
                case FIRE:
                    startEditingInputText();
                    return;
            }
        }
    }
    
    
    protected void keyPressed(int keyCode) {
        final int gameAction = getGameAction(keyCode);
        if (inputTextIsActive) {
        } else {
            switch (gameAction) {
                case LEFT:
                case DOWN:
                    moveActiveField(true);
                    return;
                case RIGHT:
                case UP:
                    moveActiveField(false);
                    return;
                    /*
                case FIRE:
                    startEditingInputText();
                    return;
                     */
            }
        }
    }
    
    
    
    private void startEditingInputText() {
        inputTextIsActive = true;
        if (useTextBoxForInput) {
            if (activeField == ACTIVE_PHONE_NUMBER) {
                phoneBox.setString(phoneNumber);
                current = phoneBox;
            } else if (activeField == ACTIVE_MESSAGE) {
                msgBox.setString(message);
                current = msgBox;
            }
            //System.out.println("inputtextbox: "+getInputTextBox());
            getDisplay().callSerially(new Runnable() {
                public void run() {
                    getDisplay().setCurrent(current);
                }
            });
            
        }
    }
    
    
    
    
    private void stopEditingInputText(boolean confirmChanges) {
        inputTextIsActive = false;
        if (useTextBoxForInput) {
            if (confirmChanges) {
                if (activeField == ACTIVE_PHONE_NUMBER) {
                    setPhoneNumber(current.getString());
                } else if (activeField == ACTIVE_MESSAGE) {
                    setMessage(current.getString());
                }
                
            }
            //System.out.println("setting current !!!");
            getDisplay().setCurrent(this);
            
        }
    }
    
    public void setCommandListener(CommandListener l){
        this.l = l;
    }
    
    public CommandListener getCommandListener(){
        return l;
    }
    
    public void setBGColor(int color){
        ((DefaultColorSchema)colorSchema).setBGColor(color);
    }
    
    public void setFGColor(int color){
        ((DefaultColorSchema)colorSchema).setFGColor(color);
    }
    
    private Display getDisplay() {
        return display;
    }
    
    public void commandAction(Command command, Displayable displayable) {
        if(command.equals(SEND_COMMAND)){
            new Thread(new Runnable(){
                public void run(){
                    sendSMS();
                }
            }).start();
            
        }
        
        if(l != null){
            l.commandAction(command, displayable);
        }
        
    }
    
    private class InputTextBox extends TextBox implements CommandListener {
        
        private final Command CONFIRM_COMMAND = new Command("OK",Command.OK,1);
        private final Command CANCEL_COMMAND = new Command("Cancel",Command.CANCEL,1);
        
        public InputTextBox(String title, int maximumChars, int constraints) {
            super(title,null,maximumChars,constraints);
            setCommandListener(this);
            addCommand(CONFIRM_COMMAND);
            addCommand(CANCEL_COMMAND);
        }
        
        public void commandAction(Command c, Displayable d) {
            /*
            System.out.println("Command axtion from input text box: command="+c.getLabel()+", d="+d);
            try {
                throw new RuntimeException("test");
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
             */
            if (d == this) {
                if (c == CONFIRM_COMMAND) {
                    // confirm
                    stopEditingInputText(true);
                } else if (c == CANCEL_COMMAND) {
                    // cancel
                    stopEditingInputText(false);
                }
            }
        }
        
        
    }
    
}
