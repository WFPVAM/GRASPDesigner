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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;

import net.frontlinesms.data.EntityField;

/**
 * 
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/> 
 */
/**
 *         This class represents a Couple of a FormField and a BindingContainer
 *         objects. It is used in FormField objects to store the bindings
 *         instead of a Map implementation (because it's hard to manage in hibernate)
 */

@Entity
public class FormFieldAndBinding implements Serializable {
	
	
	//TABLE'S COLUMN NAMES
	private static final String FIELD_FORMFIELD = "fField";
	private static final String FIELD_BINDINGCOINTANER = "bContainer";
	private static final String FIELD_PUSHED = "pushed";
	
	//INSTANCE PROPERTIES
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long id;
	@ManyToOne(targetEntity=FormField.class)
	private FormField fField;
	@OneToOne(targetEntity=BindingContainer.class,cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private BindingContainer bContainer;
	/** The reuired attached to this field. */
	private Boolean pushed=false;//Fabaris_Raji
	//ENTITY FIELDS
	public enum Field implements EntityField<FormFieldAndBinding> {
		FORMFIELD(FIELD_FORMFIELD),
		BINDINGCONTAINER(FIELD_BINDINGCOINTANER),
		PUSHED(FIELD_PUSHED);
		
		private String fieldName;
		
		Field(String fieldName) {
			this.fieldName = fieldName;
		}
		public String getFieldName() {
			return this.fieldName;
		}
		
	}
	
	public FormFieldAndBinding() {}
	
	public FormFieldAndBinding(FormField fField, BindingContainer bContainer) {
		this.fField = fField;
		this.bContainer = bContainer;
	}

	public FormField getfField() {
		return fField;
	}

	public void setfField(FormField fField) {
		this.fField = fField;
	}

	public BindingContainer getbContainer() {
		return bContainer;
	}

	public void setbContainer(BindingContainer bContainer) {
		this.bContainer = bContainer;
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

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof FormFieldAndBinding) {
			FormFieldAndBinding obj = (FormFieldAndBinding) arg0;
			//if (!obj.getfField().getName().equals(this.fField.getName())) {
			if (!obj.getfField().equals(this.fField)) {
				return false;
			}
			if (!obj.getbContainer().equals(this.bContainer)) {
				return false;
			}
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	
	
	

	
	
	
}
