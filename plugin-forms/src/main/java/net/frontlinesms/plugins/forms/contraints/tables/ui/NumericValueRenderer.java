package net.frontlinesms.plugins.forms.contraints.tables.ui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextField;

public class NumericValueRenderer extends AbstractConstraintCellRenderer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4634972510950463620L;

	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
		JTextField field = new JTextField();
		field.setText((String)value);
		return field;
	}
}
