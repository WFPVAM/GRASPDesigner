package net.frontlinesms.plugins.forms.contraints.tables.ui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

public class AlphaNumericValueRenderer extends AbstractConstraintCellRenderer implements TableCellRenderer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5016106030157325187L;
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		JTextField field = new JTextField();
		field.setText((String)value);
		return field;
	}

}
