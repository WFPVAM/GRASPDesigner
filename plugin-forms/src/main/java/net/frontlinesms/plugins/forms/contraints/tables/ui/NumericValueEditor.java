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

import net.frontlinesms.plugins.forms.ui.JNumberTextField;

public class NumericValueEditor extends AbstractConstraintCellEditor {

	String currentValue;
	
	private ConstraintTableModel model;
	
	public NumericValueEditor(ConstraintTableModel model) {
		super(new JNumberTextField());
		((JTextField)this.editorComponent).setText(currentValue);
		this.model = model;
	}
	
	public void setCurrentValue(String currentValue){
		this.currentValue = currentValue;
	}
	

	@Override
	public Object getCellEditorValue() {
		return currentValue;
	}

	@Override
	public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, final int arg3, final int arg4) {
		final JTextField numericField = new JNumberTextField();
		numericField.setText(currentValue);
		numericField.setBorder(BorderFactory.createEmptyBorder());

		numericField.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				changeValue(e);

			}
			public void insertUpdate(DocumentEvent e) {
				changeValue(e);
			}
			public void changedUpdate(DocumentEvent e) {
				// changeValue();
			}
			public void changeValue(DocumentEvent e) {
				int strLength = numericField.getDocument().getLength();
				try {
					String value = numericField.getDocument().getText(0, strLength);
					currentValue = value;
					model.setValueAt(value, arg3, arg4);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		if(currentValue == null){
			numericField.setText((String)arg1);
		}else{
			numericField.setText(currentValue);
		}
		return numericField;
	}
}