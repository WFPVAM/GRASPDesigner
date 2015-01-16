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
package net.frontlinesms.ui.handler.settings;

import java.sql.Time;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.List;

import javax.swing.JOptionPane;

import net.frontlinesms.AppProperties;
import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.domain.DataTransfer;
import net.frontlinesms.settings.BaseSectionHandler;
import net.frontlinesms.settings.DatabaseSettings;
import net.frontlinesms.settings.DatabaseTransferSettings;

import net.frontlinesms.settings.FrontlineValidationMessage;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.UiProperties;
import net.frontlinesms.ui.handler.core.DatabaseConnectionFailedDialog;
import net.frontlinesms.ui.i18n.InternationalisationUtils;
import net.frontlinesms.ui.settings.UiSettingsSectionHandler;
import org.apache.log4j.Logger;
/**
 * UI Handler for the database transfer
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/> 
 *
 */
public class SettingsDatabaseTransferSectionHandler extends BaseSectionHandler implements UiSettingsSectionHandler, ThinletUiEventHandler {
	private static final String UI_SECTION_DATABASE = "/ui/core/settings/general/pnDatabaseTransferSettings.xml";
	private static final String UI_SECTION_DATABASE_AS_DIALOG = "/ui/core/database/pnSettings.xml";
	private static final String I18N_DATATRANSFER_SETTINGS  = "menuitem.edit.datatransfer.settings";		
	/** The panel containing individual settings controls */
	private static final String COMPONENT_PN_DATABASE_SETTINGS = "pnSettings";
	/** The constant property key for database passwords */
	private static final String PASSWORD_PROPERTY_KEY = "dbpassword";
	private static final String I18N_DATATRANSFER_SETTINGS_CHANGED = "message.datatransfer.settings.changed";		
	private static final String SECTION_ITEM_IS_SHARABLE = "APPEARANCE_SHARABLE";
	private static final String SECTION_ITEM_IS_MANUAL = "APPEARANCE_MANUAL";
	private static final String SECTION_ITEM_IS_SCHEDULED = "APPEARANCE_SCHEDULED";
	private static final String COMPONENT_PN_ALERTS = "pnAlerts";
	/** Thinlet Component Name: checkbox indicating sharable*/	
	private static final String COMPONENT_CB_SHARABLE = "cbSharable";
	/** Thinlet Component Name: checkbox indicating manual*/	
	private static final String COMPONENT_CB_MANUAL ="cbManual";
	/** Thinlet Component Name: checkbox indicating sheduled*/	
	private static final String COMPONENT_CB_SCHEDULED ="cbScheduled";	

	DatabaseTransferSettings selectedSettings;
	Map<String, Object> originalValues;
	Logger log;

	private Object dialogComponent;	
	public SettingsDatabaseTransferSectionHandler (UiGeneratorController ui) {
		super(ui);
	}
	protected void init() {	
		this.panel = uiController.loadComponentFromFile(UI_SECTION_DATABASE, this);		
		refreshSettingsPanel();
		/**Populate panel from database*/
		initSharableSettings();
	}

	/** Refresh the panel containing settings specific to the currently-selected {@link DatabaseSettings}. */
	private void refreshSettingsPanel() {
		/** populate the settings panel*/	
		Object settingsPanel = find(COMPONENT_PN_DATABASE_SETTINGS);		
		ArrayList<String> als=new ArrayList<String>();			
		DatabaseTransferSettings selectedSettings=new DatabaseTransferSettings();
		/*select the string properties from the table*/
		als=selectedSettings.getPropertyKeys();
		Object field = null;			
		for(String key :als) {
			// TODO would be nice to set icons for the different settings
			this.uiController.add(settingsPanel, this.uiController.createLabel(key));
			// TODO may want to set the types of these, e.g. password, number etc.
			String value = selectedSettings.getPropertyValue(key);			
			if (key.equals(PASSWORD_PROPERTY_KEY)) {				
				field = this.uiController.createPasswordfield(key, value);
			} else {
				field = this.uiController.createTextfield(key, value);
			}

			this.uiController.add(settingsPanel, field);
		}
	}	
	public void initSharableSettings() {		
		//log.trace("ENTER");
		/**Populate panel*/
		DatabaseTransferSettings selectedSettings=new DatabaseTransferSettings();		
		ArrayList<String> alsb=new ArrayList<String>();	
		/*Get the list of boolean values from table*/
		alsb=selectedSettings.getPropertyBooleanKeys();

		boolean isSharable=false;		
		boolean isManual=false;
		boolean isScheduled=false;

		UiProperties uiProperties = UiProperties.getInstance();			
		for(String key :alsb) { 			
			if (key.equals("sharable")) {
				isSharable=selectedSettings.getPropertyBooleanValue(key);					 
				uiProperties.setSharable(isSharable);						
			}
			if(key.equals("manual")){
				isManual = selectedSettings.getPropertyBooleanValue(key);
				uiProperties.setManual(isManual);
			} 			 
			if(key.equals("scheduled")){
				isScheduled =selectedSettings.getPropertyBooleanValue(key);	
				uiProperties.setScheduled(isScheduled);
			}			
		}
		 
		String radioButtonName=null;			
		if(isSharable) {						
			this.uiController.setSelected(find(COMPONENT_CB_SHARABLE), true);	
			if(isManual) {
				radioButtonName = COMPONENT_CB_MANUAL;
				this.uiController.setSelected(find(radioButtonName), true);		
			}
			if(isScheduled) {
				radioButtonName = COMPONENT_CB_SCHEDULED;
				this.uiController.setSelected(find(radioButtonName), true);		
			}  
		}
		else
		{				
			isManual=false;
			isScheduled=false;
			this.uiController.setSelected(find(COMPONENT_CB_SHARABLE), false);					
		}	

		uiProperties.setSharable(isSharable);		
		uiProperties.setManual(isManual);
		uiProperties.setScheduled(isScheduled);
	}

