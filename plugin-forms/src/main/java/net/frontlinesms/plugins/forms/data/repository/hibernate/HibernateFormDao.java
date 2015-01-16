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

package net.frontlinesms.plugins.forms.data.repository.hibernate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.data.domain.Contact;
import net.frontlinesms.data.domain.Group;
import net.frontlinesms.data.events.EntityDeleteWarning;
import net.frontlinesms.data.repository.hibernate.BaseHibernateDao;
import net.frontlinesms.events.EventBus;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.forms.data.domain.Form;
import net.frontlinesms.plugins.forms.data.domain.FormField;
import net.frontlinesms.plugins.forms.data.domain.FormFieldType;
import net.frontlinesms.plugins.forms.data.repository.FormDao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateQueryException;

/**
 * Hibernate implementation of {@link FormDao}
 * 
 * @author Alex Anderson <alex@frontlinesms.com>
 * @author Morgan Belkadi <morgan@frontlinesms.com>
 * *@author Fabaris Srl: Andrea Zanchi,Attila Aknai,Maria Cilione,Rajimol Joseph 
 * www.fabaris.it <http://www.fabaris.it/> 
 */
public class HibernateFormDao extends BaseHibernateDao<Form> implements FormDao, EventObserver {

	// > CONSTRUCTOR
	/** Create new instance of this DAO */
	public HibernateFormDao() {
		super(Form.class);
	}

	public void notify(FrontlineEventNotification notification) {
		if (notification instanceof EntityDeleteWarning<?>) {
			EntityDeleteWarning<?> deleteWarning = (EntityDeleteWarning<?>) notification;
			Object dbEntity = deleteWarning.getDatabaseEntity();

			if (dbEntity instanceof Group) {
				// de-reference any groups which are attached to forms
				dereferenceGroup((Group) dbEntity);
				return;
			}
		}
	}

	public void setEventBus(EventBus eventBus) {
		eventBus.registerObserver(this);
		super.setEventBus(eventBus);
	}

	/** @see FormDao#getFormsForUser(Contact, Collection) */
	@SuppressWarnings("unchecked")
	public Collection<Form> getFormsForUser(Contact contact, Collection<Integer> currentFormIds) {
		ArrayList<String> parametersNames = new ArrayList<String>();
		parametersNames.add("finalised");
		parametersNames.add("contact");

		ArrayList<Object> parametersValues = new ArrayList<Object>();
		parametersValues.add(true);
		parametersValues.add(contact);

		String queryString = "SELECT DISTINCT form" + " FROM Form AS form, GroupMembership AS mem" + " WHERE form."
				+ Form.FIELD_PERMITTED + " = mem.group" + " AND form." + Form.FIELD_FINALISED + " = :finalised"
				+ " AND mem.contact = :contact";

		if (currentFormIds.size() > 0) {
			Long[] longFormIds = new Long[currentFormIds.size()];
			int i = 0;
			for (Integer integer : currentFormIds)
				longFormIds[i++] = (long) integer;

			parametersNames.add("ids");
			parametersValues.add(longFormIds);
			queryString += " AND form." + Form.FIELD_ID + " NOT IN (:ids)";
		}

		return super.getHibernateTemplate().findByNamedParam(queryString,
				parametersNames.toArray(new String[parametersNames.size()]), parametersValues.toArray());
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> getList(Class<T> entityClass, String hqlQuery, Object... values) {
		return this.getHibernateTemplate().find(hqlQuery, values);
	}

	/** @see FormDao#getFromMobileId(int) */
	public Form getFromId(long id) {
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq(Form.FIELD_ID, id));
		return super.getUnique(criteria);
	}

	/** @see FormDao#saveForm(Form) */
	public void saveForm(Form form) {
		super.saveWithoutDuplicateHandling(form);
	}

	/** @see FormDao#updateForm(Form) */
	public void updateForm(Form form) {
		// We're not checking if the form is finalised here, because the group
		// can be modified, even finalised
		super.updateWithoutDuplicateHandling(form);
	}

	/** @see FormDao#deleteForm(Form) */
	public void deleteForm(Form form) {
		super.delete(form); 
	}


	/** @see FormDao#getAllForms() */
	public Collection<Form> getAllForms() {
		return super.getAll();
	}
	
	public Collection<Form> getAllActiveForms() {
		DetachedCriteria criteria = super.getCriterion();
		//criteria.add(Restrictions.sqlRestriction(" Form.isHidden is null OR Form.isHidden =0"));
		criteria.add(Restrictions.or(Restrictions.isNull(Form.FIELD_ISHIDDEN), Restrictions.eq(Form.FIELD_ISHIDDEN, new Integer(0))));
		return super.getList(criteria);
	}

	/** @see FormDao#getCount() */
	public int getCount() {
		return super.countAll();
	}

	/** @see FormDao#finaliseForm(Form) */
	public void finaliseForm(Form form) throws IllegalStateException {
		form.finalise();

		try {
			super.update(form);
		} catch (DuplicateKeyException e) {
			throw new RuntimeException("This mobile ID has already been set.");
		}
	}

