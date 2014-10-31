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


package org.netbeans.microedition.lcdui.laf;


import java.io.IOException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import org.netbeans.microedition.lcdui.laf.ColorSchema;

/**
 *
 * @author breh
 */
public class DefaultColorSchema extends ColorSchema {
    
    private static final int BACKGROUND=0xCCCCCC;
    private static final int HI_BACKGROUND=0x171402;
    private static final int FOREGROUND = 0x00;
   
    private int bgColor = BACKGROUND;
    private int fgColor = FOREGROUND;
    
    private static DefaultColorSchema defaultColorSchema;
    private static Image LOGO_IMAGE;
    
    static {
        try {
            LOGO_IMAGE = Image.createImage("/org/netbeans/microedition/resources/dir.png");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public DefaultColorSchema() {
    }
    
    public int getColor(int aColorSpecifier) {
        switch (aColorSpecifier) {
            case Display.COLOR_BACKGROUND:
                return bgColor;
            case Display.COLOR_HIGHLIGHTED_BACKGROUND:
                return bgColor;
            case Display.COLOR_BORDER:
                return fgColor;
            case Display.COLOR_FOREGROUND:
                return fgColor;
            case Display.COLOR_HIGHLIGHTED_BORDER:
                return fgColor;
            case Display.COLOR_HIGHLIGHTED_FOREGROUND:
                return fgColor;                
        } // else        
        throw new IllegalArgumentException("colorSpecified has not defined value");
    }

    public Image getBackgroundImage() {
        return LOGO_IMAGE;
    }

    public int getBackgroundImageAnchorPoint() {
        return Graphics.RIGHT | Graphics.BOTTOM;
    }

    public boolean isBackgroundImageTiled() {
        return false;
    }

    public boolean isBackgroundTransparent() {
        return false;
    }
    
    public void setFGColor(int color){
        this.fgColor = color;
    }

    public void setBGColor(int color){
        this.bgColor = color;
    }
    
    
}