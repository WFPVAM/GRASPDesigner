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

/**
 * @author Alex
 * @author Fabaris Srl: Attila Aknai
 * www.fabaris.it <http://www.fabaris.it/>  <http://www.fabaris.it/>  
 */
public interface HttpTriggerEventListener {
	/**
	 * Adds a new item to the log. 
	 * @param message message to log
	 * TODO this should actually use an enumerated list of different events
	 */
	public void log(String message);

	/**
	 * Sends an SMS to the requested number.
	 * @param toPhoneNumber The number to send the SMS to
	 * @param message The message to send in the SMS
	 */
	public void sendSms(String toPhoneNumber, String message);

	public String testConnection(String phone, String data);
	public String formResponse(String phone, String data);

	public String syncUserForms(String phone, String data);
}
