package net.frontlinesms.ui.handler.settings;

import java.util.ArrayList;
import java.util.List;
import net.frontlinesms.AppProperties;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.settings.FrontlineValidationMessage;
import net.frontlinesms.settings.BaseSectionHandler;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.i18n.InternationalisationUtils;
import net.frontlinesms.ui.settings.UiSettingsSectionHandler;

import org.apache.log4j.Logger;

/**
 * UI Handler for the "Appearance" section of the Core Settings
 * @author Morgan Belkadi <morgan@frontlinesms.com>
 */
public class SettingsWebServerSectionHandler extends BaseSectionHandler implements UiSettingsSectionHandler, ThinletUiEventHandler {
	protected final Logger log = FrontlineUtils.getLogger(this.getClass());

	private static final String UI_SECTION_APPEARANCE = "/ui/core/settings/webServer/pnWebServerSettings.xml";

	private static final String COMPONENT_TF_WEB_SERVER_PATH = "tfWebServerPath";


	private static final String SECTION_ITEM_WEB_SERVER_PATH = "WEB_SERVER_PATH";
	private static final String I18N_SETTINGS_MENU_WEBSERVER = "settings.menu.webServer";

	private static final String SECTION_ICON = "/icons/webServer16x16.png";

	public SettingsWebServerSectionHandler (UiGeneratorController uiController) {
		super(uiController);
		this.uiController = uiController;
	}
	
	protected void init() {
		this.panel = uiController.loadComponentFromFile(UI_SECTION_APPEARANCE, this);
		iniWebServerSettings();
	}
	
	/** Show the settings dialog for the home tab. */
	public void iniWebServerSettings() {
		log.trace("ENTER");
		String webServerLocation = AppProperties.getInstance().getWebServerImagesPath();
                log.debug("Image location [" + webServerLocation + "]");
		if (webServerLocation != null && webServerLocation.length() > 0) {
			this.uiController.setText(find(COMPONENT_TF_WEB_SERVER_PATH), webServerLocation);
		}
		// Save the original values
		this.originalValues.put(SECTION_ITEM_WEB_SERVER_PATH, webServerLocation);
		log.trace("EXIT");
	}

	public void save() {
		log.trace("Saving Web server settings...");
		String webServerPath = this.uiController.getText(this.uiController.find(panel, COMPONENT_TF_WEB_SERVER_PATH));
		AppProperties appProperties = AppProperties.getInstance();
		appProperties.setWebServerImagesPath(webServerPath);
		appProperties.saveToDisk();
		log.trace("EXIT");
	}

	public List<FrontlineValidationMessage> validateFields() {
		List<FrontlineValidationMessage> validationMessages = new ArrayList<FrontlineValidationMessage>();
		return validationMessages;
	}
		
	
	public void webServerPathChanged(String webServerPath) {
		this.uiController.setText(find(COMPONENT_TF_WEB_SERVER_PATH), webServerPath);
		this.settingChanged(SECTION_ITEM_WEB_SERVER_PATH, webServerPath);
	}
	
	
	/**
	 * @param component
	 * @see UiGeneratorController#showOpenModeFileChooser(Object)
	 */
	public void showDirectoryChooser(Object component) {
		this.uiController.showDirectoryChooser(this, "webServerPathChanged");
	}

	public String getTitle() {
		return InternationalisationUtils.getI18nString(I18N_SETTINGS_MENU_WEBSERVER);
	}
	
	public Object getSectionNode() {
		return createSectionNode(InternationalisationUtils.getI18nString(I18N_SETTINGS_MENU_WEBSERVER), this, this.getIcon());
	}

	private String getIcon() {
		return SECTION_ICON;
	}
}
