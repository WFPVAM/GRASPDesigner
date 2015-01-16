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
package net.frontlinesms.plugins.forms.data.repository.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.events.DatabaseEntityNotification;
import net.frontlinesms.data.events.EntityDeleteWarning;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormResponse;
import net.frontlinesms.plugins.forms.data.repository.FormResponseDao;

/**
 * Hibernate implementation of {@link FormResponseDao}
 * @author Alex
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/> 
 */
public class HibernateFormResponseDao extends BaseHibernateDao<FormResponse> implements FormResponseDao, EventObserver {

	/** Create new instance of this DAO */
	public HibernateFormResponseDao() {
		super(FormResponse.class);
	}
	
	public void setEventBus(EventBus eventBus){
		eventBus.registerObserver(this);
		super.setEventBus(eventBus);
	}
	
	/** @see FormResponseDao#getFormResponseCount(Form) */
	public int getFormResponseCount(Form form) {
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq(FormResponse.FIELD_FORM, form));
		return super.getCount(criteria);
	}

	/** @see FormResponseDao#getFormResponses(Form, int, int) */
	public List<FormResponse> getFormResponses(Form form, int startIndex, int limit) {
		// TODO please write a unit test to demonstrate the working code working and the non-working
		// code NOT working.  This has proved difficult so far.  However, we (i.e. Morgan ;) worked
		// out what is wrong with the non-working code - it's returning <limit> objects rather than
		// <limit> FormResponses - it's counting the ResponseValue objects as well as the FormResponses.
		// Why?  Not sure.  Does it behave this way in the unit tests?  Nope.  )¬;
		
		// THIS DOES NOT WORK
//		DetachedCriteria criteria = super.getCriterion();
//		criteria.add(Restrictions.eq(FormResponse.FIELD_FORM, form));
//		return super.getList(criteria, startIndex, limit);
		
		// THIS WORKS:
		String selectString = "SELECT fr FROM " + FormResponse.class.getName() + " fr " +
				"WHERE " + FormResponse.FIELD_FORM + "=?";
		// TODO: CONTROLLARE PERCHE' su INTELLIJ la riga sotto va bene, mentre su eclipse no
		//return super.getList(selectString, startIndex, limit, form);
		return super.getList(selectString, form);
	}

	/** @see FormResponseDao#getFormResponses(Form) */
	public List<FormResponse> getFormResponses(Form form) {
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq(FormResponse.FIELD_FORM, form));
		return super.getList(criteria);
	}
	/** @see FormResponseDao#getFormResponse(String)
	 *  @author Fabaris a.aknai
	 */
	public FormResponse getFormResponse(String formResponseIdentifier) {
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq(FormResponse.FIELD_CODEFORM, formResponseIdentifier));
		return super.getUnique(criteria);
	}

	/** @see FormResponseDao#saveResponse(FormResponse) */
	public void saveResponse(FormResponse formResponse) {
		super.saveWithoutDuplicateHandling(formResponse);
	}
	
	public void updateResponse(FormResponse formResponse)
	{
		super.updateWithoutDuplicateHandling(formResponse);
	}
	public void notify(FrontlineEventNotification notification) {
		if (notification instanceof DatabaseEntityNotification<?>) {
			Object entity = ((DatabaseEntityNotification<?>) notification).getDatabaseEntity();
			
			if (entity instanceof Form && notification instanceof EntityDeleteWarning<?>) {
				deleteResponsesOf((Form) entity);
			}
		}
	}

	/**
	 * Deletes all responses related to a form
	 * @param form
	 */
	private void deleteResponsesOf(Form form) {
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq(FormResponse.FIELD_FORM, form));
		
		for (FormResponse formResponse : super.getList(criteria)) {
			super.delete(formResponse);
		}
	}

	public void delete(FormResponse formResponse) {
		super.delete(formResponse);
	}

	public FormResponse saveResponseAndReturnEntity(FormResponse formResponse) {
		return super.saveAndReturnEntityWithoutDuplicateHandling(formResponse);
	}

}