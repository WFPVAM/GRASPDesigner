/*
 * FrontlineSMS <http://www.frontlinesms.com>
 * Copyright 2007, 2008 kiwanja
 * 
 * This file is part of FrontlineSMS.
 * 
 * FrontlineSMS is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 * 
 * FrontlineSMS is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FrontlineSMS. If not, see <http://www.gnu.org/licenses/>.
 */
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
/**
 * 
 */
package net.frontlinesms.plugins.forms.ui;

import java.awt.Desktop;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.Login;
import net.frontlinesms.LoginDetail;
import net.frontlinesms.csv.CsvExporter;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.Group;
import net.frontlinesms.data.repository.ContactDao;
import net.frontlinesms.data.repository.GroupMembershipDao;
import net.frontlinesms.data.repository.UserDao;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.BasePluginThinletTabController;
import net.frontlinesms.plugins.forms.FormsPluginController;
import net.frontlinesms.plugins.forms.csv.CsvFormExporter;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldAndBinding;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.domain.FormResponse;
import net.frontlinesms.plugins.forms.data.domain.ResponseValue;
import net.frontlinesms.plugins.forms.data.domain.Survey;
import net.frontlinesms.plugins.forms.data.repository.ConfigurationDao;
import net.frontlinesms.plugins.forms.data.repository.FormDao;
import net.frontlinesms.plugins.forms.data.repository.FormResponseDao;
import net.frontlinesms.plugins.forms.ui.components.FComponent;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;
import net.frontlinesms.plugins.forms.ui.components.VisualForm;
import net.frontlinesms.plugins.forms.xform.XformFormExporter;
import net.frontlinesms.ui.Icon;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.events.TabChangedNotification;
import net.frontlinesms.ui.handler.ComponentPagingHandler;
import net.frontlinesms.ui.handler.PagedComponentItemProvider;
import net.frontlinesms.ui.handler.PagedListDetails;
import net.frontlinesms.ui.handler.contacts.GroupSelecterDialog;
import net.frontlinesms.ui.handler.contacts.SingleGroupSelecterDialogOwner;
import net.frontlinesms.ui.i18n.InternationalisationUtils;
import net.frontlinesms.ui.i18n.TextResourceKeyOwner;

import org.apache.log4j.Logger;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;

import thinlet.Thinlet;

/**
 * Thinlet controller class for the FrontlineSMS Forms plugin.
 * 
 * 
 * 
 * 
 * @author Alex
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph,Mureed Al-Barghouthi
 *         www.fabaris.it <http://www.fabaris.it/>
 */
