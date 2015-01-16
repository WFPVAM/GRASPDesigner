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
package net.frontlinesms.plugins.forms.request;

import java.util.Set;

/**
 * A request containing details of forms filled in.
 * @author Alex
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/> 
 */
public class DataSubmissionRequest extends FormsRequestDescription {
	/** Set of data submitted in this request. */
	private final Set<SubmittedFormData> submittedData;
	/**Fabaris_A.zanchi useful to create an univoque response*/
	private String formIdentificator;
	private String formResponseName;

	/**
	 * Create a new instance of this class.
	 * @param submittedData
	 */
	public DataSubmissionRequest(Set<SubmittedFormData> submittedData) {
		super();
		this.submittedData = submittedData;
	}

	/**
	 * @return the submittedData
	 */
	public Set<SubmittedFormData> getSubmittedData() {
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
	
	
}
