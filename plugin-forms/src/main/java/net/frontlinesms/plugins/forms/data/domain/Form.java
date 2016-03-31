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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Cascade;

import net.frontlinesms.data.domain.Group;

/**
 * A form for filling in with data.
 * @author Alex Anderson <alex@frontlinesms.com>
 * @author Morgan Belkadi <morgan@frontlinesms.com>
 * @author Dieterich Lawson <dieterich@medic.frontlinesms.com>
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph  
 *    www.fabaris.it <http://www.fabaris.it/>     
 */
@SuppressWarnings("serial")
@Entity
public class Form implements Serializable {
//> FIELD NAMES
	/** Column name for {@link #mobileId} */
	public static final String FIELD_ID = "id";	
	/** Column name for {@link #id_flsmsId} */
	public static final String FIELD_ID_IDMAC = "id_flsmsId";
	/** Column name for {@link #finalised} */
	public static final String FIELD_FINALISED = "finalised";
	/** Column name for {@link #permittedGroup} */
	public static final String FIELD_PERMITTED = "permittedGroup";
	/** Column name for {@link #isHidden} */
	public static final String FIELD_ISHIDDEN = "isHidden";
        /** Column name for {@link #FormVersion} */
        public static final String FIELD_FORMVERSION = "FormVersion";
          /** Column name for {@link #PreviousPublishedName} */
        public static final String FIELD_PREVIOUSPUBLISHEDNAME = "PreviousPublishedName";
           /** Column name for {@link #IsDeleted} */
        public static final String FIELD_ISDELETED = "IsDeleted";
           /** Column name for {@link #DeletedDate} */
        public static final String FIELD_DELETEDDATE = "DeletedDate";
               /** Column name for {@link #PreviousPublishedName} */
        public static final String FIELD_PREVIOUSPUBLISHEDID = "PreviousPublishedID";
	/**Aggiunto da maria c.*/
//> INSTANCE PROPERTIES
	/** Unique id for this entity.  This is for hibernate usage. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true,nullable=false,updatable=false)
	private long id;
	public long getId() {
		return id;
	}

	/** To know if the form has been finalised yet */
	private boolean finalised;	
	/** The name of this form */
	private String name;
	/** Phone numbers which are allowed to download this form. */
	@ManyToOne
	private Group permittedGroup;
	/** Fields attached to this form */