@TextResourceKeyOwner(prefix = { "I18N_", "COMMON_", "SENTENCE_", "TOOLTIP_" })
public class FormsThinletTabController extends
		BasePluginThinletTabController<FormsPluginController> implements
		SingleGroupSelecterDialogOwner, PagedComponentItemProvider,
		EventObserver {
	/** Logging object */
	private static Logger LOG = FrontlineUtils.getLogger(FrontlineSMS.class);
	// > CONSTANTS
	/** XML file containing forms pane for viewing results of a form */
	protected static final String UI_FILE_RESULTS_VIEW = "/ui/plugins/forms/formsTab_resultsView.xml";
	/** XML file containing dialog for exporting form data */
	private static final String UI_FILE_FORM_EXPORT_DIALOG = "/ui/plugins/forms/formExportDialogForm.xml";
	/**
	 * Fabaris_a.zanchi XML file containing dialog for exporting form data
	 */
	private static final String UI_FILE_XFORM_EXPORT_DIALOG = "/ui/plugins/forms/formExportDialogXForm.xml";
	/** XML file containing dialog for choosing which contacts to send a form to */
	private static final String XML_CHOOSE_CONTACTS = "/ui/plugins/forms/dgChooseContacts.xml";

	/** Component name of the forms list */
	private static final String FORMS_LIST_COMPONENT_NAME = "formsList";

	// > I18N KEYS
	/** i18n key: "Form Name" */
	public static final String I18N_KEY_FORM_NAME = "plugins.forms.editor.name.label";
	/** i18n key: "please insert a name for the form" **/
	public static final String I18N_KEY_MESSAGE_FIELD_NAME_FORMS = "plugins.forms.form";
	/** i18n key: "Form Editor" */
	static final String I18N_KEY_FORMS_EDITOR = "plugins.forms.editor.title";
	/** i18n key: "You have not entered a name for this form" */
	static final String I18N_KEY_MESSAGE_FORM_NAME_BLANK = "plugins.forms.name.blank";

	static final String I18N_KEY_MESSAGE_FORM_NO_NAME = "plugins.forms.no.name";// Fabaris_raji
	public static final String I18N_KEY_MESSAGE_FIELD_NAME_INVALID = "plugins.forms.invalid";// Fabaris_raji
	public static final String I18N_KEY_MESSAGE_FIELD_NAME_BLANK = "plugins.forms.blank";// Fabaris_raji
	public static final String I18N_KEY_MESSAGE_FIELD_NAME_EXISTS = "plugins.forms.exists";// Fabaris_raji
	public static final String I18N_KEY_MESSAGE_FIELD_NAME_REP_EXISTS = "plugins.forms.rep.exists";// Fabaris_raji

	/** i18n key: "You will not be able to edit this form again." */
	private static final String I18N_KEY_CONFIRM_FINALISE = "plugins.forms.send.finalise.confirm";
        private static final String I18N_KEY_CONFIRM_UNPUBLISH = "plugins.forms.send.unpublish.confirm";
	/** i18n key: "There are no contacts to notify." */
	private static final String I18N_KEY_NO_CONTACTS_TO_NOTIFY = "plugins.forms.send.nocontacts";
	private static final String I18N_KEY_SET_GROUP_BEFORE = "plugins.forms.set.group.before";
	/** i18n key: "Form submitter" */
	public static final String I18N_FORM_SUBMITTER = "plugins.forms.submitter";
	/** i18n key: "Your form 'formname' has been sent to N contacts." */
	private static final String I18N_FORM_SENT_DIALOG_MESSAGE = "plugins.forms.message.form.sent";
	/** i18n key: "This form result malformed:cannot be sent." */
	public static final String I18N_FORM_MALFORMED_ERROR = "plugins.forms.message.form.malformed.error";
	/**
	 * i18n key:
	 * "This form result malformed:Defaults component must be on the top of the form."
	 */
	public static final String I18N_FORM_COMPONENTS_ERROR = "plugins.forms.message.form.components.error";

	/** i18n key: "Currency field" */
	public static final String I18N_FCOMP_CURRENCY = "plugins.forms.field.currency";
	public static final String I18N_FCOMP_NUMBER = "plugins.forms.field.number";
	public static final String I18N_FCOMP_PASSWORD = "plugins.forms.field.password";
	public static final String I18N_FCOMP_PHONENUMBER = "plugins.forms.field.phonenumber";
	public static final String I18N_FCOMP_TEXT_AREA = "plugins.forms.field.textarea";
	public static final String I18N_FCOMP_TEXT_FIELD = "plugins.forms.field.textfield";
	public static final String I18N_FCOMP_List = "plugins.forms.field.list";
	// Added by Fabaris_a.zanchi
	public static final String I18N_FCOMP_REPEATABLES = "plugins.forms.field.repeatables";
	public static final String I18N_FCOMP_REPEATABLES_BASIC = "plugins.forms.field.repeatables.basic";
	// Added by Fabaris_a.zanchi
	public static final String I18N_FCOMP_GEOLOCATION = "plugins.forms.field.geolocation";
	public static final String I18N_FCOMP_BARCODE = "plugins.forms.field.barcode";
	public static final String I18N_FCOMP_IMAGE = "plugins.forms.field.image";
	// Added by Fabaris_Raji
	public static final String I18N_FCOMP_DROP_DOWN_LIST = "plugins.forms.field.dropdownlist";
	public static final String I18N_FCOMP_RADIOBUTTON = "plugins.forms.field.radiobutton";

	public static final String I18N_FCOMP_SEPARATOR = "plugins.forms.field.seperator";
	// public static final String I18N_FCOMP_MULTILINE =
	// "plugins.forms.field.multiline"; //25/10/2013
	// public static final String I18N_FCOMP_SINGLELINE =
	// "plugins.forms.field.singleline"; //25/10/2013

	public static final String I18N_FCOMP_CHECKBOX = "plugins.forms.field.checkbox";
	public static final String I18N_FCOMP_TIME = "common.time";
	public static final String I18N_FCOMP_TRUNCATED_TEXT = "plugins.forms.field.truncatedtext";
	public static final String I18N_FCOMP_WRAPPED_TEXT = "plugins.forms.field.wrappedtext";
	private static final String I18N_PLUGINS_FORMS_NOT_SET = "plugins.forms.not.set";
	private static final String I18N_PLUGINS_FORMS_GROUP = "plugins.forms.group";
	private static final String I18N_PLUGINS_FORMS_CHOOSE_GROUP = "plugins.forms.choose.group";
	public static final String COMMON_PALETTE = "plugins.forms.palette";
	public static final String COMMON_PREVIEW = "plugins.forms.preview";
	// Fabaris_a.zanchi
	public static final String COMMON_PREVIEW_REPEATABLES = "plugins.forms.repeatables.preview";
	public static final String COMMON_PREVIEW_BASIC_REPEATABLES = "plugins.forms.repeatables.basic.preview";
	public static final String COMMON_PROPERTY = "plugins.forms.property";
	public static final String COMMON_VALUE = "plugins.forms.value";
	public static final String TOOLTIP_DRAG_TO_REMOVE = "plugins.forms.tooltip.drag.to.remove";
	public static final String TOOLTIP_DRAG_TO_PREVIEW = "plugins.forms.tooltip.drag.to.preview";
	public static final String SENTENCE_DELETE_KEY = "plugins.forms.sentence.delete.key";
	public static final String SENTENCE_UP_KEY = "plugins.forms.sentence.up.key";
	public static final String SENTENCE_DOWN_KEY = "plugins.forms.sentence.down.key";
	private static final String MESSAGE_NO_FILENAME = "message.filename.blank";
	private static final String MESSAGE_EXPORT_TASK_SUCCESSFUL = "message.export.successful";
	private static final String MESSAGE_EXPORT_TASK_FAILED = "message.export.failed";
	private static final String MESSAGE_BAD_DIRECTORY = "message.bad.directory";
	private static final String MESSAGE_CONFIRM_FILE_OVERWRITE = "message.file.overwrite.confirm";
	private static final Object UI_FORM_TAB_NAME = ":forms";
	public static final String BINDINGS_POLICY_LABEL = "plugin.forms.bindings.policy";
	public static final String CONSTRAINTS_POLICY_LABEL = "plugins.forms.constraints.policy";
	// Fabaris_a.zanchi strings for survey
	public static final String SURVEY_LABEL = "plugin.forms.survey.label";
	public static final String SURVEY_EDITLABEL = "plugin.forms.survey.editlabel";
	public static final String SURVEY_LIST = "plugin.forms.survey.list";
	public static final String SURVEY_LISTELEMENTS = "plugin.forms.survey.listelements";
	public static final String SURVEY_ADD = "plugin.forms.survey.add";
	public static final String SURVEY_REMOVE = "plugin.forms.survey.remove";
	public static final String REPEATABLE_BASIC_INFINITY = "plugins.forms.repeatables.infinity";
	public static final String REPEATABLES_BASIC_REPEATATIONS = "plugins.forms.repeatables.repeatations";
	// Fabaris_A.zanchi strings for close message dialog
	public static final String CLOSE_DIALOG_MESSAGE = "plugin.forms.closedialog.message";
	public static final String CLOSE_DIALOG_BUTTON1 = "plugin.forms.closedialog.button1";
	public static final String CLOSE_DIALOG_BUTTON2 = "plugin.forms.closedialog.button2";
	public static final String CLOSE_DIALOG_BUTTON3 = "plugin.forms.closedialog.button3";
	// Fabaris_a.zanchi strings for required stuff
	public static final String REQUIRED_LABEL = "plugin.forms.required.label";
	public static final String ERROR_EMPTY_LIST = "plugin.forms.error.emptylist";
	public static final String ERROR_EMPTY_LIST_POSITION = "plugin.forms.error.emptylist.position";
	public static final String ERROR_EMPTY_LIST_TITLE = "plugin.forms.error.emptylist.title";
	public static final String ERROR_EMPTY_REPEATABLE = "plugin.forms.error.emptyrep.title";
	// Fabaris_a.zanchi strings for errors on visibiliy conditions table
	public static final String ERROR_DROP_DISABLED_ONSELF = "plugin.forms.bindings.dropdisabled.onself";
	public static final String ERROR_DROP_DISABLED_ONTYPE = "plugin.forms.bindings.dropdisabled.ontype";
	public static final String ERROR_DROP_DISABLE_NOPREVIOUS = "plugin.forms.bindings.dropdisabled.noprevious";
	public static final String ERROR_MULTI_RELEVANT = "plugin.forms.error.multirelevant";

	// Fabaris_a.zanchi strings for nice names for formfield components
	public static final String CHECK_BOX_STRING = "plugin.forms.formfieldtype.checkbox";
	public static final String CURRENCY_FIELD_STRING = "plugin.forms.formfieldtype.currencyfield";
	public static final String DATE_FIELD_STRING = "plugin.forms.formfieldtype.datefield";
	public static final String DROP_DOWN_LIST_STRING = "plugin.forms.formfieldtype.dropdown";
	public static final String EMAIL_FIELD_STRING = "plugin.forms.formfieldtype.email";
	public static final String NUMERIC_TEXT_FIELD_STRING = "plugin.forms.formfieldtype.numericfield";
	public static final String PHONE_NUMBER_FIELD_STRING = "plugin.forms.formfieldtype.phonenumberfield";
	public static final String RADIO_BUTTON_STRING = "plugin.forms.formfieldtype.radiobutton";

	public static final String SEPARATOR_STRING = "plugin.forms.formfieldtype.separator";
	// public static final String MULTILINE_STRING =
	// "plugin.forms.formfieldtype.multiline"; 24/10/2013
	// public static final String SINGLELINE_STRING =
	// "plugin.forms.formfieldtype.singleline"; 24/10/2013

	public static final String REPEATABLES_STRING = "plugins.forms.field.repeatables";
	public static final String REPEATABLES_BASIC_STRING = "plugins.forms.field.repeatables.basic";
	public static final String TEXT_AREA_STRING = "plugin.forms.formfieldtype.textarea";
	public static final String TEXT_FIELD_STRING = "plugin.forms.formfieldtype.textfield";
	public static final String TRUNCATED_TEXT_STRING = "plugin.forms.formfieldtype.truncatedtext";
	public static final String WRAPPED_TEXT_STRING = "plugin.forms.formfieldtype.wrappedtext";
	public static final String GEOLOCATION_STRING = "plugin.forms.formfieldtype.gelocation";
	public static final String BARCODE_STRING = "plugin.forms.formfieldtype.barcode";
	public static final String IMAGE_STRING = "plugin.forms.formfieldtype.image";

	// Fabaris_raji
	public static final String ERROR_EMPTY_REP_NAME = "plugin.forms.emptyRepname";
	public static final String ERROR_EMPTY_NAME = "plugin.forms.emptyname";
	public static final String ERROR_EMPTY_NAME_POSITION = "plugin.forms.emptyname.position";
	public static final String ERROR_EMPTY_NAME_TITLE = "plugin.forms.emptyname.title";
        
        
        //Added by Mureed Al-Barghouth (UNOPS team)
        public static final String I18N_FCOMP_SIGNATURE = "plugins.forms.field.signature";
        public static final String SIGNATURE_STRING = "plugin.forms.formfieldtype.signature";
        
        
	private static FormsThinletTabController ctrl;

	// > INSTANCE PROPERTIES
	/** DAO for {@link Contact}s */
	private ContactDao contactDao;
	/** DAO for {@link Form}s */
	// private FormDao formsDao;
	FormDao formsDao;
	/** DAO for {@link FormResponse}s */
	private FormResponseDao formResponseDao;
	/** DAO for getting {@link Contact}s in {@link Group}s */
	private GroupMembershipDao groupMembershipDao;

	/** UI table displaying the results. */
	private Object formResultsComponent;
	/** Paging handler for the results component */
	private ComponentPagingHandler formResponseTablePageControls;

	private Object exportDialog;

	/** aggiunto da maria c. */
	public LoginDetail ld = new LoginDetail();
	public Login lg = new Login();
	private ConfigurationDao configurationDao;

	// public ConfigurationDao configurationDao;

	// > CONSTRUCTORS
	public FormsThinletTabController(FormsPluginController pluginController,
			UiGeneratorController ui) {
		super(pluginController, ui);

		this.ui.getFrontlineController().getEventBus().registerObserver(this);
		ctrl = this;
	}

	// > INSTANCE METHODS
	/** Refresh the tab's display. */
	public void refresh() {
		Object formList = getFormsList();

		// If there was something selected previously, we will attempt to select
		// it again after updating the list
		Object previousSelectedItem = this.ui.getSelectedItem(formList);
		Form previousSelectedForm = previousSelectedItem == null ? null : this.getForm(previousSelectedItem);
		ui.removeAll(formList);
		Object newSelectedItem = null;
		for (Form f : formsDao.getAllActiveForms()) {
			Object formNode = getNode(f);
			ui.add(formList, formNode);
			if (previousSelectedForm != null && f.getFormMobileId() == previousSelectedForm.getFormMobileId()) {
				newSelectedItem = formNode;
			}
		}

		// Restore the selected item
		if (newSelectedItem != null) {
			this.ui.setSelectedItem(formList, newSelectedItem);
		}

		// We should enable or disable buttons as appropriate
		formsList_selectionChanged();
	}

	// > THINLET EVENT METHODS
	public void showFormsPluginInfo() {
		FormsAboutDialogHandler.createAndShow(this.ui, this.getPluginController());
	}

	/** Show the dialog for exporting form results. */
	public void showFormExportDialog() {
		exportDialog = ui.loadComponentFromFile(UI_FILE_FORM_EXPORT_DIALOG, this);
		ui.add(exportDialog);
	}

	/** Show the dialog for exporting form results. */
	public void showXFormExportDialog() {
		exportDialog = ui.loadComponentFromFile(UI_FILE_XFORM_EXPORT_DIALOG, this);
		ui.add(exportDialog);
	}

	/**
	 * On click links to the Management Tool.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	// Fabaris_raji
	public void showLaunchformTest() {
		try {
			UserDao userDao = FormsThinletTabController.getCurrentInstance().getPluginController().getUserDao();
			String name = ld.getUsername();
			Form selected = getSelectedForm();
			long formId = selected.getId();
			String url = "http://localhost/Management_Tool/faces/public/LoginUtente.jsf?" + "formId=" + formId + "&username=" + name;
			Desktop dt = Desktop.getDesktop();
			URI uri = new URI(url);
			dt.browse(uri.resolve(uri));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void showLaunchformTestmenu() {
		try {
			UserDao userDao = FormsThinletTabController.getCurrentInstance().getPluginController().getUserDao();
			String name = ld.getUsername();
			Form selected = getSelectedForm();
			long formId = selected.getId();
			String url = "http://localhost/Management_Tool/faces/public/LoginUtente.jsf?" + "formId=" + formId + "&username=" + name;
			Desktop dt = Desktop.getDesktop();
			URI uri = new URI(url);
			dt.browse(uri.resolve(uri));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void showSaveModeFileChooser(Object textFieldToBeSet) {
		this.ui.showFileChooser(textFieldToBeSet);
	}

	/** Show the AWT Forms Editor window */
	public void showFormsEditor() {
		VisualForm form = new VisualForm();
		form = FormsUiController.getInstance().showNewFormsEditor(ui.getFrameLauncher(), form);
		if (form != null) {
			Form savedForm = saveFormInformation(form);
			VisualForm visualForm = VisualForm.getVisualForm(savedForm);
			List<PreviewComponent> old = new ArrayList<PreviewComponent>();
			old.addAll(visualForm.getComponents());
			visualForm = FormsUiController.getInstance().showFormsEditor(ui.getFrameLauncher(), visualForm);
			if (visualForm != null) {
				savedForm.setName(visualForm.getName());
                                savedForm.setFormVersion(0);
				updateForm(old, visualForm.getComponents(), savedForm, visualForm);
				refresh();
			}
		}
	}

	public void removeSelected(Object component) {
		Object[] selected = this.ui.getSelectedItems(component);
		if (selected != null) {
			for (Object selectedComponent : selected) {
				this.ui.remove(selectedComponent);
			}
		}
	}

	/**
	 * Calls the export method according to the supplied information, and the
	 * user selection.
	 * 
	 * @param aggregate
	 * @param dataPath
	 * @param exportDialog
	 */
	public void formsTab_exportResults(String dataPath) {
		if (!dataPath.contains(File.separator) || !(new File(dataPath.substring(0, dataPath.lastIndexOf(File.separator))).isDirectory())) {
			this.ui.alert(InternationalisationUtils.getI18nString(MESSAGE_BAD_DIRECTORY));
		}
		else if (dataPath.substring(dataPath.lastIndexOf(File.separator), dataPath.length()).equals(File.separator)) {
			this.ui.alert(InternationalisationUtils.getI18nString(MESSAGE_NO_FILENAME));
		}
		else {
			log.debug("Filename is [" + dataPath + "] before [" + CsvExporter.CSV_EXTENSION + "] check.");
			if (!dataPath.endsWith(CsvExporter.CSV_EXTENSION)) {
				dataPath += CsvExporter.CSV_EXTENSION;
			}

			log.debug("Filename is [" + dataPath + "] after [" + CsvExporter.CSV_EXTENSION + "] check.");
			File csvFile = new File(dataPath);
			if (csvFile.exists() && csvFile.isFile()) {
				// Show confirmation dialog
				ui.showConfirmationDialog("doExport('" + dataPath + "')", this, MESSAGE_CONFIRM_FILE_OVERWRITE);
			}
			else {
				doExport(dataPath);
			}
		}
	}

	/**
	 * Fabaris_a.zanchi Calls the export method according to the supplied
	 * information, and the user selection.
	 * 
	 * @param aggregate
	 * @param dataPath
	 * @param exportDialog
	 */
	public void formsTab_exportResultsXform(String dataPath) {
		if (!dataPath.contains(File.separator) || !(new File(dataPath.substring(0, dataPath.lastIndexOf(File.separator))).isDirectory())) {
			this.ui.alert(InternationalisationUtils.getI18nString(MESSAGE_BAD_DIRECTORY));
		}
		else if (dataPath.substring(dataPath.lastIndexOf(File.separator), dataPath.length()).equals(File.separator)) {
			this.ui.alert(InternationalisationUtils.getI18nString(MESSAGE_NO_FILENAME));
		}
		else {
			log.debug("Filename is [" + dataPath + "] before [" + ".xml" + "] check.");
			if (!dataPath.endsWith(".xml")) {
				dataPath += ".xml";
			}

			log.debug("Filename is [" + dataPath + "] after [" + ".xml" + "] check.");

			File csvFile = new File(dataPath);
			if (csvFile.exists() && csvFile.isFile()) {
				// Show confirmation dialog
				ui.showConfirmationDialog("doExportXform('" + dataPath + "')", this, MESSAGE_CONFIRM_FILE_OVERWRITE);
			}
			else {
				doExportXform(dataPath);
			}
		}

	}

	public void doExport(String filename) {
		ui.removeConfirmationDialog();

		refresh();

		Object formsList = find("formsList");
		Form selectedForm = getForm(ui.getSelectedItem(formsList));
		if (selectedForm == null)
			return;
		File file = new File(filename);
		try {
			CsvFormExporter.exportForm(file, selectedForm, contactDao, formResponseDao);
			this.ui.setStatus(InternationalisationUtils.getI18nString(MESSAGE_EXPORT_TASK_SUCCESSFUL));
		} catch (IOException ex) {
			log.debug(InternationalisationUtils.getI18nString(MESSAGE_EXPORT_TASK_FAILED), ex);
			// FIXME create a proper UiGeneratorController.alert(String,
			// Throwable) method so we don't have to cobble this stuff together
			// every time
			this.ui.alert(InternationalisationUtils.getI18nString(MESSAGE_EXPORT_TASK_FAILED) + ": " + ex.getLocalizedMessage());
		} finally {
			removeDialog(exportDialog);
		}
	}

	public void doExportXform(String filename) {
		ui.removeConfirmationDialog();

		refresh();

		Object formsList = find("formsList");
		Form selectedForm = getForm(ui.getSelectedItem(formsList));
		if (selectedForm == null)
			return;
		File file = new File(filename);
		try {
			XformFormExporter.exportXformToFile(file, selectedForm);

			this.ui.setStatus(InternationalisationUtils.getI18nString(MESSAGE_EXPORT_TASK_SUCCESSFUL));
		} catch (Exception ex) {
			log.debug(InternationalisationUtils.getI18nString(MESSAGE_EXPORT_TASK_FAILED), ex);
			// FIXME create a proper UiGeneratorController.alert(String,
			// Throwable) method so we don't have to cobble this stuff together
			// every time
			this.ui.alert(InternationalisationUtils.getI18nString(MESSAGE_EXPORT_TASK_FAILED) + ": " + ex.getLocalizedMessage());
		} finally {
			removeDialog(exportDialog);
		}
	}

	/**
	 * Called when the user has selected a different item on the forms tree.
	 * 
	 * @param formsList
	 */
	public void formsList_selectionChanged() {
		Form selectedForm = getForm(ui.getSelectedItem(getFormsList()));
int responseCount = this.formResponseDao.getFormResponseCount(selectedForm);
		if (selectedForm != null && selectedForm.isFinalised() && responseCount>0) {
			showResultsPanel(selectedForm);
		}
		else {
			// Nothing selected
			Object pnRight = find("pnRight");
			ui.removeAll(pnRight);
		}

		enableMenuOptions(find("formsList_toolbar"));
	}

	/**
	 * Show the GUI to edit a form.
	 * 
	 * @param list
	 *            Reference to the Forms tree object.
	 */
	public void formsList_editSelected() {
		Form selectedForm = getSelectedForm();
		Form selectedFormFromDB = this.formsDao.getFromId_IdMac(selectedForm.getId_flsmsId());
		selectedForm = selectedFormFromDB;
		if (selectedFormFromDB == null)
			return;
		if (selectedFormFromDB.isFinalised()) {
			selectedForm.finalise();
		}
		if (selectedForm != null && !selectedForm.isFinalised()) {
			VisualForm visualForm = VisualForm.getVisualForm(selectedForm);
			List<PreviewComponent> old = new ArrayList<PreviewComponent>();
			old.addAll(visualForm.getComponents());
			visualForm = FormsUiController.getInstance().showFormsEditor(ui.getFrameLauncher(), visualForm);
			if (visualForm != null) {
				if (!visualForm.getName().equals(selectedForm.getName())) {
					selectedForm.setName(visualForm.getName());
				}
				updateForm(old, visualForm.getComponents(), selectedForm, visualForm);
				formsList_selectionChanged();
			}
		}
	}

	public void _formsList_editSelected(VisualForm newOne) {
		Form selectedForm = getSelectedForm();
		Form selectedFormFromDB = null;
		if (selectedForm != null) {
			selectedFormFromDB = this.formsDao.getFromId_IdMac(selectedForm.getId_flsmsId());
		}
		else {
			return;
		}
		if (selectedFormFromDB == null) {
			return;
		}
		if (!selectedFormFromDB.isFinalised()) {
			VisualForm visualForm = VisualForm.getVisualForm(selectedFormFromDB);
			List<PreviewComponent> old = new ArrayList<PreviewComponent>();
			old.addAll(visualForm.getComponents());
			if (newOne != null)
				updateForm(old, newOne.getComponents(), selectedFormFromDB, newOne);
		}
	}

	/** Shows a selecter for assigning a {@link Group} to a {@link Form} */
	public void formsList_showGroupSelecter() {
		Form selectedForm = getSelectedForm();
		log.info("FormsThinletTabController.showGroupSelecter() : " + selectedForm);

		if (selectedForm != null) {
			GroupSelecterDialog selecter = new GroupSelecterDialog(ui, this);

			selecter.init(InternationalisationUtils.getI18nString(I18N_PLUGINS_FORMS_CHOOSE_GROUP), ui.getRootGroup());

			selecter.show();
		}
	}

        public void formsList_unpublishSelected() {
		Form selectedForm = getSelectedForm();
		log.info("FormsThinletTabController.showGroupSelecter() : " + selectedForm);

		if (selectedForm != null) {
	//TODO enable only if the form is finalized
        
         ui.showConfirmationDialog("showFinalizedSelectionDialog", this, I18N_KEY_CONFIRM_UNPUBLISH);
    
         selectedForm.setPreviousPublishedName(selectedForm.getName());
         selectedForm.setPreviousPublishedID(selectedForm.getId_flsmsId());
         formsDao.setFinalisedForm(selectedForm);
         this.formsDao.updateForm(selectedForm);

                 
         }
        //           
              
		}
	
	public void groupSelectionCompleted(Group group) {
		// TODO Auto-generated method stub
		Form form = getSelectedForm();
		log.info("Form: " + form);
		log.info("Group: " + group);
		System.out.println(group);
		// if(group != null) {
		/** Aggiunto da Fabaris_maria cilione */
		// if (!form.isFinalised()) {
//		if ((form.getPermittedGroup() == null)) { // Fabaris_raji
			/** Fabaris_maria cilione */
			// Set the permitted group for this form, then save it
			form.setPermittedGroup(group);
			this.formsDao.updateForm(form);
			this.refresh();
//		}
	}

	/**
	 * @param groupSelecter
	 * @param groupList
	 */
	public void setSelectedGroup(Object groupSelecter, Object groupList) {
		Form form = getForm(groupSelecter);
		log.info("Form: " + form);
		Group group = ui.getGroup(ui.getSelectedItem(groupList));
		log.info("Group: " + group);
		if (group != null) {
			// Set the permitted group for this form, then save it
			form.setPermittedGroup(group);
			this.formsDao.updateForm(form);
			this.refresh();

			removeDialog(groupSelecter);
		}
	}

	/**
	 * Attempt to send the form selected in the forms list
	 * 
	 * @param formsList
	 *            the forms list component
	 */
	public void formsList_sendSelected() {
		Form selectedForm = getSelectedForm();
		if (selectedForm != null) {
			// check the form has a group set
			if (selectedForm.getPermittedGroup() == null) {
				// The form has no group set, so we should explain that this
				// needs to be done.
				// FIXME i18n
				ui.alert(InternationalisationUtils.getI18nString(I18N_KEY_SET_GROUP_BEFORE));
			}
			else if (!selectedForm.isFinalised()) { // check the form is
				// finalized.
				// if form is not finalized, warn that it will be!
				ui.showConfirmationDialog("showSendSelectionDialog", this, I18N_KEY_CONFIRM_FINALISE);
			}
			else {
				// show dialog for selecting group members to send the form to
				showSendSelectionDialog();
			}
		}
	}

	/**
	 * Fabaris_A.zanchi Called when the button "finalize" is pressed
	 */
	public void formsList_finalizeSelected() {

		Form selectedForm = getSelectedForm();
		if (selectedForm != null) {
			if (selectedForm.getPermittedGroup() == null) {
				ui.alert(InternationalisationUtils.getI18nString(I18N_KEY_SET_GROUP_BEFORE));
			}
			else if (!selectedForm.isFinalised()) { // check the form is
				// finalised.
				// if form is not finalised, warn that it will be!
				ui.showConfirmationDialog("showFinalizeSelectionDialog", this, I18N_KEY_CONFIRM_FINALISE);
			}
		}
	}

	/**
	 * Fabaris_A.zanchi Called when the confirmation dialog for finalizing is
	 * accepted
	 */
	public void showFinalizeSelectionDialog() {
		System.out.println("blbl");
		ui.removeConfirmationDialog();
		Form selected = getSelectedForm();
		if (selected != null) {
                    if (selected.getFormVersion() == null){
                        selected.setFormVersion(0);
                    }
                    selected.setFormVersion(selected.getFormVersion() + 1 );
                   // this.formsDao.updateForm(selected);
			formsDao.finaliseForm(selected);
			this.refresh();
		}
	}
        public void showFinalizedSelectionDialog(){
            ui.removeConfirmationDialog();
            this.refresh();
        }
	/**
	 * Show dialog for selecting users to send a form to. If the form is not
	 * finalised, it will be finalised within this method.
	 */
	public void showSendSelectionDialog() {
		ui.removeConfirmationDialog();

		Form form = getSelectedForm();
		if (form != null) {
			// if form is not finalized, finalize it now
			if (!form.isFinalised()) {
				formsDao.finaliseForm(form);
				this.refresh();
			}

			// show selection dialog for Contacts in the form's group
			Object chooseContactsDialog = ui.loadComponentFromFile(XML_CHOOSE_CONTACTS, this);
			ui.setAttachedObject(chooseContactsDialog, form);

			// Add each contact in the group to the list. The user can then
			// remove any contacts they don't
			// want to be sent an SMS about the form at this time.
			Object contactList = ui.find(chooseContactsDialog, "lsContacts");
			for (Contact contact : this.groupMembershipDao.getActiveMembers(form.getPermittedGroup())) {
				Object listItem = ui.createListItem(contact.getDisplayName(), contact);
				ui.add(contactList, listItem);
			}
			ui.add(chooseContactsDialog);
		}
	}

	/**
	 * Send a form to the contacts selected in the dialog.
	 * 
	 * @param dgChooseContacts
	 *            Dialog containing the contact selection
	 */
	public void sendForm(Object dgChooseContacts) {
		// Work out which contacts we should be sending the form to
		Object[] recipientItems = ui.getItems(ui.find(dgChooseContacts, "lsContacts"));
		Form form = getForm(dgChooseContacts);
		/** Aggiunto da Fabaris_maria cilione. */
		List<String> xform = new ArrayList<String>();
		String listform = new String();
		for (FormField ff : form.getFields()) {
			listform = listform + ff.getX_form();
		}
		xform.add(listform);

		/** Fabaris_Maria cilione. */

		if (recipientItems.length == 0) {
			// There are no contacts in the "send to" list. We should remove the
			// dialog and inform the user
			// of the problem.
			ui.alert(InternationalisationUtils.getI18nString(I18N_KEY_NO_CONTACTS_TO_NOTIFY));
			ui.removeDialog(dgChooseContacts);
		}
		else {
			HashSet<Contact> selectedContacts = new HashSet<Contact>();

			for (Object o : recipientItems) {
				Object attachment = ui.getAttachedObject(o);
				if (attachment instanceof Contact) {
					selectedContacts.add((Contact) attachment);
				}
				else if (attachment instanceof Group) {
					Group g = (Group) attachment;
					selectedContacts.addAll(this.groupMembershipDao.getActiveMembers(g));
					ui.alert(InternationalisationUtils.getI18nString(I18N_FORM_SENT_DIALOG_MESSAGE));
				}

			}
			try {
				ByteArrayInputStream is = new ByteArrayInputStream(listform.getBytes("UTF-8"));
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Error parsing form XML:" + e.getMessage());
				ui.alert(InternationalisationUtils.getI18nString(I18N_FORM_MALFORMED_ERROR));
				ui.removeDialog(dgChooseContacts);
				return;
			}
			// Issue the send command to the plugin controller
			// this.getPluginController().sendForm(form, selectedContacts);
			/** Aggiunto da FABARIS_maria cilione. */
			int c = this.getPluginController().sendForm(form, selectedContacts, xform);
			/** Fabaris_maria cilione. */
			if (c == 0) {
				ui.removeDialog(dgChooseContacts);
				return;
			}
			ui.alert(InternationalisationUtils.getI18nString(I18N_FORM_SENT_DIALOG_MESSAGE, form.getName(), Integer.toString(c)));
			// ui.alert(InternationalisationUtils.getI18nString(I18N_FORM_SENT_DIALOG_MESSAGE,
			// Integer.toString(form.getFormMobileId()),
			// Integer.toString(selectedContacts.size())));
			ui.removeDialog(dgChooseContacts);
		}

	}

	/** Finds the forms list and deletes the selected item. */
	public void formsList_deleteSelected() {
		Form selectedForm = getSelectedForm();

		if (selectedForm != null) {
                        //s3 delete finalized form
//			if (selectedForm.isFinalised()) {
//				//ui.removeConfirmationDialog();
//				JOptionPane.showConfirmDialog(ui, "Can't delete finalized forms", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
//			}
			//else {
				// Fabaris_a.zanchi deletes every survey owned by this form
                  if (selectedForm.getPreviousPublishedName() != null){
                        selectedForm.setIsHidden(1);
                        selectedForm.setIsDeleted(1);
                          DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                          Date date = new Date();
                          String d =dateFormat.format(date);
                          selectedForm.setDeletedDate(d);
                          selectedForm.setName(selectedForm.getName()+"_DEL_"+selectedForm.getDeletedDate());
                        this.formsDao.updateForm(selectedForm);
                        
                  }
                  else{
                    
				selectedForm.freeSurveyFromFormField();
				getPluginController().getFormDao().updateForm(selectedForm);
				List<Survey> ownedByForm = getPluginController().getSurveyDao().getSurveyOwnedByForm(selectedForm);
				for (Survey sur : ownedByForm) {
					getPluginController().getSurveyDao().deleteSurvey(sur);
				}
				// end of survey deletion
				this.formsDao.deleteForm(selectedForm);
                  }	//}
		}
		this.refresh();
		// Now remove the confirmation dialog.
		ui.removeConfirmationDialog();

	}
	
	/** Finds the forms list and hides the selected item. */
	public void formsList_hideSelected() {
		Form selectedForm = getSelectedForm();

		if (selectedForm != null) {
			if (!selectedForm.isFinalised()) {
				ui.removeConfirmationDialog();
				JOptionPane.showConfirmDialog(ui, "Can't hide non-finalized forms", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			}
			else {
				// set to hidden status the selected form
				/*
				selectedForm.freeSurveyFromFormField();				
				getPluginController().getFormDao().updateForm(selectedForm);
				List<Survey> ownedByForm = getPluginController().getSurveyDao().getSurveyOwnedByForm(selectedForm);
				for (Survey sur : ownedByForm) {
					getPluginController().getSurveyDao().deleteSurvey(sur);
				}
				*/
				// end of survey deletion
				selectedForm.setIsHidden(1);
				this.formsDao.updateForm(selectedForm);
			}
		}
		this.refresh();
		// Now remove the confirmation dialog.
		ui.removeConfirmationDialog();

	}
	/**
	 * Duplicates the selected form.
	 * 
	 * @param formsList
	 */
	public void formsList_duplicateSelected() {
		Form selected = getSelectedForm();
		// Fabaris_raji

		List<String> lista = new ArrayList<String>();
		for (Form f : formsDao.getAllForms()) {
			String formname = f.getName();
			lista.add(formname);

		}
		FormsEditorDialog.createAndShowGUI(lista, selected, this);
		// commented by Fabaris_raji

		
//		  HashMap<FormField, FormField> oldCompsTOnewComps = new
//		  HashMap<FormField, FormField>(); assert (selected != null) :
//		  "Duplicate Form button should not be enabled if there is no form selected!"
//		  ; Form clone = new Form(selected.getName() + '*'); clone = new
//		  Form(name); for (FormField oldField : selected.getFields()) {
//		 FormField newField = new FormField(oldField.getType(),
//		  oldField.getLabel(), oldField.getX_form(), oldField.getName(),
//		  oldField.getRequired());
//		  newField.setCalculated(oldField.getCalculated()); //Fabaris_a.aknai
//		  cloning formula newField.setFormula(oldField.getFormula());
//		  newField.setReadOnly(oldField.isReadOnly());
//		  newField.setNumberOfRep(oldField.getNumberOfRep());
//		  //System.out.println
//		  ("Field '"+oldField.getName()+"' Is calculated:"+oldField
//		  .getCalculated()+" - "+oldField.getFormula());
//		  
//		  // Map to trace new formfields ownership
//		  oldCompsTOnewComps.put(oldField, newField);
//		  
//		  if (oldField.getType() == FormFieldType.DROP_DOWN_LIST ||
//		  oldField.getType() == FormFieldType.RADIO_BUTTON) {
//		  newField.setSurvey(oldField.getSurvey()); } if (oldField.getType() ==
//		  FormFieldType.REPEATABLES || oldField.getType() ==
//		  FormFieldType.REPEATABLES_BASIC) {
//		  newField.setSurvey(oldField.getSurvey()); for (FormField rep :
//		  oldField.getRepetables()) { FormField newRep = new
//		  FormField(rep.getType(), rep.getLabel(), rep.getX_form(),
//		  rep.getName(), rep.getRequired());
//		  newRep.setCalculated(rep.getCalculated()); //Fabaris_a.aknai cloning
//		  formula newRep.setFormula(rep.getFormula());
//		  newRep.setReadOnly(rep.isReadOnly());
//		  newRep.setSurvey(rep.getSurvey()); // copy costraints for
//		  (ConstraintContainer cont : rep.getConstraints()) {
//		  newRep.addConstraint(cont.clone()); }
//		  
//		  newRep.setBindingsPolicy(rep.getBindingsPolicy());
//		  newRep.setConstraintPolicy(rep.getConstraintPolicy());
//		 * newField.addRepetable(newRep);
//		  
//		  // Map to trace new formfields ownership oldCompsTOnewComps.put(rep,
//		  newRep); } } clone.addField(newField, oldField.getPositionIndex());
//		  // copy costraints for (ConstraintContainer cont :
//		  oldField.getConstraints()) { newField.addConstraint(cont.clone()); }
//		  
//		  newField.setConstraintPolicy(oldField.getConstraintPolicy());
//		  newField.setBindingsPolicy(oldField.getBindingsPolicy()); }
//		  
//		  // cycle again for bindings
//                  for (FormField oldField :
//		  selected.getFields()) {
//            for (FormFieldAndBinding fb :
//		  oldField.getBindingCouples()) {
//		  oldCompsTOnewComps.get(oldField).addBinding
//		  (oldCompsTOnewComps.get(fb.getfField()), fb.getbContainer().clone());
//		  } if (oldField.getType() == FormFieldType.REPEATABLES ||
//		  oldField.getType() == FormFieldType.REPEATABLES_BASIC) { for
//		  (FormField oldRep : oldField.getRepetables()) { for
//		  (FormFieldAndBinding fb : oldRep.getBindingCouples()) {
//		  oldCompsTOnewComps
//		  .get(oldRep).addBinding(oldCompsTOnewComps.get(fb.getfField()),
//		  fb.getbContainer().clone()); } } }
//		  
//		  }
//		  
//		  clone.setBindingsPolicy(selected.getBindingsPolicy());
//		  clone.setDesignerVersion(selected.getDesignerVersion());
//		  
//		  this.formsDao.saveForm(clone); UserDao user =
//		  FormsThinletTabController
//		  .getCurrentInstance().getPluginController().getUserDao(); String
//		  idMacchina = user.getAdminFrontlineSMS_ID();
//		  clone.setId_flsmsId(clone.getId()+"_"+idMacchina); VisualForm vForm =
//		 VisualForm.getVisualForm(clone);
//		 this.updateForm(vForm.getComponents(), vForm.getComponents(), clone,
//		 vForm); this.refresh();
//		
//             
	}

	/**
	 * Form selection has changed, so decide which toolbar and popup options
	 * should be available considering the current selection.
	 */
	public void formsTab_enabledFields(Object formsList_toolbar,
			Object formsList_popupMenu) {
		enableMenuOptions(formsList_toolbar);
		enableMenuOptions(formsList_popupMenu);
	}

	/**
	 * Enable menu options for the supplied menu component.
	 * 
	 * @param menuComponent
	 *            Menu component, a button bar or popup menu
	 * @param selectedComponent
	 *            The selected object of the control that this menu applied to
	 */
	private void enableMenuOptions(Object menuComponent) {
		Object selectedComponent = formsList_getSelected();
		Form selectedForm = getForm(selectedComponent);
		for (Object o : ui.getItems(menuComponent)) {
			String name = ui.getName(o);
			if (name != null) {
				if (ui.getItems(getFormsList()).length == 0) {
					ui.setVisible(o, (!name.startsWith("mi") && !name.startsWith("sp")) || name.endsWith("New"));
				}
				else {
					ui.setVisible(o, true);
					if (name.contains("Delete")) {
						// Tricky to remove the component for a form when the
						// field is selected. If someone wants to
						// solve that, they're welcome to enable delete here for
						// FormFields
						// ui.setEnabled(o,
						// ui.getAttachedObject(selectedComponent) instanceof
						// Form);
                                                //By Saad
                                           
						ui.setEnabled(o, ui.getAttachedObject(selectedComponent) instanceof Form && selectedForm != null && !selectedForm.isFinalised());// delete finalized form && !selectedForm.isFinalised()
					}
					else if (name.contains("Edit")) {
						ui.setEnabled(o, selectedForm != null && !selectedForm.isFinalised());
					}
					else if (name.contains("New")) {
						ui.setEnabled(o, true);
					}
                                        else if (name.contains("Unpublish")) {
						ui.setEnabled(o, ui.getAttachedObject(selectedComponent) instanceof Form && selectedForm != null && selectedForm.isFinalised());
					}
					else {
						ui.setEnabled(o, selectedForm != null);
					}
				}
			}
		}
	}

	public PagedListDetails getListDetails(Object list, int startIndex,
			int limit) {
		Form selectedForm = getSelectedForm();
		int totalItemCount = this.formResponseDao.getFormResponseCount(selectedForm);
if(totalItemCount>0){
	
		ArrayList<Object> responseRows = new ArrayList<Object>();
		for (FormResponse response : formResponseDao.getFormResponses(selectedForm, startIndex, limit)) {
			Object row = getRow(response);
			responseRows.add(row);
		}

		return new PagedListDetails(totalItemCount, responseRows.toArray(new Object[0]));
	}
	else
		return null;
	}

	/**
	 * Update the results for the selected form, taking into account the page
	 * number as well.
	 */
	public void formsTab_updateResults() {
		this.formResponseTablePageControls.refresh();
	}

	/**
	 * Shows a confirmation dialog before calling a method. The method to be
	 * called is passed in as a string, and then called using reflection.
	 * 
	 * @param methodToBeCalled
	 */
	public void showFormConfirmationDialog(String methodToBeCalled) {
		ui.showConfirmationDialog(methodToBeCalled, this);
	}

	// > THINLET EVENT HELPER METHODS
	/**
	 * @return the {@link Form} selected in the {@link #getFormsList()}, or
	 *         <code>null</code> if none is selected
	 */
	private Form getSelectedForm() {
		Object selectedComponent = formsList_getSelected();
		if (selectedComponent == null)
			return null;
		else
			return getForm(selectedComponent);
	}

	/** @return gets the ui component selected in the forms list */
	private Object formsList_getSelected() {
		return this.ui.getSelectedItem(getFormsList());
	}

	/** @return the forms list component */
	private Object getFormsList() {
		return find(FormsThinletTabController.FORMS_LIST_COMPONENT_NAME);
	}

	/**
	 * Given a {@link VisualForm}, the form edit window, this saves its details.
	 */
	private Form saveFormInformation(VisualForm visualForm) {

		Form form = new Form(visualForm.getName());
		form.setBindingsPolicy(visualForm.getBindingsPolicy());
		form.setOwner(visualForm.owner);

		// Fabaris_a.zanchi checks for drop down or radio buttuns with no list
		// assigned
		checkForEmptyDropDownList(visualForm);

		form.setDesignerVersion(this.ui.getFrontlineController().getSoftwareVersion());
		this.formsDao.saveForm(form);
		UserDao user = FormsThinletTabController.getCurrentInstance().getPluginController().getUserDao();
		String fsms_id = new String();
//		try {
//			fsms_id = user.getAdminFrontlineSMS_ID();
//		} catch (Exception e) {
//			fsms_id = new String();
//		}
//                DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH:mm:ss");
//                Calendar cal = Calendar.getInstance();
//                String d = dateFormat.format(cal.getTime()).toString();
//                fsms_id=d;
                fsms_id =UUID.randomUUID().toString();
		form.setId_flsmsId(form.getId() + "-" + fsms_id);
		// this.formsDao.saveForm(form);
		String listid = form.getId_flsmsId(); // formsDao.executeQueryXForm();

		// boolean isHead = true;
		boolean isTail = false;
		int index = 0;
		for (PreviewComponent comp : visualForm.getComponents()) {
			if (index == visualForm.getComponents().size() - 1)
				isTail = true;
			/*
			 * if (comp.getType() == FormFieldType.REPEATABLES) { //manage
			 * repeatables for (PreviewComponent repComp :
			 * comp.getRepeatables()) {
			 * repComp.generateEmptyFormFieldWithKey(comp
			 * .getComponent().getName(), index); } }
			 */
			comp.generateEmptyFormField(listid, index, isTail);
			comp.populateFormField();
			// isHead = false;
			index++;

		}

		Form savedForm = form; // this.formsDao.getFromId(Long.parseLong(listid));
		/* <data id=\"" + listid + "\"> id modificato da maria c. */
		String beginInstance = new String("<?xml version=\"1.0\"?><h:html xmlns=\"http://www.w3.org/2002/xforms\" xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:ev=\"http://www.w3.org/2001/xml-events\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:jr=\"http://openrosa.org/javarosa\">" + "<h:head><h:title>" + form.getName() + "</h:title><model><instance><data apos=\"'\"><id>" + listid + "</id>");
		String endInstance = "</data></instance>";
		String instance = new String();
		String bindings = new String("");
		String openBody = "</model></h:head><h:body><group appearance=\"field-list\">";
		index = 0;
		for (PreviewComponent pc : visualForm.getComponents()) {
			FormFieldType fieldType = FComponent.getFieldType(pc.getComponent().getClass());

			if (fieldType == FormFieldType.REPEATABLES) {
				// manages repeatables
				instance = instance + "<" + pc.getFormField().getName() + "_" + index + ">" + pc.getInstanceStringForRepetablesWithSurvey() + "</" + pc.getFormField().getName() + "_" + index + ">";
				// if (visualForm.getBindingsPolicy().equals("All"))
				bindings = bindings + pc.getBindStringAndOrPolicy();
				// else if (visualForm.getBindingsPolicy().equals("Any"))
				// bindings = bindings + pc.getBindStringOrPolicy(null);

				// Tries to remove every repeatable to add them again

				for (FormField repFF : pc.getRepeteablesFormFields()) {
					pc.getFormField().addRepetable(repFF);
				}
                                
				for (int x = 0; x < pc.getRepeatables().size(); x++) {
					PreviewComponent rpc = pc.getRepeatables().get(x);
					FormField ff = rpc.getFormField();
					ff.setCalculated(rpc.isCalculated());
					if (rpc.getFormula() != null) {
						ff.setFormula(rpc.getFormula().toFormulaString());
					}
					else {
						ff.setFormula(null);
					}
				}
			}else if (fieldType == FormFieldType.REPEATABLES_BASIC) {
				// manages repeatables
				if (pc.isBasicContainerFixed()) {
					instance = instance + "<count_" + pc.getFormField().getName() + "_" + index + ">" + pc.getNumberOfReps() + "</count_" + pc.getFormField().getName() + "_" + index + ">";
				}
				instance = instance + "<" + pc.getFormField().getName() + "_" + index + ">" + pc.getInstanceStringForRepeatables() + "</" + pc.getFormField().getName() + "_" + index + ">";
				// if (visualForm.getBindingsPolicy().equals("All"))
				bindings = bindings + pc.getBindStringAndOrPolicy();
				// else if (visualForm.getBindingsPolicy().equals("Any"))
				// bindings = bindings + pc.getBindStringOrPolicy(null);

				// Tries to remove every repeatable to add them again

				for (FormField repFF : pc.getRepeteablesFormFields()) {
					pc.getFormField().addRepetable(repFF);
				}
				// Fabaris_a.aknai
				for (int x = 0; x < pc.getRepeatables().size(); x++) {
					PreviewComponent rpc = pc.getRepeatables().get(x);
					FormField ff = rpc.getFormField();
					ff.setCalculated(rpc.isCalculated());
					if (rpc.getFormula() != null) {
						ff.setFormula(rpc.getFormula().toFormulaString());
					}
					else {
						ff.setFormula(null);
					}
				}
			}else {
				// Fabaris_a.aknai
				FormField ff = pc.getFormField();
				ff.setCalculated(pc.isCalculated());
				if (pc.getFormula() != null) {
					ff.setFormula(pc.getFormula().toFormulaString());
				}
				else {
					ff.setFormula(null);
				}

				if (fieldType != FormFieldType.SEPARATOR) {
					if (fieldType == FormFieldType.WRAPPED_TEXT || fieldType == FormFieldType.TRUNCATED_TEXT) {
						XMLOutputter outputter = new XMLOutputter();
						String escapedLabel = outputter.escapeAttributeEntities(pc.getComponent().getLabel());
						System.out.println(escapedLabel);
						instance = instance + "<" + pc.getFormField().getName() + "_" + index + ">" + escapedLabel + "</" + pc.getFormField().getName() + "_" + index + ">";
					}
					else if (fieldType == FormFieldType.CURRENCY_FIELD) {
						instance = instance + "<" + pc.getFormField().getName() + "_" + index + "/>" + "<" + pc.getFormField().getName() + "_" + index + "_curr" + "/>";
					}
					else {
						instance = instance + "<" + pc.getFormField().getName() + "_" + index + "/>";
					}
					// if (visualForm.getBindingsPolicy().equals("All"))
					bindings = bindings + pc.getBindStringAndOrPolicy();
					// else if (visualForm.getBindingsPolicy().equals("Any"))
					// bindings = bindings + pc.getBindStringOrPolicy(null);
				}
			}

			savedForm.addField(pc.getFormField());
			index++;
		}
		// Fabaris a.aknai
		// Set incremental positionIndex for repeteable sections
		for (FormField ff : savedForm.getFields()) {
			for (FormField rff : ff.getRepetables()) {
				rff.setPositionIndex(index);
				index++;
			}
		}
		FormField ff = visualForm.getComponents().get(0).getFormField();
		ff.setX_form(beginInstance + instance + endInstance + bindings + openBody + ff.getX_form());
		// Fabaris_a.aknai setting the owner if it is null
		String email;
		try {
			email = user.getSupervisorEmail();
		} catch (Exception e) {
			email = null;
		}
		if (form.getOwner() == null)
			form.setOwner(email);
		this.formsDao.updateForm(savedForm);

		// saves the owner form of reference lists
		for (Survey sur : visualForm.getTemporarySurveys()) {
			Survey toUpdate = getPluginController().getSurveyDao().getSurveyByNameAndOwner(sur.getName(), null);
			toUpdate.setOwner(savedForm);
			getPluginController().getSurveyDao().updateSurvey(toUpdate);
		}
		this.refresh();
		return savedForm;
	}

	// private void updateForm(List<PreviewComponent> old,
	// List<PreviewComponent> newComp, Form form, VisualForm visualForm) {
	public void updateForm(List<PreviewComponent> old,
			List<PreviewComponent> newComp, Form form, VisualForm visualForm) {
		// Fabaris_raji
		// Let's remove from database the ones the user removed

		List<PreviewComponent> toRemove = new ArrayList<PreviewComponent>();
		for (PreviewComponent c : old) {
			if (!newComp.contains(c)) {
				form.removeField(c.getFormField());
				toRemove.add(c);
			}
		}

		List<PreviewComponent> toAdd = new ArrayList<PreviewComponent>();
		for (PreviewComponent c : newComp) {
			if (!old.contains(c)) {
				c.generateEmptyFormField(Integer.toString(form.getFormMobileId()), newComp.indexOf(c), false);
				form.addField(c.getFormField());
				toAdd.add(c);
			}
		}

		// Fabaris_a.zanchi checks for drop down or radio buttuns with no list
		// assigned
		checkForEmptyDropDownList(visualForm);

		// a.zanchi removes the removed-components references from other
		// components

		for (PreviewComponent toRemoveComp : toRemove) {
			for (PreviewComponent c : newComp) {
				c.removeAllBindings(toRemoveComp);
			}
		}
                //********************************c
                String fsms_id = new String();
                fsms_id =UUID.randomUUID().toString();
		form.setId_flsmsId(form.getId() + "-" + fsms_id);
                //********************************c
		String formIndex = form.getId_flsmsId(); // Integer.toString(form.getFormMobileId());
		boolean isTail = false;
		String beginInstance = new String("<?xml version=\"1.0\"?><h:html xmlns=\"http://www.w3.org/2002/xforms\" xmlns:h=\"http://www.w3.org/1999/xhtml\" xmlns:ev=\"http://www.w3.org/2001/xml-events\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:jr=\"http://openrosa.org/javarosa\">" + "<h:head><h:title>" + form.getName() + "</h:title><model><instance><data apos=\"'\"><id>" + formIndex + "</id>");
		String endInstance = "</data></instance>";
		String instance = new String();
		String bindings = new String("");
		String openBody = "</model></h:head><h:body><group appearance=\"field-list\">";

		int index = 0;

		// newComp E' L'ARRAYLIST DEI PREVIEW COMPONENT
		for (PreviewComponent c : newComp) {
			// System.out.println("setting position "+index+" to field "+c.getFormField().getName());
			c.getFormField().setPositionIndex(index);
			if (index == newComp.size() - 1)
				isTail = true;
			c.generateEmptyFormField(formIndex, index, isTail);
			c.populateFormField();
			index++;
		}
		for (PreviewComponent c : newComp) {
			for (PreviewComponent rc : c.getRepeatables()) {
				// System.out.println("setting position "+index+" to field "+rc.getFormField().getName());
				rc.getFormField().setPositionIndex(index);
				index++;
			}
		}

		// CICLANO TUTTI GLI OGGETTI DELLA LISTA PER ATTACCARE IL TAG DI
		// CHIUSURA
		index = 0;
		for (PreviewComponent c : newComp) {
			FormFieldType fieldType = FComponent.getFieldType(c.getComponent().getClass());

			if (fieldType == FormFieldType.REPEATABLES) {
				// manages repeatables
				instance = instance + "<" + c.getFormField().getName() + "_" + index + ">" + c.getInstanceStringForRepetablesWithSurvey() + "</" + c.getFormField().getName() + "_" + index + ">";
				// if (visualForm.getBindingsPolicy().equals("All"))
				bindings = bindings + c.getBindStringAndOrPolicy();

				for (FormField repFF : c.getRepeteablesFormFields()) {
					// checks if repeatables are alredy in container!!
					if (!c.getFormField().getRepetables().contains(repFF))
						c.getFormField().addRepetable(repFF);
				}

				// remove unused repeatables
				c.getFormField().deleteUnusedRepeatables(c.getRepeteablesFormFields());

				// Fabaris_a.aknai
				for (int x = 0; x < c.getRepeatables().size(); x++) {
					PreviewComponent rpc = c.getRepeatables().get(x);
					FormField ff = rpc.getFormField();
					ff.setForm(form);
					ff.setCalculated(rpc.isCalculated());
					if (rpc.getFormula() != null) {
						ff.setFormula(rpc.getFormula().toFormulaString());
					}
					else {
						ff.setFormula(null);
					}
				}
			}
			else if (fieldType == FormFieldType.REPEATABLES_BASIC) {
				// manages repeatables
				if (c.isBasicContainerFixed()) {
					instance = instance + "<count_" + c.getFormField().getName() + "_" + index + ">" + c.getNumberOfReps() + "</count_" + c.getFormField().getName() + "_" + index + ">";
				}
				instance = instance + "<" + c.getFormField().getName() + "_" + index + ">" + c.getInstanceStringForRepeatables() + "</" + c.getFormField().getName() + "_" + index + ">";
				// if (visualForm.getBindingsPolicy().equals("All"))
				bindings = bindings + c.getBindStringAndOrPolicy();
				// else if (visualForm.getBindingsPolicy().equals("Any"))
				// bindings = bindings + c.getBindStringOrPolicy(null);

				for (FormField repFF : c.getRepeteablesFormFields()) {
					// checks if repeatables are alredy in container!!
					if (!c.getFormField().getRepetables().contains(repFF))
						c.getFormField().addRepetable(repFF);
				}

				// remove unused repeatables
				c.getFormField().deleteUnusedRepeatables(c.getRepeteablesFormFields());
				// Fabaris_a.aknai
				for (int x = 0; x < c.getRepeatables().size(); x++) {
					PreviewComponent rpc = c.getRepeatables().get(x);
					FormField ff = rpc.getFormField();
					ff.setForm(form);
					ff.setCalculated(rpc.isCalculated());
					if (rpc.getFormula() != null) {
						ff.setFormula(rpc.getFormula().toFormulaString());
					}
					else {
						ff.setFormula(null);
					}
				}
			}
			else {
				// Fabaris_a.aknai
				FormField ff = c.getFormField();
				ff.setCalculated(c.isCalculated());
				if (c.getFormula() != null) {
					ff.setFormula(c.getFormula().toFormulaString());
				}
				else {
					ff.setFormula(null);
				}

				if (fieldType != FormFieldType.SEPARATOR) {
					if (fieldType == FormFieldType.WRAPPED_TEXT || fieldType == FormFieldType.TRUNCATED_TEXT) {
						XMLOutputter outputter = new XMLOutputter();
						String escapedLabel = outputter.escapeAttributeEntities(c.getComponent().getLabel());
						System.out.println(escapedLabel);
						instance = instance + "<" + c.getFormField().getName() + "_" + index + ">" + escapedLabel + "</" + c.getFormField().getName() + "_" + index + ">";
					}
					else if (fieldType == FormFieldType.CURRENCY_FIELD) {
						instance = instance + "<" + c.getFormField().getName() + "_" + index + "/>" + "<" + c.getFormField().getName() + "_" + index + "_curr" + "/>";
					}
					else {
						instance = instance + "<" + c.getFormField().getName() + "_" + index + "/>";
					}
					// if (form.getBindingsPolicy().equals("All"))
					bindings = bindings + c.getBindStringAndOrPolicy();
					// else if (form.getBindingsPolicy().equals("Any"))
					// bindings = bindings + c.getBindStringOrPolicy(null);

				}
			}
			index++;
		}

		form.setDesignerVersion(this.ui.getFrontlineController().getSoftwareVersion());

		FormField ff = newComp.get(0).getFormField();
		ff.setX_form(beginInstance + instance + endInstance + bindings + openBody + ff.getX_form());

		form.setBindingsPolicy(visualForm.getBindingsPolicy());
		// Fabaris a.aknai setting id_flsmsId if it is null
		UserDao user = FormsThinletTabController.getCurrentInstance().getPluginController().getUserDao();
		//String fsms_id = new String();
               //***********c*************
             //  fsms_id =UUID.randomUUID().toString();
//		try {
//			fsms_id = user.getAdminFrontlineSMS_ID();
//		} catch (Exception e) {
//			fsms_id = null;
//		}
		//if (form.getId_flsmsId() == null){
               //******c************
		//form.setId_flsmsId(form.getId() + "_" + fsms_id);
              //  }
                       
              
		// Fabaris_a.aknai setting the owner if it is null
		String email;
		try {
			email = user.getSupervisorEmail();
		} catch (Exception e) {
			email = null;
		}
		if (form.getOwner() == null)
			form.setOwner(email);
		this.formsDao.updateForm(form);
		this.refresh();

	}

	/** Adds the result panel to the forms tab. */
	private void addFormResultsPanel() {
		Object pnRight = find("pnRight");
		ui.removeAll(pnRight);
		Object resultsView = ui.loadComponentFromFile(UI_FILE_RESULTS_VIEW, this);

		formResultsComponent = ui.find(resultsView, "formResultsList");
		this.formResponseTablePageControls = new ComponentPagingHandler(ui, this, this.formResultsComponent);

		Object placeholder = ui.find(resultsView, "pageControlsPanel");
		int index = ui.getIndex(ui.getParent(placeholder), placeholder);
		ui.add(ui.getParent(placeholder), this.formResponseTablePageControls.getPanel(), index);
		ui.remove(placeholder);

		ui.add(pnRight, resultsView);
	}

	/**
	 * Adds the form results panel to the GUI, and refreshes it for the selected
	 * form.
	 * 
	 * @param selected
	 *            The form whose results should be displayed.
	 */
	private void showResultsPanel(Form selected) {
		addFormResultsPanel();
		Object pagePanel = find("pagePanel");
		ui.setVisible(pagePanel, true);
		Object pnResults = find("pnFormResults");
		ui.setInteger(pnResults, "columns", 2);

		form_createColumns(selected);
		formsTab_updateResults();

		ui.setEnabled(formResultsComponent, selected != null && ui.getItems(formResultsComponent).length > 0);
		ui.setEnabled(find("btExportFormResults"), selected != null && ui.getItems(formResultsComponent).length > 0);
		this.ui.setVisible(this.ui.find("btExportFormResults"), false);
	}

	/**
	 * @param selectedComponent
	 *            Screen component's selectedItem
	 * @return a {@link Form} if a form or formfield was selected, or
	 *         <code>null</code> if none could be found
	 */
	private Form getForm(Object selectedComponent) {
		Object selectedAttachment = ui.getAttachedObject(selectedComponent);
		if (selectedAttachment == null || !(selectedAttachment instanceof Form)) {
			// The selected item was not a form item, so probably was a child of
			// that. Get it's parent, and check if that was a form instead
			selectedAttachment = this.ui.getAttachedObject(this.ui.getParent(selectedComponent));
		}

		if (selectedAttachment == null || !(selectedAttachment instanceof Form)) {
			// No form was found; return null
			return null;
		}
		else {
			return (Form) selectedAttachment;
		}
	}

	/** Aggiunto da Fabaris_maria c. */
	private FormField getFormField() {
		Object selectedComponentfield = getFormsList();
		Object selectedAttachment = ui.getAttachedObject(selectedComponentfield);
		if (selectedAttachment == null || !(selectedAttachment instanceof Form)) {
			// The selected item was not a form item, so probably was a child of
			// that. Get it's parent, and check if that was a form instead
			selectedAttachment = this.ui.getAttachedObject(this.ui.getParent(selectedComponentfield));
		}

		if (selectedAttachment == null || !(selectedAttachment instanceof Form)) {
			// No form was found; return null
			return null;
		}
		else {
			return (FormField) selectedAttachment;
		}
	}

	/** Fabaris_maria cilione. */
	/**
	 * Gets {@link Thinlet} table row component for the supplied
	 * {@link FormResponse}
	 * 
	 * @param response
	 *            the {@link FormResponse} to represent as a table row
	 * @return row component to insert in a thinlet table
	 */
	private Object getRow(FormResponse response) {
		Object row = ui.createTableRow(response);
		Contact sender = contactDao.getFromMsisdn(response.getSubmitter());
		String senderDisplayName = sender != null ? sender.getDisplayName() : response.getSubmitter();
		ui.add(row, ui.createTableCell(senderDisplayName));
		for (ResponseValue result : response.getResults()) {
			ui.add(row, ui.createTableCell(result.toString()));
		}
		return row;
	}

	/**
	 * Creates a {@link Thinlet} tree node for the supplied form.
	 * 
	 * @param form
	 *            The form to represent as a node.
	 * @return node to insert in thinlet tree
	 */
	private Object getNode(Form form) {
		log.trace("ENTER");
		// Create the node for this form

		log.debug("Form [" + form.getName() + "]");

		Image icon = getIcon(form.isFinalised() ? FormIcon.FORM_FINALISED : FormIcon.FORM);
		Object node = ui.createNode(form.getName(), form);
		ui.setExpanded(node, false);
		ui.setIcon(node, Thinlet.ICON, icon);

		// Create a node showing the group for this form
		Group g = form.getPermittedGroup();
		String groupName = g == null ? InternationalisationUtils.getI18nString(I18N_PLUGINS_FORMS_NOT_SET) : g.getName();
		Object groupNode = ui.createNode(InternationalisationUtils.getI18nString(I18N_PLUGINS_FORMS_GROUP, groupName), null);
		ui.setIcon(groupNode, Icon.GROUP);
		ui.add(node, groupNode);

		for (FormField field : form.getFields()) {
			Object child = ui.createNode(field.getLabel(), field);
			ui.setIcon(child, Thinlet.ICON, getIcon(field.getType()));
			ui.add(node, child);
		}
		log.trace("EXIT");
		return node;
	}

	private void form_createColumns(Form selected) {
		Object resultsTable = find("formResultsList");
		Object header = Thinlet.get(resultsTable, Thinlet.HEADER);
		ui.removeAll(header);
		if (selected != null) {
			// FIXME check if this constant can be removed from
			// frontlinesmsconstants class
			Object column = ui.createColumn(InternationalisationUtils.getI18nString(I18N_FORM_SUBMITTER), null);
			ui.setWidth(column, 100);
			ui.setIcon(column, Icon.PHONE_CONNECTED);
			ui.add(header, column);
			// For some reason we have a number column
			int count = 0;
			for (FormField field : selected.getFields()) {
				if (field.getType().hasValue()) {
					column = ui.createColumn(field.getLabel(), new Integer(++count));
					ui.setInteger(column, "width", 100);
					ui.setIcon(column, getIcon(field.getType()));
					ui.add(header, column);

				}
			}
		}
	}

	// > ACCESSORS
	/**
	 * Set {@link FormDao}
	 * 
	 * @param formsDao
	 *            new value for {@link #formsDao}
	 */
	public void setFormsDao(FormDao formsDao) {
		this.formsDao = formsDao;
	}

	/**
	 * Set {@link FormResponseDao}
	 * 
	 * @param formResponseDao
	 *            new value for {@link FormResponseDao}
	 */
	public void setFormResponseDao(FormResponseDao formResponseDao) {
		this.formResponseDao = formResponseDao;
	}

	/**
	 * Set {@link #contactDao}
	 * 
	 * @param contactDao
	 *            new value for {@link #contactDao}
	 */
	public void setContactDao(ContactDao contactDao) {
		this.contactDao = contactDao;
	}

	/*
	 * //29.10.2013 public void setConfigurationDao(ConfigurationDao
	 * configurationDao){ this.configurationDao = configurationDao; }
	 * 
	 * public ConfigurationDao getConfigurationDao(){ return
	 * this.configurationDao; }
	 */

	// > TEMPORARY METHODS THAT NEED SORTING OUT
	/**
	 * Gets an icon with the specified name.
	 * 
	 * @param iconPath
	 * @return currently this returns <code>null</code> - needs to be
	 *         implemented!
	 */
	private Image getIcon(String iconPath) {
		return this.ui.getIcon(iconPath);
	}

	/**
	 * Gets the icon for a particular {@link FComponent}.
	 * 
	 * @param fieldType
	 * @return icon to use for a particular {@link FComponent}.
	 */
	public Image getIcon(FormFieldType fieldType) {
		if (fieldType == FormFieldType.CHECK_BOX)
			return getIcon(FormIcon.CHECKBOX);

		if (fieldType == FormFieldType.CURRENCY_FIELD)
			return getIcon(FormIcon.CURRENCY_FIELD);

		if (fieldType == FormFieldType.DATE_FIELD)
			return getIcon(FormIcon.DATE_FIELD);
		if (fieldType == FormFieldType.EMAIL_FIELD)
			return getIcon(FormIcon.EMAIL_FIELD);
		if (fieldType == FormFieldType.NUMERIC_TEXT_FIELD)
			return getIcon(FormIcon.NUMERIC_TEXT_FIELD);
		/*
		 * if (fieldType == FormFieldType.PASSWORD_FIELD) return
		 * getIcon(FormIcon.PASSWORD_FIELD);
		 */
		if (fieldType == FormFieldType.PHONE_NUMBER_FIELD)
			return getIcon(FormIcon.PHONE_NUMBER_FIELD);
		if (fieldType == FormFieldType.TEXT_AREA)
			return getIcon(FormIcon.TEXT_AREA);
		if (fieldType == FormFieldType.TEXT_FIELD)
			return getIcon(FormIcon.TEXT_FIELD);

		// Fabaris_Raji
		if (fieldType == FormFieldType.DROP_DOWN_LIST)
			return getIcon(FormIcon.DROP_DOWN_LIST);
		// Fabaris_Raji
		if (fieldType == FormFieldType.RADIO_BUTTON)
			return getIcon(FormIcon.RADIO_BUTTON);
		// Fabaris_Raji
		if (fieldType == FormFieldType.SEPARATOR)
			return getIcon(FormIcon.SEPARATOR);
		/*
		 * if (fieldType == FormFieldType.TIME_FIELD) return
		 * getIcon(FormIcon.TIME_FIELD);
		 */
		if (fieldType == FormFieldType.TRUNCATED_TEXT)
			return getIcon(FormIcon.TRUNCATED_TEXT);
		if (fieldType == FormFieldType.WRAPPED_TEXT)
			return getIcon(FormIcon.WRAPPED_TEXT);
		// Fabaris a_zanchi
		if (fieldType == FormFieldType.REPEATABLES)
			return getIcon(FormIcon.REPEATABLES);
		if (fieldType == FormFieldType.REPEATABLES_BASIC)
			return getIcon(FormIcon.REPEATABLES_BASIC);
		// Fabaris a_zanchi
		if (fieldType == FormFieldType.GEOLOCATION)
			return getIcon(FormIcon.GEOLOCATION);
		//********************************************
		if (fieldType == FormFieldType.BARCODE)
			return getIcon(FormIcon.BARCODE);
		//********************************************
		if (fieldType == FormFieldType.IMAGE)
			return getIcon(FormIcon.IMAGE);
                //Mureed
                if (fieldType == FormFieldType.SIGNATURE)
			return getIcon(FormIcon.SIGNATURE);
		
		throw new IllegalStateException("No icon is mapped for field type: " + fieldType);
	}

	/**
	 * Set the DAO for this class
	 * 
	 * @param groupMembershipDao
	 */
	public void setGroupMembershipDao(GroupMembershipDao groupMembershipDao) {
		this.groupMembershipDao = groupMembershipDao;
	}

	// ---------------------------------------------------------------------------
	// 15/10/2013
	public void setConfigurationDao(ConfigurationDao configurationDao) {
		this.configurationDao = configurationDao;
	}

	public ConfigurationDao getConfigurationDao() {
		return this.configurationDao;
	}

	// ----------------------------------------------------------------------------

	/**
	 * Warn the user if he changes to another tab and has unsaved changes
	 */
	public void notify(FrontlineEventNotification notification) {
		// This object is registered to the UIGeneratorController and get
		// notified when the users changes tab
		if (notification instanceof TabChangedNotification) {
			String newTabName = ((TabChangedNotification) notification).getNewTabName();
			if (newTabName.equals(UI_FORM_TAB_NAME)) {
				this.refresh();
			}
		}
	}

	/**
	 * Fabaris_a.zanchi Sort-of a singleton getInstance method
	 * 
	 * @return
	 */
	public static FormsThinletTabController getCurrentInstance() {
		return ctrl;
	}

	/**
	 * Fabaris_a.zanchi checks for empty drop down list (Without any component)
	 * if there are displays a warning
	 * 
	 * @param vForm
	 */
	private void checkForEmptyDropDownList(VisualForm vForm) {
		HashMap<PreviewComponent, Integer> errorPc = new HashMap<PreviewComponent, Integer>();
		boolean displayWarning = false;
		int i = 1;
		for (PreviewComponent pc : vForm.getComponents()) {
			if (pc.getType() == FormFieldType.DROP_DOWN_LIST || pc.getType() == FormFieldType.RADIO_BUTTON || pc.getType() == FormFieldType.REPEATABLES) {
				if ((pc.getSurvey() == null) || (pc.getSurvey().getValues() == null) || (pc.getSurvey().getValues().size() == 0)) {
					displayWarning = true;
					errorPc.put(pc, i);
				}
			}
			i++;
		}
		if (displayWarning) {
			String errors = InternationalisationUtils.getI18nString(ERROR_EMPTY_LIST_TITLE) + "\n";
			for (PreviewComponent pc : errorPc.keySet()) {
				errors = errors + InternationalisationUtils.getI18nString(ERROR_EMPTY_LIST_POSITION, pc.getComponent().getName(), String.valueOf(errorPc.get(pc)), pc.getType().toString()) + "\n";
			}
			String message = InternationalisationUtils.getI18nString(ERROR_EMPTY_LIST);
			// message = message + InternationalisationUtils.
			JOptionPane.showMessageDialog(ui, message + "\n" + errors);
		}
	}

	/**
	 * Fabaris_a.zanchi External saveFormInformation accessor to save form
	 * prototypes from exeternal classes
	 * 
	 * @param form
	 */
	public void saveFormInformationPublic(VisualForm form) {
		saveFormInformation(form);
	}

	/**
	 * Fabaris a.zanchi External updarteForm accessor.
	 * 
	 * @param old
	 * @param newComp
	 * @param form
	 * @param visualForm
	 */
	public void updateFormPublic(List<PreviewComponent> old,
			List<PreviewComponent> newComp, Form form, VisualForm visualForm) {
		updateForm(old, newComp, form, visualForm);
	}

}
