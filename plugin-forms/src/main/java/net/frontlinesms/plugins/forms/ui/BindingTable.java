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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;

import net.frontlinesms.plugins.forms.data.domain.BindingContainer;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.domain.SurveyElement;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent.BindType;
import net.frontlinesms.plugins.forms.ui.components.VisualForm;

/**
 * Class modeling the Table of Bindings
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/>  
 * 
 */
public class BindingTable extends JTable {

	enum ValueType {
		CHECK, STRING, NUMERIC, LISTED_ELEMENT;
	}

	static Object[] valuesForNumeric = new Object[] { BindType.EQUALS, BindType.NOT_ACTIVE, BindType.NOT_NULL, BindType.NULL, BindType.NOT_EQUALS, BindType.GREATER_THAN,
			BindType.LESS_THAN, BindType.GREATER_EQUALS_THAN, BindType.LESS_EQUALS_THAN };
	static Object[] valuesForString = new Object[] { BindType.EQUALS, BindType.NOT_ACTIVE, BindType.NOT_NULL, BindType.NULL, BindType.NOT_EQUALS };
	static Object[] valuesForCheck = new Object[] { BindType.NOT_ACTIVE, BindType.IS_CHECKED };
	static Object[] valuesForListElements = new Object[] {BindType.EQUALS, BindType.NOT_ACTIVE, BindType.NOT_EQUALS };
	static Object[] valuesForGeneric = new Object[] { BindType.NOT_ACTIVE, BindType.NOT_NULL, BindType.NULL };

	// These are the combo box values

	public BindingTable() {
		super(new BindingTableModel());
		this.setDragEnabled(true);
		this.getTableHeader().setReorderingAllowed(false);
		TableColumn col = this.getColumnModel().getColumn(1);

		BindType[] types = BindType.values();
		/*
		 * String[] values = new String[types.length]; for (int i = 0; i <
		 * values.length; i++) { values[i] = types[i].toString(); }
		 */

		this.addMouseListener(new MouseAdapter() {
			/**
			 * This mouseClicked implementation is used to highglight a
			 * component in the preview panel
			 * 
			 */
			@Override
			public void mouseClicked(MouseEvent arg0) {
				BindingTableModel model = (BindingTableModel) BindingTable.this.getModel();
				model.highlightBinding(BindingTable.this.getSelectedRow());
			}

		});

		col.setCellEditor(new BindTypeComboBoxEditor(types));
		col.setCellRenderer(new BindTypeRenderer());
		/*this line enables the dropdown for component selection in the binding table˙
		this.getColumnModel().getColumn(0).setCellEditor(new ComponentNameComboboxEditor());
		*/
		this.getColumnModel().getColumn(2).setCellEditor(new AlphaNumericValueEditor(""));
		this.getColumnModel().getColumn(2).setCellRenderer(new MyValueRenderer());
		this.getTableHeader().setDefaultRenderer(new ConstrainHeaderRenderer(this));// raji
		col.setPreferredWidth(130);
	}

	public void addBinding(PreviewComponent pComponent, BindingContainer bType) {
		BindingTableModel model = (BindingTableModel) this.getModel();
		model.addRow(new Object[] { pComponent, bType });
		this.validate();
		this.updateUI();
	}

	public void updateBindingType(BindType newType, int selectedRow) {
		BindingTableModel model = (BindingTableModel) this.getModel();
		// int selected = this.getSelectedRow();
		model.updateBindingType(newType, selectedRow);
		this.validate();
		this.updateUI();
	}

	public void clean() {
		BindingTableModel model = (BindingTableModel) this.getModel();
		model.clean();
	}

	/**
	 * Fabaris_a.zanchi Invoked when a Binding is removed from the component
	 * 
	 * @return an array of lenght 2 of objects. It actually returns the
	 *         previewComponent on which the binding is active and the type of
	 *         binding
	 */
	public Object[] removeSelectedBinding() {
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
	}

