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
package net.frontlinesms.plugins.forms.data.repository.hibernate;

import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormResponse;
import net.frontlinesms.plugins.forms.data.domain.Survey;
import net.frontlinesms.plugins.forms.data.repository.SurveyDao;
/**
 * 
 *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/> 
 */
public class HibernateSurveyDao extends BaseHibernateDao<Survey> implements SurveyDao, EventObserver {

	protected HibernateSurveyDao() {
		super(Survey.class);
		// TODO Auto-generated constructor stub
	}
	
	public void setEventBus(EventBus eventBus) {
		eventBus.registerObserver(this);
		super.setEventBus(eventBus);
	}

	public void saveSurvey(Survey survey) {
		super.saveWithoutDuplicateHandling(survey);
		
	}

	public void deleteSurvey(Survey survey) {
		super.delete(survey); 
		
	}

	public Collection<Survey> getAllSurvey() {
		return super.getAll();
	}

	public void notify(FrontlineEventNotification notification) {
		// TODO Auto-generated method stub
		
	}

	public void updateSurvey(Survey survey) {
		super.updateWithoutDuplicateHandling(survey);
		
	}
	
	/**Fabaris_A.zanchi
	 * Returns a list of surveys owned by a form
	 * 
	 * @param form: the owner form 
	 * @return
	 */
	public List<Survey> getSurveyOwnedByForm(Form form) {
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq(Survey.FORM, form));
		return super.getList(criteria);
	}
	
	/**Fabaris_A.zanchi
	 * 
	 * @param name
	 * @pram owner the owner form
	 * @return
	 */
	public Survey getSurveyByNameAndOwner(String name, Form owner) {
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq(Survey.NAME, name));
		criteria.add(Restrictions.isNull(Survey.FORM));
		return super.getUnique(criteria);
	}

}
