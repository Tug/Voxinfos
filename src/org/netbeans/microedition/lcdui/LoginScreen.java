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

import javax.microedition.lcdui.*;
import org.netbeans.microedition.lcdui.laf.ColorSchema;
import org.netbeans.microedition.lcdui.laf.DefaultColorSchema;

/**
 *
 * @author breh
 * edited: Karol Harezlak
 */
public class LoginScreen extends Canvas implements CommandListener{
    
    
    public static Command LOGIN_COMMAND = new Command("Login",Command.OK, 1);
    
    private static final int ACTIVE_USERNAME = 1;
    private static final int ACTIVE_PASSWORD = 2;
    private static final int ACTIVE_LOGIN_BUTTON = 3;
    private int activeField = ACTIVE_USERNAME;
    
    private String loginButtonText;
    private boolean useLoginButton = true;
    
    private int backgroundImageAnchorPoint;
    
    private InputTextBox inputTextBox;
    
    private boolean useTextBoxForInput = true;
    private boolean inputTextIsActive = false;
    
    private int borderStyle;
    private int hiBorderStyle;
    
    private Font titleFont;
    private Font inputFont;
    private Font loginButtonFont;
    
    private int visibleInputFieldLength = 12;
    private int maximumInputSize;
    private static final int borderPadding = 2;
    private static final int labelPadding = 4;
    
    private int loginTitleY;
    private int usernameY;
    private int passwordY;
    private int usernameX;
    private int passwordX;
    
    private int inputFieldsWidth;
    private int inputFieldsHeight;
    private int loginButtonWidth;
    private int loginButtonHeight;
    private int loginButtonY;
    private int loginButtonX;
    
    private int usernameLabelWidth;
    private int passwordLabelWidth;
    
    
    private String usernameLabel;
    private String passwordLabel;
    private String username = "";
    private String password = "";
    private String shownPassword = "";
    private static final char PASSWORD_CHAR = '*';
    private String loginScreenTitle;
    private Display display;
    private CommandListener l;
    private ColorSchema colorSchema;
    
