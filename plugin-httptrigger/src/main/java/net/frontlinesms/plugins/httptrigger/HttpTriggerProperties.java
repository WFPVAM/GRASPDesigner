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

import net.frontlinesms.resources.UserHomeFilePropertySet;

/**
 * Persistent properties for the HttpTrigger plugin.
 * @author Alex Anderson <alex@frontlinesms.com>
 * @author Fabaris Srl: Attila Aknai
 * www.fabaris.it <http://www.fabaris.it/>  <http://www.fabaris.it/>  
 */
public class HttpTriggerProperties extends UserHomeFilePropertySet {
	
//> STATIC CONSTANTS
	private static final int DEFAULT_LISTEN_PORT = 8011;

//> PROPERTY KEYS
	public static final String PROP_AUTOSTART = "listener.autostart"; // FIXME this should be private
	public static final String PROP_LISTEN_PORT = "listener.port"; // FIXME this should be private
	private static final String PROP_SCRIPT_FILE_PATHS = "script.file.path";
	private static final String PROP_IGNORE_PATHS = "ignore.path";
	
	/** Singleton instance of this class */
	private static HttpTriggerProperties INSTANCE;
	
	private HttpTriggerProperties() {
		super("plugins.httptrigger");
	}

	public int getListenPort() {
		return super.getPropertyAsInt(PROP_LISTEN_PORT, DEFAULT_LISTEN_PORT);
	}
	
	public void setListenPort(int port) {
		super.setPropertyAsInteger(PROP_LISTEN_PORT, port);
	}
	
	public boolean isAutostart() {
		return super.getPropertyAsBoolean(PROP_AUTOSTART, false);
	}
	
	/** @param value new value for #PROP_AU */
	public void setAutostart(boolean value) {
		super.setPropertyAsBoolean(PROP_AUTOSTART, value);
	}

	public String[] getScriptFilePaths() {
		return super.getPropertyValues(PROP_SCRIPT_FILE_PATHS);
	}
	
	public String[] getIgnoreList() {
		return super.getPropertyValues(PROP_IGNORE_PATHS, "favicon.ico");
	}

//> STATIC FACTORIES
	/**
	 * Lazy getter for {@link #instance}
	 * @return The singleton instance of this class
	 */
	public static synchronized HttpTriggerProperties getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new HttpTriggerProperties();
		}
		return INSTANCE;
	}
}
