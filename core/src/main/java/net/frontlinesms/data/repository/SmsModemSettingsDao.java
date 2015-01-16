package net.frontlinesms.data.repository;

import java.util.List;

import net.frontlinesms.data.domain.SmsModemSettings;

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

/**
 * Factory class to manage phone details.
 * @author Alex Anderson
 */
public interface SmsModemSettingsDao {
	/**
	 * Save a {@link SmsModemSettings} to the database.
	 * @param settings settings to save
	 */
	public void saveSmsModemSettings(SmsModemSettings settings);
	
	/**
	 * Gets {@link SmsModemSettings} for a particular device
	 * @param serial serial number of the device whose settings we are looking for
	 * @return settings for a particular device, or <code>null</code> if none are available
	 */
	public SmsModemSettings getSmsModemSettings(String serial);

	/**
	 * Updates changes to {@link SmsModemSettings}
	 * @param settings settings which have changed
	 */
	public void updateSmsModemSettings(SmsModemSettings settings);
	
	/**
	 * Count the number of {@link SmsModemSettings} in the database
	 */
	public int getCount();
	
	/**
	 * Get all the {@link SmsModemSettings} in the database
	 */
	public List<SmsModemSettings> getAll();
}
