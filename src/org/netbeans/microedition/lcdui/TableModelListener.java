/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt. 
  * 
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */ 

/*
 * TableModelListener.java
 *
 * Created on August 4, 2005, 5:40 PM
 *
 */

package org.netbeans.microedition.lcdui;

/**
 * A listener for changes of the table model
 * @author breh
 */
public interface TableModelListener {
	
	/**
	 * The supplied <code>TableModel</code> has changed.
	 * @param model a changed TableModel.
	 */
	public void tableModelChanged(TableModel model);
	
}
