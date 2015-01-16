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
package net.frontlinesms.plugins.forms.data.domain;

import java.io.Serializable;

import javax.persistence.*;



import net.frontlinesms.data.EntityField;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent;
import net.frontlinesms.plugins.forms.ui.components.PreviewComponent.BindType;

/**Class modeling the domain object who represents a binding.
 * It contains a BindType and the optionals value and range value
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph        
 *   www.fabaris.it <http://www.fabaris.it/>  
 *
 */
@Entity

public class BindingContainer implements Cloneable, Serializable{


	@SuppressWarnings("unused")
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long id;
	
	/* COLUMN NAMES*/
	private static final String FIELD_VALUE = "value";
	private static final String FIELD_MINRANGE = "minRange";
	private static final String FIELD_MAXRANGE = "maxRange";
	private static final String FIELD_BINDINGTYPE = "bType";
	
	//ENTITY FIELDS
	/** 
	 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph        
	 *   www.fabaris.it <http://www.fabaris.it/>  
	 */
	public enum Field implements EntityField<BindingContainer> {
		VALUE(FIELD_VALUE),
		MINRANGE(FIELD_MINRANGE),
		MAXRANGE(FIELD_MAXRANGE),
		BINDINGTYPE(FIELD_BINDINGTYPE);
		
		private final String fieldName;
		
		Field(String fieldName) { this.fieldName = fieldName; }

		public String getFieldName() {
			return this.fieldName;
		}
		
	}
	
	//INSTANCE PROPERTIES
	
	//@Column(name=FIELD_BINDINGTYPE)
	@Enumerated(EnumType.STRING)
	private BindType bType;
	@Column(name=FIELD_VALUE)
	private String value;
	@Column(name=FIELD_MINRANGE)
	private int minRange;
	@Column(name=FIELD_MAXRANGE)
	private int maxRange;	
	/** The flag pushed attached to this field. */
	private Boolean pushed=false;//Fabaris_Raji
	
	public BindingContainer() {
		
	}

	public BindingContainer(BindType bType, String value, int minRange, int maxRange) {
		this.bType = bType;
		this.value = value;
		this.minRange = minRange;
		this.maxRange = maxRange;
	}

	public BindType getbType() {
		return bType;
	}

	public void setType(BindType newType) {
		this.bType = newType;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public int getMinRange() {
		return minRange;
	}

	public int getMaxRange() {
		return maxRange;
	}
	/**
	 * checks if pushed or not.
	 * @param True if pushed
	 */
	public boolean isPushed() {
		return pushed;
	}
	/**
	 * Sets pushed as true or false
	 * @param pushed
	 */
	public void setPushed(boolean pushed) {
		this.pushed = pushed;
	}

	
	public BindingContainer clone() {
		BindingContainer cloned = new BindingContainer(bType, value, minRange, maxRange);
		return cloned;
	}

	/**
	 * a.zanchi
	 */
	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof BindingContainer))
			return false;
		else {
			BindingContainer ext = (BindingContainer) arg0;
			if (ext.bType.equals(this.bType) && ext.getValue() == this.value && ext.getMaxRange() == this.maxRange && ext.getMinRange() == this.minRange)
				return true;
			else
				return false;
		}
	}

	@Override
	public int hashCode() {
		String hashString = this.getValue()+minRange+maxRange+bType;
		return hashString.hashCode();
	}
	
	
}
