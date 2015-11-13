package net.frontlinesms.ui.handler.phones;

import net.frontlinesms.FrontlineUtils;
import net.frontlinesms.data.domain.SmsModemSettings;
import net.frontlinesms.data.repository.SmsModemSettingsDao;
import net.frontlinesms.messaging.sms.modem.SmsModem;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.handler.settings.SettingsDeviceSectionHandler;
import net.frontlinesms.ui.i18n.InternationalisationUtils;
import net.frontlinesms.ui.i18n.TextResourceKeyOwner;

import org.apache.log4j.Logger;

/**
 * @author Morgan Belkadi <morgan@frontlinesms.com>
 * @author alex@frontlinesms.com
 *  @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/>  
 * */
 
@TextResourceKeyOwner
public class DeviceSettingsDialogHandler implements ThinletUiEventHandler {

//> UI LAYOUT FILES
	/** UI XML File Path: phone settings dialog TODO what is this dialog for? */
	private static final String UI_FILE_MODEM_SETTINGS_DIALOG = "/ui/core/phones/dgModemSettings.xml";
	/** FIXME comment please */
	private static final String UI_FILE_PANEL_MODEM_SETTINGS = "/ui/core/phones/pnDeviceSettings.xml";
	
//> UI COMPONENT NAMES
	/** UI Component name: checkbox for use device (at all) on/off setting */
	private static final String COMPONENT_RB_PHONE_DETAILS_ENABLE = "rbPhoneDetailsEnable";
	/** UI Component name: checkbox for use device for sending on/off setting */
	private static final String COMPONENT_PHONE_SENDING = "cbSending";
	/** UI Component name: checkbox for use device for receiving on/off setting */
	private static final String COMPONENT_PHONE_RECEIVING = "cbReceiving";
	/** UI Component name: checkbox for delete read messages on/off setting */
	private static final String COMPONENT_PHONE_DELETE = "cbDeleteMsgs";
	/** UI Component name: checkbox for delivery reports on/off setting */
	private static final String COMPONENT_PHONE_DELIVERY_REPORTS = "cbUseDeliveryReports";
	/** UI Component name: TODO */
	private static final String COMPONENT_PN_PHONE_SETTINGS = "pnPhoneSettings";
	/** UI Component name: textfield containing the SMSC number */
	private static final String COMPONENT_SMSC_NUMBER = "tfSmscNumber";
	/** UI Component name: textfield containing the PIN */
	private static final String COMPONENT_SIM_PIN = "tfPin";

//> INSTANCE PROPERTIES
	/** I18n Text Key: TODO */
	private static final String COMMON_SETTINGS_FOR_PHONE = "common.settings.for.phone";
	
	/** Logger */
	private Logger LOG = FrontlineUtils.getLogger(this.getClass());
	
	private UiGeneratorController ui;
	private Object dialogComponent;
	private SmsModem device;
	private boolean isNewPhone;
	
//> CONSTRUCTORS AND INITIALISERS
	public DeviceSettingsDialogHandler(UiGeneratorController ui, SmsModem device, boolean isNewPhone) {
		this.ui = ui;
		this.device = device;
		this.isNewPhone = isNewPhone;
	}
	
