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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.h2.util.FileUtils;

import net.frontlinesms.csv.CsvExporter;
import net.frontlinesms.csv.CsvImporter;
import net.frontlinesms.csv.CsvParseException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.plugins.PluginController;

import net.frontlinesms.plugins.forms.FormsPluginController;

import net.frontlinesms.plugins.forms.data.FormHandlingException;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.domain.FormResponse;
import net.frontlinesms.plugins.forms.data.domain.ResponseValue;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.handler.core.FileChooser;

/**
 * This class handles the form import dialog.
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/> 
 * 
 */
public class FormImportDialogHandler extends ImportDialogHandler {

	private Object exportDialog;
	private boolean openChooseCompleted = false;
	private String filter;

	public FormImportDialogHandler(UiGeneratorController ui) {
		super(ui, EntityType.FORMS);
		// TODO Auto-generated constructor stub
	}

	@Override
	void doSpecialImport(String dataPath) throws CsvParseException {
		// TODO Auto-generated method stub

	}

	@Override
	protected CsvImporter getImporter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setImporter(String filename) throws CsvParseException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void appendPreviewHeaderItems(Object header) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Object[] getPreviewRows() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	String getWizardTitleI18nKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	String getOptionsFilePath() {
		return UI_FILE_OPTIONS_PANEL_FORM;
	}

	/**
	 * This method "breaks" the execution flow and takes the responsibility to
	 * create a custom dialog window. instead of letting superclass(es) do the
	 * work
	 */
	@Override
	public void showWizard() {
		exportDialog = uiController.loadComponentFromFile("/ui/core/importexport/pnImportForm.xml", this);
		uiController.add(exportDialog);
		// populateList(filter);
	}

	/**
	 * Method call when the "Import" button is pressed
	 * 
	 */
	@Override
	public void doImport(String filename) {
		// Object selectedRow =
		// uiController.getSelectedItem(uiController.find("formImport_contactList"));
		// Contact selectedContact = (Contact)
		// uiController.getAttachedObject(selectedRow);

		/* Reads the xml content of the file */
		File formFile = new File(filename);
		byte[] buffer = new byte[(int) formFile.length()];
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new FileInputStream(formFile));
		} catch (FileNotFoundException e) {
			uiController.alert(e.getMessage());
		}
		try {
			dis.readFully(buffer);
		} catch (IOException e) {
			uiController.alert(e.getMessage());
		}
		String xmlContent = new String(buffer);

		/* Retrieves the original form we are importing by the id */
		Long formId = null;
		try {
			formId = Long.parseLong((FormsPluginController.getFormIdFromXml(xmlContent)));
		} catch (Exception e) {
			uiController.alert(e.getMessage());
		}

		List<ResponseValue> rValues = new ArrayList<ResponseValue>();
		Form returnedForm = uiController.getFrontlineController().getFormDao().getFromId(formId);
		for (FormField ff : returnedForm.getFields()) {
			try {
				
				if (ff.getType() == FormFieldType.SEPARATOR)
					rValues.add(new ResponseValue(""));
				else if (ff.getType() == FormFieldType.REPEATABLES) {
				/*	// manage repeatables
					ResponseValue containerValue = new ResponseValue(FormsPluginController.getFormDataXml(xmlContent, ff));
					rValues.add(containerValue);
					boolean goOn = true;
					int index = 0;
					while (goOn == true) {
						String[] repContents = new String[ff.getRepetables().size()];
						for (int i = 0; i < ff.getRepetables().size(); i++) {
							repContents[i] = FormsPluginController.getFormRepeatableDataXml(xmlContent, ff, ff.getRepetables().get(i), index);
							i++;
						}
						// check if array is made of empty strings
						goOn = false;
						for (int b = 0; b < repContents.length; b++) {
							System.out.println(repContents[b]);
							if (repContents[b] != null)
								goOn = true;
						}
						if (goOn) {
							for (String s : repContents) {
								// creates a new response value with the string
								// value of the nested component.
								// if null it creates an empty response value
								containerValue.addRepetableValue(new ResponseValue(s == null ? "" : s));
							}
						}
						index++;
					}*/
					
					//Response Value for repeatables container. Writes the number of repetitions (could be useful)
					ResponseValue containerValue = null;
					int numberOfReps;
					try {
						//30.10.2013
						//numberOfReps = FormsPluginController.countRepetitionOfRepeatable(xmlContent, ff.getId()+"_"+ff.getPositionIndex());
						
						numberOfReps = FormsPluginController.countRepetitionOfRepeatable(xmlContent, ff.getName()+"_"+ff.getPositionIndex());
					} catch (Exception e) {
						throw new FormHandlingException(e.getMessage());
					}
					
					containerValue = new ResponseValue(String.valueOf(numberOfReps));
					
					//populate repeatable items values
					int i = 0;
					List<FormField> repeatableSection = ff.getRepetables();
					while (i < numberOfReps) {
						List<String> repValues = null;
						try {
							//30.10.2013
							//repValues = FormsPluginController.getValuesForRepetition(xmlContent, ff.getId()+"_"+ff.getPositionIndex(), repeatableSection, i);
							
							repValues = FormsPluginController.getValuesForRepetition(xmlContent, ff.getName()+"_"+ff.getPositionIndex(), repeatableSection, i);
						} catch (Exception e) {
							throw new FormHandlingException(e.getMessage());
						}
						for (String val : repValues) {
							containerValue.addRepetableValue(new ResponseValue(val));
						}
						i++;
					}
					rValues.add(containerValue);
				} else
					rValues.add(new ResponseValue(FormsPluginController.getFormDataXml(xmlContent, ff)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/* Saves the form response */
		FormResponse fResponse = new FormResponse("andrea", returnedForm, rValues);
		uiController.getFrontlineController().getFormResponseDao().saveResponse(fResponse);
		uiController.removeDialog(exportDialog);

	}

	@Override
	public void openChooseComplete(String filePath) {
		uiController.setText(uiController.find("tfFilename"), filePath);
		/*
		 * checks if the filepath points to an xml file to activate if
		 * (filePath.endsWith(".xml"))
		 */
		this.openChooseCompleted = true;
		validateSelection();

	}

	/**
	 * Override to use a filter for xml files
	 * 
	 */
	@Override
	public void showOpenModeFileChooser() {
		FileChooser fc = FileChooser.createFileChooser(this.uiController, this, "openChooseComplete");
		fc.setFileFilter(new FileNameExtensionFilter("FrontlineSMS Exported Form (" + ".xml" + ")", "xml"));
		fc.show();
	}

	/**
	 * Method to filter contacts
	 * 
	 * @param filter
	 */
	public void setContactFilter(String filter) {
		this.filter = filter;
	}

	public void filterContacts() {
		populateList(this.filter);
	}

	/**
	 * Support method to pouplate the contacts list
	 * 
	 */
	private void populateList(String filter) {
		uiController.removeAll(uiController.find("formImport_contactList"));
		ContactDao cDao = uiController.getFrontlineController().getContactDao();
		List<Contact> contacts = null;
		if (this.filter == null)
			contacts = cDao.getAllContacts();
		else {
			int count = cDao.getContactsFilteredByNameCount(filter);
			contacts = cDao.getContactsFilteredByName(filter, 0, count);
		}

		Object contactsTable = uiController.find("formImport_contactList");
		for (Contact c : contacts)
			uiController.add(contactsTable, uiController.createListItem(c));
	}

	/**
	 * This method is called when a contact is selected and it enables the
	 * import button.
	 */
	public void validateSelection() {
		if (openChooseCompleted)
			uiController.setEnabled(uiController.find("btDoImport"), true);
	}

}
