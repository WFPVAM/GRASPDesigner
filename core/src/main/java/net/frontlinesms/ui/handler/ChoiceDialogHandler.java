package net.frontlinesms.ui.handler;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;


/**
 * This handles the "choice" dialog, which lets the user choose between "Yes", "No" & "Cancel" with
 * custom labels.
 * @author Morgan Belkadi <morgan@frontlinesms.com>
 */
public class ChoiceDialogHandler implements ThinletUiEventHandler {

//> STATIC CONSTANTS
	/** UI XML File Path */
	private static final String UI_FILE_DELETE_OPTION_DIALOG_FORM = "/ui/core/util/dgChoice.xml";

	/** UI Thinlet component: panel containing all custom labels **/
	private static final String UI_COMPONENT_PN_LABELS = "pnLabels";
	private static final String UI_COMPONENT_PN_LABELS_FOR_FORM = "formPanel";
	private static final String UI_COMPONENT_BT_YES = "btYes";
	private static final String UI_COMPONENT_BT_NO = "btNo";
	private static final String UI_COMPONENT_BT_CANCEL = "btCancel";
	private static final String UI_COMPONENT_BT_CLOSE = "btClose";
	private static final String I18N_SENTENCE_DELETE_SUBGROUP_FROM_GROUP = "sentence.choise.remove.contacts.of.subgroups";
	
//> INSTANCE PROPERTIES
	private Logger log = Logger.getLogger(this.getClass());
	private UiGeneratorController ui;

	private Object dialogComponent;

	private ThinletUiEventHandler buttonActionHandler;
	
//> CONSTRUCTORS
	public ChoiceDialogHandler (UiGeneratorController uiController, ThinletUiEventHandler handler) {
		this.ui = uiController;
		this.buttonActionHandler = handler;
		this.dialogComponent = this.ui.loadComponentFromFile(UI_FILE_DELETE_OPTION_DIALOG_FORM, this);
	}
	
	
	
	public void showChoiceDialog (boolean showCancelButton,boolean showYesNoButton, boolean showFormMessage , String yesMethod, String noMethod, String propertyKey, String ... i18nValues) {
		log.trace("Populating choice dialog with custom labels (Key:" + propertyKey + ")");
		Object btCancel = find(UI_COMPONENT_BT_CANCEL);
		Object btClose = find(UI_COMPONENT_BT_CLOSE);
		this.ui.setVisible(btCancel, showCancelButton);
		this.ui.setVisible(btClose,!showCancelButton);
		Object pnLabels = find(UI_COMPONENT_PN_LABELS);		
		if(showFormMessage){
			String val = InternationalisationUtils.getI18nString(I18N_SENTENCE_DELETE_SUBGROUP_FROM_GROUP);
			if(null != val && !"".equals(val)){
				this.ui.add(pnLabels, this.ui.createLabel(val));
			}
		}
		for (String label : InternationalisationUtils.getI18nStrings(propertyKey, i18nValues)) {
			this.ui.add(pnLabels, this.ui.createLabel(label));
		}
		setButtonMethod(UI_COMPONENT_BT_YES, yesMethod);
		setButtonMethod(UI_COMPONENT_BT_NO, noMethod);
		this.ui.setVisible(find(UI_COMPONENT_BT_YES), showYesNoButton);
		this.ui.setVisible(find(UI_COMPONENT_BT_NO), showYesNoButton);
		this.ui.add(this.dialogComponent);
		log.trace("EXIT");
	}
	
	public void removeDialog() {
		ui.remove(this.dialogComponent);
	}
	
	public void setFirstButtonText (String text) {
		setButtonText(UI_COMPONENT_BT_YES, text);
	}
	
	public void setSecondButtonText (String text) {
		setButtonText(UI_COMPONENT_BT_NO, text);
	}
	
	public void setFirstButtonIcon(String iconPath) {
		setButtonIcon(UI_COMPONENT_BT_YES, iconPath);
	}
	
	public void setSecondButtonIcon(String iconPath) {
		setButtonIcon(UI_COMPONENT_BT_NO, iconPath);
	}
	
//> PRIVATE HELPER METHODS
	private void setButtonText(String buttonName, String newText) {
		this.ui.setText(find(buttonName), newText);
	}
	
	private void setButtonIcon(String buttonName, String iconPath) {
		this.ui.setIcon(find(buttonName), iconPath);
	}
	
	private void setButtonMethod(String buttonName, String method) {
		this.ui.setAction(find(buttonName), method, this.dialogComponent, this.buttonActionHandler);
	}
	
	private Object find(String componentName) {
		return this.ui.find(this.dialogComponent, componentName);
	}
}