	public void dereferenceGroup(Group group) {
		DetachedCriteria criteria = super.getCriterion();
		Criterion equals = Restrictions.eq(Form.FIELD_PERMITTED, group);
		Criterion like = Restrictions.like("permittedGroup.path", group.getPath() + Group.PATH_SEPARATOR,
				MatchMode.START);
		criteria.add(Restrictions.or(equals, like));
		List<Form> forms = getList(criteria);

		for (Form formWithDereferencedGroup : forms) {
			formWithDereferencedGroup.setPermittedGroup(null);
			try {
				this.update(formWithDereferencedGroup);
			} catch (DuplicateKeyException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException("There was a problem removing the deleted group from its attached form.");
			}
		}
	}

	/**
	 * aggiunto da Fabaris_maria c. e Fabaris_raji esegue query per prendere i
	 * dati che servono per la creazione del xform
	 *Fabaris_ a.zanchi: esegue query per ottenere l'id del futuro form salvato.
	 **/
	public String executeQueryXForm() {
		/*
		 * String sql =""+"select max(id) " + "from Form  " ;
		 */

		/*
		 * Fabaris_a.zanchi selects the max id, works even with an empty Form Table and
		 * more reliable.
		 *  Invoking this method a native SQL query is executed
		 * -INSTEAD OF HIBERNATE+SPRING QUERY-, so this part is strictly MS SQL
		 * Server related
		 */
		final String sql = "select ident_current('Form')";
		List resultList = (List) getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				SQLQuery sq = session.createSQLQuery(sql);
				return sq.list();
			}
		});

		BigDecimal bigResult = (BigDecimal) (resultList.get(0));
		//if (!(bigResult.equals(BigDecimal.valueOf(1)))) {
			//bigResult = bigResult.add(new BigDecimal(1));
			//System.out.println("adding");
		//}
		return bigResult.toString();

		/** Fabaris_maria cilione. Fabari_raji */
	}

	/** aggiunto da Fabaris_maria cilione. */
	public String querySendXForm(Form form) {

		String sql = " select ff.x_form from FormField as ff, Form as f where f.id=" + form.getFormMobileId() + " "
				+ "    and f.id = ff.form " + "";
		List<?> listid = getHibernateTemplate().find(sql);
		//System.out.println(form.getFormMobileId());
		return listid.toString();
	}
	/** Fabaris_maria cilione. **/

	public Form getFromId_IdMac(String id_idMac) {
		DetachedCriteria criteria = super.getCriterion();
		criteria.add(Restrictions.eq(Form.FIELD_ID_IDMAC, id_idMac));
		/*String sql = "select f.id from Form as f where f.id_flsmsId='"+id_idMac+"'";
		List<?> listid = getHibernateTemplate().find(sql);
		//System.out.println(listid.size());
		//System.out.println(listid.get(0));
		//System.out.println(listid.toString());
		if (listid != null && listid.size() != 0) {
			Long id = (Long) listid.get(0);
			System.out.println(id.longValue());
			return this.getFromId(id.longValue());
		}
		else
			return null;*/
		Form f =  super.getUnique(criteria);
		return f;
	}

	public long countFormsByGroup(Group g) {
		String sql = "select count(*) from Form as f where f.permittedGroup.path = ?";
		return super.getCount(sql, new Object[]{g.getPath()});
	}
	
	public long countFormsByGroups(Collection<Group> groups) {
		if(!groups.isEmpty()){
			List<String> paths = new ArrayList<String>();
			List result = null;
			for (Group group : groups){
				paths.add(group.getPath());
			}
			Session session = getHibernateTemplate().getSessionFactory().openSession();
			Query query = session.createQuery("select count(*) from Form form where form.permittedGroup.path IN (:paths)");
			result = query.setParameterList("paths", paths).list();
			session.close();
			if(result.isEmpty()){
				return 0;
			}else{
				return (Long)result.get(0);
			}
		}
		return 0;
	}
	
	public Collection<Form> getFormsByGroups(Collection<Group> groups){
		List<String> paths = new ArrayList<String>();
		List result = null;
		for (Group group : groups){
			paths.add(group.getPath());
		}
		Session session = getHibernateTemplate().getSessionFactory().openSession();
		Query query = session.createQuery("from Form form where form.permittedGroup.path IN (:paths)");
		result = query.setParameterList("paths", paths).list();
		session.close();
		return new ArrayList<Form>(result);
	}
	
	
	
	private String join(Collection<?> s, String delimiter) {
		StringBuilder builder = new StringBuilder();
		Iterator<?> iter = s.iterator();
		while (iter.hasNext()) {
			String value = (String) iter.next();
			if(value != null){
				value = value.replaceAll("(\r\n|\n\r|\r|\n)", "");
				builder.append(value);
				builder.append(delimiter);
				if (!iter.hasNext()) {
					break;
				}
			}else{
				value = " ";
				builder.append(value);
				builder.append(delimiter);
			}
		}
		return builder.toString();
	}
	
	
	
	
	public String getCSVByForm(Form form) {
		
	 	
	 	Session session = getHibernateTemplate().getSessionFactory().openSession();
	 	session.doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				List<String> columnNamesCsv1 = new ArrayList<String>();
				columnNamesCsv1.add("id");
			 	columnNamesCsv1.add("sender");
			 	columnNamesCsv1.add("code");
			}
		});
	 	
	 	
	 	
	 	session.close();
		return null;
	}
}