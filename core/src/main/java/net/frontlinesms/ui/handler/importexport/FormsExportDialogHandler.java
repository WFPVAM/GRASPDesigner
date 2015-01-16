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

import java.awt.Component;
import java.awt.Image;
import java.io.IOException;
import java.util.List;

import thinlet.Thinlet;

import net.frontlinesms.data.domain.Group;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.repository.FormDao;
import net.frontlinesms.plugins.forms.ui.FormIcon;
import net.frontlinesms.plugins.forms.ui.FormsEditorDialog;
import net.frontlinesms.plugins.forms.ui.components.FComponent;
import net.frontlinesms.ui.Icon;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;
/**
* @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
* www.fabaris.it <http://www.fabaris.it/> 
* 
*/
public class FormsExportDialogHandler extends ExportDialogHandler<Form> {
	
	private static final String I18N_PLUGINS_FORMS_NOT_SET = "plugins.forms.not.set";
	private static final String I18N_PLUGINS_FORMS_GROUP = "plugins.forms.group";

	public FormsExportDialogHandler(UiGeneratorController ui) {
		super(Form.class, ui);
	}

	@Override
	protected void doSpecialExport(String dataPath) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doSpecialExport(String dataPath, List<Form> selected) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	String getWizardTitleI18nKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	String getOptionsFilePath() {
		return this.UI_FILE_OPTIONS_PANEL_FORM;
	}
	
	/**Fabaris_a.zanchi Overriden method to open an Export Form in a different way.
	 * This method is called by the FrontLineSms dialog system.
	 * When the flow arrives here it "breaks" the rule by creating a custom View (Jframe) to export Forms.
	 * The reason is that the standard Frontline dialog system is designed to export all of the selected components (e.g all contacts, 
	 * all keywords, etc), while we want the export just some forms selecting them in a list. So it's likely easier to implement
	 * this in a different way.
	 * 
	 */
	@Override
	public void showWizard() {
		//INSERT STUFF HERE 
		Object formList = uiController.find("formsList");
		Object window = uiController.loadComponentFromFile("/ui/core/importexport/pnExportForm.xml", this);
	}
	
	private Object getNode(Form form) {
		log.trace("ENTER");
		// Create the node for this form

		log.debug("Form [" + form.getName() + "]");

		Image icon = this.uiController.getIcon(form.isFinalised() ? FormIcon.FORM_FINALISED : FormIcon.FORM);
		Object node = this.uiController.createNode(form.getName(), form);
		this.uiController.setIcon(node, Thinlet.ICON, icon);

		// Create a node showing the group for this form
		Group g = form.getPermittedGroup();
		String groupName = g == null ? InternationalisationUtils.getI18nString(I18N_PLUGINS_FORMS_NOT_SET) : g
				.getName();
		Object groupNode = this.uiController.createNode(InternationalisationUtils.getI18nString(I18N_PLUGINS_FORMS_GROUP, groupName),
				null);
		this.uiController.setIcon(groupNode, Icon.GROUP);
		this.uiController.add(node, groupNode);

		for (FormField field : form.getFields()) {
			Object child = this.uiController.createNode(field.getLabel(), field);
			this.uiController.setIcon(child, Thinlet.ICON, getIcon(field.getType()));
			this.uiController.add(node, child);
		}
		log.trace("EXIT");
		return node;
	}
	
	/**
	 * Gets the icon for a particular {@link FComponent}.
	 * 
	 * @param fieldType
	 * @return icon to use for a particular {@link FComponent}.
	 */
	public Image getIcon(FormFieldType fieldType) {
		if (fieldType == FormFieldType.CHECK_BOX)
			return this.uiController.getIcon(FormIcon.CHECKBOX);
		/*if (fieldType == FormFieldType.CURRENCY_FIELD)
			return this.uiController.getIcon(FormIcon.CURRENCY_FIELD);*/
		if (fieldType == FormFieldType.DATE_FIELD)
			return this.uiController.getIcon(FormIcon.DATE_FIELD);
		if (fieldType == FormFieldType.EMAIL_FIELD)
			return this.uiController.getIcon(FormIcon.EMAIL_FIELD);
		if (fieldType == FormFieldType.NUMERIC_TEXT_FIELD)
			return this.uiController.getIcon(FormIcon.NUMERIC_TEXT_FIELD);
		/*if (fieldType == FormFieldType.PASSWORD_FIELD)
			return this.uiController.getIcon(FormIcon.PASSWORD_FIELD);*/
		if (fieldType == FormFieldType.PHONE_NUMBER_FIELD)
			return this.uiController.getIcon(FormIcon.PHONE_NUMBER_FIELD);
		if (fieldType == FormFieldType.TEXT_AREA)
			return this.uiController.getIcon(FormIcon.TEXT_AREA);
		if (fieldType == FormFieldType.TEXT_FIELD)
			return this.uiController.getIcon(FormIcon.TEXT_FIELD);

		// Fabaris_Raji
		if (fieldType == FormFieldType.DROP_DOWN_LIST)
			return this.uiController.getIcon(FormIcon.DROP_DOWN_LIST);
		// Fabaris_Raji
		if (fieldType == FormFieldType.RADIO_BUTTON)
			return this.uiController.getIcon(FormIcon.RADIO_BUTTON);
		// Fabaris_Raji
		if (fieldType == FormFieldType.SEPARATOR)
			return this.uiController.getIcon(FormIcon.SEPARATOR);
		if (fieldType == FormFieldType.GEOLOCATION)
			return this.uiController.getIcon(FormIcon.GEOLOCATION);
		if (fieldType == FormFieldType.BARCODE)
			return this.uiController.getIcon(FormIcon.BARCODE);
		
		if (fieldType == FormFieldType.IMAGE)
			return this.uiController.getIcon(FormIcon.IMAGE);
                
                if (fieldType == FormFieldType.SIGNATURE)
			return this.uiController.getIcon(FormIcon.SIGNATURE);
		/*if (fieldType == FormFieldType.TIME_FIELD)
			return this.uiController.getIcon(FormIcon.TIME_FIELD);*/
		if (fieldType == FormFieldType.TRUNCATED_TEXT)
			return this.uiController.getIcon(FormIcon.TRUNCATED_TEXT);
		if (fieldType == FormFieldType.WRAPPED_TEXT)
			return this.uiController.getIcon(FormIcon.WRAPPED_TEXT);
		// Fabaris_a.zanchi
		if (fieldType == FormFieldType.REPEATABLES)
			return this.uiController.getIcon(FormIcon.REPEATABLES);
		throw new IllegalStateException("No icon is mapped for field type: " + fieldType);
	}
	
	public void populateList() {
		Object window = this.uiController.loadComponentFromFile("/ui/core/importexport/pnFormDetails.xml");
		Object formList = this.uiController.find(window, "formsList");
		FormDao formDao = this.uiController.getFrontlineController().getFormDao();
		for (Form f : formDao.getAllForms()) {
			Object formNode = getNode(f);
			uiController.add(formList, formNode);
		}
	}
}
