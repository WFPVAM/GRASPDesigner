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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * A field in a form.
 * 
 * @author Alex
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph  
 *    www.fabaris.it <http://www.fabaris.it/>    

 */
@SuppressWarnings("serial")
@Entity
public class FormField implements Serializable {
	
	//Fabaris_a.zanchi
	public static String AND_POLICY = "All";
	public static String OR_POLICY = "Any";
	
	// > INSTANCE PROPERTIES
	/** Unique id for this entity. This is for hibernate usage. */
	@SuppressWarnings("unused")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false, updatable = false)
	private long id;
	/** The label attached to this field. */
	private String label;
	/** The name attached to this field. */
	@Column(nullable = false)
	private String name; // Fabaris_raji
	/** The reuired attached to this field. */
	private Boolean required;// Fabaris_Raji
	/** The flag pushed attached to this field. */
	private Boolean pushed = false;// Fabaris_Raji
	/** The type of this field. */
	@Enumerated(EnumType.STRING)
	private FormFieldType type;
	// Fabaris_raji
	private String id_flsmsId;
	//private long form_id;
	
	public long getId()
	{
		return id;
	}

	public String getId_flsmsId() {
		return id_flsmsId;
	}

	public void setId_flsmsId(String id_flsmsId) {
		this.id_flsmsId = id_flsmsId;
	}
	// Fabaris_raji
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Form.class)
	private Form form;

	/** The position of the field within the form. */
	private int positionIndex;
	/** Aggiunto da Fabaris_maria cilione. */
	// a.zanchi
	@org.hibernate.annotations.Type(type = "text")
	private String x_form;

	/**
	 * Fabaris_a.zanchi: list of repetable form fields depending on this form
	 * field
	 */
	@OneToMany(targetEntity = FormField.class, cascade = javax.persistence.CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@LazyCollection(LazyCollectionOption.FALSE)
	@OrderBy(value="positionIndex asc")
	private List<FormField> repetableFormFields = new ArrayList<FormField>();

	/** Fabaris_a.zanchi the eventual survey reference */
	@OneToOne
	// @Cascade(CascadeType.DELETE_ORPHAN)
	private Survey survey;

	/** Fabaris_a.zanchi number of repetitions if repeatable */
	private int numberOfRep;
	/** Fabaris_a.aknai is calculated */
	private Boolean calculated;
	/** Fabaris_a.aknai formula*/
	private String formula;

	// Fabaris_a.zanchi
	private Boolean isReadOnly = false;
	
	//policy for constraints and bindings
	private String constraintPolicy;
	private String bindingsPolicy;
	
	@OneToMany(targetEntity = ConstraintContainer.class, cascade=javax.persistence.CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<ConstraintContainer> constraints = new ArrayList<ConstraintContainer>();

	@OneToMany(targetEntity = FormFieldAndBinding.class, cascade = javax.persistence.CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<FormFieldAndBinding> bindingCouples = new ArrayList<FormFieldAndBinding>();
	
	@OneToMany(targetEntity = FormResponse.class)
	@Cascade(CascadeType.DELETE_ORPHAN)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<FormResponse> formResponses = new ArrayList<FormResponse>();
	
	/*
	 * WORKING
	 * 
	 * @OneToMany(targetEntity=FormField.class) private List<FormField> bFields
	 * = new ArrayList<FormField>();
	 * 
	 * @OneToMany(targetEntity=BindingContainer.class) private
	 * List<BindingContainer> bContainers = new ArrayList<BindingContainer>();
	 */

	/** Aggiunto da Fabaris_maria cilione. */
	// > CONSTRUCTORS
	/** Empty constructor for hibernate */
	FormField() {
	}

	/**
	 * Get a new {@link FormField}.
	 * 
	 * @param type
	 * @param label
	 * @param name
	 * @param required
	 * @param required
	 */
	public FormField(FormFieldType type, String label, String x_form, String name, boolean required) {
		this.type = type;
		this.label = label;
		this.name = name; // Fabaris_raji
		this.required = required; // Fabaris_raji
		/** Aggiunto Fabaris_maria cilione. */
		this.x_form = x_form;
	}

	public String getX_form() {
		return x_form;
	}

	public void setX_form(String x_form) {
		this.x_form = x_form;
	}

	/** Fabars_maria cilione. */
	// > ACCESSOR METHODS
	/** @return {@link #type} */
	public FormFieldType getType() {
		return this.type;
	}

	/** @return {@link #label} */
	public String getLabel() {
		return this.label;
	}

	/**
	 * @param label
	 *            new value for {@link #label}
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	// Fabaris_raji
	/** @return {@link #name} */
	public String getName() {
		return this.name;
	}

	/**
	 * @param label
	 *            new value for {@link #name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** @return {@link #required} */
	public Boolean getRequired() {
		return this.required;
	}

	/**
	 * @param label
	 *            new value for {@link #required}
	 */
	public void setRequired(Boolean required) {
		this.required = required;
	}

	// Fabaris_raji
	/** @return {@link #positionIndex} */
	public int getPositionIndex() {
		return this.positionIndex;
	}

	/**
	 * @param positionIndex
	 *            new value for {@link #positionIndex}
	 */
	public void setPositionIndex(int positionIndex) {
		this.positionIndex = positionIndex;
	}

	/**
	 * checks if pushed or not.
	 * 
	 * @param True
	 *            if pushed
	 */// Fabaris_raji
	public boolean isPushed() {
		return pushed;
	}

	/**
	 * Sets pushed as true or false
	 * 
	 * @param pushed
	 */
	public void setPushed(boolean pushed) {
		this.pushed = pushed;
	}// Fabaris_raji

	// > GENERATED CODE
	/** @see java.lang.Object#hashCode() */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());// Fabaris_raji
		// result = prime * result + ((required) ? 0 :
		// required.hashCode());//Fabaris_raji
		result = prime * result + positionIndex;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		/** aggiunto da Fabaris_maria cilione. */
		result = prime * result + ((x_form == null) ? 0 : x_form.hashCode());
		/** Fabaris_maria cilione. */
		return result;
	}

	/** @see java.lang.Object#equals(java.lang.Object) */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormField other = (FormField) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (positionIndex != other.positionIndex)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		/** aggiunto da Fabairs_maria cilione. */
		if (x_form == null)
			return false;
		/** maria c. */
		
		//Fabaris_a.zanchi checks for name 
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		
		return true;
	}

	public void setForm(Form form) {
		this.form = form;
                
	}

	/** Aggiunto da Fabaris_maria cilione. */
	public Form getForm() {
		return form;
	}
	
	
	public void addResponse(FormResponse response){
		this.formResponses.add(response);
	}
	
	public void removeResponse(FormResponse response){
		this.formResponses.remove(response);
	}
	
	/** Fabaris_maria cilione. */

	// Fabaris_a.zanchi
	/**
	 * Inserts a new binding between formFields
	 * 
	 * @param f
	 *            object of type {@link FormField}
	 * @param bind
	 *            object of type {@link BindingContainer}
	 */
	public void addBinding(FormField f, BindingContainer bind) {
	//	System.out.println(this.bindingCouples.size());
		FormFieldAndBinding toAdd = new FormFieldAndBinding(f, bind);
		if (!this.bindingCouples.contains(toAdd)) {
			this.bindingCouples.add(toAdd);
		}
	}
	

	/**
	 * Fabaris_a.zanchi Removes a Binding
	 * 
	 * @param f
	 * @param bind
	 */
	public void removeBinding(FormField f, BindingContainer bind) {
		for (FormFieldAndBinding fab : this.bindingCouples) {
			if (f.equals(fab.getfField()) && bind.equals(fab.getbContainer()))
				this.bindingCouples.remove(fab);
		}
	}

	/**
	 * Fabaris_a.zanchi Method removing every binding reference of a specified FormField
	 * 
	 * @param f
	 *            object of type Formfield
	 */
	public void removeAllBindings(FormField f) {
		for (FormFieldAndBinding fab : this.bindingCouples) {
			if (fab.getfField().equals(f)) {
				this.bindingCouples.remove(fab);
			}
		}
	}

	public void cleanBindingCouples() {
		this.bindingCouples.clear();
	}

	/**
	 * Fabaris_a.zanchi Method changing an existing binding
	 * 
	 * @param f
	 *            the {@link FormField} on which the binding is active
	 * @param oldBind
	 *            the old type of bind
	 * @param newBind
	 *            the new type of bind
	 */
	public void changeBinding(FormField f, BindingContainer oldBind, BindingContainer newBind) {
		for (FormFieldAndBinding fab : this.bindingCouples) {
			if (f.equals(fab.getfField()) && oldBind.equals(fab.getbContainer())) {
				fab.setbContainer(newBind);
			}
		}
	}
	
	/**Fabaris_a.zanchi
	 * 
	 * Deletes the bindings removed in the form designer comparing the differences 
	 * 
	 * @param componentToBind
	 */
	public void deleteUnusedBindings(Map<FormField, ArrayList<BindingContainer>> componentToBind) {
		ArrayList<FormFieldAndBinding> copy =  new ArrayList<FormFieldAndBinding>();
		for (FormFieldAndBinding f : this.bindingCouples) {
			copy.add(f);
		}
		for (FormFieldAndBinding fb : copy) {
			if (componentToBind.keySet().contains(fb.getfField())) {
				if (componentToBind.get(fb.getfField()).contains(fb.getbContainer())) {
					
				}
				else {
					this.bindingCouples.remove(fb);
				}
			}
			else {
				this.bindingCouples.remove(fb);
			}
		}
	}
	
	/**Fabaris_a.zanchi
	 * 
	 * Deletes repeatables elements not used anymore after modifications in the form designer on preview components
	 * 
	 */
	public void deleteUnusedRepeatables(List<FormField> usedRepeatables) {
		ArrayList<FormField> copy = new ArrayList<FormField>();
		for (FormField f : this.getRepetables()) {
			copy.add(f);
		}
		for (FormField f : copy) {
			if (!usedRepeatables.contains(f)) {
				this.getRepetables().remove(f);
			}
		}
	}

	public List<FormFieldAndBinding> getBindingCouples() {
		return this.bindingCouples;
	}

	public void addRepetable(FormField fField) {
		this.repetableFormFields.add(fField);
	}

	public void removeRepeatable(FormField fField) {
		this.repetableFormFields.remove(fField);
	}

	public List<FormField> getRepetables() {
		return this.repetableFormFields;
	}

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public int getNumberOfRep() {
		return numberOfRep;
	}

	public void setNumberOfRep(int numberOfRep) {
		this.numberOfRep = numberOfRep;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public List<ConstraintContainer> getConstraints() {
		return constraints;
	}

	public void addConstraint(ConstraintContainer cont) {
		if (!this.constraints.contains(cont))
			this.constraints.add(cont);
	}
	
	public void removeConstraint(ConstraintContainer cont) {
		this.constraints.remove(cont);
	}
	
	public void deleteUnusedConstraints(List<ConstraintContainer> usedCont) {
		ArrayList<ConstraintContainer> copy = new ArrayList<ConstraintContainer>();
		for (ConstraintContainer cont : this.constraints) {
			copy.add(cont);
		}
		
		for (ConstraintContainer cont : copy) {
			if (!usedCont.contains(cont)) {
				this.constraints.remove(cont);
			}
		}
	}

	public Boolean getCalculated() {
		return calculated;
	}

	public void setCalculated(Boolean calculated) {
		this.calculated = calculated;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getConstraintPolicy() {
		return constraintPolicy;
	}

	public void setConstraintPolicy(String constraintPolicy) {
		this.constraintPolicy = constraintPolicy;
	}

	public String getBindingsPolicy() {
		return bindingsPolicy;
	}

	public void setBindingsPolicy(String bindingsPolicy) {
		this.bindingsPolicy = bindingsPolicy;
	}

	/*public long getForm_id() {
		return form_id;
	}

	public void setForm_id(long form_id) {
		this.form_id = form_id;
	}*/
}