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
package net.frontlinesms.plugins.forms.data.repository;

import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;

import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.Group;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;

/**
 * Data Access Object for {@link Form}.
 * @author Alex
 * @author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/> 
 */
public interface FormDao {
	/**
	 * Get all forms that a user does not already have.
	 * @param contact
	 * @param currentFormMobileIds
	 * @return list of forms to send to this user
	 */
	public Collection<Form> getFormsForUser(Contact contact, Collection<Integer> currentFormMobileIds);
	
	
	public long countFormsByGroup(Group g);
	
	public long countFormsByGroups(Collection<Group> groups);
	
	/**
	 * Get a form from its ID.
	 * @param id
	 * @return {@link Form} with the specified id or <code>null</code> if none could be found.
	 */
	public Form getFromId(long mobileId);

	/**Fabaris_a.zanchi
	 * Get a form from its ID_IdMac (the id with the id of the machine)
	 * @param id
	 * @return {@link Form} with the specified id or <code>null</code> if none could be found.
	 */
	public Form getFromId_IdMac(String id_idMac);
	
	
	public String getCSVByForm(Form form);
	
	/**
	 * Saves a form to the data source.
	 * @param form form to save
	 */
	public void saveForm(Form form);

	/**
	 * Updates the form in the data source
	 * @param form the form to update
	 */
	public void updateForm(Form form);

	/**
	 * Deletes a form from the data source.
	 * @param form form to save
	 */
	public void deleteForm(Form form);
	
	/** @return all forms saved in the data source */
	public Collection<Form> getAllForms();
	
	/** @return only the form set as visible in the designer */
	public Collection<Form> getAllActiveForms();
	
	/** @return number of forms saved in the data source */
	public int getCount();
	
	public Collection<Form> getFormsByGroups(Collection<Group> groups);
	
	/**
	 * Finalise a form to prevent it being edited again.
	 * @param form The form to finalise
	 * @throws IllegalStateException If the form could not be finalised, either because it has no group set, or because the data source has run out of mobile IDs
	 */
	public void finaliseForm(Form form) throws IllegalStateException;
        public void setFinalisedForm(Form form) throws IllegalStateException;       
	/** Remove all references to a {@link Group} from {@link Entity}s in the Forms plugin. */
	public void dereferenceGroup(Group group);
	/**Aggiunto da Fabaris_maria cilione.*/
	public String executeQueryXForm();
	/**Fabaris_maria cilione*/
	/**Aggiunto da Fabaris_maria cilione.*/
	public String querySendXForm(Form form);
	/**Fabaris_maria cilione*/
}
