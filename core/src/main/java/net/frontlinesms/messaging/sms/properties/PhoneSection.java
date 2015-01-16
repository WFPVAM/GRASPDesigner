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
package net.frontlinesms.messaging.sms.properties;


/**
 * Encapsulates a String representing a phone.
 *
 * @author Carlos Eduardo Endler Genz
 * @date 09/02/2009
 */
public final class PhoneSection {
	public static final String BUTTON_ICON = "/icons/user_sender.png";
	private String value;

	public PhoneSection(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
