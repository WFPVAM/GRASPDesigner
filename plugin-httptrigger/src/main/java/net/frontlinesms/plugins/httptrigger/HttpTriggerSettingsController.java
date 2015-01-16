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
package net.frontlinesms.plugins.httptrigger;

import net.frontlinesms.plugins.PluginSettingsController;
import net.frontlinesms.plugins.httptrigger.ui.HttpTriggerSettingsRootSectionHandler;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;
import net.frontlinesms.ui.settings.UiSettingsSectionHandler;
/**
 * This class is used to handle httpTrigger's specific settings in the preferences area of frontlineSMS
 */
public class HttpTriggerSettingsController implements ThinletUiEventHandler, PluginSettingsController {
	private UiGeneratorController uiController;
	private HttpTriggerPluginController pluginController;
	private String pluginIcon;

	public HttpTriggerSettingsController(HttpTriggerPluginController pluginController, UiGeneratorController uiController, String pluginIcon) {
		this.pluginController = pluginController;
		this.uiController = uiController;
		this.pluginIcon = pluginIcon;
	}

	public String getTitle() {
		return this.pluginController.getName(InternationalisationUtils.getCurrentLocale());
	}
	
	public void addSubSettingsNodes(Object rootSettingsNode) {}
	
	public UiSettingsSectionHandler getHandlerForSection(String section) {
		return null;
	}

	public UiSettingsSectionHandler getRootPanelHandler() {
		return new HttpTriggerSettingsRootSectionHandler(this.uiController, this.getTitle(), this.pluginIcon);
	}

	public Object getRootNode() {
		HttpTriggerSettingsRootSectionHandler rootHandler = new HttpTriggerSettingsRootSectionHandler(this.uiController, this.getTitle(), this.pluginIcon);
		return rootHandler.getSectionNode();
	}

}
