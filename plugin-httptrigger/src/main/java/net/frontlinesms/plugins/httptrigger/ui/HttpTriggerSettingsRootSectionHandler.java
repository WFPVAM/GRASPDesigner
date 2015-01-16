/**
 * GRASP(Geo-referential Real-time Acquisition Statistics Platform) Designer Tool <http://www.fabaris.it>
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
package net.frontlinesms.plugins.httptrigger.ui;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.events.AppPropertiesEventNotification;
import net.frontlinesms.plugins.httptrigger.HttpTriggerProperties;
import net.frontlinesms.settings.BaseSectionHandler;
import net.frontlinesms.settings.FrontlineValidationMessage;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.settings.UiSettingsSectionHandler;

public class HttpTriggerSettingsRootSectionHandler extends BaseSectionHandler implements UiSettingsSectionHandler, ThinletUiEventHandler {
	private static final String UI_SECTION_ROOT = "/ui/plugins/httptrigger/settings/pnRootSettings.xml";
	private static final String UI_COMPONENT_TF_PORT = "tfPort";
	private static final String UI_COMPONENT_CB_AUTO_START = "cbAutoStart";
	private static final String I18N_SETTINGS_MESSAGE_INVALID_PORT = "plugins.httptrigger.settings.invalid.port.number";
	
	private static final String SECTION_ITEM_PORT_NUMBER = "PLUGINS_HTTPTRIGGER_PORT_NUMBER";
	private static final String SECTION_ITEM_AUTO_START = "PLUGINS_HTTPTRIGGER_AUTO_START";
	private String pluginName;
	private String pluginIcon;

	public HttpTriggerSettingsRootSectionHandler (UiGeneratorController ui, String pluginName, String pluginIcon) {
		super(ui);
		this.pluginName = pluginName;
		this.pluginIcon = pluginIcon;
	}
	
	protected void init() {
		this.panel = uiController.loadComponentFromFile(UI_SECTION_ROOT, this);
		
		HttpTriggerProperties httpTriggerProperties = HttpTriggerProperties.getInstance();
		String portNumber = Integer.toString(httpTriggerProperties.getListenPort());
		boolean isAutoStart = httpTriggerProperties.isAutostart();
		this.uiController.setText(find(UI_COMPONENT_TF_PORT), portNumber);
		this.uiController.setSelected(find(UI_COMPONENT_CB_AUTO_START), isAutoStart);
		
		this.originalValues.put(SECTION_ITEM_PORT_NUMBER, portNumber);
		this.originalValues.put(SECTION_ITEM_AUTO_START, isAutoStart);
	}
	
	/**
	 * Called when the "Auto start" checkbox has changed state
	 * @param autoStart
	 */
	public void autoStartChanged(boolean autoStart) {
		super.settingChanged(SECTION_ITEM_AUTO_START, autoStart);
	}
	
	/**
	 * Called when the port number has changed
	 * @param autoStart
	 */
	public void portNumberChanged(String portNumber) {
		super.settingChanged(SECTION_ITEM_PORT_NUMBER, portNumber);
	}

	public void save() {
		// Save the port number
		HttpTriggerProperties properties = HttpTriggerProperties.getInstance();
		properties.setListenPort(Integer.parseInt(this.uiController.getText(find(UI_COMPONENT_TF_PORT))));
		properties.setAutostart(this.uiController.isSelected(find(UI_COMPONENT_CB_AUTO_START)));
		properties.saveToDisk();
		
		this.eventBus.notifyObservers(new AppPropertiesEventNotification(HttpTriggerProperties.class, HttpTriggerProperties.PROP_LISTEN_PORT));
		this.eventBus.notifyObservers(new AppPropertiesEventNotification(HttpTriggerProperties.class, HttpTriggerProperties.PROP_AUTOSTART));
	}

	public List<FrontlineValidationMessage> validateFields() {
		List<FrontlineValidationMessage> validationMessages = new ArrayList<FrontlineValidationMessage>();
		
		try {
			if (Integer.parseInt(this.uiController.getText(find(UI_COMPONENT_TF_PORT))) <= 0) {
				validationMessages.add(new FrontlineValidationMessage(I18N_SETTINGS_MESSAGE_INVALID_PORT, null, this.pluginIcon));
			}
		} catch (NumberFormatException e) {
			validationMessages.add(new FrontlineValidationMessage(I18N_SETTINGS_MESSAGE_INVALID_PORT, null, this.pluginIcon));
		}
		
		return validationMessages;
	}

	public String getTitle() {
		return this.pluginName;
	}

	public Object getSectionNode() {
		return createSectionNode(getTitle(), this, this.pluginIcon);
	}
}
