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
package net.frontlinesms.data.domain;
import java.sql.Time;

import javax.persistence.*;

import net.frontlinesms.data.EntityField;
	
/**
	 * Data object representing a DataTransfer.  
	 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
	 * www.fabaris.it <http://www.fabaris.it/>  
	 */
@Entity
public class DataTransfer {
		/** Table name */
		//public static final String TABLE_NAME = "DataTransfer";
	//> COLUMN NAME CONSTANTS
		/** Column name for {@link #ID} */
		private static final String FIELD_ID ="id";
		/** Column name for {@link #servername} */
		private static final String FIELD_SERVERNAME ="servername";
		/** Column name for {@link #serverport} */
		private static final String FIELD_SERVERPORT ="serverport";
		/** Column name for {@link #dbname} */
		private static final String FIELD_DBNAME ="dbname";
		/** Column name for {@link #dbusername} */
		private static final String FIELD_USERNAME ="dbusername";
		/** Column name for {@link #dbpassword} */		
		private static final String FIELD_PASSWORD ="dbpassword";
		/** Column name for {@link #sharable} */
		private static final String FIELD_SHARABLE ="sharable";
		/** Column name for {@link #scheduled} */
		private static final String FIELD_SCHEDULED ="scheduled";
		/** Column name for {@link #manual} */
		private static final String FIELD_MANUAL="manual";
		/** Column name for {@link #scheduledtime} */
		private static final String FIELD_SCHEDULEDTIME="scheduledtime";
		
	//> ENTITY FIELDS
		/** Details of the fields that this class has. */
		public enum Field implements EntityField<DataTransfer> {	
			
			/** field mapping for {@link DataTransfer#id} */
			ID(FIELD_ID),
			/** field mapping for {@link DataTransfer#servername} */
			SERVERNAME(FIELD_SERVERNAME),
			/** field mapping for {@link DataTransfer#serverport} */
			SERVERPORT(FIELD_SERVERPORT),
			/** field mapping for {@link DataTransfer#dbname} */
			DBNAME(FIELD_DBNAME),
			/** field mapping for {@link DataTransfer#username} */
			USERNAME(FIELD_USERNAME),
			/** field mapping for {@link DataTransfer#password} */
			PASSWORD(FIELD_PASSWORD),			
			/** field mapping for {@link DataTransfer#sharable} */
			SHARABLE (FIELD_SHARABLE),
			/** field mapping for {@link DataTransfer#scheduled} */
			SCHEDULED(FIELD_SCHEDULED),
			/** field mapping for {@link DataTransfer#manual} */
			MANUAL(FIELD_MANUAL),
			/** field mapping for {@link DataTransfer#scheduledtime} */
			SCHEDULEDTIME(FIELD_SCHEDULEDTIME);			
			private final String fieldName;
			/**
			 * Creates a new {@link Field}
			 * @param fieldName name of the field
			 */
			Field(String fieldName) { this.fieldName = fieldName; }
			/** @see EntityField#getFieldName() */
			public String getFieldName() { return this.fieldName; }
		}
	//> INSTANCE PROPERTIES
		/** Unique id for this entity.  This is for hibernate usage. */	
		@Id 
		@Column(unique=true,nullable=false,updatable=true)
		private int id;
		@Column(name=FIELD_SERVERNAME,nullable=true)
		private String servername;
		@Column(name=FIELD_SERVERPORT,nullable=true)
		private String serverport;
		@Column(name=FIELD_USERNAME,nullable=true)
		private String dbusername;
		@Column(name=FIELD_PASSWORD,nullable=true)
		private String dbpassword;
		@Column(name=FIELD_DBNAME,nullable=true)
		private String dbname;
		@Column(name=FIELD_SHARABLE,nullable=true)
		private boolean sharable;
		@Column(name=FIELD_SCHEDULED,nullable=true)
		private boolean scheduled;
		@Column(name=FIELD_MANUAL,nullable=true)
		private boolean manual;	
		@Column(name=FIELD_SCHEDULEDTIME,nullable=true)
		private Time scheduledtime;	
		
	//> CONSTRUCTORS
		/** Empty constructor for hibernate 
		 * @return */
		public DataTransfer() {}
		
