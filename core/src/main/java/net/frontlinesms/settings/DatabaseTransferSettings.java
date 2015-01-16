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
package net.frontlinesms.settings;

import java.io.File;


import java.io.FilenameFilter;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.DataTransfer;
import net.frontlinesms.resources.FilePropertySet;
import net.frontlinesms.resources.ResourceUtils;
import net.frontlinesms.ui.DatabaseSettings;
import net.frontlinesms.ui.handler.core.DatabaseConnectionFailedDialog;



/**
 * Class describing a set of settings for database connectivity.
 * @author aga
 */
public class DatabaseTransferSettings {	
//> INSTANCE PROPERTIES
	/** Properties attached to the database settings. */	
private DatabaseTransferSettingsPropertySet properties;
private DatabaseTransferSettings selectedSettings;
public String key;
public String value;
//> CONSTRUCTORS
	public DatabaseTransferSettings() {}
	
//> ACCESSORS
	/** @return keys for all properties */

//> INSTANCE METHODS
	/** Loads the properties from table to initialise the control panel. 
	 * @return map of string */
	
	
	public synchronized void savePropertiesTable(DataTransfer dt) {
		assert(this.selectedSettings != null) : "Cannot save null properties.";	
		
		try {			
			//properties.saveSettingsIn(dt);
			DatabaseTransferSettingsPropertySet.saveSettingsIn(dt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private String getPropertyValuek(String key) {
		DatabaseTransferSettings settings =this.selectedSettings ;
		// TODO Auto-generated method stub
		return null;
	}

	private boolean getPropertyValueb(String key) {
		DatabaseTransferSettings settings =this.selectedSettings ;
		
		  Object obj = settings;
		
		return obj.equals(obj);
	}
	/*
	 * Property keys for settings panel
	 */
	public ArrayList<String> getPropertyKeys() {		
		LinkedHashMap<String, String> settings = DatabaseTransferSettingsPropertySet.loadForSettings();	
		ArrayList<String> al=new ArrayList<String>();
	    Set<String> st = settings.keySet();	  
	    //iterate through the Set of keys
	    Iterator<String> itr = st.iterator();
	    String k=null;//have to adjust
	    while(itr.hasNext()){
	    	k=itr.next();
	    	 al.add(k);
	    }	   
	   return al;
	}	
	
	
	public ArrayList<String> getPropertyBooleanKeys() {		
		LinkedHashMap<String, Boolean> settings = DatabaseTransferSettingsPropertySet.loadForSettingsBoolean();	
		ArrayList<String> al=new ArrayList<String>();
	    Set<String> st = settings.keySet();	  
	    //iterate through the Set of keys
	    Iterator<String> itr = st.iterator();
	    String k=null;//have to adjust
	    while(itr.hasNext()){
	    	k=itr.next();
	    	 al.add(k);
	    }	   
	   return al;
	}
		// TODO Auto-generated method stub
	
	public String getPropertyValue(String key) {
		LinkedHashMap<String, String> settings =DatabaseTransferSettingsPropertySet.loadForSettings() ;
		  Object obj = settings.get(key);
		return obj.toString();
	}

	private boolean contains(String key) {
		HashMap<String, String> hMap = new HashMap<String, String>();		
		return  hMap.containsKey(key);
	}

	private void set(String key, String value) {		
	   	this.key=key;
	   	this.value=value;
	}

	public void getPropertyValue(String key, String value) {
		// TODO Auto-generated method stub
		
	}

	public boolean getPropertyBooleanValue(String key) {
		LinkedHashMap<String, Boolean> settings =DatabaseTransferSettingsPropertySet. loadForSettingsBoolean() ;
		  Object obj = settings.get(key);
		return (Boolean) obj;
	}

}	
//> STATIC FACTORIES
	
class DatabaseTransferSettingsPropertySet  {
	/** Loads the String values from table*/
	static  LinkedHashMap<String, String> loadForSettings() {
		LinkedHashMap<String, String> propertyMap = new LinkedHashMap<String, String>();
		DataTransfer dt;
		
		try {
			 dt = connection_get();
			 propertyMap.put("servername",dt.getServername());
			 propertyMap.put("serverport",dt.getServerport());
			 propertyMap.put("dbname",dt.getDbname());
			 propertyMap.put("dbusername",dt.getUsername());
			 propertyMap.put("dbpassword",dt.getPassword());			 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return propertyMap;
		
	}
	static  LinkedHashMap<String, Boolean> loadForSettingsBoolean() {
		LinkedHashMap<String, Boolean> propertyMap = new LinkedHashMap<String, Boolean>();
		DataTransfer dt;		
		try {
			 dt =connection_get();
			 propertyMap.put("sharable",dt.isSharable());			 
			 propertyMap.put("manual",dt.isManual());	
			 propertyMap.put("scheduled",dt.isScheduled());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return propertyMap;		
	}
	
	/*
	 * Method to save settings as an instance of class DataTransfer
	 */
	static  void saveSettingsIn(DataTransfer dt) {	
			
		try {
			FrontlineSMS frontline = new FrontlineSMS();	
			boolean connected = false;	
				
			while(!connected){	
				
				try{
					frontline.initApplicationContext();					
					connected = true;
					
					frontline.getDataTransferDao().saveDataTransfer(dt);
				}
				catch(RuntimeException ex) {				
					frontline.deinitApplicationContext();
					DatabaseConnectionFailedDialog.create(ex).acquireSettings();
				}
		 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
		/**Initialise database connection and select from database 
		 * @throws Exception */
		public static DataTransfer connection_get() throws Exception{
		FrontlineSMS frontline = new FrontlineSMS();	
			boolean connected = false;		
			DataTransfer datatransfer=new DataTransfer();			
			while(!connected){			
				try{
					frontline.initApplicationContext();					
					connected = true;			
					datatransfer=frontline.getDataTransferDao().getConfig();				
				}
				catch(RuntimeException ex) {				
					frontline.deinitApplicationContext();
					DatabaseConnectionFailedDialog.create(ex).acquireSettings();
				}		 
			}
			return  datatransfer;
		}
	
}