	/** Initializes the statistics dialog */
	void initDialog() {
		LOG.trace("INIT DEVICE SETTINGS DIALOG");	
		this.dialogComponent = this.ui.loadComponentFromFile(UI_FILE_MODEM_SETTINGS_DIALOG, this);
		this.ui.setText(dialogComponent, InternationalisationUtils.getI18nString(COMMON_SETTINGS_FOR_PHONE) + " '" + device.getModel() + "'");

		Object pnDeviceSettings = this.ui.loadComponentFromFile(UI_FILE_PANEL_MODEM_SETTINGS, this);
		this.ui.add(dialogComponent, pnDeviceSettings, 0);
		
		// Get the PIN and SMSC number, and display if they exist
		String smscNumber = this.device.getSmscNumber();
		if(smscNumber != null) this.ui.setText(this.find(COMPONENT_SMSC_NUMBER), smscNumber);
		String simPin = this.device.getSimPin();
		if(simPin != null) this.ui.setText(this.find(COMPONENT_SIM_PIN), simPin);
		
		if(!isNewPhone) {
			boolean useForSending = device.isUseForSending();
			boolean useForReceiving =device.isUseForReceiving();	
			Object cbDeliveryReports = this.find(COMPONENT_PHONE_DELIVERY_REPORTS);
			if(useForSending || useForReceiving) {
				this.ui.setSelected(this.find(COMPONENT_PHONE_SENDING), useForSending);
				
				ui.setEnabled(cbDeliveryReports, useForSending);
				ui.setSelected(cbDeliveryReports, device.isUseDeliveryReports());//commented by Fabaris_raji
				ui.setSelected(cbDeliveryReports,device.isUseDeliveryReports()); 
				System.out.println("DEVICE: "+device.isUseDeliveryReports()+" cbDeliveryReports "+cbDeliveryReports);
				ui.setSelected(this.find(COMPONENT_PHONE_RECEIVING), useForReceiving);
				Object cbDeleteMessages = this.find(COMPONENT_PHONE_DELETE);
							
				this.ui.setEnabled(cbDeleteMessages, useForReceiving);
				this.ui.setSelected(cbDeleteMessages, device.isDeleteMessagesAfterReceiving());
			} else {
				ui.setSelected(find("rbPhoneDetailsDisable"), true);
				ui.setSelected(find(COMPONENT_RB_PHONE_DETAILS_ENABLE), false);
				ui.deactivate(find(COMPONENT_PN_PHONE_SETTINGS));
			}

			
		}
		
		if(!device.supportsReceive()) {
			// If this phone does not support SMS receiving, we need to pass this info onto
			// the user.  We also want to gray out the options for receiving.
			ui.setEnabled(find(COMPONENT_PHONE_RECEIVING), false);
			ui.setEnabled(find(COMPONENT_PHONE_DELETE), false);		
			
		} else {
			// No error, so remove the error message.
			ui.remove(find("lbReceiveNotSupported"));
		}
		
		ui.setAttachedObject(dialogComponent, device);
		
		LOG.trace("EXIT");
	}
	
//> ACCESSORS
	public Object getDialog() {
		return this.dialogComponent;
	}
	
//> UI EVENT METHODS
	/**
	 * Event fired when the view phone details action is chosen.  We save the details
	 * of the phone to the database.
	 */
	public void updatePhoneDetails() {
		String serial = this.device.getSerial();

		boolean useForSending;
		boolean useDeliveryReports;
		boolean useForReceiving;
		boolean deleteMessagesAfterReceiving;
		
		if(ui.isSelected(find(COMPONENT_RB_PHONE_DETAILS_ENABLE))) {
			useForSending = ui.isSelected(find(COMPONENT_PHONE_SENDING));
			useDeliveryReports = ui.isSelected(find(COMPONENT_PHONE_DELIVERY_REPORTS));
			
			useForReceiving = ui.isSelected(find(COMPONENT_PHONE_RECEIVING));
		
			deleteMessagesAfterReceiving = ui.isSelected(find(COMPONENT_PHONE_DELETE));
		
		} else {
			useForSending = false;
			useDeliveryReports = false;
			useForReceiving = false;
			deleteMessagesAfterReceiving = false;
		}
		String smscNumber = ui.getText(find(COMPONENT_SMSC_NUMBER));
		String simPin = ui.getText(find(COMPONENT_SIM_PIN));
		
		device.setUseForSending(useForSending);
		device.setUseDeliveryReports(useDeliveryReports);
		if(device.supportsReceive()) {
			device.setUseForReceiving(useForReceiving);
			device.setDeleteMessagesAfterReceiving(deleteMessagesAfterReceiving);
		} else {
			useForReceiving = false;
			deleteMessagesAfterReceiving = false;
		}
		
		
		SmsModemSettingsDao smsModemSettingsDao = ui.getFrontlineController().getSmsModemSettingsDao();
		SmsModemSettings settings = smsModemSettingsDao.getSmsModemSettings(serial);
		boolean newSettings = settings == null;
		if(newSettings) {
			settings = new SmsModemSettings(serial);

			String manufacturer = device.getManufacturer();
			String model = device.getModel();
			
			settings.setManufacturer(manufacturer);
			settings.setModel(model);
		}
		settings.setUseForSending(useForSending);
		settings.setUseDeliveryReports(useDeliveryReports);
		settings.setUseForReceiving(useForReceiving);
		settings.setDeleteMessagesAfterReceiving(deleteMessagesAfterReceiving);
		settings.setSmscNumber(smscNumber);
		settings.setSimPin(simPin);
		
		if(newSettings) {
			smsModemSettingsDao.saveSmsModemSettings(settings);
		} else {
			smsModemSettingsDao.updateSmsModemSettings(settings);
		}
		
		// TODO check if this value has changed if there is any value to that
		device.setSmscNumber(smscNumber);
		// TODO check if this value has changed if there is any value to that
		// TODO how is the PIN change propagated?  Guessing that we will need to reconnect to the phone.
		device.setSimPin(simPin);
		
		removeDialog();
	}
	
	/** Used by the {@link SettingsDeviceSectionHandler} */
	public void smscNumberChanged(String smscNumber) { }
	
	/** Used by the {@link SettingsDeviceSectionHandler} */
	public void pinChanged(String pin) {}
	
	/** TODO someone please rename this method */
	public void phoneManagerDetailsUse(Object radioButton) {
		Object pnPhoneSettings = find(COMPONENT_PN_PHONE_SETTINGS);
		if(COMPONENT_RB_PHONE_DETAILS_ENABLE.equals(ui.getName(radioButton))) {
			ui.activate(pnPhoneSettings);
			// If this phone does not support SMS receiving, we need to pass this info onto
			// the user.  We also want to gray out the options for receiving.
			if(!this.device.supportsReceive()) {
				ui.setEnabled(ui.find(pnPhoneSettings, COMPONENT_PHONE_RECEIVING), false);
				ui.setEnabled(ui.find(pnPhoneSettings, COMPONENT_PHONE_DELETE), false);
			}
		} else ui.deactivate(pnPhoneSettings);
	}
	
	public void phoneManagerDetailsCheckboxChanged(Object checkbox) {
		ui.setEnabled(ui.getNextItem(ui.getParent(checkbox), checkbox, true), ui.isSelected(checkbox));
	}

	/** @see UiGeneratorController#removeDialog(Object) */
	public void removeDialog() {
		this.ui.removeDialog(dialogComponent);
	}
	public void showHelpPage(String page) {
		this.ui.showHelpPage(page);
	}

//> UI HELPER METHODS
	/** @return UI component with the supplied name, or <code>null</code> if none could be found */
	private Object find(String componentName) {
		return ui.find(this.dialogComponent, componentName);
	}
}
