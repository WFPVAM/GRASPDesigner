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
package net.frontlinesms.plugins.forms.data.repository;

import java.util.List;

import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormResponse;

/**
 * Data Access Object for {@link FormResponse}.
 * @author Alex
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/> 
 */
public interface FormResponseDao {
	/**
	 * Gets a list of responses submitted for a form.
	 * @param form form whose responses we are fetching
	 * @param startIndex 
	 * @param limit 
	 * @return list of responses submitted
	 */
	public List<FormResponse> getFormResponses(Form form, int startIndex, int limit);

	/**
	 * Gets a list of all responses submitted for a form.
	 * @param form form whose responses we are fetching
	 * @return list of responses submitted
	 */
	public List<FormResponse> getFormResponses(Form form);
	/**
	 * Get The formResponse with that identifier
	 * @author Fabaris a.aknai
	 * @param formResponseIdentifier
	 * @return the form response with that identifier
	 */
	public FormResponse getFormResponse(String formResponseIdentifier);

	/**
	 * Gets the number of responses submitted for a form.
	 * @param form 
	 * @return number of responses submitted
	 */
	public int getFormResponseCount(Form form);

	/**
	 * Save a form response
	 * @param formResponse the response to save
	 */
	public void saveResponse(FormResponse formResponse);
	
	public void updateResponse(FormResponse formResponse);
	
	public FormResponse saveResponseAndReturnEntity(FormResponse formResponse);
	
	public void delete(FormResponse responseOne);
	
}