	/**
	 * Fabaris_a.zanchi Used to make the dropbox (combobox) clickable
	 */
	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		if (arg1 > 0)
			return true;
		else
			return true;
	}

	/**
	 * Fabaris_a.zanchi Class to model the bindtype selection dropbox
	 * 
	 * @author Fabaris_a.zanchi
	 */
	public class BindTypeComboBoxEditor extends DefaultCellEditor {

		private ValueType valueType;
		private Object[] values;

		public BindTypeComboBoxEditor(Object[] items) {
			super(new JComboBox(items));
			this.values = items;
		}

		@Override
		public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, final int arg3, final int arg4) {
			final BindingTableModel model = (BindingTableModel) BindingTable.this.getModel();
			BindingContainer currentBindCont = (BindingContainer) model.getRowData(arg3)[1];
			BindType currentBind = currentBindCont.getbType();
			JComboBox cb = null;
			if (((PreviewComponent) model.getRowData(arg3)[0]).getType() == FormFieldType.CHECK_BOX) {
				cb = new JComboBox(valuesForCheck);
				valueType = ValueType.CHECK;
			} else if ((((PreviewComponent) model.getRowData(arg3)[0]).getType() == FormFieldType.NUMERIC_TEXT_FIELD)
					|| (((PreviewComponent) model.getRowData(arg3)[0]).getType() == FormFieldType.CURRENCY_FIELD)) {
				cb = new JComboBox(valuesForNumeric);
				valueType = ValueType.NUMERIC;
			} else if ((((PreviewComponent) model.getRowData(arg3)[0]).getType() == FormFieldType.TEXT_AREA)
					|| (((PreviewComponent) model.getRowData(arg3)[0]).getType() == FormFieldType.TEXT_FIELD)
					|| (((PreviewComponent) model.getRowData(arg3)[0]).getType() == FormFieldType.PHONE_NUMBER_FIELD)
					|| (((PreviewComponent) model.getRowData(arg3)[0]).getType() == FormFieldType.EMAIL_FIELD)) {
				cb = new JComboBox(valuesForString);
				valueType = ValueType.STRING;
			} else if ((((PreviewComponent) model.getRowData(arg3)[0]).getType() == FormFieldType.RADIO_BUTTON)
					|| (((PreviewComponent) model.getRowData(arg3)[0]).getType() == FormFieldType.DROP_DOWN_LIST)) {
				valueType = ValueType.LISTED_ELEMENT;
				cb = new JComboBox(valuesForListElements);
			} else {
				cb = new JComboBox(valuesForGeneric);
			}
			cb.setSelectedItem(currentBind);
			cb.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent arg0) {
					JComboBox cb = (JComboBox) arg0.getSource();
					// note: arg3 is the row
					BindingTable.this.updateBindingType((BindType) cb.getSelectedItem(), arg3);
					cb.setFocusable(false);
					BindTypeComboBoxEditor.this.fireEditingStopped();
					if (valueType == ValueType.NUMERIC) {
						String value = (String) BindingTable.this.getModel().getValueAt(arg3, arg4 + 1);
						if (value == null || value.equals("")) {
							BindingTable.this.getModel().setValueAt(String.valueOf(0), arg3, arg4 + 1);
						}
					}
				}
			});
			return cb;
		}

	}

	/**
	 * class modeling a table cell for value of binding that only accepts
	 * numeric inputs It is designed to update binding value when text changes
	 * (instead of pressing enter)
	 * 
	 * @author Fabaris_a.zanchi
	 * 
	 */
	public class NumericValueEditor extends DefaultCellEditor {

		String currentValue;

		public NumericValueEditor(String value) {
			super(new JNumberTextField());
			this.currentValue = value;
		}

		@Override
		public Object getCellEditorValue() {
			return currentValue;
		}

		@Override
		public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, final int arg3, final int arg4) {
			BindingTableModel model = (BindingTableModel) BindingTable.this.getModel();
			BindTypeRenderer dropdownType = (BindTypeRenderer) BindingTable.this.getCellRenderer(arg3, arg4 - 1);
			if (!isEditingEnabledForBindType(dropdownType.getCurrentBType())) {
				JTextField tf = new JTextField();
				String value = (String) model.getValueAt(arg3, arg4);
				currentValue = value != null ? value : "";
				tf.setText(currentValue);
				tf.setBorder(BorderFactory.createEmptyBorder());
				tf.setEnabled(false);
				tf.setEditable(false);
				return tf;
			}
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
					changeValue(e);
				}

				public void changeValue(DocumentEvent e) {
					System.out.println("changing value");
					BindingTableModel model = (BindingTableModel) BindingTable.this.getModel();
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
			return numericField;
		}

	}

	/**
	 * class modeling a table cell for value of binding that only accepts
	 * numeric inputs It is designed to update binding value when text changes
	 * (instead of pressing enter)
	 * 
	 * @author Fabaris_a.zanchi
	 * 
	 */
	public class AlphaNumericValueEditor extends DefaultCellEditor {

		String currentValue;

		public AlphaNumericValueEditor(String value) {
			super(new JTextField());
			this.currentValue = value;
		}

		@Override
		public Object getCellEditorValue() {
			return currentValue;
		}

		@Override
		public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, final int arg3, final int arg4) {
			BindingTableModel model = (BindingTableModel) BindingTable.this.getModel();
			BindTypeRenderer dropdownType = (BindTypeRenderer) BindingTable.this.getCellRenderer(arg3, arg4 - 1);
			if (!isEditingEnabledForBindType(dropdownType.getCurrentBType())) {
				JTextField tf = new JTextField();
				String value = (String) model.getValueAt(arg3, arg4);
				currentValue = value != null ? value : "";
				tf.setText(currentValue);
				tf.setBorder(BorderFactory.createEmptyBorder());
				tf.setEnabled(false);
				tf.setEditable(false);
				return tf;
			}
			String value = (String) model.getValueAt(arg3, arg4);
			currentValue = value != null ? value : "";
			final JTextField textField = new JTextField();
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
					// changeValue();
				}

				public void changeValue(DocumentEvent e) {
					BindingTableModel model = (BindingTableModel) BindingTable.this.getModel();
					int strLength = textField.getDocument().getLength();
					try {
						String value = textField.getDocument().getText(0, strLength);
						currentValue = value;
						model.setValueAt(value, arg3, arg4);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			return textField;
		}
	}

	/**
	 * Models a combo box that will be displayed in the binding table if the
	 * binding is on a dropo down or radio button element. The combo box will
	 * display elements in survey associated with element
	 * 
	 * @author Fabaris_a.zanchi
	 * 
	 */
	public class SurveyElementComboBoxEditor extends DefaultCellEditor {

		private Object[] values;
		private String currentValue;

		public SurveyElementComboBoxEditor(Object[] items) {
			super(new JComboBox(items));
			this.values = items;
		}

		@Override
		public Object getCellEditorValue() {
			return currentValue;
		}

		@Override
		public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, final int arg3, final int arg4) {
			BindingContainer bcont = (BindingContainer) ((BindingTableModel) BindingTable.this.getModel()).getRowData(arg3)[1];
			JComboBox component = new JComboBox(values);
			for (int i = 0; i < component.getItemCount(); i++) {
				if (((SurveyElement) component.getItemAt(i)).getValue().equals(bcont.getValue())) {
					component.setSelectedIndex(i);
				}
			}
			// component.setSelectedItem(bcont.getValue());
			currentValue = bcont.getValue();
			component.addPopupMenuListener(new PopupMenuListener() {
				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				}

				public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
					SurveyElementComboBoxEditor.this.fireEditingStopped();
				}

				public void popupMenuCanceled(PopupMenuEvent e) {
				}
			});
			component.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					JComboBox cb = (JComboBox) arg0.getSource();
					currentValue = ((SurveyElement) cb.getSelectedItem()).getValue();
				}
			});

			return component;
		}

	}

	/**
	 * Class to model the rendering for the inactive ComboBox for bindtype
	 * 
	 * @author Fabaris_a.zanchi
	 * 
	 */
	public class BindTypeRenderer extends DefaultTableCellRenderer {

		private BindType currentBType;

		@Override
		public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
			BindingTableModel model = (BindingTableModel) BindingTable.this.getModel();
			BindingContainer currentBindCont = (BindingContainer) model.getRowData(arg4)[1];
			BindType currentBind = currentBindCont.getbType();
			currentBType = currentBindCont.getbType();
			JComboBox displayCombo = new JComboBox(new Object[] { currentBind });
			displayCombo.setSelectedItem(0);
			return displayCombo;
		}

		public BindType getCurrentBType() {
			return this.currentBType;
		}

	}

	/**
	 * Class modeling a table cell displaying the current relevant bind value It
	 * is designed to show greyed out values when the selected relevant type
	 * doesn't accept a value
	 * 
	 * @author Fabaris_a.zanchi
	 * 
	 */
	public class MyValueRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			BindingTableModel model = (BindingTableModel) BindingTable.this.getModel();
			String bindValue = ((BindingContainer) model.getRowData(row)[1]).getValue();
			JTextField tf = new JTextField();
			tf.setBorder(BorderFactory.createEmptyBorder());
			tf.setText(bindValue);
			BindType bType = ((BindingContainer) model.getRowData(row)[1]).getbType();
			if (!isEditingEnabledForBindType(bType)) {
				tf.setEditable(false);
				tf.setEnabled(false);
			}
			return tf;
		}

	}

	public class ConstrainHeaderRenderer extends DefaultTableCellRenderer {

		DefaultTableCellRenderer renderer;

		public ConstrainHeaderRenderer(JTable table) {
			renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
		}

		@Override
		public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
			Component c = renderer.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
			renderer.setEnabled(arg0.isEnabled());
			arg0.getTableHeader().setEnabled(arg0.isEnabled());
			c.setEnabled(arg0.isEnabled());
			return c;
		}
	}

	/**
	 * Class modeling a combobox when editing the "name" cell of a visibility
	 * condition binding. Displays the list of all the components on which you
	 * can set the condition
	 * 
	 * @author Fabaris_a.zanchi
	 * 
	 */
	public class ComponentNameComboboxEditor extends DefaultCellEditor {

		private PreviewComponent currentValue;

		public ComponentNameComboboxEditor() {
			super(new JTextField());

		}

		@Override
		public Object getCellEditorValue() {
			return currentValue;
		}

		@Override
		public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, int arg3, int arg4) {

			List<PreviewComponent> componentsWithName = new ArrayList<PreviewComponent>();
			if (!FormsUiController.getInstance().getMainFrame().getDrawingPanel().isRepeatableOpen()) {
				PreviewComponent selected = FormsUiController.getInstance().getMainFrame().getSelectedComponent();
				VisualForm vForm = FormsUiController.getInstance().getMainFrame().getDrawingPanel().getCurrent();

				for (PreviewComponent comp : vForm.getComponents()) {
					if (comp.getComponent().getName() != null && !comp.getComponent().getName().equals("")) {
						if (selected != comp)
							componentsWithName.add(comp);
						else
							break;
					}
				}
			} else {
				PreviewComponent repeContainer = FormsUiController.getInstance().getMainFrame().getDrawingPanel().getPnRepeatablePreview().getCurrentRepeatableSection();
				VisualForm vForm = FormsUiController.getInstance().getMainFrame().getDrawingPanel().getCurrent();
				PreviewComponent selected = FormsUiController.getInstance().getMainFrame().getSelectedComponent();
				for (PreviewComponent comp : vForm.getComponents()) {
					if (comp.getComponent().getName() != null && !comp.getComponent().getName().equals("")) {
						if (repeContainer != comp)
							componentsWithName.add(comp);
						else
							break;
					}
				}
				for (PreviewComponent comp : repeContainer.getRepeatables()) {
					if (comp.getComponent().getName() != null && !comp.getComponent().getName().equals("")) {
						if (selected != comp)
							componentsWithName.add(comp);
						else
							break;
					}
				}
				
			}
			final JComboBox components = new JComboBox();
			DefaultComboBoxModel model = new DefaultComboBoxModel(componentsWithName.toArray());
			components.setModel(model);
			BindingTableModel tabModel = (BindingTableModel) BindingTable.this.getModel();
			components.setSelectedItem(tabModel.getRowData(arg3)[0]);
			components.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent arg0) {
					currentValue = (PreviewComponent) components.getSelectedItem();
					ComponentNameComboboxEditor.this.fireEditingStopped();
				}
			});
			components.addPopupMenuListener(new PopupMenuListener() {

				public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
					// TODO Auto-generated method stub

				}

				public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
					ComponentNameComboboxEditor.this.fireEditingStopped();

				}

				public void popupMenuCanceled(PopupMenuEvent arg0) {
					// TODO Auto-generated method stub

				}
			});
			return components;
		}

	}

	@Override
	public TableCellEditor getCellEditor(int arg0, int arg1) {
		if (arg1 != 2)
			return super.getCellEditor(arg0, arg1);
		else {
			PreviewComponent pc = (PreviewComponent) ((BindingTableModel) this.getModel()).getRowData(arg0)[0];
			if (pc.getType() == FormFieldType.DROP_DOWN_LIST || pc.getType() == FormFieldType.RADIO_BUTTON) {
				return new SurveyElementComboBoxEditor(pc.getSurvey().getValues().toArray());
			} else {
				BindTypeComboBoxEditor boxEditor = (BindTypeComboBoxEditor) this.getCellEditor(arg0, arg1 - 1);
				if (boxEditor.valueType == ValueType.NUMERIC) {
					BindingTableModel model = (BindingTableModel) BindingTable.this.getModel();
					return new NumericValueEditor((String) model.getValueAt(arg0, arg1));
				}
			}
			return super.getCellEditor(arg0, arg1);
		}
	}

	/**
	 * Fabaris_A.zanchi
	 * 
	 * @param bType
	 * @return
	 */
	public static boolean isEditingEnabledForBindType(BindType bType) {
		if (bType == BindType.NOT_ACTIVE || bType == BindType.NULL || bType == BindType.NOT_NULL || bType == BindType.IS_CHECKED) {
			return false;
		} else
			return true;
	}

}
