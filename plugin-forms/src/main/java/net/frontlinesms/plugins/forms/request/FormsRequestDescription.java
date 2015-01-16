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

package net.frontlinesms.plugins.forms.request;

/**
 * A decoded incoming forms message.
 * @author Alex Anderson <alex@frontlinesms.com>
 */
public abstract class FormsRequestDescription {
	
//> INSTANCE PROPERTIES
	/** Port that this contact should be sent forms on. */
	private Integer smsPort;
	/** Indicates whether this client can receive data messages out-of-session.  If <code>true</code>, we can send
	 * forms directly to this client; if <code>false</code> we should only send text notifications of new forms, and
	 * wait for the client to specifically request forms before sending a data message. */
	private boolean outOfSessionSmsSupported;
	
//> ACCESSOR METHODS
	/** @param smsPort the smsPort to set */
	public void setSmsPort(int smsPort) {
		this.smsPort = smsPort;
	}
	/** @return the smsPort */
	public Integer getSmsPort() {
		return smsPort;
	}

	/** @param outOfSessionSmsSupported new value for {@link #outOfSessionSmsSupported} */
	public void setOutOfSessionSmsSupported(boolean outOfSessionSmsSupported) {
		this.outOfSessionSmsSupported = outOfSessionSmsSupported;
	}
	/** @return {@link #outOfSessionSmsSupported} */
	public boolean isOutOfSessionSmsSupported() {
		return outOfSessionSmsSupported;
	}
}
