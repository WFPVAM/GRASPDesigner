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
package net.frontlinesms.ui.handler.core;

import java.awt.RenderingHints.Key;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import java.util.List;

import org.h2.value.Value;

import net.frontlinesms.AppProperties;
import net.frontlinesms.settings.DatabaseTransferSettings;
import net.frontlinesms.ui.DatabaseSettings;
import net.frontlinesms.ui.FrontlineUI;
import net.frontlinesms.ui.handler.BasePanelHandler;

import net.frontlinesms.ui.i18n.InternationalisationUtils;
import net.frontlinesms.ui.i18n.TextResourceKeyOwner;

/**
 * Thinlet UI Component event handler for displaying and modifying database settings.
 * @author@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph       
 * www.fabaris.it <http://www.fabaris.it/> 
 */
@TextResourceKeyOwner
public class DatabaseTransferSettingsPanel extends BasePanelHandler implements DatabaseTransferSettingsChangedCallbackListener {

//> STATIC CONSTANTS
	/** XML UI Layout File path: database settings panel */
	private static final String XML_SETTINGS_PANEL = "/ui/core/database/pnSettings.xml";
	/** i18n Text Key: "Database Config" - used as title for the dialog, and menu text to launch the dialog */
	private static final String I18N_KEY_DATABASE_TRANSFER_CONFIG ="common.remote.config";	
	private static final String COMPONENT_SETTINGS_SELECTION ="cbConfigFile";
	/** The panel containing individual settings controls */
	private static final String COMPONENT_SETTINGS_PANEL = "pnSettings";
	/** UI Component: cancel button */
	private static final String COMPONENT_CANCEL_BUTTON = "btCancel";
	/** The constant property key for database passwords */
	private static final String PASSWORD_PROPERTY_KEY = "password";

//> INSTANCE PROPERTIES
	/** The settings currently selected in the combobox */
	private DatabaseTransferSettings selectedSettings;
	/** A boolean saying whether or not the application must restart after the changes */
	private boolean needToRestartApplication;
	
	/** Dialog UI Component.  This should only be used if {@link #showAsDialog()} is called, and then should only be used by {@link #removeDialog()}. */
	private Object dialogComponent;
	
	/** Callback listener to take action if the database settings have changed. */
	private DatabaseTransferSettingsChangedCallbackListener settingsChangedCallbackListener;
	
//> CONSTRUCTORS
	private DatabaseTransferSettingsPanel(FrontlineUI ui) {
		super(ui);
	}
	
	/**
	 * Initialise the UI.
	 * @param restartRequired
	 */
	private void init(String selectedPath) {
		super.loadPanel(XML_SETTINGS_PANEL);	
		//LinkedHashMap<String, String> settings = DatabaseTransferSettings.loadProperties() ;
		//Set keySet = settings.keySet();	
	// populate settings panel
		refreshSettingsPanel();
	}
	
	
//> ACCESSORS
	@Override
	public Object getPanelComponent() {
		return super.getPanelComponent();
	}

	/**
	 * Sets the saveMethod
	 * @param methodCall
	 * @param eventHandler
	 */
	public void setSettingsChangedCallbackListener(DatabaseTransferSettingsChangedCallbackListener callbackListener) {
		this.settingsChangedCallbackListener = callbackListener;
	}
	
	/** @param enabled <code>true</code> if the cancel button should be enabled, <code>false</code> if it should be disabled */
	public void setCancelEnabled(boolean enabled) {
		ui.setEnabled(find(COMPONENT_CANCEL_BUTTON), enabled);
	}

//> INSTANCE HELPER METHODS
	
//> UI HELPER METHODS
	/** Refresh the panel containing settings specific to the currently-selected {@link DatabaseSettings}. */
	/** Refresh the panel containing settings specific to the currently-selected {@link DatabaseSettings}. */
	private void refreshSettingsPanel() {
		// populate the settings panel
		Object settingsPanel = super.find(COMPONENT_SETTINGS_PANEL);
		ui.removeAll(settingsPanel);		
		//this.selectedSettings.loadProperties(); commented for test
		for(String key : this.selectedSettings.getPropertyKeys()) {
			// TODO would be nice to set icons for the different settings
			ui.add(settingsPanel, ui.createLabel(key));
			// TODO may want to set the types of these, e.g. password, number etc.
			if (key.equals(PASSWORD_PROPERTY_KEY))
				ui.add(settingsPanel, ui.createPasswordfield(key, this.selectedSettings.getPropertyValue(key)));
			else
				ui.add(settingsPanel, ui.createTextfield(key, this.selectedSettings.getPropertyValue(key)));
		}
	}
	