	@OneToMany(fetch=FetchType.EAGER, targetEntity=FormField.class,cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@OrderBy(value="positionIndex asc")
	private List<FormField> fields = new ArrayList<FormField>();
       
	/**Fabaris_maria cilione.*/
	//Fabaris_a.zanchi String to represent the AND-OR binding policy
	private String bindingsPolicy;
	//Fabaris_a.zanchi String to represent the designer software version who created the Form
	private String designerVersion;		
	private boolean pushed=false;//Fabaris_raji	
	private String owner;//Fabaris_raji	
	private String id_flsmsId;//Fabaris_raji	
	private Integer isHidden=0;
        private Integer FormVersion= 0;
        private String PreviousPublishedName;
        private String DeletedDate;
        private Integer IsDeleted=0;
        private String PreviousPublishedID;

	/**Fabaris_maria cilione.*/
	//> CONSTRUCTORS
	/** Empty constructor for hibernate */
	Form() {}
	
	/**Aggiunto da Fabaris_maria cilione.*/
	
	public Form(String name) {
		this.name = name;
		this.finalised = false;
		
	}
	/**Fabaris_maria cilione*/
//> ACCESSOR METHODS
	/**
	 * Check whether this form is finalised
	 * @return <code>true</code> if this form has had its {@link #finalised} flag set to true; <code>false</code> otherwise.
	 */
	public boolean isFinalised() {
		return this.finalised;
	}
	/**
	 * Set the form as finalised
	 */
	public void finalise() {
		this.finalised = true;
	}
        public void unFinalise(){
            this.finalised=false;
           
        }
        
        public void setPreviousPublishedName(String previousName){
             this.PreviousPublishedName =previousName;
        }
        
        public String getPreviousPublishedName(){
            return this.PreviousPublishedName;
        }
        /*
        change the form verion
        */
        public void setFormVersion( int versionNumber){
           this.FormVersion = versionNumber; 
        }
	/** @return {@link #name} */
	public String getName() {
		return this.name;
	}

	/** @param name the new value for {@link #name} */
	public void setName(String name) {
		this.name = name;
	}
	/** @return {@link #fields} */
	public List<FormField> getFields() {
		return Collections.unmodifiableList(this.fields);
	}
        
      
	
	/** @return {@link #permittedGroup} */
	public Group getPermittedGroup() {
		return permittedGroup;
	}
        public Integer getFormVersion(){
            return this.FormVersion;
        }

	/** @param group new value for {@link #permittedGroup} */
	public void setPermittedGroup(Group group) {
		this.permittedGroup = group;
	}
	
	//Fabaris_a.zanchi
		public String getBindingsPolicy() {
			return bindingsPolicy;
		}
		//Fabaris_a.zanchi
		/**Method to set the AND-OR policy for bindings
				 * Please use the strings "All" or "Any";
				 * 
				 * @param bindingsPolicy
		 */
		public void setBindingsPolicy(String bindingsPolicy) {
					this.bindingsPolicy = bindingsPolicy;
		}
		//Fabaris_a.zanchi
		public String getDesignerVersion() {
			return designerVersion;
		}

		public void setDesignerVersion(String designerVersion) {
			this.designerVersion = designerVersion;
		}
		//Fabaris_a.zanchi
		//Fabaris_raji
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

		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public String getId_flsmsId() {
			return id_flsmsId;
		}

		public void setId_flsmsId(String id_flsmsId) {
			this.id_flsmsId = id_flsmsId;
		}
		//Fabaris_raji
		
	/**
	 * Adds a new field at the end of the form
	 * @param newField the field to add
	 */
	public void addField(FormField newField) {
		this.addField(newField, this.fields.size());
	}

	/**
	 * Removes a field from this form.
	 * @param formField the field to remove
	 */
	public void removeField(FormField formField) {
		this.fields.remove(formField);
	}

	/**
	 * Adds a new field at the specified position.
	 * @param newField the {@link FormField} to add
	 * @param position the position on the form to add the new field at
	 */
	public void addField(FormField newField, int position) {
		newField.setPositionIndex(position);
		newField.setForm(this);
		this.fields.add(position, newField);
	}
	
	/** @return the mobileId of this form in the data source 
	 * This id actually is the database id of the form, but we keep using this function to prevent errors with previous versions 
	 * */
	public int getFormMobileId() {
		return (int)id;
	}

	/** @return number of fields that are editable */
	public int getEditableFieldCount() {
		int count = 0;
		for(FormField field : this.fields) {
			if(field.getType().hasValue()) {
				++count;
			}
		}
		return count;
	}
	
	
//> GENERATED CODE
//	/** @see java.lang.Object#hashCode() */
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
//		result = prime * result + os;
//		result = prime * result + ((name == null) ? 0 : name.hashCode());
//		return result;
//	}
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result + (finalised ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		/**aggiunto da Fabaris_maria cilione*/
//		result = prime * result + ((Code_Form == null) ? 0 : Code_Form.hashCode());
		/**Fabaris_maria cilione*/
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Form other = (Form) obj;
		if (fields == null) {
			if (other.fields != null)
				return false;
		} else if (!fields.equals(other.fields))
			return false;
		if (finalised != other.finalised)
			return false;

//		/**Aggiunto da Fabaris_maria cilione*/
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	
	/**Fabaris_A.zanchi
	 * Sets the survey null on every formfield component 
	 */
	public void freeSurveyFromFormField() {
		for (FormField ff : this.fields) {
			ff.setSurvey(null);
			if (ff.getType() == FormFieldType.REPEATABLES || ff.getType() == FormFieldType.REPEATABLES_BASIC) {
				for (FormField insideFF : ff.getRepetables()) {
					insideFF.setSurvey(null);
					insideFF.setForm(this);
				}
			}
		}
	}

	public int getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(int isHidden) {
		this.isHidden = isHidden;
	}
        
        public void setIsDeleted(int deleted){
            this.IsDeleted= deleted;
        }
        public int getIsDeleted(){
            return this.IsDeleted;
        }
        public void setDeletedDate(String deletedDate){
            this.DeletedDate =deletedDate;
        }
        public String getDeletedDate(){
            return this.DeletedDate;
        }
        public void setPreviousPublishedID(String previousPublishedId ){
            this.PreviousPublishedID= previousPublishedId;
        }
        public String getPreviousPublishedID(){
            return this.PreviousPublishedID;
        }
}
