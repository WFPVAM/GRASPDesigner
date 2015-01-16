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
package net.frontlinesms.plugins.forms.response;

import java.util.Collection;

import net.frontlinesms.plugins.forms.request.SubmittedFormData;


/**
 * @author Alex
  * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph
  *  www.fabaris.it <http://www.fabaris.it/>  
  */
public class SubmittedDataResponse implements FormsResponseDescription {
	/** Data ids submitted successfully. */
	private Collection<SubmittedFormData> submittedData;
	/**Fabaris_a.zanchi useful to univoquely identify a form */
	private String formIdentificator;
	private String formResponseName;
	private long formResponceId = -1;
	
	/**
	 * Create a new instance of this class.
	 * @param submittedData
	 */
	public SubmittedDataResponse(Collection<SubmittedFormData> submittedData) {
		this.submittedData = submittedData;
	}
	
	/** @return {@link #submittedData} */
	public Collection<SubmittedFormData> getSubmittedData() {
		return submittedData;
	}

	public String getFormIdentificator() {
		return formIdentificator;
	}

	public void setFormIdentificator(String formIdentificator) {
		this.formIdentificator = formIdentificator;
	}

	public String getFormResponseName() {
		return formResponseName;
	}

	public void setFormResponseName(String formResponseName) {
		this.formResponseName = formResponseName;
	}

	public long getFormResponceId() {
		return formResponceId;
	}

	public void setFormResponceId(long formResponceId) {
		this.formResponceId = formResponceId;
	}
	
}
