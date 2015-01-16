package net.frontlinesms.plugins.forms.contraints.tables.ui;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer;
import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer.ConstraintDomain;
import net.frontlinesms.plugins.forms.data.domain.ConstraintContainer.ConstraintNumber;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.ui.FormsUiController;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;

public class ConstraintTable extends JTable {

	public ConstraintTable() {
		super(new ConstraintTableModel());
		this.getTableHeader().setReorderingAllowed(false);
		TableColumn col = this.getColumnModel().getColumn(0);
		this.getTableHeader().setDefaultRenderer(
				new ConstrainHeaderRenderer(this));
	}

	// public abstract void addCellRendererAndEditor(TableColumn col);

	public void addConstrainToDisplay(ConstraintContainer cont) {
		ConstraintTableModel model = (ConstraintTableModel) this.getModel();
		model.addConstraintToDisplay(cont);
	}

	/**
	 * Fabaris_A.zanchi Changes the type of the condition at a given table row
	 * 
	 * @param type
	 * @param row
	 */
	public void updateConstrainType(ConstraintNumber type, int row) {
		ConstraintTableModel model = (ConstraintTableModel) this.getModel();
		model.updateConstrainType(type, row);
	}

	public void removeSelectedConstraint() {
		int index = this.getSelectedRow();
		if (index != -1) {
			ConstraintTableModel model = (ConstraintTableModel) this.getModel();
			model.removeConstraintAtRow(index);
		}
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return true;
	}

	/**
	 * Fabaris_a.zanchi Add a new default condition that can be further
	 * modified.
	 */
	public void addDefaultCostrain() {
		ConstraintTableModel model = (ConstraintTableModel) this.getModel();
		PreviewComponent selected = FormsUiController.getInstance()
				.getSelectedComponent();
		if (selected.getType().equals(FormFieldType.TEXT_AREA) || selected.getType().equals(FormFieldType.TEXT_FIELD)){
			model.addRow(new Object[] { ConstraintNumber.MAXIMUM_NUMBER_OF_CHARACTER, "" });
		}else{
			model.addRow(new Object[] { ConstraintNumber.EQUALS, "" });
		}
	}

	public void clean() {
		ConstraintTableModel model = (ConstraintTableModel) this.getModel();
		model.clean();// getCellEditor(int row, int column)
		if (super.getCellEditor() != null && isEditing()) {
			// try to stop cell editing, and failing that, cancel it
			super.getCellEditor().cancelCellEditing();
//			setCellEditor(null);
//			setCellEditor(this.getCellEditor(getSelectedRow(),
//					getSelectedColumn()));

		}
	}

	@Override
	public javax.swing.table.TableCellRenderer getCellRenderer(int row,
			int column) {
		PreviewComponent selected = FormsUiController.getInstance()
				.getSelectedComponent();
		if(selected != null){
			if (column != 0) {
				if (selected != null && (selected.getType() == FormFieldType.NUMERIC_TEXT_FIELD
						|| selected.getType() == FormFieldType.CURRENCY_FIELD)) {
					return new NumericValueRenderer();
				} else if (selected != null && ( selected.getType() == FormFieldType.TEXT_AREA
						|| selected.getType() == FormFieldType.TEXT_FIELD)) {
					return new AlphaNumericValueRenderer();
				} else {
					return super.getCellRenderer(row, column);
				}
			} else {
				if (selected != null && (selected.getType() == FormFieldType.NUMERIC_TEXT_FIELD
						|| selected.getType() == FormFieldType.CURRENCY_FIELD)) {
					return new ConstraintComboBoxRenderer(ConstraintDomain.NUMERIC);
				} else if (selected != null && ( selected.getType() == FormFieldType.TEXT_AREA
												|| selected.getType() == FormFieldType.TEXT_FIELD)) {
					return new ConstraintComboBoxRenderer(ConstraintDomain.TEXT);
				} else
					return super.getCellRenderer(row, column);
			}
		}
		return super.getCellRenderer(row, column);
	}

	@Override
	public void columnMoved(TableColumnModelEvent e) {
		saveRunningEditorResults();
		super.columnMoved(e);
	}

	private void saveRunningEditorResults() {
		// TableCellEditor cellEditor = getCellEditor();
		// if (cellEditor != null) {
		// if (!cellEditor.stopCellEditing()) {
		// cellEditor.cancelCellEditing();
		// }
		// }
	}

	@Override
	public void columnMarginChanged(ChangeEvent e) {
		saveRunningEditorResults();
		super.columnMarginChanged(e);
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		PreviewComponent selected = FormsUiController.getInstance()
				.getSelectedComponent();
		if(selected != null){
			if (column != 0) {
				if (selected.getType() == FormFieldType.NUMERIC_TEXT_FIELD
						|| selected.getType() == FormFieldType.CURRENCY_FIELD) {
					return new NumericValueEditor(
							(ConstraintTableModel) this.getModel());
				} else if (selected.getType() == FormFieldType.TEXT_AREA
						|| selected.getType() == FormFieldType.TEXT_FIELD) {
					return new AlphaNumericValueEditor((String) this.getModel().getValueAt(row, column));
				} else {
					return super.getCellEditor(row, column);
				}
			} else {
				if (selected.getType() == FormFieldType.NUMERIC_TEXT_FIELD
						|| selected.getType() == FormFieldType.CURRENCY_FIELD) {
					return new ConstraintComboBoxEditor(
							(ConstraintTableModel) this.getModel(),
							ConstraintDomain.NUMERIC);
				} else if (selected.getType() == FormFieldType.TEXT_AREA
						|| selected.getType() == FormFieldType.TEXT_FIELD) {
					return new ConstraintComboBoxEditor(
							(ConstraintTableModel) this.getModel(),
							ConstraintDomain.TEXT);
				} else
					return super.getCellEditor(row, column);
			}
		}
		return super.getCellEditor(row, column);
	}
}