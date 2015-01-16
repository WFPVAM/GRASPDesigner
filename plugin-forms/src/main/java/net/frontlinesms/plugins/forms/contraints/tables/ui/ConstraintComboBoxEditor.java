package net.frontlinesms.plugins.forms.contraints.tables.ui;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.CellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer;
import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer.ConstraintDomain;
import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer.ConstraintNumber;

public class ConstraintComboBoxEditor extends AbstractConstraintCellEditor{
	private ConstraintTableModel model;
	private ConstraintNumber[] types;
	
	public ConstraintComboBoxEditor(ConstraintTableModel model , ConstraintDomain domain) {
		super(new JComboBox(ConstraintNumber.getTheValues(domain)));
		this.types = ConstraintNumber.getTheValues(domain);
		this.model = model;
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, final int row, int column) {
		final ConstraintTableModel model = (ConstraintTableModel) table.getModel();
		ConstraintContainer currentConstrCont = (ConstraintContainer) model.getRowData(row);
		ConstraintNumber currentConstr = currentConstrCont.getcNumber();
		JComboBox cb = new JComboBox(types);
		cb.setSelectedItem(currentConstr);
		cb.setEditable(false);
		final JTable t = table;
		if(value != null){
			cb.setSelectedItem(value.toString());
		}
		cb.setEditable(false);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				JComboBox cb = (JComboBox) arg0.getSource();
				//note: arg3 is the row
				ConstraintComboBoxEditor.this.model.updateConstrainType((ConstraintNumber) cb.getSelectedItem(), row);
				cb.setFocusable(false);
				ConstraintComboBoxEditor.this.fireEditingStopped();
				t.changeSelection(row, 1, false, false);
				t.editCellAt(row, 1);
			}
		});
		return cb;
	}
}


