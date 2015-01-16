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
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DropMode;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.frontlinesms.plugins.forms.data.domain.BindingContainer;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent.BindType;

/**
 * Class modeling the Table of Bindings
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 *   www.fabaris.it <http://www.fabaris.it/>  
 */
public class RepetableTable extends JTable {

	// These are the combobox values

	public RepetableTable() {
		super(new RepetableTableModel());
		this.setDragEnabled(true);
		this.getTableHeader().setReorderingAllowed(false);
	/*	TableColumn col = this.getColumnModel().getColumn(1);
		BindType[] types = BindType.values();
		String[] values = new String[types.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = types[i].toString();
		}
		*/
		this.addMouseListener(new MouseAdapter() {
			/**
			 * This mouseClicked implementation is used to highglight a component in the preview panel
			 */
			@Override
			public void mouseClicked(MouseEvent arg0) {
				RepetableTableModel model = (RepetableTableModel) RepetableTable.this.getModel();
				model.highlightBinding(RepetableTable.this.getSelectedRow());
			}
			
		});
		
		//col.setCellEditor(new MyComboBoxEditor(types));
	//	col.setCellRenderer(new MyComboBoxRenderer());
		//col.setPreferredWidth(130);
	}

	public void addRepetable(PreviewComponent pComponent) {
		RepetableTableModel model = (RepetableTableModel) this.getModel();
		model.addRow(new Object[] { pComponent });
	}

	/*public void updateBindingType(BindType newType, int selectedRow) {
		BindingTableModel model = (BindingTableModel) this.getModel();
		//int selectedRow = this.getSelectedRow();
		model.updateBindingType(newType, selectedRow);
	}*/

	public void clean() {
		RepetableTableModel model = (RepetableTableModel) this.getModel();
		model.clean();
	}

	/**a.zanchi
	 * Invoked when a Binding is removed from the component
	 * 
	 * @return an array of lenght 2 of objects. It actually returns the previewComponent on which the binding is active and the type of binding
	 */
	/*public Object[] removeSelectedBinding() {
		int index = this.getSelectedRow();
		if (index != -1) {
			Object[] ret = new Object[2];
			BindingTableModel model = (BindingTableModel) this.getModel();
			ret[0] = (PreviewComponent) model.getRowData(index)[0];
			ret[1] = (BindingContainer) model.getRowData(index)[1];
			model.removeRow(index);
			return ret;
		}
		return null;
	} */

	/**
	 * a.zanchi Used to make the dropbox (combobox) clickable
	 */
/*	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		if (arg1 > 0)
			return true;
		else
			return false;
	} */

	/**Fabaris_a.zanchi Class to model the bindtype selection dropbox
	 *
	 * @author a.zanchi
	 */
	/*public class MyComboBoxEditor extends DefaultCellEditor {

		private Object[] values;

		public MyComboBoxEditor(Object[] items) {
			super(new JComboBox(items));
			this.values = items;
		}

		@Override
		public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, final int arg3, int arg4) {
			final BindingTableModel model = (BindingTableModel) RepetableTable.this.getModel();
			BindingContainer currentBindCont = (BindingContainer) model.getRowData(arg3)[1];
			BindType currentBind = currentBindCont.getbType();
			JComboBox cb = new JComboBox(values);
			cb.setSelectedItem(currentBind);
			cb.addItemListener(new ItemListener() {
				
				public void itemStateChanged(ItemEvent arg0) {
					JComboBox cb = (JComboBox) arg0.getSource();
					//note: arg3 is the row
					RepetableTable.this.updateBindingType((BindType) cb.getSelectedItem(), arg3);
					cb.setFocusable(false);
					MyComboBoxEditor.this.fireEditingStopped();
				}
			});
			return cb;
		}

	}*/
	
	/**Fabaris_a.zanchi Class to model the rendering for the inactive ComboBox
	 * 
	 * @author a.zanchi
	 *
	 */
	/*public class MyComboBoxRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
			BindingTableModel model = (BindingTableModel) RepetableTable.this.getModel();
			BindingContainer currentBindCont = (BindingContainer) model.getRowData(arg4)[1];
			BindType currentBind = currentBindCont.getbType();
			JComboBox displayCombo = new JComboBox(new Object[]{currentBind});
			displayCombo.setSelectedItem(0);
			return displayCombo;
		}
		
	}*/

}
