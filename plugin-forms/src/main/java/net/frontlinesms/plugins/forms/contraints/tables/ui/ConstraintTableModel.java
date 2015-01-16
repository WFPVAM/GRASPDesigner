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
package net.frontlinesms.plugins.forms.contraints.tables.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer.ConstraintNumber;
import net.frontlinesms.plugins.forms.ui.FormsUiController;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;
/**
 * Class modeling the constrain table model
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph  
 * www.fabaris.it <http://www.fabaris.it/>  
 *
 */
public class ConstraintTableModel extends AbstractTableModel {
	
	private ArrayList<ConstraintContainer> constraints;
	private String[] columnNames = new String[] {"Type", "Value" };

	public ConstraintTableModel() {
		constraints = new ArrayList<ConstraintContainer>();
	}
	
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnNames.length;
	}

	public int getRowCount() {
		// TODO Auto-generated method stub
		return constraints.size();
	}
	
	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		return columnNames[column];
	}

	
	public Object getValueAt(int row, int column) {
		// TODO Auto-generated method stub
		if (column == 0){
			return constraints.get(row).getcNumber();
		}
		if (column == 1)
			return constraints.get(row).getValue();
		else 
			return null;
	}
	
	@Override
	public void setValueAt(Object arg0, int row, int column) {
		if (column > 0) {
			this.constraints.get(row).setValue((String) arg0);
			FormsUiController.getInstance().updateConstraint(this.constraints.get(row), row);
			fireTableDataChanged();
		}
	}

	/**Fabaris_a.zanchi 
	 * Custom method to retrieve the data on a row. Must be used by external
	 * classes instead of getValueAt() that is used just to get something displayable on the table
	 * 
	 * @param row the index of the row to retrieve
	 * @return an array of objects containing the data
	 */
	public Object getRowData(int row) {
		return this.constraints.get(row);
	}
	
	public void addConstraintToDisplay(ConstraintContainer cont) {
		this.constraints.add(cont);
		fireTableDataChanged();
	}
	
	/**Fabaris_a.zanchi
	 * Method to insert new data in the table model.
	 * @param data
	 */
	public void addRow(Object[] data) {
		ConstraintContainer cont = new ConstraintContainer();
		cont.setcNumber((ConstraintNumber) data[0]);
		cont.setValue((String) data[1]);
		this.constraints.add(cont);
		FormsUiController.getInstance().addConstraint(cont);
		fireTableDataChanged();
	}
	
	/**Fabaris_a.zanchi
	 * Updates the constrain type at a given index
	 * @param type
	 * @param index
	 */
	public void updateConstrainType(ConstraintNumber type, int index) {
		this.constraints.get(index).setcNumber(type);
		FormsUiController.getInstance().updateConstraint(this.constraints.get(index), index);
		fireTableDataChanged();
	}
	
	public void removeConstraintAtRow(int row) {
		this.constraints.remove(row);
		FormsUiController.getInstance().removeConstraint(row);
		fireTableDataChanged();
	}
	
	public void clean() {
		this.constraints = new ArrayList<ConstraintContainer>();
		fireTableDataChanged();
	}

}
