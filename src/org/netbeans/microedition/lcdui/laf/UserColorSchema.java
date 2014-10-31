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

/*
 * UserColorSchema.java
 *
 * Created on July 21, 2006, 11:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.microedition.lcdui.laf;

import java.util.Hashtable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * A color schema, which can hold user color values. When using this implementation,
 * please note you have to manually repaint the shown component when the color is
 * changed. There is no event model which could inform the component about the change.
 *
 * @author breh
 * edited: Karol Karol Harezlak
 */
public class UserColorSchema extends ColorSchema {
    
    private Hashtable colorSchema = new Hashtable(8);
    private Image bgImg;
    private int bgImgAnchorPoint = Graphics.LEFT | Graphics.TOP;
    private boolean bgImgTiled = false;
    private boolean bgTransparent = false;
    private int fgColor;
    private int bgColor;
    
    
    /** Creates a new instance of UserColorSchema */
    public UserColorSchema() {
    }

    /**
     * Sets color to user color schema
     * @param aColorSpecifier - color specifier from Display.COLOR* values
     * @param color - color to be used for given specified
     */
    public void setColor(int aColorSpecifier, int color) {
        colorSchema.put(new Integer(aColorSpecifier), new Integer(color));
    }
    
    /**
     * Gets color based on color specifier. The color specifer 
     * corresponds to values listed in Display class.
     * @param aColorSpecifier - color specifier from Display.COLOR* constants
     * @return color to be used for given specifier. If a wrong specified is specified
     * or the instance does not have given color defined, it returns -1
     */
    public int getColor(int aColorSpecifier) {
        Integer returnValue = (Integer)colorSchema.get(new Integer(aColorSpecifier));
        if (returnValue != null) {
            return returnValue.intValue();
        }
        return -1;
    }

    /**
     * Sets background image to schema
     * @param backgroundImage background image
     */
    public void setBackgroundImage(Image backgroundImage) {
        this.bgImg = backgroundImage;
    }
    
    /**
     * Gets background image. 
     * @return background image, by default returns null, no image is defined.
     */
    public Image getBackgroundImage() {
        return bgImg;
    }

    /**
     * Sets anchor point for background image. The anchor point is based
     * on anchor values from Graphics class
     * @param anchorPoint - an anchor point
     */
    public void setBackgroundImageAnchorPoint(int anchorPoint) {
        this.bgImgAnchorPoint = anchorPoint;
    }
    
    /**
     * Gets anchor point for the background image. For details see 
     * {@link ColorSchema#getBackgroundImageAnchorPoint parent method}
     * @return anchor point
     */
    public int getBackgroundImageAnchorPoint() {
        return bgImgAnchorPoint;
    }

    /**
     * Sets the fact whether the background image should be tiled or not
     * @param tiled if true, the image should be tiled, false otherwise
     */
    public void setBackgroundImageTiled(boolean tiled) {
        this.bgImgTiled = tiled;
    }
    
    /**
     * Should be background image used as a tiled background. For details 
     * see parent class.
     * @return true of the background image should be tiled, false otherwise. The
     * default value is false.
     */
    public boolean isBackgroundImageTiled() {
        return bgImgTiled;
    }

    /**
     * Sets the fact whether the background should be transparent
     * @param transparent - if true, the background should be transparent, 
     * false otherwise
     */
    public void setBackgroundTransparent(boolean transparent) {
        this.bgTransparent = transparent;
    }
    
    /**
     * 
     * Should be the background transparent? For details see definition in parent
     * class.
     * @return true if the background should be transparent, false otherwise. The default 
     * value is false.
     */
    public boolean isBackgroundTransparent() {
        return bgTransparent;
    }
     
    public void setFGColor(int color){
        this.fgColor = color;
    }

    public void setBGColor(int color){
        this.bgColor = color;
    }
    
    
}
