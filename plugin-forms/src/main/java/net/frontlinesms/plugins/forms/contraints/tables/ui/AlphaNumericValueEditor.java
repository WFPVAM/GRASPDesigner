package net.frontlinesms.plugins.forms.contraints.tables.ui;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class AlphaNumericValueEditor extends AbstractConstraintCellEditor{
	String currentValue;

	public AlphaNumericValueEditor(String value) {
		super(new JTextField(value));
			this.currentValue = value;
	}
	
	@Override
	public Object getCellEditorValue() {
		return currentValue;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean arg2, final int arg3, final int arg4) {
		final JTextField textField = new JTextField();
		final JTable t = table;
		ConstraintTableModel model = (ConstraintTableModel) table.getModel();
		String val = (String) model.getValueAt(arg3, arg4);
		currentValue = val != null ? val : ""; 
		textField.setText(currentValue);
		textField.setBorder(BorderFactory.createEmptyBorder());
		
		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				changeValue(e);
			}

			public void insertUpdate(DocumentEvent e) {
				changeValue(e);
			}

			public void changedUpdate(DocumentEvent e) {
			}

			public void changeValue(DocumentEvent e) {
				ConstraintTableModel model = (ConstraintTableModel) t.getModel();
				int strLength = textField.getDocument().getLength();
				try {
					String value = textField.getDocument().getText(0, strLength);
					currentValue = value;
					model.setValueAt(currentValue, arg3, arg4);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		return textField;
	}
}