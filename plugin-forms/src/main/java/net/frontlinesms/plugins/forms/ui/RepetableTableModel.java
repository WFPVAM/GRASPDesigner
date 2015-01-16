/*
*GRASP(Geo-referential Real-time Acquisition Statistics Platform) Designer Tool <http://www.fabaris.it>
* Copyright © 2012 ,Fabaris s.r.l
* This file is part of GRASP Designer Tool.  
*  GRASP Designer Tool is free software: you can redistribute it and/or modify it
*  under the terms of the GNU Lesser General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or (at
*  your option) any later version.  
*  GRASP Designer Tool is distributed in the hope that it will be useful, but
*  WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser  General Public License for more details.  
*  You should have received a copy of the GNU Lesser General Public License
*  along with GRASP Designer Tool. 
*  If not, see <http://www.gnu.org/licenses/>
*/
package net.frontlinesms.plugins.forms.ui;

import java.util.ArrayList;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import net.frontlinesms.plugins.forms.data.domain.BindingContainer;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent.BindType;

/**
 * This class represent the model of the BindingTable
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 *  www.fabaris.it <http://www.fabaris.it/>  
 */
public class RepetableTableModel extends AbstractTableModel implements TableModelListener {
	private ArrayList<PreviewComponent> components;
	//private ArrayList<BindingContainer> bindType;
	private String[] columnNames = new String[] { "Component"};

	public RepetableTableModel() {

		components = new ArrayList<PreviewComponent>();
		//bindType = new ArrayList<BindingContainer>();
		this.addTableModelListener(this);

	}

	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		return columnNames[column];
	}

	public Object getValueAt(int row, int column) {
		/*
		 * Fabaris_a.zanchi Modified to return a String representation of the objects 
		 */
		
			return components.get(row).getComponent().getLabel() +" : " + components.get(row).getComponent().getDescription() ;
		
		/*if (column == 1)
			return bindType.get(row).getbType();
		else
			return bindType.get(row).getValue(); */
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (column != 2) 
			super.setValueAt(aValue, row, column);
		else {
			String sValue = (String) aValue;
 		//	this.updateBindingValue(sValue.trim(), row);
		}
			
	}

	public void addRow(Object[] rowData) {
		this.components.add((PreviewComponent) rowData[0]);
		//this.bindType.add((BindingContainer) rowData[1]);
		fireTableDataChanged();
	}

	public void removeRow(int row) {
		if (row >= 0 && row < this.components.size()) {
			this.components.remove(row);
		//	this.bindType.remove(row);
			fireTableDataChanged();
		}

	}

	public int getRowCount() {
		return this.components.size();
	}

	public void clean() {
		this.components = new ArrayList<PreviewComponent>();
	//	this.bindType = new ArrayList<BindingContainer>();
		fireTableDataChanged();
	}

	/**Fabaris_a.zanchi 
	 * Custom method to retrieve the data on a row. Must be used by external
	 * classes instead of getValueAt() that is used just to get something displayable on the table
	 * 
	 * @param row the index of the row to retrieve
	 * @return an array of objects containing the data
	 */
	public Object[] getRowData(int row) {
		Object[] ret = new Object[2];
		ret[0] = this.components.get(row);
	//	ret[1] = this.bindType.get(row);
		return ret;
	}
	
	/**Fabaris_a.zanchi 
	 * Method invoked when a binding type changes. NOT called if the value of the binding changes
	 * 
	 * @param newType the new Type of Binding
	 * @param row the row of the changing binding
	 */
	/*public void updateBindingType(BindType newType, int row) {
		PreviewComponent pComponent = this.components.get(row);
		BindingContainer oldTypeContainer = this.bindType.get(row);
		BindingContainer newTypeContainer = oldTypeContainer.clone();
		newTypeContainer.setType(newType);
		FormsUiController.getInstance().bindChanged(pComponent, oldTypeContainer, newTypeContainer);
	} */
	
	/**Fabaris_a.zanchi 
	 * Method invoked when a binding value changes. NOT called if the type of the binding changes
	 * 
	 * @param newValue a String object
	 * @param row
	 */
/*	public void updateBindingValue(String newValue, int row) {
		PreviewComponent pComponent = this.components.get(row);
		BindingContainer oldValueContainer = this.bindType.get(row);
		BindingContainer newValueContainer = oldValueContainer.clone();
		newValueContainer.setValue(newValue);
		FormsUiController.getInstance().bindChanged(pComponent, oldValueContainer, newValueContainer);
	} */
	

	/**Fabaris_a.zanchi 
	 * Method invoked when a row of the binding table is selected, so the
	 * binded component could be highlighted
	 * 
	 * @param row
	 */
	public void highlightBinding(int row) {
		FormsUiController.getInstance().highlightBinding(this.components.get(row), null);
	}
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub
		
	}

	
}
