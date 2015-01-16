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
package net.frontlinesms.ui.handler.importexport;

import java.io.UnsupportedEncodingException;

import net.frontlinesms.csv.CsvImporter;
import net.frontlinesms.csv.CsvParseException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

/**
 * @author aga
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/> 
 * 
 */

public abstract class ImportDialogHandler extends ImportExportDialogHandler {
	/** UI XML File Path: This is the outline for the dialog for IMPORTING */
	private static final String UI_FILE_IMPORT_WIZARD_FORM = "/ui/core/importexport/importWizardForm.xml";

	/** i18n Text Key: "The CSV file couldn't be parsed. Please check the format." */
	private static final String I18N_FILE_NOT_PARSED = "importexport.file.not.parsed";
	protected static final String I18N_IMPORT_SUCCESSFUL = "importexport.import.successful";
	/** I18n Text Key: TODO document */
	private static final String MESSAGE_IMPORT_TASK_FAILED = "message.import.failed";
	/** I18n Text Key: TODO document */
	private static final String MESSAGE_IMPORT_TASK_SUCCESSFUL = "message.import.successful";

	private static final String COMPONENT_TB_VALUES = "tbValues";
	private static final String COMPONENT_PN_VALUES_TABLE = "pnValuesTable";
	protected static final String COMPONENT_PN_CHECKBOXES = "pnInfo";
	protected static final String COMPONENT_PN_CHECKBOXES_2 = "pnInfo2";
	
//> STATIC CONSTANTS
	public enum EntityType {
		/** Export entity type: {@link Contact} */
		CONTACTS,
		/** Export entity type: {@link Message} */
		MESSAGES,
		/**Fabaris_a.zanchi Export entity type: {@link Form} */
		FORMS,
		/**Fabaris_raji Export entity type: {@link Form} */
		SqlImportDialogHandler;
		
	}

	/** The type of object we are dealing with, one of {@link #S}, {@link #TYPE_KEYWORD}, {@link #TYPE_MESSAGE}. */
	protected final EntityType type;
	
//> PROPERTIES
	
//> CONSTRUCTORS
	public ImportDialogHandler(UiGeneratorController ui, EntityType type) {
		super(ui);
		this.type = type;
	}	
	
//> ACCESSORS
	@Override
	String getDialogFile() {
		return UI_FILE_IMPORT_WIZARD_FORM;
	}

//> UI EVENT METHODS
	abstract void doSpecialImport(String dataPath) throws CsvParseException;
	
	public void openChooseComplete(String filePath) {
		this.uiController.setText(uiController.find(this.wizardDialog, "tfDirectory"), filePath);
		this.loadCsvFile(filePath);
	}
	
	/**
	 * Executes the import action.
	 * @param dataPath The path to the file to import data from.
	 * @throws UnsupportedEncodingException 
	 */
	public void doImport(String dataPath) throws UnsupportedEncodingException {
		log.trace("ENTER");
		// Make sure that a file has been selected to import from
		if (dataPath.equals("")) {
			log.debug("dataPath is blank.");
			uiController.alert(InternationalisationUtils.getI18nString(MESSAGE_NO_FILENAME));
			log.trace("EXIT");
			return;
		}
		
		try {
			doSpecialImport(dataPath);
			
			uiController.setStatus(InternationalisationUtils.getI18nString(MESSAGE_IMPORT_TASK_SUCCESSFUL));
			uiController.removeDialog(wizardDialog);
		} catch(CsvParseException ex) {
			log.debug(InternationalisationUtils.getI18nString(MESSAGE_IMPORT_TASK_FAILED), ex);
			uiController.alert(InternationalisationUtils.getI18nString(MESSAGE_IMPORT_TASK_FAILED) + ": " + ex.getMessage());
		}
		log.trace("EXIT");
	}
	
	public void columnCheckboxChanged() {
		if(this.getImporter() != null) {
			refreshValuesTable();
		}
	}
	
//> INSTANCE HELPER METHODS
	protected abstract CsvImporter getImporter();
	protected abstract void setImporter(String filename) throws CsvParseException;
	protected abstract void appendPreviewHeaderItems(Object header);
	protected abstract Object[] getPreviewRows();
	
	private void loadCsvFile(String filename) {
		try {
			setImporter(filename);
		} catch (CsvParseException e) {
			this.uiController.alert(InternationalisationUtils.getI18nString(I18N_FILE_NOT_PARSED));
		}
		
		Object pnValuesTable = this.uiController.find(this.wizardDialog, COMPONENT_PN_VALUES_TABLE);
		this.uiController.setVisible(pnValuesTable, true);
		this.refreshValuesTable();
	}
	
	private Object getPreviewHeader() {
		Object header = this.uiController.createTableHeader();

		Object iconHeader = this.uiController.createColumn("", "");
		this.uiController.setWidth(iconHeader, 20);
		this.uiController.add(header, iconHeader);
		
		appendPreviewHeaderItems(header);
		
		return header;
	}
	
	private void refreshValuesTable() {
		Object pnValuesTable = this.uiController.find(this.wizardDialog, COMPONENT_PN_VALUES_TABLE);
		if (pnValuesTable != null) {
			// Clear the table
			Object valuesTable = this.uiController.find(this.wizardDialog, COMPONENT_TB_VALUES);
			this.uiController.removeAll(valuesTable);
			
			this.uiController.add(valuesTable, getPreviewHeader());

			for(Object previewRow : getPreviewRows()) {
				this.uiController.add(valuesTable, previewRow);
			}
		}
	}
}
