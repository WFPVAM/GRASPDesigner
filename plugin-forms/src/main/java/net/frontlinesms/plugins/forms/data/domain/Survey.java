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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * Class modeling a "Survey" to display in a dropox form components. It just has
 * some String values
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/> 
 * 
 */
@Entity
public class Survey implements Serializable, Comparable<Survey>{

	public static final String FORM = "owner";
	public static final String NAME = "name";
	
	@SuppressWarnings("unused")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false, updatable = false)
	private long id;

	private String name;
	// initialize here (instead of constructor) to mantain datas when hibernate
	// retrieves from database
	// @OneToMany(targetEntity = SurveyElement.class, cascade =
	// javax.persistence.CascadeType.ALL)
	// @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	// @IndexColumn(name="order")
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	// @LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)  // fetch = FetchType.EAGER
	//@IndexColumn(name = "position", nullable = false, base = 0)
	//@JoinColumn(name = "survey", nullable = false)
	
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@LazyCollection(LazyCollectionOption.FALSE)
	@OrderBy(value="positionIndex asc")
	private List<SurveyElement> values = new ArrayList<SurveyElement>();
	
	@ManyToOne
	private Form owner;
	
	// default Constructor
	public Survey() {

	}

	public void addValue(String value) {
		int size = this.values.size();
		this.values.add(new SurveyElement(value, size));
	}

	public void removeValue(String value) {
		ArrayList<SurveyElement> copy = new ArrayList<SurveyElement>();
		for (SurveyElement el : this.values) {

			copy.add(el); 
		}
		for (SurveyElement el : copy) {
			if (el.getValue().equals(value))
				this.values.remove(el);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if (this.owner != null)
			return this.owner.getName()+"."+this.name;
		else
			return this.name;
	}

	public List<SurveyElement> getValues() {
		return this.values;
	}

	
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Survey) {
			Survey comparing = (Survey) arg0;
			if (comparing.toString().equals(this.toString())) {
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}
	}
	
	public SurveyElement containsAdefaultValue(){
		for (SurveyElement el : this.getValues()) {
			if (el.isDefaultValue()){
				return el;
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.toString().hashCode();
	}

	public Survey clone() {
		Survey cloned = new Survey();
		//cloned.setName(String.valueOf(this.getId()));          //30.10.2013        //
		cloned.setName(this.getName());
		for (SurveyElement s : this.getValues()) {
			cloned.addValue(s.getValue());
		}
		return cloned;
	}

	public Form getOwner() {
		return owner;
	}

	public void setOwner(Form owner) {
		this.owner = owner;
	}

	/**Fabaris_A.zanchi
	 * Implementation for correct sorting of surveys
	 */
	public int compareTo(Survey arg0) {
		if (arg0.getOwner() == null && this.owner != null)
			return 1;
		else if (arg0.getOwner() != null && this.owner == null)
			return -1;
		else
			return this.toString().compareToIgnoreCase(arg0.toString());
	}

	public long getId() {
		return id;
	}
	
}
