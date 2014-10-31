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
 * TableModel.java
 *
 * Created on August 2, 2005, 3:16 PM
 *
 */

package org.netbeans.microedition.lcdui;

/**
 * TableModel interface specifies methods, which <code>Table</code> component
 * uses to get data it shows. The following example shows how 
 * the model is being used in Table:
 * <p><code>
 * TableModel myTableModel = new MyTableModel();
 * Table myTable = new Table();
 * myTable.setModel(myTableModel);
 * </code></p>
 * @author breh
 */
public interface TableModel {
	
	/**
	 * Adds <code>TableModelListener</code> to this model.
	 * @param listener listener to be added
	 */
	public void addTableModelListener(TableModelListener listener);
	
	/**
	 * Removes <code>TableModelListener</code> from this model.
	 * @param listener listener to be removed
	 */
	public void removeTableModelListener(TableModelListener listener);
	
	/**
	 * Gets the number of columns of the table
	 * @return column count
	 */
	public int getColumnCount();
	
	/**
	 * Gets the number of rows of the table
	 * @return row count
	 */
	public int getRowCount();
	
	/**
	 * Decides wheter this table is using headers (column names).
	 * @return true if the column names are being supplied and should be visualized, false otherwise
	 */
	public boolean isUsingHeaders();
		
	/**
	 * Gets the name of the given column. The given index 
	 * should never exceed the number specified by the
	 * <code>getColumnCount()</code> method.
	 * @param column index of column of which the name should be returned. May return null.
	 * @return The name of the column
	 */
	public String getColumnName(int column);
	
	/**
	 * Gets the value of a table cell at a specified location. For example
	 * <code>getValue(2,3)</code> returns a cell value from 2nd column and 3rd
	 * row.
	 * <p/>
	 * The given column and row should never exceed the numbers specified by the
	 * <code>getColumnCount()</code> or <code>getRowCount()</code> methods.
	 * @param column column index of the value
	 * @param row row index of the value
	 * @return value for the given cell coordinates. May return null if there is no value.
	 */
	public Object getValue(int column, int row);
	
}
