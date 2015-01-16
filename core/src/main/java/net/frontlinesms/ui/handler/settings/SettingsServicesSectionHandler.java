package net.frontlinesms.ui.handler.settings;

import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class SettingsServicesSectionHandler extends SettingsEmptySectionHandler {
	private static final String I18N_SETTINGS_MENU_SERVICES = "settings.menu.services";

	public SettingsServicesSectionHandler(UiGeneratorController ui) {
		super(ui, I18N_SETTINGS_MENU_SERVICES);
	}
	
	public Object getSectionNode() {
		Object servicesRootNode = createSectionNode(InternationalisationUtils.getI18nString(I18N_SETTINGS_MENU_SERVICES), this, "/icons/database_execute.png");

		SettingsDevicesSectionHandler devicesHandler = new SettingsDevicesSectionHandler(uiController);
		uiController.add(servicesRootNode, devicesHandler.getSectionNode());
		
		SettingsInternetServicesSectionHandler internetServicesHandler = new SettingsInternetServicesSectionHandler(uiController);
		uiController.add(servicesRootNode, internetServicesHandler.getSectionNode());
		
		SettingsMmsSectionHandler mmsHandler = new SettingsMmsSectionHandler(uiController);
		uiController.add(servicesRootNode, mmsHandler.getSectionNode());
		
		return servicesRootNode;
	}
}
