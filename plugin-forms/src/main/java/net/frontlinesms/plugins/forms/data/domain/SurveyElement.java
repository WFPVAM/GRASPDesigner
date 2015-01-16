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

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
/**
 * Class modeling a "Survey Element" .
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/> 
 * 
 */
@Entity
public class SurveyElement {

	@SuppressWarnings("unused")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false, updatable = false)
	private long id;

	private String value;

	private int positionIndex;
	
	private Boolean defaultValue = false;
	
	public SurveyElement() {
		
	}
	
	public SurveyElement(String value) {
		this.value = value;
	}
	
	public SurveyElement(String value, int position) {
		this.value = value;
		this.positionIndex = position;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.value;
	}

	public int getPositionIndex() {
		return positionIndex;
	}

	public void setPositionIndex(int position) {
		this.positionIndex = position;
	}
	
	public Boolean isDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(Boolean defaultValue) {
		this.defaultValue = defaultValue;
	}
}