		/**
		 * Creates a DataTransfer with the specified attributes.
		 * @param id The id
		 * @param servername The name of the remote server
		 * @param server port The port of the  server 
		 * @param dbname The database name    
		 * @param username The username                   
		 * @param password The password 
		 * @param sharable isSharable             
		 * @param scheduled isScheduled
		 * @param manual isManual
		 * @param scheduledtime
		 * 
		 */
		public DataTransfer(String servername, String serverport,String dbname,String dbusername, String dbpassword, 
				boolean sharable,boolean scheduled,boolean manual,Time scheduledtime) {				
			this.servername =servername;
			this.serverport =serverport;			
			this.dbname=dbname;		
			this.dbusername=dbusername;
			this.dbpassword=dbpassword;
			this.sharable=sharable;
			this.scheduled=scheduled;
			this.manual=manual;
			this.scheduledtime=scheduledtime;
		}
		public DataTransfer(String servername, String serverport,String dbname,String username, String password)
		{			
		this.servername =servername;
		this.serverport =serverport;			
		this.dbname=dbname;		
		this.dbusername=username;
		this.dbpassword=password;		
	     }

		public DataTransfer(boolean sharable, boolean scheduled,boolean manual) {
			// TODO Auto-generated constructor stub
			this.sharable=sharable;
			this.scheduled=scheduled;
			this.manual=manual;
		}

		//> ACCESSOR METHODS
		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	
		public String getServername() {
			return servername;
		}

		public void setServername(String servername) {
			this.servername = servername;
		}

		public String getServerport() {
			return serverport;
		}

		public void setServerport(String serverport) {
			this.serverport = serverport;
		}

		public String getUsername() {
			return dbusername;
		}

		public void setUsername(String dbusername) {
			this.dbusername = dbusername;
		}

		public String getPassword() {
			return dbpassword;
		}

		public void setPassword(String dbpassword) {
			this.dbpassword = dbpassword;
		}

		public String getDbname() {
			return dbname;
		}

		public void setDbname(String dbname) {
			this.dbname = dbname;
		}

		public boolean isSharable() {
			return sharable;
		}

		public void setSharable(boolean sharable) {
			this.sharable = sharable;
		}

		public boolean isScheduled() {
			return scheduled;
		}

		public void setScheduled(boolean scheduled) {
			this.scheduled = scheduled;
		}

		public boolean isManual() {
			return manual;
		}

		public void setManual(boolean manual) {
			this.manual = manual;
		}
		public Time getScheduledtime() {
			return scheduledtime;
		}

		public void setScheduledtime(Time scheduledtime) {
			this.scheduledtime =scheduledtime;
		}
		

	
	//> GENERATED CODE
		/** @see Object#toString() */
		@Override
		public String toString() {
			return this.getClass().getName() + "[" +
					"id=" + this.id + ";" +
					"servername=" + this.servername + ";" +
					"serverport=" + this.serverport + ";" +
					"dbname=" + this.dbname + ";" +				
					"username=" + this.dbusername + ";" +
					"password=" + this.dbpassword+ ";" +						
					"sharable=" + this.servername + ";" +
					"scheduled=" + this.serverport + ";" +
					"manual=" + this.dbname + ";" +				
					"scheduledtime=" + this.scheduledtime+ ";" +				
					"]";
		}
		/** Generates a hashcode for the {@link DataTransfer} using the {@link #user_id} field. */
		
		//@Override
		
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((servername == null) ? 0 : servername.hashCode());
			return result;
		}
		
		/** Checks that the two {@link User_Credentials}have the same {@link #user_id} */
		/** @see java.lang.Object#equals(java.lang.Object) */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DataTransfer other = (DataTransfer) obj;
			if (servername== null) {
				if (other.servername != null)
					return false;
			} else if (!servername.equals(other.servername))
				return false;
			if (dbpassword!= other.getPassword())
				return false;
			
			return true;
		}
		public String getSortingField(Field sortBy) {
			switch (sortBy) {
			case DBNAME:
				return getDbname();					
			default:
				throw new IllegalStateException("Trying to sort a user by something different than the name or surname");
			}
			
		}

	}



