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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import net.frontlinesms.data.EntityField;
import net.frontlinesms.data.domain.Email.Field;

/** Object representing Roles of a user
 * Class to populate Roles
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
 * www.fabaris.it <http://www.fabaris.it/>  
 *  
 */

@Entity
public class Roles {
	//> COLUMN_CONSTANTS
	/** Column name for {@link #name} */
	private static final String DESCRIPTION ="description";	
	//> ENTITY FIELDS
	public enum Field implements EntityField<Roles> {
		
		FDESCRIPTION(DESCRIPTION);		
		/** name of a field */
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
	@SuppressWarnings("unused")
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) 
	@Column(unique=true,nullable=false,updatable=false) 
	private int id;
	@Column(name=DESCRIPTION)	
	private String Description;	
	Roles(){}	
	public Roles(String Description) {
		this.Description = Description;		
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Roles other = (Roles) obj;
		if (Description == null) {
			if (other.Description != null)
				return false;		
			} 
		return true;
	}

}
