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
package net.frontlinesms.plugins.forms.response;

import java.util.Collection;

import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.plugins.forms.data.domain.Form;

/**
 * Response wrapping forms to send to a user.
 * @author Alex
 */
public class NewFormsResponse implements FormsResponseDescription {
	/** The contact that this response will be sent to. */
	private final Contact contact;
	/** New forms to send to a contact. */
	private final Collection<Form> newForms;

	/**
	 * Create a new response wrapping forms to send.
	 * @param contact
	 * @param newForms
	 */
	public NewFormsResponse(Contact contact, Collection<Form> newForms) {
		this.contact = contact;
		this.newForms = newForms;
	}

	/**
	 * @return the contact
	 */
	public Contact getContact() {
		return contact;
	}

	/**
	 * @return the newForms
	 */
	public Collection<Form> getNewForms() {
		return newForms;
	}

}