	public void savedb() {
		/**Get the settings  we are modifying and save in table */
		DatabaseTransferSettings selectedSettings =new DatabaseTransferSettings ();	
		DataTransfer dt=new DataTransfer();
		List<Setting_transfer> displayedSettings = getDisplayedSettingValues();
		/**Linked Hash map for String values */
		LinkedHashMap<String, String> lhm= new LinkedHashMap<String, String>();	
		/**Linked hash map for boolean values */
		LinkedHashMap<String, Boolean> lhmb= new LinkedHashMap<String, Boolean>();	
		/**Populate string values*/
		for(Setting_transfer displayed : displayedSettings) {
			lhm.put(displayed.getKey(), displayed.getValue());		
		}		
		boolean isSharable 		= this.uiController.isSelected(this.uiController.find(panel,COMPONENT_CB_SHARABLE));
		boolean isManual 		= this.uiController.isSelected(this.uiController.find(panel, COMPONENT_CB_MANUAL));
		boolean isScheduled 	= this.uiController.isSelected(this.uiController.find(panel, COMPONENT_CB_SCHEDULED));
		/**Populate boolean values*/
		lhmb.put("sharable",isSharable);
		lhmb.put("manual",isManual);
		lhmb.put("scheduled",isScheduled);
		/**Populate DataTransfer Instance*/
		dt.setServername(lhm.get("servername"));
		dt.setServerport(lhm.get("serverport"));
		dt.setDbname(lhm.get("dbname"));
		dt.setUsername(lhm.get("dbusername"));
		dt.setPassword(lhm.get("dbpassword"));		
		dt.setSharable(lhmb.get("sharable"));
		dt.setManual(lhmb.get("manual"));
		dt.setScheduled(lhmb.get("scheduled"));
		/**SCHEDULED TIME IS FIXED*/
		String Fixedscheduledtime="10:00:00";
		java.sql.Time scheduledtime = java.sql.Time.valueOf(Fixedscheduledtime);	
		dt.setScheduledtime(scheduledtime);		
		selectedSettings.savePropertiesTable(dt);
		alert(InternationalisationUtils.getI18nString(I18N_DATATRANSFER_SETTINGS_CHANGED));
		//this.uiController.alert(InternationalisationUtils.getI18nString(I18N_MESSAGE_DATABASE_SETTINGS_CHANGED));
	}	 
	public void alert(String msg) {	
		JOptionPane.showMessageDialog(null, msg);				
	}

	/** @return the settings and values that are currently displayed on the UI*/
	private List<Setting_transfer> getDisplayedSettingValues() {
		Object settingsPanel = find(COMPONENT_PN_DATABASE_SETTINGS);
		Object[] settingsComponents = this.uiController.getItems(settingsPanel);		
		ArrayList<Setting_transfer> settings = new ArrayList<Setting_transfer>();
		for (int settingIndex=1; settingIndex<settingsComponents.length; settingIndex+=2) {
			// This code assumes that all settings are in TEXTFIELDS; this may change in the future.
			Object tf = settingsComponents[settingIndex];
			String key = this.uiController.getName(tf);			
			String value = this.uiController.getText(tf);			
			settings.add(new Setting_transfer(key, value));			
		}		
		return settings;
	} 

	/**
	 * Show this panel as a dialog.  The dialog will be removed by default by the removeDialog method.
	 * @param titleI18nKey*/

	public void showAsDialog() {
		Object dialogComponent = this.uiController.createDialog("Pwals");
		this.panel = this.uiController.loadComponentFromFile(UI_SECTION_DATABASE_AS_DIALOG, this);
		this.init();		
		this.uiController.add(dialogComponent, panel);
		this.uiController.setCloseAction(dialogComponent, "removeDialog", dialogComponent, this);
		this.uiController.add(dialogComponent);
		this.dialogComponent = dialogComponent;
	} 

	public void setSharable(Object panel, boolean isSharable) {
		this.uiController.setEnabled(panel, isSharable);
		for (Object obj : this.uiController.getItems(panel)) {
			this.uiController.setEnabled(obj, isSharable);
		}

	}
	public void logoRadioButtonChanged(Object panel, boolean isCustom) {
		Object newValue;
		if (this.uiController.isSelected(find(COMPONENT_CB_SHARABLE))) {
			newValue = COMPONENT_CB_SHARABLE;
		} else if (this.uiController.isSelected(find(COMPONENT_CB_MANUAL))) {
			newValue = COMPONENT_CB_MANUAL;
		} else {
			newValue = COMPONENT_CB_SCHEDULED;
		}

		super.settingChanged(SECTION_ITEM_IS_SHARABLE, newValue);
	}
	public void isSharableChanged(boolean isSharable) {
		super.settingChanged(SECTION_ITEM_IS_SHARABLE,isSharable);
	}
	public void isManualChanged(boolean isManual) {
		super.settingChanged(SECTION_ITEM_IS_MANUAL,isManual);
	}
	public void isScheduledChanged(boolean isScheduled) {
		super.settingChanged(SECTION_ITEM_IS_SCHEDULED,isScheduled);
	}

	public void removeDialog() {
		this.uiController.remove(this.dialogComponent);
	}

	public List<FrontlineValidationMessage> validateFields() {
		return null;
	}

	public Object getSectionNode() {
		return createSectionNode(InternationalisationUtils.getI18nString(I18N_DATATRANSFER_SETTINGS), this, "/icons/datatransfer.png");
	}
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void save() {		
	}
}
class Setting_transfer {
	private final String key;
	private final String value;

	public Setting_transfer(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}


}
