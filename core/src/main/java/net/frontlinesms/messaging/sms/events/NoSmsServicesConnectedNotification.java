package net.frontlinesms.messaging.sms.events;

import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.messaging.sms.SmsService;

/**
 * Event thrown when {@link SmsService} detection finds that there are no devices. 
 * @author Alex Anderson <alex@frontlinesms.com>
 */
public class NoSmsServicesConnectedNotification implements FrontlineEventNotification {
	private final boolean incompatibleDevicesDetected;
	private final boolean ownedPortsDetected;
	
	public NoSmsServicesConnectedNotification(boolean incompatibleDevicesDetected, boolean ownedPortsDetected) {
		this.incompatibleDevicesDetected = incompatibleDevicesDetected;
		this.ownedPortsDetected = ownedPortsDetected;
	}

	public boolean isIncompatibleDevicesDetected() {
		return incompatibleDevicesDetected;
	}

	public boolean isOwnedPortsDetected() {
		return ownedPortsDetected;
	}

	public boolean isNoDevices() {
		return !incompatibleDevicesDetected && !ownedPortsDetected;
	}
}
