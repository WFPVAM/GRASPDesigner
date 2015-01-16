package net.frontlinesms.plugins.forms.contraints.tables.ui;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer;
import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer.ConstraintDomain;
import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer.ConstraintNumber;

/**
 * The cell renderer for the combo box
 * @author Utente01
 *
 */
public class ConstraintComboBoxRenderer extends AbstractConstraintCellRenderer{
	
	private ConstraintNumber[] types;
	
	public ConstraintComboBoxRenderer(ConstraintDomain text){
		super();
		this.types = ConstraintNumber.getTheValues(text);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column) {
		ConstraintTableModel model = (ConstraintTableModel) table.getModel();
		ConstraintContainer currentConstCont = (ConstraintContainer) model.getRowData(row);
		ConstraintNumber currentConstr= currentConstCont.getcNumber();
		JComboBox displayCombo = new JComboBox(new Object[]{currentConstr});
		if(value != null ){
			displayCombo.setSelectedItem(0);
			displayCombo.setEnabled(table.isEnabled());
		}
		return displayCombo;
	}		
}
