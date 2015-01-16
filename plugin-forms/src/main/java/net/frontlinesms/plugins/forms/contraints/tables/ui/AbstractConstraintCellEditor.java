package net.frontlinesms.plugins.forms.contraints.tables.ui;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public abstract class AbstractConstraintCellEditor extends DefaultCellEditor {

	public AbstractConstraintCellEditor(JComboBox comboBox) {
		super(comboBox);
		// TODO Auto-generated constructor stub
	}
	
	public AbstractConstraintCellEditor(JCheckBox checkBox) {
		super(checkBox);
		// TODO Auto-generated constructor stub
	}
	
	public AbstractConstraintCellEditor(JTextField textField) {
		super(textField);
		
	}
	
}
