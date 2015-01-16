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
package net.frontlinesms.listener;

import org.smslib.CIncomingMessage;

import net.frontlinesms.data.domain.*;
import net.frontlinesms.messaging.FrontlineMessagingService;
import net.frontlinesms.messaging.FrontlineMessagingServiceStatus;

/**
 * Abstract - implement this one event listener to be passed SMS messages.
 * 
 * @author Ben Whitaker ben(at)masabi(dot)com
 */
public interface FrontlineMessagingListener {
	/**
	 * Event Handler for incoming SMS messages
	 * @param receiver The device that this message was received on.
	 * @param incomingMessage The received message.
	 */
	public void incomingMessageEvent(FrontlineMessagingService receiver, CIncomingMessage incomingMessage);
	
	/**
	 * Event Handler for outgoing SMS messages
	 * @param sender The device that this message was sent on.
	 * @param outgoingMessage The sent message.
	 */
	public void outgoingMessageEvent(FrontlineMessagingService sender, FrontlineMessage outgoingMessage);
	
	/**
	 * called when one of the SMS devices (phones or http senders) has a change in status,
	 * such as detection, connection, disconnecting, running out of batteries, etc.
	 * see PhoneHandler.STATUS_CODE_MESSAGES[smsDeviceEventCode] to get the relevant messages
	 *  
	 * @param activeService
	 * @param serviceStatus the new status of the {@link SmsService}
	 */
	public void messagingServiceEvent(FrontlineMessagingService activeService, FrontlineMessagingServiceStatus serviceStatus);
		
}