    /** Creates a new instance of LoginScreen */
    public LoginScreen(Display display) {
        this.display = display;
        colorSchema = new DefaultColorSchema();
        setDefaulBorderStyles();
        setDefaultFonts();
        
        titleFont = Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD|Font.STYLE_ITALIC,Font.SIZE_MEDIUM);
        loginButtonFont = Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_SMALL);
        
        // default values
        this.usernameLabel = "Username:";
        this.passwordLabel = "Password:";
        this.loginButtonText = "Login";
        this.maximumInputSize = 20;
        
        setUseLoginButton(useLoginButton);
        super.setCommandListener(this);
    }
    
    public void setUsername(String username) {
        if (username == null) {
            username = "";
        }
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setPassword(String password) {
        if (password == null) {
            password = "";
        }
        this.password = password;
        char[] shownPwd = new char[password.length()];
        for (int i=0; i < shownPwd.length; i++) {
            shownPwd[i] = PASSWORD_CHAR;
        }
        this.shownPassword = new String(shownPwd,0,shownPwd.length);
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setLoginTitle(String loginTitleText) {
        this.loginScreenTitle = loginTitleText;
    }
    
    public void setUseLoginButton(boolean useLoginButton){
        this.useLoginButton = useLoginButton;
        if(!useLoginButton)
            addCommand(LOGIN_COMMAND);
        else
            removeCommand(LOGIN_COMMAND);
    }
    public void setLabelTexts(String usernameLabel, String passwordLabel) {
        this.usernameLabel = usernameLabel;
        this.passwordLabel = passwordLabel;
    }
    
    public Display getDisplay(){
        return display;
    }
    
    public void setDefaulBorderStyles() {
        borderStyle = getDisplay().getBorderStyle(false);
        hiBorderStyle = getDisplay().getBorderStyle(true);
    }
    
    public void setDefaultFonts() {
        titleFont = Font.getFont(Font.FONT_STATIC_TEXT);
        inputFont = Font.getFont(Font.FONT_INPUT_TEXT);
        loginButtonFont = titleFont;
    }
    
    public void setFonts(Font titleFont, Font inputFont, Font loginButtonFont) {
        this.titleFont = titleFont;
        this.inputFont = inputFont;
        this.loginButtonFont = loginButtonFont;
    }
    
    
    public void setBackgroundImageAnchorPoint(int anchorPoint) {
        this.backgroundImageAnchorPoint = anchorPoint;
    }
    
    protected void showNotify() {
        computeMetrics();
    }
    
    protected void sizeChanged(int w, int h) {
        computeMetrics();
    }
    
    
    private int computeYMetrics(int baseY) {
        loginTitleY = baseY + 2*labelPadding;
        usernameY = loginTitleY + titleFont.getHeight() + 2*labelPadding;
        passwordY = usernameY + inputFieldsHeight + labelPadding;
        loginButtonY = passwordY  + inputFieldsHeight + 2 * labelPadding;
        return loginButtonY + loginButtonHeight;
    }
    
    
    private void computeMetrics() {
        final int width = getWidth();
        final int height = getHeight();
        final int centerY = height/2;
        final int centerX = width/2;
        
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
        
        usernameLabelWidth = inputFont.stringWidth(usernameLabel);
        passwordLabelWidth = inputFont.stringWidth(passwordLabel);
        final int labelWidth = Math.max(usernameLabelWidth, passwordLabelWidth);
        usernameLabelWidth = labelWidth;
        passwordLabelWidth = labelWidth;
        
        usernameX = centerX - (usernameLabelWidth + labelPadding + inputFieldsWidth)/2;
        if (usernameX < 0) {
            usernameX = 0;
            inputFieldsWidth = width - labelWidth - labelPadding - 1;
        }
        passwordX = centerX - (passwordLabelWidth + labelPadding + inputFieldsWidth)/2;
        if (passwordX < 0) {
            passwordY = 0;
            inputFieldsWidth = width - labelWidth - labelPadding - 1;
        }
        
        loginButtonHeight = loginButtonFont.getHeight() + borderPadding*2;
        loginButtonWidth = loginButtonFont.stringWidth("X"+loginButtonText+"X") + borderPadding*2;
        loginButtonX = centerX - loginButtonWidth / 2;
        
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
        // draw login screen title
        if (loginScreenTitle != null) {
            g.setColor(getColorSchema().getColor(Display.COLOR_FOREGROUND));
            g.setFont(titleFont);
            g.drawString(loginScreenTitle,centerX,loginTitleY,Graphics.HCENTER | Graphics.TOP);
        }
        
        // draw username
        g.setFont(inputFont);
        //g.setColor(textboxBackgroundColor);
        
        int x,y,w,h;
        //x = center - inputFieldsWidth/2;
        x = usernameX + usernameLabelWidth + labelPadding;
        y = usernameY;// - inputFieldsHeight + borderPadding;
        w = inputFieldsWidth;
        h = inputFieldsHeight;
        g.setColor(0xffffff);
        g.fillRoundRect(x,y,w,h,6,6);
        boolean usernameActive = activeField == ACTIVE_USERNAME;
        setColorByState(g,getColorSchema().getColor(Display.COLOR_BORDER),getColorSchema().getColor(Display.COLOR_HIGHLIGHTED_BORDER),usernameActive);
        setStyleByState(g,borderStyle,hiBorderStyle,usernameActive);
        g.drawRoundRect(x,y,w,h,6,6);
        setColorByState(g,getColorSchema().getColor(Display.COLOR_FOREGROUND),getColorSchema().getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND),usernameActive);
        g.setClip(x+borderPadding,y+borderPadding,w-2*borderPadding,h-2*borderPadding);
        g.drawString(username,x + borderPadding, usernameY + borderPadding,Graphics.LEFT | Graphics.TOP);
        g.setClip(0,0,width,height);
        g.setColor(getColorSchema().getColor(Display.COLOR_FOREGROUND));
        g.drawString(usernameLabel,usernameX,usernameY + borderPadding,Graphics.LEFT | Graphics.TOP);
        // draw password
        boolean passwordActive = activeField == ACTIVE_PASSWORD;
        setColorByState(g,getColorSchema().getColor(Display.COLOR_BACKGROUND),getColorSchema().getColor(Display.COLOR_HIGHLIGHTED_BACKGROUND),passwordActive);
        //x = center - inputFieldsWidth/2;
        x = passwordX + passwordLabelWidth + labelPadding;
        y = passwordY;// - inputFieldsHeight + borderPadding;
        w = inputFieldsWidth;
        h = inputFieldsHeight;
        g.setColor(0xffffff);
        g.fillRoundRect(x,y,w,h,6,6);
        setColorByState(g,getColorSchema().getColor(Display.COLOR_BORDER),getColorSchema().getColor(Display.COLOR_HIGHLIGHTED_BORDER),passwordActive);
        setStyleByState(g,borderStyle,hiBorderStyle,passwordActive);
        g.drawRoundRect(x,y,w,h,6,6);
        setColorByState(g,getColorSchema().getColor(Display.COLOR_FOREGROUND),getColorSchema().getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND),passwordActive);
        g.setClip(x+borderPadding,y+borderPadding,w-2*borderPadding,h-2*borderPadding);
        g.drawString(shownPassword,x + borderPadding,y + borderPadding,Graphics.LEFT | Graphics.TOP);
        g.setClip(0,0,width,height);
        g.setColor(getColorSchema().getColor(Display.COLOR_FOREGROUND));
        g.drawString(passwordLabel,passwordX,passwordY + borderPadding,Graphics.LEFT | Graphics.TOP);
        
        // draw login button
        if (useLoginButton) {
            boolean loginButtonActive = activeField == ACTIVE_LOGIN_BUTTON;
            setColorByState(g,getColorSchema().getColor(Display.COLOR_BACKGROUND),getColorSchema().getColor(Display.COLOR_HIGHLIGHTED_BACKGROUND),loginButtonActive);
            x = loginButtonX;
            y = loginButtonY;
            w = loginButtonWidth;
            h = loginButtonHeight;
            g.fillRoundRect(x,y,w,h,6,6);
            setColorByState(g,getColorSchema().getColor(Display.COLOR_BORDER),getColorSchema().getColor(Display.COLOR_HIGHLIGHTED_BORDER),loginButtonActive);
            setStyleByState(g,borderStyle,hiBorderStyle,loginButtonActive);
            g.drawRoundRect(x,y,w,h,6,6);
            setColorByState(g,getColorSchema().getColor(Display.COLOR_FOREGROUND),getColorSchema().getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND),loginButtonActive);
            //g.setClip(x+borderPadding,y+borderPadding,w-2*borderPadding,h-2*borderPadding);
            g.setFont(loginButtonFont);
            g.drawString(loginButtonText,centerX,y+borderPadding,Graphics.HCENTER | Graphics.TOP);
            g.setClip(0,0,width,height);
        }
    }
    
    
    
    private int getLastActiveItem() {
        return useLoginButton ? ACTIVE_LOGIN_BUTTON : ACTIVE_PASSWORD;
    }
    
    
    private void moveActiveField(boolean down) {
        int add = down ? 1 : -1;
        activeField += add;
        if (activeField > getLastActiveItem()) {
            activeField = ACTIVE_USERNAME;
        } else if (activeField < ACTIVE_USERNAME) {
            activeField = getLastActiveItem();
        }
        repaint();
    }
    
    
    private void fireLoginEvent() {
        CommandListener l = getCommandListener();
        if (l != null) {
            l.commandAction(LOGIN_COMMAND,this);
        }
    }
    
    protected void keyReleased(int keyCode) {
        final int gameAction = getGameAction(keyCode);
        if (inputTextIsActive) {
        } else {
            switch (gameAction) {
                case FIRE:
                    if (activeField == ACTIVE_LOGIN_BUTTON) {
                        fireLoginEvent();
                    } else {
                        startEditingInputText();
                    }
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
                /*case FIRE:
                    startEditingInputText();
                    return;
                 */
            }
        }
    }
    
    
    private InputTextBox getInputTextBox() {
        if (inputTextBox == null) {
            inputTextBox = new InputTextBox();
        }
        return inputTextBox;
    }
    
    private void startEditingInputText() {
        inputTextIsActive = true;
        if (useTextBoxForInput) {
            getInputTextBox().setTextBoxMode(activeField);
            if (activeField == ACTIVE_USERNAME) {
                getInputTextBox().setString(username);
            } else if (activeField == ACTIVE_PASSWORD) {
                getInputTextBox().setString(password);
            }
            //System.out.println("inputtextbox: "+getInputTextBox());
            getDisplay().callSerially(new Runnable() {
                public void run() {
                    getDisplay().setCurrent(getInputTextBox());
                }
            });
            
        }
    }
    
    
    
    
    private void stopEditingInputText(boolean confirmChanges) {
        inputTextIsActive = false;
        if (useTextBoxForInput) {
            if (confirmChanges) {
                if (activeField == ACTIVE_USERNAME) {
                    setUsername(getInputTextBox().getString());
                } else if (activeField == ACTIVE_PASSWORD) {
                    setPassword(getInputTextBox().getString());
                }
            }
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
    
    public void commandAction(Command command, Displayable displayable) {
        if(displayable == this){
            if(command.equals(LOGIN_COMMAND)){
                fireLoginEvent();
            }else if(l != null){
                l.commandAction(command, displayable);
            }
        }
    }
    
    private class InputTextBox extends TextBox implements CommandListener {
        
        private final Command CONFIRM_COMMAND = new Command("OK",Command.ITEM,1);
        private final Command CANCEL_COMMAND = new Command("Cancel",Command.CANCEL,1);
        
        public InputTextBox() {
            super(null,null,maximumInputSize,0);
            InputTextBox.this.setCommandListener(this);
            addCommand(CONFIRM_COMMAND);
            addCommand(CANCEL_COMMAND);
        }
        
        public void setTextBoxMode(int mode) {
            if (mode == ACTIVE_USERNAME) {
                setConstraints(TextField.NON_PREDICTIVE);
                setTitle(usernameLabel);
            } else if (mode == ACTIVE_PASSWORD) {
                setConstraints(TextField.NON_PREDICTIVE | TextField.PASSWORD);
                setTitle(passwordLabel);
            } else {
                // else something wrong has happened
                throw new IllegalArgumentException("Wrong mode: "+mode);
            }
        }
        
        public void commandAction(Command c, Displayable d) {
            
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
