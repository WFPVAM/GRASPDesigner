package net.frontlinesms.plugins.forms.properties.tables.ui;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import net.frontlinesms.plugins.forms.ui.MyTableModel;

public class TableLabelPropertyCellEditor extends DefaultCellEditor{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String currentValue;
	
	public TableLabelPropertyCellEditor(String currentValue) {
		super(new JTextField(currentValue));
		this.currentValue = currentValue;
	}
	
	@Override
	public Object getCellEditorValue() {
		return currentValue;
	}
	
	@Override
	public Component getTableCellEditorComponent(final JTable table, Object value,boolean isSelected,final int row,final int column) {
		MyTableModel model = (MyTableModel) table.getModel();
		model.setValueAt(currentValue, row, column);
		final JTextField textField = new JTextField();
		String val = "";
		if( model.getValueAt(row, column) != null){
			val = (String)model.getValueAt(row, column);
		}
		textField.setText((String)value);
		textField.setEditable(true);
		textField.setDocument(new LabelPropertyFieldDocument(400));
		textField.getDocument().addDocumentListener(new DocumentListener() {
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
				MyTableModel model = (MyTableModel) table.getModel();
				int strLength = textField.getDocument().getLength();
				try {
					String value = textField.getDocument().getText(0, strLength);
					currentValue = value;
					model.setValueAt(value, row, column);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		if(currentValue == null){
			textField.setText((String)value);
		}else{
			textField.setText(currentValue);
		}
		return textField;
	}

}