	private Object getConfigFileSelecter() {
		return super.find(COMPONENT_SETTINGS_SELECTION);
	}
	
	/** @return the settings and values that are currently displayed on the UI */
	private List<Setting_Transfer> getDisplayedSettingValues() {
		Object settingsPanel = super.find(COMPONENT_SETTINGS_PANEL);
		Object[] settingsComponents = ui.getItems(settingsPanel);
		
		ArrayList<Setting_Transfer> settings = new ArrayList<Setting_Transfer>();
		for (int settingIndex=1; settingIndex<settingsComponents.length; settingIndex+=2) {
			// This code assumes that all settings are in TEXTFIELDS; this may change in the future.
			Object tf = settingsComponents[settingIndex];
			String key = ui.getName(tf);
			String value = ui.getText(tf);
			settings.add(new Setting_Transfer(key, value));
		}
		
		return settings;
	}
	
//> UI EVENT METHODS
	public void configFileChanged() {
		String selected = ui.getText(ui.getSelectedItem(getConfigFileSelecter()));		
		if (selected != null) {
			this.openNewDialog(selected);
			this.removeDialog();
		}
	}
	
	/**
	 * Save button pressed: Saves the database settings and restarts FrontlineSMS to use
	 * the new settings.
	 */
	public void save() {
		// get the settings we are modifying
		DatabaseTransferSettings selectedSettings = this.selectedSettings;	
		List<Setting_Transfer> displayedSettings = getDisplayedSettingValues();
		LinkedHashMap<String, String> lhm= new LinkedHashMap<String, String>();		
			for(Setting_Transfer displayed : displayedSettings) {			
				lhm.put(displayed.getKey(), displayed.getValue());
			}				
			//selectedSettings.saveProperties(lhm);	commented to test
			this.settingsChangedCallbackListener.handleDatabaseSettingsChanged();
		}
	//}

	/** Cancel button pressed. */
	public void cancel() {
		// Currently this method should only function properly if we are displaying
		// the settings panel in a dialog.
		assert(this.dialogComponent != null) : "Currently the cancel button should only be enabled if we are displaying as a dialog.";
		
		// If we are displaying the settings panel as a dialog, remove it.
		removeDialog();
	}
	
	/** This should be called if we are showing the settings panel in a dialog.  The dialog should now be removed. */
	public void handleDatabaseSettingsChanged() {
		// Display alert warning the user that their settings have changed, and they
		// must restart FrontlineSMS for the changes to take effect.  In a perfect world,
		// we would AUTOMATICALLY restart here, but this is not quite trivial, so is not
		// implemented at this stage.
		if (needToRestartApplication)
			ui.alert("Settings saved.  Please restart FrontlineSMS immediately."); // FIXME i18n		
		removeDialog();	
	}

	/**
	 * Show this panel as a dialog.  The dialog will be removed by default by the removeDialog method.
	 * @param titleI18nKey
	 */
	public void showAsDialog(boolean needToRestartApplication) {
		this.settingsChangedCallbackListener = this;
		this.needToRestartApplication = needToRestartApplication;
		
		Object dialogComponent = ui.createDialog(InternationalisationUtils.getI18nString( I18N_KEY_DATABASE_TRANSFER_CONFIG));
		ui.add(dialogComponent, this.getPanelComponent());
		ui.setCloseAction(dialogComponent, "removeDialog", dialogComponent, this);
		ui.add(dialogComponent);
		this.dialogComponent = dialogComponent;
	}
	
//> UI EVENT METHODS
	public void removeDialog() {
		this.ui.removeDialog(this.dialogComponent);
	}
	
//> STATIC FACTORIES
	public static DatabaseTransferSettingsPanel createNew(FrontlineUI ui, String selectedPath) {
		DatabaseTransferSettingsPanel panel = new DatabaseTransferSettingsPanel(ui);
		panel.init(selectedPath);
		return panel;
	}
	
	public void openNewDialog(String selectedPath) {
		DatabaseTransferSettingsPanel databaseSettings = DatabaseTransferSettingsPanel.createNew(ui, selectedPath);
		databaseSettings.showAsDialog(needToRestartApplication);
	}
}

class Setting_Transfer {
	private final String key;
	private final String value;
	
	public Setting_Transfer(String key, String value) {
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
