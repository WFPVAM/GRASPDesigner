/*
 * FrontlineSMS <http://www.frontlinesms.com>

 * Copyright 2007, 2008 kiwanja
 * 
 * This file is part of FrontlineSMS.
 * 
 * FrontlineSMS is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 * 
 * FrontlineSMS is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FrontlineSMS. If not, see <http://www.gnu.org/licenses/>.
 */
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


import java.awt.Component;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import net.frontlinesms.plugins.forms.properties.tables.ui.TableLabelPropertyCellEditor;
import net.frontlinesms.ui.FrontlineUI;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * This class represents the Properties Table.
 * 
 * @author Carlos Eduardo Genz <li>kadu(at)masabi(dot)com
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/>  
 */
public class PropertiesTable extends JTable {
	private static final long serialVersionUID = -7523734570779253511L;

	private MyTableModel model;
	private Vector<Object> columns;
	private Vector<String> data = null;
        public static boolean section0= false;

	public PropertiesTable() {
		FrontlineFormsCellRenderer frontlineFormsCellRenderer = new FrontlineFormsCellRenderer();
		setDefaultRenderer(Object.class, frontlineFormsCellRenderer);
		model = new MyTableModel();
		columns = new Vector<Object>();
		columns.add(InternationalisationUtils.getI18nString(FormsThinletTabController.COMMON_PROPERTY));
		columns.add(InternationalisationUtils.getI18nString(FormsThinletTabController.COMMON_VALUE));	
		model.setDataVector(data, columns);		
		this.setModel(model);
		this.getTableHeader().setReorderingAllowed(false);
		this.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		this.setBorder(null);		
	}

	/**
	 * Removes all data from table.
	 */
	public void clean() {
		model.setDataVector(data, columns);
	}	 	
	
	 //a.zanchi overload del metodo per accettare diversi tipi di valori
	public void addProperty(String property, Object value) {			
		  	model.addRow(new Object[] { property, value });
			
	}
         public boolean isCellEditable(int row, int column) { 
            if (section0)
             return row==1 && column==1 ;
            else
            return (row==1&& column==1) || (row == 2&& column==1); 

        };
	// a.zanchi modifica per inserire checkbox
	@SuppressWarnings("serial")
	private class FrontlineFormsCellRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) { 
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			c.setFont(FrontlineUI.currentResourceBundle.getFont());
			return c;	
		}
	}
	
	
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		if(row == 1 && column == 1){
			return (new TableLabelPropertyCellEditor((String)this.getModel().getValueAt(row, column)));
		}
		return super.getCellEditor(row, column);
	}
	
}