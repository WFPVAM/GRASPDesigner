/*
*GRASP(Geo-referential Real-time Acquisition Statistics Platform) Designer Tool <http://www.fabaris.it>
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
package net.frontlinesms.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import net.frontlinesms.resources.FilePropertySet;
import net.frontlinesms.resources.ResourceUtils;

/**
 * Class describing a set of settings for database connectivity.

 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph      
 * www.fabaris.it <http://www.fabaris.it/>  
 */
public class DatabaseTransferSettings {
	
//> INSTANCE PROPERTIES
	/** The parent directory in which the settings file is found */
	private File parentDirectory;
	/** The name of the file containing the settings. */
	private String xmlFileName;
	/** Properties attached to the database settings. */
	private DatabaseSettingsPropertySet properties;
	
//> CONSTRUCTORS
	private DatabaseTransferSettings() {}
	
//> ACCESSORS
	/** @param fileName the name of the xml file containing the settings. */
	private void setXmlSettingsFile(File parentDirectory, String fileName) {
		this.parentDirectory = parentDirectory;
		this.xmlFileName = fileName;
	}

	/** @return the relative path to the settings file */
	public String getFilePath() {
		return this.xmlFileName;
	}
	
	/** @return keys for all properties */
	public Set<String> getPropertyKeys() {
		return this.properties.getKeys();
	}
	
	/**
	 * Set a property.
	 * @param propertyKey
	 * @param propertyValue the new value for the property
	 */
	public void setPropertyValue(String propertyKey, String propertyValue) {
		assert(this.properties.contains(propertyKey)) : "Cannot set value for non-existent property: '" + propertyKey + "'";
		this.properties.set(propertyKey, propertyValue);
	}

	/**
	 * Gets the value for a property.
	 * @param propertyKey
	 * @return the value of the property
	 */
	public String getPropertyValue(String propertyKey) {
		assert(this.properties.contains(propertyKey)) : "Cannot get value for non-existent property: '" + propertyKey + "'";
		return this.properties.get(propertyKey);
	}
	
//> INSTANCE METHODS
	/** Loads the properties from external file if they are not already initialised. */
	public synchronized void loadProperties() {
		if(this.properties == null) {
			this.properties = DatabaseSettingsPropertySet.loadForSettings(parentDirectory, xmlFileName);
		}
	}
	
	public synchronized void saveProperties() {
		assert(this.properties != null) : "Cannot save null properties.";
		this.properties.saveToDisk();
	}
	
//> STATIC FACTORIES
	/**
	 * Gets all DatabaseSettings which are available in the file system.
	 * @return
	 */
	public static List<DatabaseTransferSettings> getSettings() {
		File settingsDirectory = new File(ResourceUtils.getConfigDirectoryPath() + ResourceUtils.PROPERTIES_DIRECTORY_NAME);
		String[] settingsFiles = settingsDirectory.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".database.xml");
			}
		});
		
		List<DatabaseTransferSettings> settings = new ArrayList<DatabaseTransferSettings>();
		for(String settingsFilePath : settingsFiles) {
			settings.add(createFromPath(settingsDirectory, settingsFilePath));
		}
		return settings;
	}

	/**
	 * Creates a {@link DatabaseTransferSettings} from a file found at a particular path.
	 * @param fileName the path to the settings file
	 * @return a new {@link DatabaseTransferSettings}
	 */
	private static DatabaseTransferSettings createFromPath(File parentDirectory, String fileName) {
		DatabaseTransferSettings settings = new DatabaseTransferSettings();
		settings.setXmlSettingsFile(parentDirectory, fileName);
		return settings;
	}

	/** @return a human-readable name for these settings */
	public String getName() {
		// TODO for now, we just display the file path; in future it might be nice to pretify it somehow
		return this.xmlFileName;
	}
}

class DatabaseTransferSettingsPropertySet extends FilePropertySet {
	private DatabaseTransferSettingsPropertySet(File parentDirectory, String databaseXmlFilePath) {
		super(new File(parentDirectory, databaseXmlFilePath + ".properties"));
	}
	
	Set<String> getKeys() {
		return super.getPropertyKeys();
	}
	
	boolean contains(String key) {
		return super.getProperty(key) != null;
	}
	
	void set(String key, String value) {
		super.setProperty(key, value);
	}
	
	String get(String key) {
		return super.getProperty(key);
	}
	
	static DatabaseTransferSettingsPropertySet loadForSettings(File parentDirectory, String databaseXmlFile) {
		DatabaseTransferSettingsPropertySet props = new DatabaseTransferSettingsPropertySet(parentDirectory, databaseXmlFile);
		LinkedHashMap<String, String> propertyMap = new LinkedHashMap<String, String>();
		FilePropertySet.loadPropertyMap(propertyMap, props.getFile());
		props.setProperties(propertyMap);
		return props;
	}
}
